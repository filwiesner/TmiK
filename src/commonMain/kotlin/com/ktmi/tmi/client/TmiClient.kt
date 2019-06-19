package com.ktmi.tmi.client

import com.ktmi.irc.IrcState
import com.ktmi.irc.IrcState.*
import com.ktmi.irc.RawMessage
import com.ktmi.irc.TwitchIRC
import com.ktmi.irc.TwitchIrcImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

class TmiClient (
    token: String,
    username: String = "blank",
    secure: Boolean = true,
    context: CoroutineContext = Dispatchers.Default,
    private val irc: TwitchIRC = TwitchIrcImpl(token, username, secure, context)
) : CoroutineScope by CoroutineScope(context) {

    private val messagesFlowDispenser = FlowDispenser(irc.messages, context)
    private val stateFlowDispenser = FlowDispenser(irc.states, context)

    // == Commands ==

    fun connect() {
        launch {
            irc.connect()
            messagesFlowDispenser.initialize()
            stateFlowDispenser.initialize()
        }
    }

    fun disconnect() {
        messagesFlowDispenser.stop()
        stateFlowDispenser.stop()
        irc.disconnect()
    }

    suspend fun sendRaw(message: String) {
        if (irc.currentState == CONNECTED)
            irc.sendMessage(message)
        else throw NotConnectedException("Client is not connected to Twitch IRC.")
    }


    // == Elementary events ==
    private var flowCounter = 0
    val raw get() = messagesFlowDispenser.requestFlow()

    val connectionStatus: Flow<IrcState>
        get() = stateFlowDispenser.requestFlow()

    // == Utility ==

    // TODO try inlining in stable release. Now causes compilation errors
    fun <T> Flow<T>.collectInTmiContext(
        context: CoroutineContext = coroutineContext,
        action: suspend (value: T) -> Unit
    ) { launch { collect(action) } }
}

class NotConnectedException(msg: String? = null) : Exception(msg)