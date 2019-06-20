package com.ktmi.tmi.client.builder.scopes

import com.ktmi.tmi.client.builder.TwitchDsl
import com.ktmi.tmi.client.builder.TwitchScope
import com.ktmi.tmi.client.builder.UserContextScope
import com.ktmi.tmi.messages.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlin.coroutines.CoroutineContext

class UserScope(
    val user: String,
    parent: TwitchScope,
    coroutineContext: CoroutineContext
) : UserContextScope(parent,coroutineContext + CoroutineName("UserTwitch")) {
    private val lowerUser = user.toLowerCase()

    override suspend fun getTwitchFlow(): Flow<TwitchMessage> {
        return super.getTwitchFlow()
            .filter { it.rawMessage.author == lowerUser
                    ||it.rawMessage.tags["login"] == lowerUser
                    ||it.rawMessage.tags["display-name"]?.toLowerCase() == lowerUser
                    ||(it is JoinMessage && it.username == lowerUser)
                    ||(it is LeaveMessage && it.username == lowerUser)
                    ||(it is ClearChatMessage && it.bannedUser == lowerUser)
            }

    }
}

@TwitchDsl
inline fun TwitchScope.broadcaster(userName: String, block: UserScope.() -> Unit) =
    UserScope(userName, this, coroutineContext).apply(block)