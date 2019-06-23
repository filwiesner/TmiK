package com.ktmi.irc

import com.ktmi.irc.IrcState.*
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.send
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext

/**
 * Basic implementation of [TwitchIRC] using [Ktor](https://ktor.io/) as Websocket client
 * @param token Token in format *"oauth:token"*. You can get this token from [twitchapps](https://twitchapps.com/tmi/)
 * @param username optional username passed in initialization of connection
 * @param secure true if connection to twitch should be secure (using WSS protocol instead of WS)
 * @param context [CoroutineContext] that should be used fro receiving messages from Twitch
 * @throws UnsupportedFrameException thrown when unsupported [Frame] type is received
 */
class TwitchIrcImpl(
    private val token: String,
    private val username: String = "blank",
    private val secure: Boolean = true,
    context: CoroutineContext = Dispatchers.Default
) : TwitchIRC, CoroutineScope by CoroutineScope(context) {

    private val client = HttpClient { install(WebSockets) }
    private var session: DefaultClientWebSocketSession? = null

    private val messageChannel: Channel<RawMessage> = Channel(Channel.UNLIMITED)
    override val messages: ReceiveChannel<RawMessage> get() = messageChannel

    private val stateChannel: Channel<IrcState> = Channel(Channel.UNLIMITED)
    override val states: ReceiveChannel<IrcState> get() = stateChannel

    private var state: IrcState = DISCONNECTED
    override val currentState: IrcState
        get() = state


    override suspend fun connect() {
        setState(CONNECTING)

        // Resolve protocol and port according to 'secure' parameter
        var port = 443
        var protocol = URLProtocol.WSS
        if (!secure) {
            port = 80
            protocol = URLProtocol.WS
        }

        try {
            session = client.webSocketSession(
                method = HttpMethod.Get,
                host = "irc-ws.chat.twitch.tv",
                port = port
            ) {
                url.protocol = protocol
                url.port = port
            }

            setupMessageListener()
            sendRequestTags()
            authorize(token, username)

            // Signalize connected state
            setState(CONNECTED)

        } catch (e: Throwable) {
            session?.close(e)
            setState(DISCONNECTED)
        }
    }

    private suspend fun sendRequestTags() =
        sendMessage("CAP REQ :twitch.tv/tags twitch.tv/commands twitch.tv/membership")

    private suspend fun authorize(token: String, username: String) {
        var pass = token

        //Check if token is in the correct form
        if (!pass.startsWith("oauth:"))
            pass = "oauth:$token"

        sendMessage("PASS $pass")
        sendMessage("NICK $username")
    }

    private fun setupMessageListener() {
        // Run parallel because this is not part of connection procedure
        launch {
            // This should never happen but NullPointerException does not exist, right?
            if (session == null) return@launch

            // Receive rest of the messages
            try {
                session!!.incoming.consumeEach { receivedMessage(it) }
            } finally {
                setState(DISCONNECTED)
                session?.close(null)
            }
        }
    }

    override fun disconnect() { session?.terminate() }

    private suspend fun receivedMessage(frame: Frame) = when(frame) {
        is Frame.Text -> {
            val msg = frame.readText()
            if (msg.startsWith("PING"))
                sendMessage("PONG :tmi.twitch.tv")

            else for (line in msg.trim().lines())
                if (!ignored(line))
                    messageChannel.send(parseMessage(line))

        }

        else -> throw UnsupportedFrameException()
    }

    override suspend fun sendMessage(message: String) {
        if (session?.isActive == true)
            session?.send(message)
    }

    private suspend fun setState(state: IrcState) {
        this.state = state
        stateChannel.send(state)
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

/** Thrown when [TwitchIRC] gets an Frame that is not supported */
class UnsupportedFrameException : Exception("Frame type not supported")