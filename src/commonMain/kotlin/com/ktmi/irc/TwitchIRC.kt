package com.ktmi.irc

import kotlinx.coroutines.channels.ReceiveChannel

interface TwitchIRC {
    val messages: ReceiveChannel<RawMessage>
    val states: ReceiveChannel<IrcState>
    val currentState: IrcState

    suspend fun connect()
    fun disconnect()

    suspend fun sendMessage(message: String)
}

enum class IrcState { CONNECTING, CONNECTED, DISCONNECTED }