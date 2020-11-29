package com.ktmi.irc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Defines a IRC client that communicates with Twitch servers and forwards the communication via [ReceiveChannel]
 */
interface TwitchIRC {

    /**
     * Channel for incoming messages parsed as [RawMessage]
     */
    val messages: SharedFlow<RawMessage>

    /**
     * Channel that transmits connection status [IrcState]
     */
    val states: SharedFlow<IrcState>

    /**
     * Current connection status of IRC connection. Equals of last sent value by [states]
     */
    val currentState: IrcState

    /**
     * Connects to Twitch IRC and sends [IrcState.CONNECTING] followed by [IrcState.CONNECTED]
     */
    fun connect()

    /**
     * Disconnects from Twitch IRC and sends [IrcState.DISCONNECTED]
     */
    fun disconnect()

    /**
     * Sends message to Twitch IRC without any changes (unparsed)
     */
    fun sendMessage(message: String)
}

/**
 * Represents connection state of [TwitchIRC]
 */
enum class IrcState { CONNECTING, CONNECTED, DISCONNECTED }

abstract class IrcClient(
    private val token: String,
    private val username: String = "blank",
    private val secure: Boolean = true,
    context: CoroutineContext = Dispatchers.Default
) : TwitchIRC, CoroutineScope by CoroutineScope(context) {

    private val messageFlow = MutableSharedFlow<RawMessage>()
    override val messages: SharedFlow<RawMessage> get() = messageFlow

    private val stateFlow = MutableSharedFlow<IrcState>()
    override val states: SharedFlow<IrcState> get() = stateFlow

    protected var state: IrcState = IrcState.DISCONNECTED
    override val currentState: IrcState
        get() = state


    abstract fun connectSocket(url: String, token: String, username: String)
    abstract fun disconnectSocket()


    override fun connect() {
        disconnect()

        val port = if (secure) 443 else 80
        val protocol = if (secure) "wss" else "ws"

        connectSocket("$protocol://irc-ws.chat.twitch.tv:$port", token, username)
    }

    override fun disconnect() {
        disconnectSocket() // TODO Somehow close sharedFlows? Is it needed?
        // We could close scope
    }

    protected fun onConnectionOpened() {
        setState(IrcState.CONNECTING)
        authorize(token, username)
    }

    protected fun onClosed() {
        setState(IrcState.DISCONNECTED)
    }

    protected fun onMessageReceived(message: String) {
        if (message.startsWith("PING"))
            sendMessage("PONG :tmi.twitch.tv")
        else {
            if (message.startsWith(":tmi.twitch.tv 001"))
                setState(IrcState.CONNECTED)

            launch {
                for (line in message.trim().lines())
                    if (!line.isMessageIgnored)
                        messageFlow.emit(parseMessage(line))
            }
        }
    }


    // === Private methods ==

    private fun setState(state: IrcState) {
        this.state = state
        launch { stateFlow.emit(state) }
    }

    private val String.isMessageIgnored: Boolean
        get() {
            val words = split(" ")
            return when (words[1]) {
                "353", "366" -> true // NAMES
                else -> false
            }
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
}

/** Expected implementation of TwitchIRC interface for different platform targets */
expect fun initIrcClient(
    token: String,
    username: String = "blank",
    secure: Boolean = true,
    context: CoroutineContext = Dispatchers.Default
): TwitchIRC