package com.ktmi.tmi.client.builder.scopes.filters

import com.ktmi.tmi.client.builder.TwitchDsl
import com.ktmi.tmi.client.builder.TwitchScope
import com.ktmi.tmi.client.builder.UserStateContextScope
import com.ktmi.tmi.client.events.filterChannelUser
import com.ktmi.tmi.messages.TwitchMessage
import com.ktmi.tmi.messages.UserStateRelated
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

/**
 * [TwitchDsl] scope used to filter out [TwitchMessage]s implementing [UserStateRelated] interface
 * using given predicate (set with [withPredicate])
 * @param parent parent scope where messages are forwarded and from  where main [Flow] of [TwitchMessage]s is retrieved
 * @param coroutineContext [CoroutineContext] used for creating [TwitchMessage] listeners
 */
class FilterUserStateScope (
    parent: TwitchScope,
    coroutineContext: CoroutineContext
) : UserStateContextScope(parent,coroutineContext + CoroutineName("FilterUserTwitch")) {
    private var predicate: (suspend (UserStateRelated) -> Boolean) = { true }

    /**
     * Sets predicate of [FilterUserStateScope] used in filtering incoming [TwitchMessage]s.
     * Makes sure every message implements [UserStateRelated] interface
     */
    infix fun withPredicate(filter: suspend (UserStateRelated) -> Boolean) { predicate = filter }

    override suspend fun getTwitchFlow(): Flow<TwitchMessage> =
        super.getTwitchFlow()
            .filterChannelUser(predicate)
}

/**
 * [TwitchDsl] builder function for [FilterUserStateScope]
 * @param block body of the DSL ([FilterUserStateScope])
 */
@TwitchDsl
inline fun TwitchScope.filterUserState(block: FilterUserStateScope.() -> Unit) =
    FilterUserStateScope(this, coroutineContext).apply(block)