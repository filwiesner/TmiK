package com.ktmi.tmi.client.builder.scopes

import com.ktmi.tmi.client.builder.ChannelContextScope
import com.ktmi.tmi.client.builder.TwitchDsl
import com.ktmi.tmi.client.builder.TwitchScope
import com.ktmi.tmi.client.commands.*
import com.ktmi.tmi.messages.TwitchMessage
import com.ktmi.tmi.messages.asChannelName
import com.ktmi.tmi.messages.channelAsUsername
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlin.coroutines.CoroutineContext


/**
 * [TwitchDsl] scope used to filter out messages by username
 * @param channel name of the channel used for filtering [TwitchMessage]s
 * @param parent parent scope where messages are forwarded and from  where main [Flow] of [TwitchMessage]s is retrieved
 * @param coroutineContext [CoroutineContext] used for creating [TwitchMessage] listeners
 */
class ChannelScope(
    val channel: String,
    parent: TwitchScope,
    coroutineContext: CoroutineContext
) : ChannelContextScope(parent,coroutineContext + CoroutineName("ChannelTwitch")) {

    override suspend fun getTwitchFlow(): Flow<TwitchMessage> {
        return super.getTwitchFlow()
            .filter { it.rawMessage.channel == channel.asChannelName }
    }
}

/**
 * [TwitchDsl] builder function for [ChannelScope]
 * @param channel name used for filtering messages
 * @param block body of the DSL ([ChannelScope])
 */
@TwitchDsl
inline fun TwitchScope.channel(channel: String, block: ChannelScope.() -> Unit) =
    ChannelScope(channel, this, coroutineContext).apply(block)

/**
 * [TwitchDsl] builder function for [ChannelScope] what creates [UserScope] with channel broadcaster as filter
 * @param block body of the DSL ([ChannelScope])
 */
@TwitchDsl
inline fun ChannelScope.broadcaster(block: UserScope.() -> Unit) =
    UserScope(channel.channelAsUsername, this, coroutineContext).apply(block)




/**
 * This command will send message to specified channel.
 */
suspend fun ChannelScope.sendMessage(message: String) = sendMessage(channel.asChannelName, message)

/**
 * This command will color your text based on your chat name color.
 */
suspend fun ChannelScope.action(message: String) = action(channel, message)

/**
 * This command will allow you to permanently ban a user from the chat room
 */
suspend fun ChannelScope.ban(user: String) = ban(channel.asChannelName, user)

/**
 * This command will allow you to lift a permanent ban on a user from the chat room.
 * You can also use this command to end a ban early; this also applies to timeouts
 */
suspend fun ChannelScope.unban(user: String) = unban(channel, user)

/**
 * This command allows you to temporarily ban someone from the chat room for 10 minutes by default
 */
suspend fun ChannelScope.timeout(user: String, seconds: Int = 600) = timeout(channel, user, seconds)

/**
 * This command allows you to set a limit on how often users in the chat room are allowed to send messages (rate limiting)
 */
suspend fun ChannelScope.slowMode(seconds: Int) = slowMode(channel, seconds)

/**
 * This command allows you to disable slow mode if you had previously set it.
 */
suspend fun ChannelScope.disableSlowMode() = disableSlowMode(channel)

/**
 * This command allows you or your mods to restrict chat to all or some of your followers,
 * based on how long they’ve followed — from 0 minutes (all followers) to 3 months.
 */
suspend fun ChannelScope.followOnly(duration: String) = followOnly(channel, duration)

/**
 * This command allows you to disable followers only mode if you had previously set it.
 */
suspend fun ChannelScope.disableFollowOnly() = disableFollowOnly(channel)

/**
 * This command allows you to set your room so only users subscribed to you can talk in the chat room.
 * If you don't have the subscription feature it will only allow the Broadcaster and the channel moderators
 * to talk in the chat room.
 */
suspend fun ChannelScope.subOnly() = subOnly(channel)

/**
 * This command allows you to disable subscribers only chat room if you previously enabled it.
 */
suspend fun ChannelScope.disableSubOnly() = disableSubOnly(channel)

/**
 * This command will allow the Broadcaster and chat moderators to completely wipe the previous chat history.
 */
suspend fun ChannelScope.clearChat() = clearChat(channel)

/**
 * This command disallows users from posting non-unique messages to the channel.
 * It will check for a minimum of 9 characters that are not symbol unicode characters and then purges
 * and repetitive chat lines beyond that. R9K is a unique way of moderating essentially allowing you to stop
 * generic copy-pasted messages intended as spam among over generally annoying content.
 */
suspend fun ChannelScope.r9KBeta() = r9KBeta(channel)

/**
 * This command will disable R9K mode if it was previously enabled on the channel.
 */
suspend fun ChannelScope.disableR9KBeta() = disableR9KBeta(channel)

/**
 * This command allows you to set your room so only messages that are 100% emotes are allowed.
 */
suspend fun ChannelScope.emoteOnly() = emoteOnly(channel)

/**
 * This command allows you to disable emote only mode if you previously enabled it.
 */
suspend fun ChannelScope.disableEmoteOnly() = disableEmoteOnly(channel)

/**
 * Allows you to change the color of your username. Normal users can choose between Blue, Coral, DodgerBlue,
 * SpringGreen, YellowGreen, Green, OrangeRed, Red, GoldenRod, HotPink, CadetBlue, SeaGreen, Chocolate,
 * BlueViolet, and Firebrick. Twitch Turbo users can use any Hex value (i.e: #000000)
 */
suspend fun ChannelScope.setColor(color: String) = setColor(channel, color)

/**
 * This command will allow you to promote a user to a channel moderator
 */
suspend fun ChannelScope.mod(user: String) = mod(channel, user)

/**
 * This command will allow you to demote an existing moderator back to viewer status
 */
suspend fun ChannelScope.unmod(user: String) = unmod(channel, user)

/**
 * This command will grant VIP status to a user.
 */
suspend fun ChannelScope.vip(user: String) = vip(channel, user)

/**
 * This command will revoke VIP status from a user
 */
suspend fun ChannelScope.unvip(user: String) = unvip(channel, user)