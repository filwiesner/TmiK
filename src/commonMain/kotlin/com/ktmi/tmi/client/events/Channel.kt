@file:Suppress("unused", "EXPERIMENTAL_API_USAGE")

package com.ktmi.tmi.client.events

import com.ktmi.irc.RawMessage
import com.ktmi.tmi.dsl.builder.*
import com.ktmi.tmi.dsl.builder.scopes.ChannelScope
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
inline fun GlobalContextScope.onGlobalUserState(crossinline action: suspend (GlobalUserStateMessage) -> Unit) =
    onTwitchMessage(action)

/** Registers a listener for [JoinMessage] */
inline fun GlobalContextScope.onUserJoin(crossinline action: suspend UserContext<JoinMessage>.() -> Unit) =
    onTwitchMessage<JoinMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }
/** Registers a listener for [JoinMessage] */
inline fun ChannelScope.onUserJoin(crossinline action: suspend UserContext<JoinMessage>.() -> Unit) =
    onTwitchMessage<JoinMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }
/** Registers a listener for [JoinMessage] */
inline fun UserContextScope.onUserJoin(crossinline action: suspend UserContext<JoinMessage>.() -> Unit) =
    onTwitchMessage<JoinMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }

/** Registers a listener for [LeaveMessage] */
inline fun GlobalContextScope.onUserLeave(crossinline action: suspend UserContext<LeaveMessage>.() -> Unit) =
    onTwitchMessage<LeaveMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }
/** Registers a listener for [LeaveMessage] */
inline fun ChannelContextScope.onUserLeave(crossinline action: suspend UserContext<LeaveMessage>.() -> Unit) =
    onTwitchMessage<LeaveMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }
/** Registers a listener for [LeaveMessage] */
inline fun UserContextScope.onUserLeave(crossinline action: suspend UserContext<LeaveMessage>.() -> Unit) =
    onTwitchMessage<LeaveMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }

/** Registers a listener for [UserStateMessage] */
inline fun GlobalContextScope.onUserState(crossinline action: suspend UserContext<UserStateMessage>.() -> Unit) =
    onTwitchMessage<UserStateMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }
/** Registers a listener for [UserStateMessage] */
inline fun ChannelContextScope.onUserState(crossinline action: suspend UserContext<UserStateMessage>.() -> Unit) =
    onTwitchMessage<UserStateMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }
/** Registers a listener for [UserStateMessage] */
inline fun UserContextScope.onUserState(crossinline action: suspend UserContext<UserStateMessage>.() -> Unit) =
    onTwitchMessage<UserStateMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }
/** Registers a listener for [UserStateMessage] */
inline fun UserStateContextScope.onUserState(crossinline action: suspend UserContext<UserStateMessage>.() -> Unit) =
    onTwitchMessage<UserStateMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }

/** Registers a listener for [RoomStateMessage] */
inline fun GlobalContextScope.onRoomState(crossinline action: suspend ChannelContext<RoomStateMessage>.() -> Unit) =
    onTwitchMessage<RoomStateMessage> { mess -> ChannelContext(mess, mess.channel).action() }
/** Registers a listener for [RoomStateMessage] */
inline fun ChannelContextScope.onRoomState(crossinline action: suspend ChannelContext<RoomStateMessage>.() -> Unit) =
    onTwitchMessage<RoomStateMessage> { mess -> ChannelContext(mess, mess.channel).action() }

/** Registers a listener for [TextMessage] */
inline fun GlobalContextScope.onMessage(crossinline action: suspend UserContext<TextMessage>.() -> Unit) =
    onTwitchMessage<TextMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }
/** Registers a listener for [TextMessage] */
inline fun ChannelContextScope.onMessage(crossinline action: suspend UserContext<TextMessage>.() -> Unit) =
    onTwitchMessage<TextMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }
/** Registers a listener for [TextMessage] */
inline fun UserContextScope.onMessage(crossinline action: suspend UserContext<TextMessage>.() -> Unit) =
    onTwitchMessage<TextMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }
/** Registers a listener for [TextMessage] */
inline fun UserStateContextScope.onMessage(crossinline action: suspend UserContext<TextMessage>.() -> Unit) =
    onTwitchMessage<TextMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }

/** Registers a listener for [ClearChatMessage] */
inline fun GlobalContextScope.onClearChat(crossinline action: suspend ChannelContext<ClearChatMessage>.() -> Unit) =
    onTwitchMessage<ClearChatMessage> { mess -> ChannelContext(mess, mess.channel).action() }
/** Registers a listener for [ClearChatMessage] */
inline fun ChannelContextScope.onClearChat(crossinline action: suspend ChannelContext<ClearChatMessage>.() -> Unit) =
    onTwitchMessage<ClearChatMessage> { mess -> ChannelContext(mess, mess.channel).action() }
/** Registers a listener for [ClearChatMessage] */
inline fun UserContextScope.onClearChat(crossinline action: suspend ChannelContext<ClearChatMessage>.() -> Unit) =
    onTwitchMessage<ClearChatMessage> { mess -> ChannelContext(mess, mess.channel).action() }

/** Registers a listener for [ClearMessage] */
inline fun GlobalContextScope.onClearMessage(crossinline action: suspend ChannelContext<ClearMessage>.() -> Unit) =
    onTwitchMessage<ClearMessage> { mess -> ChannelContext(mess, mess.channel).action() }
/** Registers a listener for [ClearMessage] */
inline fun ChannelContextScope.onClearMessage(crossinline action: suspend ChannelContext<ClearMessage>.() -> Unit) =
    onTwitchMessage<ClearMessage> { mess -> ChannelContext(mess, mess.channel).action() }
/** Registers a listener for [ClearMessage] */
inline fun UserContextScope.onClearMessage(crossinline action: suspend ChannelContext<ClearMessage>.() -> Unit) =
    onTwitchMessage<ClearMessage> { mess -> ChannelContext(mess, mess.channel).action() }

/** Registers a listener for [NoticeMessage] */
inline fun GlobalContextScope.onNotice(crossinline action: suspend ChannelContext<NoticeMessage>.() -> Unit) =
    onTwitchMessage<NoticeMessage> { mess -> ChannelContext(mess, mess.channel).action() }
/** Registers a listener for [NoticeMessage] */
inline fun ChannelContextScope.onNotice(crossinline action: suspend ChannelContext<NoticeMessage>.() -> Unit) =
    onTwitchMessage<NoticeMessage> { mess -> ChannelContext(mess, mess.channel).action() }

/** Registers a listener for [UserNoticeMessage] */
inline fun GlobalContextScope.onUserNotice(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onTwitchMessage<UserNoticeMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }
/** Registers a listener for [UserNoticeMessage] */
inline fun ChannelContextScope.onUserNotice(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onTwitchMessage<UserNoticeMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }
/** Registers a listener for [UserNoticeMessage] */
inline fun UserContextScope.onUserNotice(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onTwitchMessage<UserNoticeMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }
/** Registers a listener for [UserNoticeMessage] */
inline fun UserStateContextScope.onUserNotice(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onTwitchMessage<UserNoticeMessage> { mess -> UserContext(mess, mess.username, mess.channel).action() }

/** Registers a listener for [UserNoticeMessage] */
inline fun GlobalContextScope.onWhisper(crossinline action: suspend (WhisperMessage) -> Unit) =
    onTwitchMessage(action)
/** Registers a listener for [UserNoticeMessage] */
inline fun UserContextScope.onWhisper(crossinline action: suspend (WhisperMessage) -> Unit) =
    onTwitchMessage(action)