package com.ktmi.tmi.client.events

import com.ktmi.irc.IrcState
import com.ktmi.tmi.client.builder.MainScope
import com.ktmi.tmi.client.builder.TwitchScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

inline fun MainScope.onConnectionState(crossinline action: suspend (IrcState) -> Unit) {
    launch {
        getIrcStateFlow().collect {
            action(it)
        }
    }
}

inline fun MainScope.onConnected(crossinline action: suspend () -> Unit) = onConnectionState {
    if (it == IrcState.CONNECTED)
        action()
}