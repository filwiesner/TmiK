package com.ktmi.irc

import kotlinx.coroutines.channels.ReceiveChannel

/**
 * Defines a IRC client that communicates with Twitch servers and forwards the communication via [ReceiveChannel]
 */
interface TwitchIRC {

    /**
     * Channel for incoming messages parsed as [RawMessage]
     */
    val messages: ReceiveChannel<RawMessage>

    /**
     * Channel that transmits connection status [IrcState]
     */
    val states: ReceiveChannel<IrcState>

    /**
     * Current connection status of IRC connection. Equals of last sent value by [states]
     */
    val currentState: IrcState

    /**
     * Connects to Twitch IRC and sends [IrcState.CONNECTING] followed by [IrcState.CONNECTED]
     */
    suspend fun connect()

    /**
     * Disconnects from Twitch IRC and sends [IrcState.DISCONNECTED]
     */
    fun disconnect()

    /**
     * Sends message to Twitch IRC without any changes (unparsed)
     */
    suspend fun sendMessage(message: String)
}

/**
 * Represents connection state of [TwitchIRC]
 */
enum class IrcState { CONNECTING, CONNECTED, DISCONNECTED }