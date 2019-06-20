package com.ktmi.tmi.client.builder

import com.ktmi.irc.IrcState
import com.ktmi.tmi.client.TmiClient
import com.ktmi.tmi.client.events.asTwitchMessageFlow
import com.ktmi.tmi.messages.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

@DslMarker
annotation class TwitchDsl

/**
 * Scope where all events are in relation to some **channel**
 * Events available: [JoinMessage], [LeaveMessage], [UserStateMessage], [RoomStateMessage],
 * [TextMessage], [ClearChatMessage], [ClearMessage], [NoticeMessage] and [UserNoticeMessage]
 */
abstract class ChannelContextScope(
    parent: TwitchScope?,
    context: CoroutineContext
) : TwitchScope(parent, context)

/**
 * Scope where all events are in relation to some **user**
 * Events available: [JoinMessage], [LeaveMessage], [UserStateMessage], [TextMessage],
 * [ClearChatMessage], [ClearMessage] and [UserNoticeMessage]
 */
abstract class UserContextScope(
    parent: TwitchScope?,
    context: CoroutineContext
) : TwitchScope(parent, context)

/**
 * Scope where all events are in relation to **UserState**
 * Events available: [UserStateMessage], [TextMessage] and [UserNoticeMessage]
 */
abstract class UserStateContextScope(
    parent: TwitchScope?,
    context: CoroutineContext
) : TwitchScope(parent, context)

@TwitchDsl
abstract class TwitchScope(
    val parent: TwitchScope?,
    context: CoroutineContext
) : CoroutineScope by CoroutineScope(context) {

    open suspend fun sendRaw(message: String) {
        parent?.sendRaw(message)
            ?: throw NoParentException()
    }

    open suspend fun getTwitchFlow(): Flow<TwitchMessage> =
        parent?.getTwitchFlow()
            ?: throw NoParentException()

    class NoParentException : Exception("Accessing parent of top element")
}


class MainScope(private val client: TmiClient) : TwitchScope(null,client.coroutineContext + CoroutineName("Main Scope")) {

    init {
        client.connect()
    }

    override suspend fun getTwitchFlow(): Flow<TwitchMessage> = client.raw.asTwitchMessageFlow
    fun getIrcStateFlow(): Flow<IrcState> = client.connectionStatus

    override suspend fun sendRaw(message: String) {
        client.sendRaw(message)
    }

}


@TwitchDsl
inline fun tmi(
    token: String,
    username: String = "blank",
    secure: Boolean = true,
    context: CoroutineContext = Dispatchers.Default,
    crossinline block: MainScope.() -> Unit
) {
    MainScope(
        TmiClient(token, username, secure, context)
    ).apply(block)
}