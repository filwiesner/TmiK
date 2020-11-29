package com.ktmi.tmi.dsl.plugins

import com.ktmi.tmi.dsl.builder.Container
import com.ktmi.utils.getMillis

/**
 * Throttles messages going out (sending) in given interval. Only messages sent **after** the interval are sent
 * and each messages sent resets the interval.
 * @param interval time in milliseconds after which next message can be sent
 */
fun Container.ThrottleOut(interval: Long = 5_000) = object : TwitchPlugin {
    override val name = "throttle_out"

    private var lastSent = 0L

    override fun filterOutgoing(message: String): Boolean {
        val now = getMillis()

        return (now - lastSent > interval)
            .also { if (it) lastSent = now }
    }
}