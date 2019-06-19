package com.ktmi.tmi.client.builder

import com.ktmi.irc.IrcState
import com.ktmi.tmi.client.TmiClient
import com.ktmi.tmi.client.events.asTwitchMessageFlow
import com.ktmi.tmi.messages.TwitchMessage
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

@DslMarker
annotation class TwitchDsl

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

    open fun getIrcStateFlow(): Flow<IrcState> =
        parent?.getIrcStateFlow()
            ?: throw NoParentException()

    class NoParentException : Exception("Accessing parent of top element")
}


class MainScope(private val client: TmiClient) : TwitchScope(null,client.coroutineContext + CoroutineName("Main Scope")) {

    init {
        client.connect()
    }

    override suspend fun getTwitchFlow(): Flow<TwitchMessage> = client.raw.asTwitchMessageFlow
    override fun getIrcStateFlow(): Flow<IrcState> = client.connectionStatus

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