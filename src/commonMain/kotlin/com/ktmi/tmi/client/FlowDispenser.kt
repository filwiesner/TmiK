package com.ktmi.tmi.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.Synchronized

data class FlowCallback <T> (
    val onNextValue: (value: T) -> Unit,
    val onClose: (t: Throwable?) -> Unit
)

class CompletedSource : Exception("Source channel has completed")

class FlowDispenser <T> (
    val inChannel: ReceiveChannel<T>,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {
    private val flows = mutableListOf<FlowCallback<T>>()
    private var job: Job? = null

    fun initialize() {
        if (running) return
        job = launch {
            try {
                for (value in inChannel) {
                    flows.forEach {
                        it.onNextValue(value)
                    }
                }
            } catch (t: Throwable) {
                flows.forEach { it.onClose(t) }
            }
        }
    }

    @Synchronized
    private fun addCallback(callback: FlowCallback<T>) = flows.add(callback)

    @Synchronized
    private fun removeCallback(callback: FlowCallback<T>) = flows.remove(callback)

    @Synchronized
    fun requestFlow() = callbackFlow<T> {
        val callback = FlowCallback<T>(
            { offer(it) },
            { close(it ?: CompletedSource()) }
        )
        addCallback(callback)

        if (inChannel.isClosedForReceive)
            channel.close()

        awaitClose { removeCallback(callback) }
    }

    val running get() = job?.isActive == true

    fun stop() {
        job?.cancel()
    }

    fun clear() = flows.clear()
}