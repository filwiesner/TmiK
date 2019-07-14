package com.ktmi.tmi.dsl.builder

import com.ktmi.irc.IrcState
import com.ktmi.irc.RawMessage
import com.ktmi.irc.TwitchIRC
import com.ktmi.tmi.client.TmiClient
import com.ktmi.tmi.client.events.asTwitchMessageFlow
import com.ktmi.tmi.messages.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlin.coroutines.CoroutineContext

/**
 * Annotation that marks a Twitch DSL composed of [TwitchScope]s
 */
@DslMarker
annotation class TwitchDsl

/**
 * Base class for creating [TwitchDsl] components (scopes)
 * @param parent parent scope where messages are forwarded and from  where main [Flow] of [TwitchMessage]s is retrieved
 * @param context [CoroutineContext] used for creating [TwitchMessage] listeners
 */
@TwitchDsl
abstract class TwitchScope(
    val parent: TwitchScope?,
    context: CoroutineContext
) : CoroutineScope by CoroutineScope(context) {

    /**
     * Sends raw (unparsed) message to [TwitchIRC]
     * This message is sent up the chain of [TwitchScope]s. Each [TwitchScope] can alter this message
     * @param message string message that will be sent to [TwitchIRC]
     * @throws NoParentException
     */
    open fun sendRaw(message: String) {
        parent?.sendRaw(message)
            ?: throw NoParentException()
    }

    /**
     * Retrieves main [Flow] of [TwitchMessage]s from [TmiClient]
     * This [Flow] is passed down the chain of [TwitchScope]s and each [TwitchScope] can alter the flow
     * @throws NoParentException
     */
    open fun getTwitchFlow(): Flow<TwitchMessage> =
        parent?.getTwitchFlow()
            ?: throw NoParentException()

    /** Thrown when no parent is found */
    class NoParentException : Exception("Accessing parent of top element")
}

/**
 * Scope where all events are available
 */
abstract class GlobalContextScope(
    parent: TwitchScope?,
    context: CoroutineContext
) : TwitchScope(parent, context)

/**
 * Scope where all events are in relation to some **channel**
 * Events available: [JoinMessage], [LeaveMessage], [UserStateMessage], [RoomStateMessage],
 * [TextMessage], [ClearChatMessage], [ClearMessage], [NoticeMessage] and [UserNoticeMessage]
 */
abstract class ChannelContextScope(
    val channel: String,
    parent: TwitchScope?,
    context: CoroutineContext
) : TwitchScope(parent, context) {
    override fun getTwitchFlow(): Flow<TwitchMessage> {
        return super.getTwitchFlow()
            .filter { it.rawMessage.channel == channel.asChannelName }
    }
}

/**
 * Scope where all events are in relation to some **user**
 * Events available: [JoinMessage], [LeaveMessage], [UserStateMessage], [TextMessage],
 * [ClearChatMessage], [ClearMessage] and [UserNoticeMessage]
 */
abstract class UserContextScope(
    username: String,
    parent: TwitchScope?,
    context: CoroutineContext
) : TwitchScope(parent, context) {
    private val lowerUser = username.toLowerCase()

    override fun getTwitchFlow(): Flow<TwitchMessage> {
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
 * Scope where all events are in relation to **UserState**
 * Events available: [UserStateMessage], [TextMessage] and [UserNoticeMessage]
 */
abstract class UserStateContextScope(
    parent: TwitchScope?,
    context: CoroutineContext
) : TwitchScope(parent, context)