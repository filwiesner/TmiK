package com.ktmi.irc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.coroutines.CoroutineContext

expect class IRC(
    token: String,
    username: String = "blank",
    secure: Boolean = true,
    context: CoroutineContext = Dispatchers.Default
) : TwitchIRC

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