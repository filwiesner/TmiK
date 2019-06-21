package com.ktmi.tmi.client.builder.scopes

import com.ktmi.irc.RawMessage
import com.ktmi.irc.TwitchIRC
import com.ktmi.tmi.client.TmiClient
import com.ktmi.tmi.client.builder.TwitchDsl
import com.ktmi.tmi.client.builder.TwitchScope
import com.ktmi.tmi.client.builder.UserContextScope
import com.ktmi.tmi.messages.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlin.coroutines.CoroutineContext

/**
 * [TwitchDsl] scope used to filter out messages by username
 * @param user [String] username used for filtering [TwitchMessage]s
 * @param parent parent scope where messages are forwarded and from  where main [Flow] of [TwitchMessage]s is retrieved
 * @param coroutineContext [CoroutineContext] used for creating [TwitchMessage] listeners
 */
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

/**
 * [TwitchDsl] builder function for [UserScope]
 * @param userName name used for filtering messages
 * @param block body of the DSL ([UserScope])
 */
@TwitchDsl
inline fun TwitchScope.user(userName: String, block: UserScope.() -> Unit) =
    UserScope(userName, this, coroutineContext).apply(block)