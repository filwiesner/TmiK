package com.ktmi.tmi.client.events

import com.ktmi.irc.RawMessage
import com.ktmi.tmi.dsl.builder.*
import com.ktmi.tmi.messages.asChannelName
import com.ktmi.tmi.messages.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Filters [Flow] of [TwitchMessage]s by channel name
 * @param channel name of the channel
 */
operator fun <T : TwitchMessage> Flow<T>.get(channel: String): Flow<T> {
    val channelName = channel.asChannelName
    return filter { it.rawMessage.channel == channelName }
}

/**
 * Filters [Flow] of [TwitchMessage]s using given type
 */
inline fun <reified T : TwitchMessage> Flow<TwitchMessage>.filterMessage(): Flow<T> = flow {
    filter { it is T }
        .collect {
            return@collect emit(it as T)
        }
}

/**
 * Filters [Flow] of [TwitchMessage]s to pass only [UserStateRelated] messages that matches given predicate
 * @param filter predicate used for filtering
 */
inline fun Flow<TwitchMessage>.filterChannelUser(
    crossinline filter: suspend (UserStateRelated) -> Boolean
): Flow<TwitchMessage> = this.filter { it is UserStateRelated && filter(it) }

/**
 * Transforms [RawMessage] flow to [TwitchMessage] flow
 */
val Flow<RawMessage>.asTwitchMessageFlow: Flow<TwitchMessage> get() = map {
    when(it.commandName) {
        "GLOBALUSERSTATE" -> GlobalUserStateMessage(it)
        "JOIN" -> JoinMessage(it)
        "PART" -> LeaveMessage(it)
        "USERSTATE" -> UserStateMessage(it)
        "ROOMSTATE" -> RoomStateMessage(it)
        "PRIVMSG" -> TextMessage(it)
        "CLEARCHAT" -> ClearChatMessage(it)
        "CLEARMSG" -> ClearMessage(it)
        "NOTICE" -> NoticeMessage(it)
        "USERNOTICE" -> UserNoticeMessage(it)

        else -> UndefinedMessage(it)
    }
}

/**
 * Registers a listener to one of [TwitchMessage]s specified by type
 * @param action called when message of given type is received
 */
inline fun <reified T : TwitchMessage> TwitchScope.onTwitchMessage(
    crossinline action: suspend (T) -> Unit
) {
    launch {
        getTwitchFlow()
            .filterMessage<T>()
            .collect { action(it) }
    }
}

/** Registers a listener for [GlobalUserStateMessage] */
inline fun MainScope.onGlobalUserState(crossinline action: suspend (GlobalUserStateMessage) -> Unit) =
    onTwitchMessage(action)

/** Registers a listener for [JoinMessage] */
inline fun MainScope.onUserJoin(crossinline action: suspend (JoinMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [JoinMessage] */
inline fun ChannelContextScope.onUserJoin(crossinline action: suspend (JoinMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [JoinMessage] */
inline fun UserContextScope.onUserJoin(crossinline action: suspend (JoinMessage) -> Unit) =
    onTwitchMessage(action)

/** Registers a listener for [LeaveMessage] */
inline fun MainScope.onUserLeave(crossinline action: suspend (LeaveMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [LeaveMessage] */
inline fun ChannelContextScope.onUserLeave(crossinline action: suspend (LeaveMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [LeaveMessage] */
inline fun UserContextScope.onUserLeave(crossinline action: suspend (LeaveMessage) -> Unit) =
    onTwitchMessage(action)

/** Registers a listener for [UserStateMessage] */
inline fun MainScope.onUserState(crossinline action: suspend (UserStateMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [UserStateMessage] */
inline fun ChannelContextScope.onUserState(crossinline action: suspend (UserStateMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [UserStateMessage] */
inline fun UserContextScope.onUserState(crossinline action: suspend (UserStateMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [UserStateMessage] */
inline fun UserStateContextScope.onUserState(crossinline action: suspend (UserStateMessage) -> Unit) =
    onTwitchMessage(action)

/** Registers a listener for [RoomStateMessage] */
inline fun MainScope.onRoomState(crossinline action: suspend (RoomStateMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [RoomStateMessage] */
inline fun ChannelContextScope.onRoomState(crossinline action: suspend (RoomStateMessage) -> Unit) =
    onTwitchMessage(action)

/** Registers a listener for [TextMessage] */
inline fun MainScope.onMessage(crossinline action: suspend (TextMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [TextMessage] */
inline fun ChannelContextScope.onMessage(crossinline action: suspend (TextMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [TextMessage] */
inline fun UserContextScope.onMessage(crossinline action: suspend (TextMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [TextMessage] */
inline fun UserStateContextScope.onMessage(crossinline action: suspend (TextMessage) -> Unit) =
    onTwitchMessage(action)

/** Registers a listener for [ClearChatMessage] */
inline fun MainScope.onClearChat(crossinline action: suspend (ClearChatMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [ClearChatMessage] */
inline fun ChannelContextScope.onClearChat(crossinline action: suspend (ClearChatMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [ClearChatMessage] */
inline fun UserContextScope.onClearChat(crossinline action: suspend (ClearChatMessage) -> Unit) =
    onTwitchMessage(action)

/** Registers a listener for [ClearMessage] */
inline fun MainScope.onClearMessage(crossinline action: suspend (ClearMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [ClearMessage] */
inline fun ChannelContextScope.onClearMessage(crossinline action: suspend (ClearMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [ClearMessage] */
inline fun UserContextScope.onClearMessage(crossinline action: suspend (ClearMessage) -> Unit) =
    onTwitchMessage(action)

/** Registers a listener for [NoticeMessage] */
inline fun MainScope.onNotice(crossinline action: suspend (NoticeMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [NoticeMessage] */
inline fun ChannelContextScope.onNotice(crossinline action: suspend (NoticeMessage) -> Unit) =
    onTwitchMessage(action)

/** Registers a listener for [UserNoticeMessage] */
inline fun MainScope.onUserNotice(crossinline action: suspend (UserNoticeMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [UserNoticeMessage] */
inline fun ChannelContextScope.onUserNotice(crossinline action: suspend (UserNoticeMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [UserNoticeMessage] */
inline fun UserContextScope.onUserNotice(crossinline action: suspend (UserNoticeMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [UserNoticeMessage] */
inline fun UserStateContextScope.onUserNotice(crossinline action: suspend (UserNoticeMessage) -> Unit) =
    onTwitchMessage(action)