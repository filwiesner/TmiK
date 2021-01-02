package com.ktmi.tmi.client

import com.ktmi.irc.IrcState
import com.ktmi.irc.IrcState.CONNECTED
import com.ktmi.irc.RawMessage
import com.ktmi.irc.TwitchIRC
import com.ktmi.irc.initIrcClient
import com.ktmi.tmi.dsl.builder.scopes.TmiStateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * @param token Token in format *"oauth:token"*. You can get this token from [twitchapps](https://twitchapps.com/tmi/)
 * @param username optional username passed in initialization of connection
 * @param secure true if connection to twitch should be secure (using WSS protocol instead of WS)
 * @param context [CoroutineContext] that should be used fro receiving messages from [TwitchIRC]
 * @param irc Implementation of [TwitchIRC] used for communication with Twitch
 */
class TmiClient(
    token: String,
    username: String = "blank",
    secure: Boolean = true,
    context: CoroutineContext = Dispatchers.Default,
    private val irc: TwitchIRC = initIrcClient(token, username, secure, context)
) : TmiStateProvider, CoroutineScope by CoroutineScope(context) {

    private var clientUsername: String? = null

    init {
        launch {
            val message = raw.filter {
                it.commandName == "001"
            }.first()
            clientUsername = message.channel
        }
    }

    override val username: String get() = clientUsername ?: "unknown"

    /** Connects to [TwitchIRC] */
    override fun connect() {
        irc.connect()
    }

    /** Disconnects from [TwitchIRC] */
    override fun disconnect() {
        irc.disconnect()
    }

    /**
     * Sends *raw* (not parsed) messages to [TwitchIRC]
     * @param message text that will be sent to [TwitchIRC]
     * @throws NotConnectedException when [TwitchIRC] is disconnected (its state is [IrcState.DISCONNECTED])
     */
    fun sendRaw(message: String) {
        if (irc.currentState == CONNECTED)
            irc.sendMessage(message)
        else throw NotConnectedException("Client is not connected to Twitch IRC.")
    }


    // == Elementary events ==
    /** [Flow] of [RawMessage]s received from [TwitchIRC]*/
    val raw: Flow<RawMessage>
        get() = irc.messages

    /** [Flow] of [IrcState] events received from [TwitchIRC] */
    override val connectionStatus: Flow<IrcState>
        get() = irc.states

}

/** Exception thrown when communication with [TwitchIRC] fails due to connection issues */
class NotConnectedException(msg: String? = null) : Exception(msg)