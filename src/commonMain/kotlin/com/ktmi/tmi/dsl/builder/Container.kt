package com.ktmi.tmi.dsl.builder

import com.ktmi.irc.IrcState
import com.ktmi.tmi.dsl.builder.scopes.TmiStateProvider
import com.ktmi.tmi.dsl.plugins.TwitchPlugin
import com.ktmi.tmi.messages.TwitchMessage
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Scope responsible for containing all inside it and control all the [TwitchMessage] traffic.
 * It's modifiable with [TwitchPlugin]s which can add plu&play functionality
 * @param parent parent scope where messages are forwarded and from  where main [Flow] of [TwitchMessage]s is retrieved
 * @param context [CoroutineContext] used for creating [TwitchMessage] listeners
 * @throws NoTmiStateHandlerException when ircStateFlow is not supplied and no parent is IrcStateProvider
 */
open class Container(
    parent: TwitchScope?,
    context: CoroutineContext,
    client: TmiStateProvider? = null
) : GlobalContextScope(parent, context + CoroutineName("Container")),
    TmiStateProvider {

    private val provider: TmiStateProvider
    private val plugins = mutableMapOf<String, TwitchPlugin>()

    init {
        provider = client ?: run<TmiStateProvider> {
            var currentScope: TwitchScope? = this.parent

            // Find parent who implements TmiStateProvider
            while (currentScope != null && currentScope !is TmiStateProvider) {
                currentScope = currentScope.parent
            }

            if (currentScope == null || currentScope !is TmiStateProvider)
                throw NoTmiStateHandlerException()

            currentScope
        }

        launch { provider.connectionStatus.collect {
            for (plugin in plugins.values)
                plugin.onConnectionStateChange(it)
        } }
    }

    override val username: String get() = provider.username
    override fun connect() = provider.connect()
    override fun disconnect() = provider.disconnect()

    override val connectionStatus: Flow<IrcState> = provider.connectionStatus

    override fun getTwitchFlow(): Flow<TwitchMessage> = super.getTwitchFlow()
        .filter { message ->
            plugins.values.all { it.filterIncoming(message) }
        }.map {
            var message = it

            for (plugin in plugins.values)
                message = plugin.mapIncoming(message)

            message
        }

    override fun sendRaw(message: String) {
        if (!plugins.values.all { it.filterOutgoing(message) })
            return

        var finalMessage = message
        for (plugin in plugins.values)
            finalMessage = plugin.mapOutgoing(finalMessage)

        super.sendRaw(finalMessage)
    }

    /** Adds [TwitchPlugin] to this [Container] */
    operator fun TwitchPlugin.unaryPlus() {
        val name = this.name
        if (plugins.containsKey(name))
            throw PluginAlreadyExistsException(name)

        plugins[name] = this
    }
}

/** Basic builder function for [Container] */
@TwitchDsl
inline fun TwitchScope.container(block: Container.() -> Unit) =
    Container(this, coroutineContext).apply(block)

/** Thrown when no parent is IrcStateProvider */
class NoTmiStateHandlerException : Exception("No parent of TwitchContainer provides IrcState (implements TmiStateProvider)")
/** Thrown when added [TwitchPlugin] already exist in [Container] */
class PluginAlreadyExistsException(name: String) : Exception("Plugin with name $name already exists in this container")