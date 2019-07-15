package com.ktmi.tmi.client.commands

import com.ktmi.tmi.dsl.builder.TwitchScope

/**
 * This command will color your text based on your chat name color.
 * @param channel Channel where this command should be executed
 * @param message Message that should be sent to chat of specified channel
 */
fun TwitchScope.action(channel: String, message: String) =
    sendMessage(channel, "/me $message")

/**
 * This command will allow you to permanently ban a user from the chat room.
 * @param channel Channel where this command should be executed
 * @param userName username of user tha should be banned
 */
fun TwitchScope.ban(channel: String, userName: String) =
        sendMessage(channel, "/ban $userName")

/**
 * This command will allow you to lift a permanent ban on a user from the chat room.
 * You can also use this command to end a ban early; this also applies to timeouts
 * @param channel Channel where this command should be executed
 * @param userName username of user tha should be unbanned
 */
fun TwitchScope.unban(channel: String, userName: String) =
    sendMessage(channel, "/unban $userName")

/**
 * This command allows you to temporarily ban someone from the chat room for 10 minutes by default
 * @param channel Channel where this command should be executed
 * @param userName username of user tha should be timed out
 * @param seconds number of seconds for how long should the timeout be. Default is 10 minutes
 */
fun TwitchScope.timeout(channel: String, userName: String, seconds: Int = 600) =
    sendMessage(channel,
        "/timeout $userName $seconds"
    )

/**
 * This command allows you to set a limit on how often users in the chat room are allowed to send messages (rate limiting)
 * @param channel Channel where this command should be executed
 * @param seconds number of seconds for how often can users send messages
 */
fun TwitchScope.slowMode(channel: String, seconds: Int) =
    sendMessage(channel, "/slow $seconds")

/**
 * This command allows you to disable slow mode if you had previously set it.
 * @param channel Channel where this command should be executed
 */
fun TwitchScope.disableSlowMode(channel: String) =
    sendMessage(channel, "/slowoff")

/**
 * This command allows you or your mods to restrict chat to all or some of your followers,
 * based on how long they’ve followed — from 0 minutes (all followers) to 3 months.
 * @param channel Channel where this command should be executed
 * @param duration Specifies how long should the user follow given channel in order to chat.
 * For example *"30m"* for 30 minutes or *"2d"* for two days. For more information,
 * refer to [documentation](https://help.twitch.tv/s/article/chat-commands?language=en_US#AllMods)
 */
fun TwitchScope.followOnly(channel: String, duration: String) =
    sendMessage(channel, "/followers $duration")

/**
 * This command allows you to disable followers only mode if you had previously set it.
 * @param channel Channel where this command should be executed
 */
fun TwitchScope.disableFollowOnly(channel: String) =
    sendMessage(channel, "/followersoff")

/**
 * This command allows you to set your room so only users subscribed to you can talk in the chat room.
 * If you don't have the subscription feature it will only allow the Broadcaster and the channel moderators
 * to talk in the chat room.
 * @param channel Channel where this command should be executed
 */
fun TwitchScope.subOnly(channel: String) =
    sendMessage(channel, "/subscribers")

/**
 * This command allows you to disable subscribers only chat room if you previously enabled it.
 * @param channel Channel where this command should be executed
 */
fun TwitchScope.disableSubOnly(channel: String) =
    sendMessage(channel, "/subscribersoff")

/**
 * This command will allow the Broadcaster and chat moderators to completely wipe the previous chat history.
 * @param channel Channel where this command should be executed
 */
fun TwitchScope.clearChat(channel: String) =
    sendMessage(channel, "/clear")

/**
 * This command disallows users from posting non-unique messages to the channel.
 * It will check for a minimum of 9 characters that are not symbol unicode characters and then purges
 * and repetitive chat lines beyond that. R9K is a unique way of moderating essentially allowing you to stop
 * generic copy-pasted messages intended as spam among over generally annoying content.
 * @param channel Channel where this command should be executed
 */
fun TwitchScope.r9KBeta(channel: String) =
    sendMessage(channel, "/r9kbeta")

/**
 * This command will disable R9K mode if it was previously enabled on the channel.
 * @param channel Channel where this command should be executed
 */
fun TwitchScope.disableR9KBeta(channel: String) =
    sendMessage(channel, "/r9kbetaoff")

/**
 * This command allows you to set your room so only messages that are 100% emotes are allowed.
 * @param channel Channel where this command should be executed
 */
fun TwitchScope.emoteOnly(channel: String) =
    sendMessage(channel, "/emoteonly")

/**
 * This command allows you to disable emote only mode if you previously enabled it.
 * @param channel Channel where this command should be executed
 */
fun TwitchScope.disableEmoteOnly(channel: String) =
    sendMessage(channel, "/emoteonlyoff")


/**
 * Allows you to change the color of your username. Normal users can choose between Blue, Coral, DodgerBlue,
 * SpringGreen, YellowGreen, Green, OrangeRed, Red, GoldenRod, HotPink, CadetBlue, SeaGreen, Chocolate,
 * BlueViolet, and Firebrick. Twitch Turbo users can use any Hex value (i.e: #000000)
 * @param channel Channel where this command should be executed
 * @param color name or hex value of the color
 */
fun TwitchScope.setColor(channel: String, color: String) =
    sendMessage(channel, "/color $color")

/**
 * This command will allow you to promote a user to a channel moderator
 * @param channel Channel where this command should be executed
 * @param username name of the user that should be modded
 */
fun TwitchScope.mod(channel: String, username: String) =
    sendMessage(channel, "/mod $username")

/**
 * This command will allow you to demote an existing moderator back to viewer status
 * @param channel Channel where this command should be executed
 * @param username name of the user that should be unmodded
 */
fun TwitchScope.unmod(channel: String, username: String) =
    sendMessage(channel, "/unmod $username")

/**
 * This command will grant VIP status to a user.
 * @param channel Channel where this command should be executed
 * @param username name of the user that should receive VIP status
 */
fun TwitchScope.vip(channel: String, username: String) =
    sendMessage(channel, "/vip $username")

/**
 * This command will revoke VIP status from a user
 * @param channel Channel where this command should be executed
 * @param username name of the user that should lose VIP status
 */
fun TwitchScope.unvip(channel: String, username: String) =
    sendMessage(channel, "/unvip $username")