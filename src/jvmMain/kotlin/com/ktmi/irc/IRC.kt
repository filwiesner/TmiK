package com.ktmi.irc

import com.ktmi.irc.IrcState.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import okhttp3.*
import kotlin.coroutines.CoroutineContext

/** Actual implementation of TwitchIRC interface for JVM */
actual class IRC actual constructor(
    private val token: String,
    private val username: String,
    private val secure: Boolean,
    context: CoroutineContext
) : TwitchIRC, WebSocketListener(), CoroutineScope by CoroutineScope(context) {

    private val client = OkHttpClient()
    private var ws: WebSocket? = null

    private val messageChannel: Channel<RawMessage> = Channel(Channel.UNLIMITED)
    override val messages: ReceiveChannel<RawMessage> get() = messageChannel

    private val stateChannel: Channel<IrcState> = Channel(Channel.UNLIMITED)
    override val states: ReceiveChannel<IrcState> get() = stateChannel

    private var state: IrcState = DISCONNECTED
    override val currentState: IrcState
        get() = state

    // === API ===

    override fun connect() {
        disconnect()

        val port = if (secure) 443 else 80
        val protocol = if (secure) "wss" else "ws"

        val request = Request.Builder().apply {
            url("$protocol://irc-ws.chat.twitch.tv:$port")
        }.build()

        ws = client.newWebSocket(request, this)
    }

    override fun disconnect() {
        ws?.close(1000, null)
    }

    override fun sendMessage(message: String) {
        ws?.send(message)
    }


    // === Listeners ===

    override fun onOpen(webSocket: WebSocket, response: Response) {
        setState(CONNECTING)
        authorize(token, username)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        setState(DISCONNECTED)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        // When network signal is lost two errors are thrown. Ignore one of them so only one DISCONNECT event is dispatched
        if (t.localizedMessage != "Network is unreachable: connect")
            setState(DISCONNECTED)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
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

    // === Private methods ==

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