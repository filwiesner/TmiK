package com.ktmi.tmi.client.builder.scopes.filters

import com.ktmi.tmi.client.builder.TwitchDsl
import com.ktmi.tmi.client.builder.TwitchScope
import com.ktmi.tmi.client.builder.UserContextScope
import com.ktmi.tmi.client.builder.UserStateContextScope
import com.ktmi.tmi.client.events.filterChannelUser
import com.ktmi.tmi.messages.TwitchMessage
import com.ktmi.tmi.messages.UserStateRelated
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

class FilterUserStateScope (
    parent: TwitchScope,
    coroutineContext: CoroutineContext
) : UserStateContextScope(parent,coroutineContext + CoroutineName("FilterUserTwitch")) {
    private var predicate: (suspend (UserStateRelated) -> Boolean) = { true }

    infix fun withPredicate(filter: suspend (UserStateRelated) -> Boolean) { predicate = filter }

    override suspend fun getTwitchFlow(): Flow<TwitchMessage> =
        super.getTwitchFlow()
            .filterChannelUser(predicate)
}

@TwitchDsl
inline fun TwitchScope.filterUserState(block: FilterUserStateScope.() -> Unit) =
    FilterUserStateScope(this, coroutineContext).apply(block)