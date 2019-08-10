package com.ktmi.tmi.client.events

import com.ktmi.irc.IrcState
import com.ktmi.tmi.dsl.builder.scopes.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/** Registers a listener for [IrcState] */
inline fun MainScope.onConnectionState(crossinline action: suspend (IrcState) -> Unit) {
    launch {
        connectionStatus.collect {
            action(it)
        }
    }
}

/** Registers a listener for when [IrcState] is [IrcState.CONNECTED] */
inline fun MainScope.onConnected(crossinline action: suspend () -> Unit) = onConnectionState {
    if (it == IrcState.CONNECTED)
        action()
}