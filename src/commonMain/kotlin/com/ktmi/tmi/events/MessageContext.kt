package com.ktmi.tmi.events

import com.ktmi.tmi.commands.*
import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.messages.TwitchMessage

/**
 * Context of received message that exposes helper functions for channel it's received from
 */
open class ChannelContext<T: TwitchMessage>(
    val message: T,
    val channel: String
) {
    val text get() = message.rawMessage.text ?: ""

    /** Sends message to channel in this context */
    fun TwitchScope.sendMessage(text: String) = sendMessage(channel, text)

    /** Sends message to channel in this context and color your text based on your chat name color. */
    fun TwitchScope.action(text: String) = action(channel, text)

    /** Sends a whisper message to user */
    fun TwitchScope.whisper(username: String, message: String) = whisper(channel, username, message)

    /** This command will allow you to permanently ban a user from chat room in this context */
    fun TwitchScope.ban(user: String) = ban(channel, user)

    /**
     * This command will allow you to lift a permanent ban on a user from chat room in current context.
     * You can also use this command to end a ban early; this also applies to timeouts
     */
    fun TwitchScope.unban(user: String) = unban(channel, user)

    /** This command allows you to temporarily ban user in current context from the chat room for 10 minutes by default*/
    fun TwitchScope.timeout(user: String, seconds: Int = 600) = timeout(channel, user, seconds)

    /** This command allows you to set a limit on how often users in the chat room in current context are allowed to send messages (rate limiting)*/
    fun TwitchScope.slowMode(seconds: Int) = slowMode(channel, seconds)

    /** This command allows you to disable slow mode in the chat room in current context if you had previously set it. */
    fun TwitchScope.disableSlowMode() = disableSlowMode(channel)

    /**
     * This command allows you or your mods to restrict chat to all or some of your followers,
     * based on how long they’ve followed — from 0 minutes (all followers) to 3 months.
     * @param duration Specifies how long should the user follow given channel in order to chat.
     * For example *"30m"* for 30 minutes or *"2d"* for two days. For more information,
     * refer to [documentation](https://help.twitch.tv/s/article/chat-commands?language=en_US#AllMods)
     */
    fun TwitchScope.followOnly(duration: String) = followOnly(channel, duration)

    /** This command allows you to disable followers only mode if you had previously set it. */
    fun TwitchScope.disableFollowOnly() = disableFollowOnly(channel)

    /**
     * This command allows you to set your room so only users subscribed to you can talk in the chat room.
     * If you don't have the subscription feature it will only allow the Broadcaster and the channel moderators
     * to talk in the chat room.
     */
    fun TwitchScope.subOnly() = subOnly(channel)

    /** This command allows you to disable subscribers only chat room if you previously enabled it.*/
    fun TwitchScope.disableSubOnly() = disableSubOnly(channel)

    /** This command will allow the Broadcaster and chat moderators to completely wipe the previous chat history.*/
    fun TwitchScope.clearChat() = clearChat(channel)

    /**
     * This command disallows users from posting non-unique messages to the channel.
     * It will check for a minimum of 9 characters that are not symbol unicode characters and then purges
     * and repetitive chat lines beyond that. R9K is a unique way of moderating essentially allowing you to stop
     * generic copy-pasted messages intended as spam among over generally annoying content.
     */
    fun TwitchScope.r9KBeta() = r9KBeta(channel)

    /** This command will disable R9K mode if it was previously enabled on the channel.*/
    fun TwitchScope.disableR9KBeta() = disableR9KBeta(channel)

    /** This command allows you to set your room so only messages that are 100% emotes are allowed.*/
    fun TwitchScope.emoteOnly() = emoteOnly(channel)

    /** This command allows you to disable emote only mode if you previously enabled it.*/
    fun TwitchScope.disableEmoteOnly() = disableEmoteOnly(channel)

    /**
     * Allows you to change the color of your username. Normal users can choose between Blue, Coral, DodgerBlue,
     * SpringGreen, YellowGreen, Green, OrangeRed, Red, GoldenRod, HotPink, CadetBlue, SeaGreen, Chocolate,
     * BlueViolet, and Firebrick. Twitch Turbo users can use any Hex value (i.e: #000000)
     */
    fun TwitchScope.setColor(color: String) = setColor(channel, color)

    /** This command will allow you to promote a user to a channel moderator*/
    fun TwitchScope.mod(user: String) = mod(channel, user)

    /** This command will allow you to demote an existing moderator back to viewer status */
    fun TwitchScope.unmod(user: String) = unmod(channel, user)

    /** This command will grant VIP status to a user. */
    fun TwitchScope.vip(user: String) = vip(channel, user)

    /** This command will revoke VIP status from a user */
    fun TwitchScope.unvip(user: String) = unvip(channel, user)
}

/**
 * Context of received message that exposes helper functions for channel and user it's received from
 */
open class UserContext<T: TwitchMessage>(
    message: T,
    val username: String?,
    channel: String
) : ChannelContext<T>(message, channel) {

    /** Sends a whisper message to user */
    fun TwitchScope.whisper(message: String) {
        if (username != null)
            whisper(username, message)
    }

    /** This command will allow you to permanently ban a user from chat room in this context */
    fun TwitchScope.ban() {
        if (username != null)
            ban(channel, username)
    }

    /**
     * This command will allow you to lift a permanent ban on a user from chat room in current context.
     * You can also use this command to end a ban early; this also applies to timeouts
     */
    fun TwitchScope.unban() {
        if (username != null)
            unban(username)
    }

    /** This command allows you to temporarily ban user in current context from the chat room for 10 minutes by default*/
    fun TwitchScope.timeout(seconds: Int = 600) {
        if (username != null)
            timeout(username, seconds)
    }

    /** This command will allow you to promote a user to a channel moderator*/
    fun TwitchScope.mod() {
        if (username != null)
            mod(username)
    }

    /** This command will allow you to demote an existing moderator back to viewer status */
    fun TwitchScope.unmod() {
        if (username != null) unmod(username)
    }

    /** This command will grant VIP status to a user. */
    fun TwitchScope.vip() {
        if (username != null)
            vip(username)
    }

    /** This command will revoke VIP status from a user */
    fun TwitchScope.unvip() {
        if (username != null)
            unvip(username)
    }
}