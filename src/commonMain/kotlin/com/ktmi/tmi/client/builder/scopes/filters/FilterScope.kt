package com.ktmi.tmi.client.builder.scopes.filters

import com.ktmi.tmi.client.builder.TwitchDsl
import com.ktmi.tmi.client.builder.TwitchScope
import com.ktmi.tmi.messages.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlin.coroutines.CoroutineContext

class FilterTwitchScope(
    parent: TwitchScope,
    coroutineContext: CoroutineContext
) : TwitchScope(parent,coroutineContext + CoroutineName("UserTwitch")) {
    private var predicate: (suspend (TwitchMessage) -> Boolean) = { true }

    infix fun withPredicate(filter: suspend (TwitchMessage) -> Boolean) { predicate = filter }

    override suspend fun getTwitchFlow(): Flow<TwitchMessage> =
        super.getTwitchFlow()
            .filter(predicate)
}

@TwitchDsl
inline fun TwitchScope.filter(block: FilterTwitchScope.() -> Unit) =
    FilterTwitchScope(this, coroutineContext).apply(block)