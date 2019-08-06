package com.ktmi.tmi.dsl.builder.scopes

import com.ktmi.tmi.dsl.builder.TwitchDsl
import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.dsl.builder.UserStateContextScope
import com.ktmi.tmi.dsl.builder.scopes.filters.filterUserState
import com.ktmi.tmi.messages.channelAsUsername

/**
 * [TwitchDsl] builder function that creates [UserStateContextScope] with channel broadcaster as filter
 * @param block body of the DSL
 */
@TwitchDsl
inline fun TwitchScope.broadcaster(block: UserStateContextScope.() -> Unit) =
    filterUserState {
        withPredicate { it.channel.channelAsUsername == it.username}
        block()
    }

/**
 * [TwitchDsl] builder function that creates [UserStateContextScope] with moderators as a filter
 * @param block body of the DSL
 */
@TwitchDsl
inline fun TwitchScope.moderators(block: UserStateContextScope.() -> Unit) =
    filterUserState {
        withPredicate { it.isMod }
        block()
    }

/**
 * [TwitchDsl] builder function that creates [UserStateContextScope] with subscribers as a filter
 * @param block body of the DSL
 */
@TwitchDsl
inline fun TwitchScope.subscribers(block: UserStateContextScope.() -> Unit) =
    filterUserState {
        withPredicate { it.badges?.containsKey("subscriber") == true }
        block()
    }