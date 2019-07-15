package com.ktmi.tmi.dsl.builder.scopes

import com.ktmi.tmi.dsl.builder.TwitchDsl
import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.dsl.builder.UserContextScope
import com.ktmi.tmi.messages.TwitchMessage
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

/**
 * [TwitchDsl] scope used to filter out messages by username
 * @param user [String] username used for filtering [TwitchMessage]s
 * @param parent parent scope where messages are forwarded and from  where main [Flow] of [TwitchMessage]s is retrieved
 * @param coroutineContext [CoroutineContext] used for creating [TwitchMessage] listeners
 */
class UserScope(
    username: String,
    parent: TwitchScope,
    coroutineContext: CoroutineContext
) : UserContextScope(username, parent,coroutineContext + CoroutineName("UserTwitch"))

/**
 * [TwitchDsl] builder function for [UserScope]
 * @param userName name used for filtering messages
 * @param block body of the DSL ([UserScope])
 */
@TwitchDsl
inline fun TwitchScope.user(userName: String, block: UserScope.() -> Unit) =
    UserScope(userName, this, coroutineContext).apply(block)