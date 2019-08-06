@file:Suppress("FunctionName")

package com.ktmi.tmi.dsl.builder.scopes

import com.ktmi.tmi.dsl.builder.*
import com.ktmi.tmi.dsl.builder.scopes.filters.filterUserState
import com.ktmi.tmi.messages.channelAsUsername

@PublishedApi
internal inline fun TwitchScope._broadcaster(block: UserStateContextScope.() -> Unit) = filterUserState {
    withPredicate { it.channel.channelAsUsername == it.username}
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
internal inline fun TwitchScope._moderators(block: UserStateContextScope.() -> Unit) = filterUserState {
    withPredicate { it.isMod }
    block()
}
/** [TwitchDsl] builder function that creates [UserStateContextScope] with moderators as a filter */
@TwitchDsl inline fun GlobalContextScope.moderators(block: UserStateContextScope.() -> Unit) = _moderators(block)
/** [TwitchDsl] builder function that creates [UserStateContextScope] with moderators as a filter */
@TwitchDsl inline fun ChannelContextScope.moderators(block: UserStateContextScope.() -> Unit) = _moderators(block)
/** [TwitchDsl] builder function that creates [UserStateContextScope] with moderators as a filter */
@TwitchDsl inline fun UserContextScope.moderators(block: UserStateContextScope.() -> Unit) = _moderators(block)
/** [TwitchDsl] builder function that creates [UserStateContextScope] with moderators as a filter */
@TwitchDsl inline fun UserStateContextScope.moderators(block: UserStateContextScope.() -> Unit) = _moderators(block)

@PublishedApi
internal inline fun TwitchScope._subscribers(block: UserStateContextScope.() -> Unit) = filterUserState {
    withPredicate { it.badges?.containsKey("subscriber") == true }
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