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

//private sealed class DispenserActor
//private class Add<T>(val callback: FlowCallback<T>) : DispenserActor()
//private class Remove<T>(val callback: FlowCallback<T>) : DispenserActor()
//private class NotifyAll<T>(val value: T) : DispenserActor()

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
    private val _flows = mutableListOf<FlowCallback<T>>()
    private var job: Job? = null

    @Synchronized
    private fun changeState(block: (MutableList<FlowCallback<T>>) -> Unit) = block(_flows)

    /** When called, [FlowDispenser] will start to consume [inChannel] events */
    fun initialize() {
        if (running) return
        job = launch {
            try {
                for (value in inChannel)
                    changeState { flows ->
                        flows.forEach { it.onNextValue(value) }
                    }
            } catch (t: Throwable) {
                changeState { flows ->
                    flows.forEach { it.onClose(t) }
                }
            }
        }
    }

    /** Creates one [Flow] listening to [inChannel] */
    fun requestFlow() = callbackFlow<T> {
        val callback = FlowCallback<T>(
            { offer(it) },
            { close(it ?: FinishedSourceException()) }
        )
        changeState { flows ->
            flows.add(callback)
        }

        if (inChannel.isClosedForReceive)
            channel.close()

        awaitClose { changeState { flows ->
            flows.remove(callback)
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
    fun clear() = changeState { flows ->
        flows.clear()
    }
}