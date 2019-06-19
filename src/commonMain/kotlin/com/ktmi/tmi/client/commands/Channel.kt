package com.ktmi.tmi.client.commands

import com.ktmi.tmi.client.builder.TwitchScope
import com.ktmi.tmi.messages.asChannelName

/**
 * Join channel
 */
suspend fun TwitchScope.join(channel: String) =
    sendRaw("JOIN ${channel.asChannelName}")

/**
 * Leave a channel
 */
suspend fun TwitchScope.leave(channel: String) =
    sendRaw("PART ${channel.asChannelName}")

/**
 * This command will send message to specified channel.
 */
suspend fun TwitchScope.sendMessage(channel: String, message: String) =
    sendRaw("PRIVMSG ${channel.asChannelName} :$message")

/**
 * This command will color your text based on your chat name color.
 */
suspend fun TwitchScope.action(channel: String, message: String) =
    sendMessage(channel, "/me $message")

/**
 * This command will allow you to permanently ban a user from the chat room
 */
suspend fun TwitchScope.ban(channel: String, userName: String) =
        sendMessage(channel, "/ban $userName")

/**
 * This command will allow you to lift a permanent ban on a user from the chat room.
 * You can also use this command to end a ban early; this also applies to timeouts
 */
suspend fun TwitchScope.unban(channel: String, userName: String) =
    sendMessage(channel, "/unban $userName")

/**
 * This command allows you to temporarily ban someone from the chat room for 10 minutes by default
 */
suspend fun TwitchScope.timeout(channel: String, userName: String, seconds: Int = 600) =
    sendMessage(channel,
        "/timeout $userName $seconds"
    )

/**
 * This command allows you to set a limit on how often users in the chat room are allowed to send messages (rate limiting)
 */
suspend fun TwitchScope.slowMode(channel: String, seconds: Int) =
    sendMessage(channel, "/slow $seconds")

/**
 * This command allows you to disable slow mode if you had previously set it.
 */
suspend fun TwitchScope.disableSlowMode(channel: String) =
    sendMessage(channel, "/slowoff")

/**
 * This command allows you or your mods to restrict chat to all or some of your followers,
 * based on how long they’ve followed — from 0 minutes (all followers) to 3 months.
 */
suspend fun TwitchScope.followOnly(channel: String, duration: String) =
    sendMessage(channel, "/followers $duration")

/**
 * This command allows you to disable followers only mode if you had previously set it.
 */
suspend fun TwitchScope.disableFollowOnly(channel: String) =
    sendMessage(channel, "/followersoff")

/**
 * This command allows you to set your room so only users subscribed to you can talk in the chat room.
 * If you don't have the subscription feature it will only allow the Broadcaster and the channel moderators
 * to talk in the chat room.
 */
suspend fun TwitchScope.subOnly(channel: String) =
    sendMessage(channel, "/subscribers")

/**
 * This command allows you to disable subscribers only chat room if you previously enabled it.
 */
suspend fun TwitchScope.disableSubOnly(channel: String) =
    sendMessage(channel, "/subscribersoff")

/**
 * This command will allow the Broadcaster and chat moderators to completely wipe the previous chat history.
 */
suspend fun TwitchScope.clearChat(channel: String) =
    sendMessage(channel, "/clear")

/**
 * This command disallows users from posting non-unique messages to the channel.
 * It will check for a minimum of 9 characters that are not symbol unicode characters and then purges
 * and repetitive chat lines beyond that. R9K is a unique way of moderating essentially allowing you to stop
 * generic copy-pasted messages intended as spam among over generally annoying content.
 */
suspend fun TwitchScope.r9KBeta(channel: String) =
    sendMessage(channel, "/r9kbeta")

/**
 * This command will disable R9K mode if it was previously enabled on the channel.
 */
suspend fun TwitchScope.disableR9KBeta(channel: String) =
    sendMessage(channel, "/r9kbetaoff")

/**
 * This command allows you to set your room so only messages that are 100% emotes are allowed.
 */
suspend fun TwitchScope.emoteOnly(channel: String) =
    sendMessage(channel, "/emoteonly")

/**
 * This command allows you to disable emote only mode if you previously enabled it.
 */
suspend fun TwitchScope.disableEmoteOnly(channel: String) =
    sendMessage(channel, "/emoteonlyoff")


/**
 * Allows you to change the color of your username. Normal users can choose between Blue, Coral, DodgerBlue,
 * SpringGreen, YellowGreen, Green, OrangeRed, Red, GoldenRod, HotPink, CadetBlue, SeaGreen, Chocolate,
 * BlueViolet, and Firebrick. Twitch Turbo users can use any Hex value (i.e: #000000)
 */
suspend fun TwitchScope.setColor(channel: String, color: String) =
    sendMessage(channel, "/color $color")

/**
 * This command will allow you to promote a user to a channel moderator
 */
suspend fun TwitchScope.mod(channel: String, username: String) =
    sendMessage(channel, "/mod $username")

/**
 * This command will allow you to demote an existing moderator back to viewer status
 */
suspend fun TwitchScope.unmod(channel: String, username: String) =
    sendMessage(channel, "/unmod $username")

/**
 * This command will grant VIP status to a user.
 */
suspend fun TwitchScope.vip(channel: String, username: String) =
    sendMessage(channel, "/vip $username")

/**
 * This command will revoke VIP status from a user
 */
suspend fun TwitchScope.unvip(channel: String, username: String) =
    sendMessage(channel, "/unvip $username")