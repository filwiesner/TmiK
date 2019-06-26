package com.ktmi.tmi.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.Synchronized

/**
 * Data class holding two listeners. [onNextValue] and [onClose]
 */
private data class FlowCallback <T> (
    val onNextValue: (value: T) -> Unit,
    val onClose: (t: Throwable?) -> Unit
)

/**
 * [Exception] thrown when given source finished sending messages
 */
class FinishedSourceException : Exception("Source channel has finished sending messages")

/**
 * Turns [Channel] into [Flow]. Holds [FlowCallback]s that will be notified of new value or channel closing.
 * Consumes events in given [Flow]
 * @param inChannel [Channel] consumed and propagated to [FlowCallback]s
 * @param coroutineContext [CoroutineContext] used for consuming [inChannel]
 */
class FlowDispenser <T> (
    val inChannel: ReceiveChannel<T>,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {
    private val flows = mutableListOf<FlowCallback<T>>()
    private var job: Job? = null

    private val mutex = Mutex()

    /** When called, [FlowDispenser] will start to consume [inChannel] events */
    fun initialize() {
        if (running) return
        job = launch {
            try {
                for (value in inChannel) {
                    mutex.withLock {
                        flows.forEach {
                            it.onNextValue(value)
                        }
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


    /** Creates one [Flow] listening to [inChannel] */
    fun requestFlow() = callbackFlow<T> {
        val callback = FlowCallback<T>(
            { offer(it) },
            { close(it ?: FinishedSourceException()) }
        )

        mutex.withLock { flows.add(callback) }

        if (inChannel.isClosedForReceive)
            channel.close()

        awaitClose { launch {
            mutex.withLock { flows.remove(callback) }
        } }
    }

    /** true if [inChannel] events are consumed */
    val running get() = job?.isActive == true

    /** stops consuming [inChannel] events */
    fun stop() {
        job?.cancel()
    }

    /** clears all requested [Flow]s.
     * @see requestFlow
     */
    fun clear() = flows.clear()
}