package com.ktmi.tmi.client

import com.ktmi.irc.IrcState
import com.ktmi.irc.IrcState.*
import com.ktmi.irc.RawMessage
import com.ktmi.irc.TwitchIRC
import com.ktmi.irc.TwitchIrcImpl
import com.ktmi.tmi.dsl.builder.TmiStateProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

/**
 * @param token Token in format *"oauth:token"*. You can get this token from [twitchapps](https://twitchapps.com/tmi/)
 * @param username optional username passed in initialization of connection
 * @param secure true if connection to twitch should be secure (using WSS protocol instead of WS)
 * @param context [CoroutineContext] that should be used fro receiving messages from [TwitchIRC]
 * @param irc Implementation of [TwitchIRC] used for communication with Twitch
 */
class TmiClient (
    token: String,
    username: String = "blank",
    secure: Boolean = true,
    context: CoroutineContext = Dispatchers.Default,
    private val irc: TwitchIRC = TwitchIrcImpl(token, username, secure, context)
) : TmiStateProvider, CoroutineScope by CoroutineScope(context) {

    private val messagesFlowDispenser = FlowDispenser(irc.messages, context)
    private val stateFlowDispenser = FlowDispenser(irc.states, context)

    private var clientUsername: String? = null

    init { launch {
        try { raw.collect {
            if (it.commandName == "001") {
                clientUsername = it.channel
                cancel()
            }
        } } catch (e: CancellationException) { }
    } }

    override val username: String get() = clientUsername ?: "unknown"

    /** Connects to [TwitchIRC] */
    override fun connect() {
        launch {
            irc.connect()
            messagesFlowDispenser.initialize()
            stateFlowDispenser.initialize()
        }
    }

    /** Disconnects from [TwitchIRC] */
    override fun disconnect() {
        messagesFlowDispenser.stop()
        stateFlowDispenser.stop()
        irc.disconnect()
    }

    /**
     * Sends *raw* (not parsed) messages to [TwitchIRC]
     * @param message text that will be sent to [TwitchIRC]
     * @throws NotConnectedException when [TwitchIRC] is disconnected (its state is [IrcState.DISCONNECTED])
     */
    suspend fun sendRaw(message: String) {
        if (irc.currentState == CONNECTED)
            irc.sendMessage(message)
        else throw NotConnectedException("Client is not connected to Twitch IRC.")
    }


    // == Elementary events ==
    /** [Flow] of [RawMessage]s received from [TwitchIRC]*/
    val raw get() = messagesFlowDispenser.requestFlow()

    /** [Flow] of [IrcState] events received from [TwitchIRC] */
    override val connectionStatus: Flow<IrcState>
        get() = stateFlowDispenser.requestFlow()

}

/** Exception thrown when communication with [TwitchIRC] fails due to connection issues */
class NotConnectedException(msg: String? = null) : Exception(msg)