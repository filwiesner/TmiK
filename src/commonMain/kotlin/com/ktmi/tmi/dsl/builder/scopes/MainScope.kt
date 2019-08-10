package com.ktmi.tmi.dsl.builder.scopes

import com.ktmi.irc.IrcState
import com.ktmi.irc.RawMessage
import com.ktmi.irc.TwitchIRC
import com.ktmi.tmi.client.TmiClient
import com.ktmi.tmi.client.events.asTwitchMessageFlow
import com.ktmi.tmi.dsl.builder.Container
import com.ktmi.tmi.dsl.builder.TwitchDsl
import com.ktmi.tmi.messages.TwitchMessage
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

/** Supplies [TmiClient] functionality */
interface TmiStateProvider {

    /** Username of account on which is [TmiClient] running */
    val username: String

    /** Connects [TmiClient] to Twitch */
    fun connect()

    /** Disconnects [TmiClient] from Twitch */
    fun disconnect()

    /** Retrieves connection state [Flow] of [IrcState] messages from [TmiClient] */
    val connectionStatus: Flow<IrcState>
}

/**
 * Root [TwitchScope] of [TwitchDsl]
 * @param client [TmiClient] that will be used from retrieving [Flow] of [RawMessage]s
 * and sending string messages to [TwitchIRC]
 */
class MainScope(
    private val client: TmiClient
) : Container(
    null,
    client.coroutineContext + CoroutineName("Main Scope"),
    client
), TmiStateProvider {

    override val username get() = client.username
    override fun connect() = client.connect()
    override fun disconnect() = client.disconnect()
    override val connectionStatus: Flow<IrcState> = client.connectionStatus

    override fun getTwitchFlow(): Flow<TwitchMessage> = client.raw.asTwitchMessageFlow

    override fun sendRaw(message: String) {
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
            .also(TmiClient::connect)
    ).apply(block)
}