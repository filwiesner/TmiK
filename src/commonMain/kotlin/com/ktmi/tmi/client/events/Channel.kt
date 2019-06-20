package com.ktmi.tmi.client.events

import com.ktmi.irc.RawMessage
import com.ktmi.tmi.client.builder.*
import com.ktmi.tmi.messages.asChannelName
import com.ktmi.tmi.messages.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

operator fun <T : TwitchMessage> Flow<T>.get(channel: String): Flow<T> {
    val channelName = channel.asChannelName
    return filter { it.rawMessage.channel == channelName }
}

inline fun <reified T : TwitchMessage> Flow<TwitchMessage>.filterMessage(): Flow<T> = flow {
    filter { it is T }
        .collect {
            return@collect emit(it as T)
        }
}

inline fun Flow<TwitchMessage>.filterChannelUser(
    crossinline filter: suspend (UserStateRelated) -> Boolean
): Flow<TwitchMessage> = this.filter { it is UserStateRelated && filter(it) }

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

inline fun <reified T : TwitchMessage> TwitchScope.onTwitchMessage(
    crossinline action: suspend (T) -> Unit
) {
    launch {
        getTwitchFlow()
            .filterMessage<T>()
            .collect { action(it) }
    }
}

inline fun MainScope.onGlobalUserState(crossinline action: suspend (GlobalUserStateMessage) -> Unit) =
    onTwitchMessage(action)

inline fun MainScope.onUserJoin(crossinline action: suspend (JoinMessage) -> Unit) =
    onTwitchMessage(action)
inline fun ChannelContextScope.onUserJoin(crossinline action: suspend (JoinMessage) -> Unit) =
    onTwitchMessage(action)
inline fun UserContextScope.onUserJoin(crossinline action: suspend (JoinMessage) -> Unit) =
    onTwitchMessage(action)

inline fun MainScope.onUserLeave(crossinline action: suspend (LeaveMessage) -> Unit) =
    onTwitchMessage(action)
inline fun ChannelContextScope.onUserLeave(crossinline action: suspend (LeaveMessage) -> Unit) =
    onTwitchMessage(action)
inline fun UserContextScope.onUserLeave(crossinline action: suspend (LeaveMessage) -> Unit) =
    onTwitchMessage(action)

inline fun MainScope.onUserState(crossinline action: suspend (UserStateMessage) -> Unit) =
    onTwitchMessage(action)
inline fun ChannelContextScope.onUserState(crossinline action: suspend (UserStateMessage) -> Unit) =
    onTwitchMessage(action)
inline fun UserContextScope.onUserState(crossinline action: suspend (UserStateMessage) -> Unit) =
    onTwitchMessage(action)
inline fun UserStateContextScope.onUserState(crossinline action: suspend (UserStateMessage) -> Unit) =
    onTwitchMessage(action)

inline fun MainScope.onRoomState(crossinline action: suspend (RoomStateMessage) -> Unit) =
    onTwitchMessage(action)
inline fun ChannelContextScope.onRoomState(crossinline action: suspend (RoomStateMessage) -> Unit) =
    onTwitchMessage(action)

inline fun MainScope.onMessage(crossinline action: suspend (TextMessage) -> Unit) =
    onTwitchMessage(action)
inline fun ChannelContextScope.onMessage(crossinline action: suspend (TextMessage) -> Unit) =
    onTwitchMessage(action)
inline fun UserContextScope.onMessage(crossinline action: suspend (TextMessage) -> Unit) =
    onTwitchMessage(action)
inline fun UserStateContextScope.onMessage(crossinline action: suspend (TextMessage) -> Unit) =
    onTwitchMessage(action)

inline fun MainScope.onClearChat(crossinline action: suspend (ClearChatMessage) -> Unit) =
    onTwitchMessage(action)
inline fun ChannelContextScope.onClearChat(crossinline action: suspend (ClearChatMessage) -> Unit) =
    onTwitchMessage(action)
inline fun UserContextScope.onClearChat(crossinline action: suspend (ClearChatMessage) -> Unit) =
    onTwitchMessage(action)

inline fun MainScope.onClearMessage(crossinline action: suspend (ClearMessage) -> Unit) =
    onTwitchMessage(action)
inline fun ChannelContextScope.onClearMessage(crossinline action: suspend (ClearMessage) -> Unit) =
    onTwitchMessage(action)
inline fun UserContextScope.onClearMessage(crossinline action: suspend (ClearMessage) -> Unit) =
    onTwitchMessage(action)

inline fun MainScope.onNotice(crossinline action: suspend (NoticeMessage) -> Unit) =
    onTwitchMessage(action)
inline fun ChannelContextScope.onNotice(crossinline action: suspend (NoticeMessage) -> Unit) =
    onTwitchMessage(action)

inline fun MainScope.onUserNotice(crossinline action: suspend (UserNoticeMessage) -> Unit) =
    onTwitchMessage(action)
inline fun ChannelContextScope.onUserNotice(crossinline action: suspend (UserNoticeMessage) -> Unit) =
    onTwitchMessage(action)
inline fun UserContextScope.onUserNotice(crossinline action: suspend (UserNoticeMessage) -> Unit) =
    onTwitchMessage(action)
inline fun UserStateContextScope.onUserNotice(crossinline action: suspend (UserNoticeMessage) -> Unit) =
    onTwitchMessage(action)