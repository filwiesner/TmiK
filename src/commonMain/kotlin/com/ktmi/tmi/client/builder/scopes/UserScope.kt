package com.ktmi.tmi.client.builder.scopes

import com.ktmi.tmi.client.builder.TwitchDsl
import com.ktmi.tmi.client.builder.TwitchScope
import com.ktmi.tmi.messages.TwitchMessage
import com.ktmi.tmi.messages.author
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlin.coroutines.CoroutineContext

class UserTwitchScope(
    val user: String,
    parent: TwitchScope,
    coroutineContext: CoroutineContext
) : TwitchScope(parent,coroutineContext + CoroutineName("UserTwitch")) {
    private val lowerUser = user.toLowerCase()

    override suspend fun getTwitchFlow(): Flow<TwitchMessage> {
        return super.getTwitchFlow()
            .filter {
                it.rawMessage.author == lowerUser || it.rawMessage.tags["login"] == lowerUser
            }

    }
}

@TwitchDsl
inline fun TwitchScope.user(userName: String, block: UserTwitchScope.() -> Unit) =
    UserTwitchScope(userName, this, coroutineContext).apply(block)