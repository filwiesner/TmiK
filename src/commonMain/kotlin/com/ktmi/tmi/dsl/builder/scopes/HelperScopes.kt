@file:Suppress("FunctionName")

package com.ktmi.tmi.dsl.builder.scopes

import com.ktmi.tmi.dsl.builder.*
import com.ktmi.tmi.dsl.builder.scopes.filters.filterUserState
import com.ktmi.tmi.messages.channelAsUsername
import com.ktmi.tmi.messages.isBroadcaster
import com.ktmi.tmi.messages.isMod
import com.ktmi.tmi.messages.isSubscriber

@PublishedApi
internal inline fun TwitchScope._broadcaster(block: UserStateContextScope.() -> Unit) = filterUserState {
    withPredicate { it.isBroadcaster }
    block()
}
/** [TwitchDsl] builder function that creates [UserStateContextScope] with channel broadcaster as filter */
@TwitchDsl inline fun GlobalContextScope.broadcaster(block: UserStateContextScope.() -> Unit) = _broadcaster(block)
/** [TwitchDsl] builder function that creates [UserStateContextScope] with channel broadcaster as filter */
@TwitchDsl inline fun ChannelContextScope.broadcaster(block: UserStateContextScope.() -> Unit) = _broadcaster(block)
/** [TwitchDsl] builder function that creates [UserStateContextScope] with channel broadcaster as filter */
@TwitchDsl inline fun UserContextScope.broadcaster(block: UserStateContextScope.() -> Unit) = _broadcaster(block)
/** [TwitchDsl] builder function that creates [UserStateContextScope] with channel broadcaster as filter */
@TwitchDsl inline fun UserStateContextScope.broadcaster(block: UserStateContextScope.() -> Unit) = _broadcaster(block)

@PublishedApi
internal inline fun TwitchScope._moderators(
    includingBroadcaster: Boolean = true,
    block: UserStateContextScope.() -> Unit
) = filterUserState {
    withPredicate { it.isMod || (includingBroadcaster && it.isBroadcaster) }
    block()
}
/** [TwitchDsl] builder function that creates [UserStateContextScope] with moderators as a filter */
@TwitchDsl inline fun GlobalContextScope.moderators(includingBroadcaster: Boolean = true, block: UserStateContextScope.() -> Unit)
        = _moderators(includingBroadcaster, block)
/** [TwitchDsl] builder function that creates [UserStateContextScope] with moderators as a filter */
@TwitchDsl inline fun ChannelContextScope.moderators(includingBroadcaster: Boolean = true, block: UserStateContextScope.() -> Unit)
        = _moderators(includingBroadcaster, block)
/** [TwitchDsl] builder function that creates [UserStateContextScope] with moderators as a filter */
@TwitchDsl inline fun UserContextScope.moderators(includingBroadcaster: Boolean = true, block: UserStateContextScope.() -> Unit)
        = _moderators(includingBroadcaster, block)
/** [TwitchDsl] builder function that creates [UserStateContextScope] with moderators as a filter */
@TwitchDsl inline fun UserStateContextScope.moderators(includingBroadcaster: Boolean = true, block: UserStateContextScope.() -> Unit)
        = _moderators(includingBroadcaster, block)

@PublishedApi
internal inline fun TwitchScope._subscribers(block: UserStateContextScope.() -> Unit) = filterUserState {
    withPredicate { it.isSubscriber }
    block()
}
/** [TwitchDsl] builder function that creates [UserStateContextScope] with subscribers as a filter */
@TwitchDsl inline fun GlobalContextScope.subscribers(block: UserStateContextScope.() -> Unit) = _subscribers(block)
/** [TwitchDsl] builder function that creates [UserStateContextScope] with subscribers as a filter */
@TwitchDsl inline fun ChannelContextScope.subscribers(block: UserStateContextScope.() -> Unit) = _subscribers(block)
/** [TwitchDsl] builder function that creates [UserStateContextScope] with subscribers as a filter */
@TwitchDsl inline fun UserContextScope.subscribers(block: UserStateContextScope.() -> Unit) = _subscribers(block)
/** [TwitchDsl] builder function that creates [UserStateContextScope] with subscribers as a filter */
@TwitchDsl inline fun UserStateContextScope.subscribers(block: UserStateContextScope.() -> Unit) = _subscribers(block)