package com.ktmi.tmi.dsl.plugins

import com.ktmi.irc.IrcState
import com.ktmi.tmi.messages.TwitchMessage

/** Pluggable object that transforms or filters incoming and/or outgoing messages */
interface TwitchPlugin {

    /** Unique name of the plugin. Used for identification */
    val name: String

    /** Filter incoming messages. Return false if message should be skipped */
    fun filterIncoming(message: TwitchMessage): Boolean
            = true

    /** Filter outgoing messages. Return false if message should be skipped */
    fun filterOutgoing(message: String): Boolean
            = true

    /** Map incoming messages */
    fun mapIncoming(message: TwitchMessage): TwitchMessage
            = message

    /** Map outgoing messages */
    fun mapOutgoing(message: String): String
            = message

    /** Triggered on [IrcState] event */
    fun onConnectionStateChange(newState: IrcState) { }
}