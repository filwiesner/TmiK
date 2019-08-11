package com.ktmi.tmi.dsl.builder.scopes

import com.ktmi.tmi.dsl.builder.ChannelContextScope
import com.ktmi.tmi.dsl.builder.TwitchDsl
import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.messages.TwitchMessage
import com.ktmi.tmi.messages.asChannelName
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

/**
 * [TwitchDsl] scope used to filter out messages by username
 * @param channel name of the channel used for filtering [TwitchMessage]s
 * @param parent parent scope where messages are forwarded and from  where main [Flow] of [TwitchMessage]s is retrieved
 * @param coroutineContext [CoroutineContext] used for creating [TwitchMessage] listeners
 */
class ChannelScope(
    channel: String,
    parent: TwitchScope,
    coroutineContext: CoroutineContext
) : ChannelContextScope(channel.asChannelName, parent,coroutineContext + CoroutineName("ChannelTwitch"))

/**
 * [TwitchDsl] builder function for [ChannelScope]
 * @param channel name used for filtering messages
 * @param block body of the DSL ([ChannelScope])
 */
@TwitchDsl
inline fun TwitchScope.channel(channel: String, block: ChannelScope.() -> Unit) =
    ChannelScope(channel, this, coroutineContext).apply(block)