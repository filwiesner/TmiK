package com.ktmi.irc

import com.ktmi.irc.IrcState.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import org.w3c.dom.MessageEvent
import kotlin.coroutines.CoroutineContext


actual class IRC actual constructor(
    private val token: String,
    private val username: String,
    private val secure: Boolean,
    context: CoroutineContext
) : TwitchIRC, CoroutineScope by CoroutineScope(context) {
    private var ws: WebSocket? = null

    private val messageChannel: Channel<RawMessage> = Channel(Channel.UNLIMITED)
    override val messages: ReceiveChannel<RawMessage> get() = messageChannel

    private val stateChannel: Channel<IrcState> = Channel(Channel.UNLIMITED)
    override val states: ReceiveChannel<IrcState> get() = stateChannel

    private var state: IrcState = DISCONNECTED
    override val currentState: IrcState
        get() = state


    override fun connect() {
        disconnect()

        val port = if (secure) 443 else 80
        val protocol = if (secure) "wss" else "ws"

        ws = WebSocket("$protocol://irc-ws.chat.twitch.tv:$port")

        ws!!.apply {
            onopen = {
                setState(CONNECTING)
                authorize(token, username)
            }
            onclose = { setState(DISCONNECTED) }
            onmessage = ::onMessage
            onerror = { setState(DISCONNECTED) }
        }
    }

    override fun disconnect() {
        ws?.apply {
            onopen = null
            onclose = null
            onmessage = null
            onerror = null
        }
        ws?.close(1000)
    }

    override fun sendMessage(message: String) {
        ws?.send(message)
    }

    // === Private methods ==

    private fun onMessage(ev: MessageEvent) {
        val text = ev.data as String

        if (text.startsWith("PING"))
            sendMessage("PONG :tmi.twitch.tv")
        else {
            if (text.startsWith(":tmi.twitch.tv 001"))
                setState(CONNECTED)

            launch {
                for (line in text.trim().lines())
                    if (!ignored(line))
                        messageChannel.send(parseMessage(line))
            }
        }
    }

    private fun setState(state: IrcState) {
        this.state = state
        launch { stateChannel.send(state) }
    }

    private fun authorize(token: String, username: String) {
        sendMessage("CAP REQ :twitch.tv/tags twitch.tv/commands twitch.tv/membership")

        var pass = token
        //Check if token is in the correct form
        if (!pass.startsWith("oauth:"))
            pass = "oauth:$token"

        sendMessage("PASS $pass")
        sendMessage("NICK $username")
    }

    private fun ignored(message: String): Boolean {
        val words = message.split(" ")
        return when {

            words[1].let { it == "353" || it == "366" } ->
                true // NAMES

            else -> false
        }
    }
}