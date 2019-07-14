package com.ktmi.tmi.client.commands

import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.messages.asChannelName

/**
 * Join channel
 * @param channel Channel where this command should be executed
 */
fun TwitchScope.join(channel: String) =
    sendRaw("JOIN ${channel.asChannelName}")

/**
 * Leave a channel
 * @param channel Channel where this command should be executed
 */
fun TwitchScope.leave(channel: String) =
    sendRaw("PART ${channel.asChannelName}")

/**
 * This command will send message to specified channel.
 * @param channel Channel where this command should be executed
 * @param message Message that should be sent to chat of specified channel
 */
fun TwitchScope.sendMessage(channel: String, message: String) =
    sendRaw("PRIVMSG ${channel.asChannelName} :$message")