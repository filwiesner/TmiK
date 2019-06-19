package com.ktmi.tmi.client.events

import com.ktmi.irc.RawMessage
import com.ktmi.tmi.client.TmiClient
import com.ktmi.tmi.client.builder.TwitchScope
import com.ktmi.tmi.messages.asChannelName
import com.ktmi.tmi.messages.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

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

inline fun TwitchScope.onGlobalUserState(crossinline action: suspend (GlobalUserStateMessage) -> Unit) =
    onTwitchMessage(action)

inline fun TwitchScope.onUserJoin(crossinline action: suspend (JoinMessage) -> Unit) =
    onTwitchMessage(action)

inline fun TwitchScope.onUserLeave(crossinline action: suspend (LeaveMessage) -> Unit) =
    onTwitchMessage(action)

inline fun TwitchScope.onUserState(crossinline action: suspend (UserStateMessage) -> Unit) =
    onTwitchMessage(action)

inline fun TwitchScope.onRoomState(crossinline action: suspend (RoomStateMessage) -> Unit) =
    onTwitchMessage(action)

inline fun TwitchScope.onMessage(crossinline action: suspend (TextMessage) -> Unit) =
    onTwitchMessage(action)

inline fun TwitchScope.onClearChat(crossinline action: suspend (ClearChatMessage) -> Unit) =
    onTwitchMessage(action)

inline fun TwitchScope.onClearMessage(crossinline action: suspend (ClearMessage) -> Unit) =
    onTwitchMessage(action)

inline fun TwitchScope.onNotice(crossinline action: suspend (NoticeMessage) -> Unit) =
    onTwitchMessage(action)

inline fun TwitchScope.onUserNotice(crossinline action: suspend (UserNoticeMessage) -> Unit) =
    onTwitchMessage(action)