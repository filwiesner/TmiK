package com.ktmi.tmi.dsl.plugins

import com.ktmi.irc.IrcState
import com.ktmi.irc.IrcState.*
import com.ktmi.tmi.client.TmiClient
import com.ktmi.tmi.client.commands.join
import com.ktmi.tmi.client.events.onTwitchMessage
import com.ktmi.tmi.messages.JoinMessage
import com.ktmi.tmi.messages.LeaveMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Creates [TwitchPlugin] that reconnects user to [TmiClient] after disconnection and re-joins all channels
 * @param attempts number of attempts after which it will give up (unlimited by default)
 * @param interval the interval between attempts (10s by default)
 */
fun Container.Reconnect(attempts: Int = 0, interval: Long = 10_000) = object : TwitchPlugin {
    private val activeChannels = mutableListOf<String>()
    private var currState = DISCONNECTED
    private var connectingJob: Job? = null

    override val name = "reconnect"

    init {
        onTwitchMessage<JoinMessage> {
            if (it.username == username)
                activeChannels.add(it.channel)
        }
        onTwitchMessage<LeaveMessage> {
            if (it.username == username)
                activeChannels.remove(it.channel)
        }
    }

    override fun onConnectionStateChange(newState: IrcState) {
        if (currState == newState || newState == CONNECTING) return

        currState = newState
        if (newState == DISCONNECTED)
            connectingJob = tryToConnect()
        else if (newState == CONNECTED) {
            connectingJob?.cancel()
            rejoinChannels()
        }

    }

    private fun tryToConnect() = launch {
        suspend fun reconnect() {
            if (currState != CONNECTED) {
                connect()
                delay(interval)
            }
        }

        try {
            if (attempts > 0) for (attempt in 1..attempts)
                reconnect()
            else while (true)
                reconnect()

        } catch (e: CancellationException) {}
    }

    private fun rejoinChannels() { launch {
        activeChannels.forEach { join(it) }
    } }
}