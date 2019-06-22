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
import kotlin.coroutines.CoroutineContext

/**
 * Annotation that marks a Twitch DSL composed of [TwitchScope]s
 */
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
    open suspend fun sendRaw(message: String) {
        parent?.sendRaw(message)
            ?: throw NoParentException()
    }

    /**
     * Retrieves main [Flow] of [TwitchMessage]s from [TmiClient]
     * This [Flow] is passed down the chain of [TwitchScope]s and each [TwitchScope] can alter the flow
     * @throws NoParentException
     */
    open suspend fun getTwitchFlow(): Flow<TwitchMessage> =
        parent?.getTwitchFlow()
            ?: throw NoParentException()

    /** Thrown when no parent is found */
    class NoParentException : Exception("Accessing parent of top element")
}

/**
 * Root [TwitchScope] of [TwitchDsl]
 * @param client [TmiClient] that will be used from retrieving [Flow] of [RawMessage]s
 * and sending string messages to [TwitchIRC]
 */
class MainScope(
    private val client: TmiClient
) : TwitchScope(null,client.coroutineContext + CoroutineName("Main Scope")) {

    init {
        client.connect()
    }

    override suspend fun getTwitchFlow(): Flow<TwitchMessage> = client.raw.asTwitchMessageFlow

    /** Retrieves connection state [Flow] of [IrcState] messages from [TmiClient] */
    fun getIrcStateFlow(): Flow<IrcState> = client.connectionStatus

    override suspend fun sendRaw(message: String) {
        client.sendRaw(message)
    }

}

/**
 * Main builder of [TwitchScope]. Creates the root scope ([MainScope]).
 * @param token Token in format *"oauth:token"*. You can get this token from [twitchapps](https://twitchapps.com/tmi/)
 * @param username optional username passed in initialization of connection
 * @param secure true if connection to twitch should be secure (using WSS protocol instead of WS)
 * @param context [CoroutineContext] that should be used fro receiving messages from Twitch
 * @param block Extension function used to pass in [MainScope]
 */
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