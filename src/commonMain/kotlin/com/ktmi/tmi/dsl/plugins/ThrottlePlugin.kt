package com.ktmi.tmi.dsl.plugins

import com.ktmi.tmi.dsl.builder.Container
import com.ktmi.utils.getMillis

fun Container.ThrottleOut(interval: Long = 5_000) = object : TwitchPlugin {
    override val name = "throttle_out"

    private var lastSent = 0L

    override fun filterOutgoing(message: String): Boolean {
        val now = getMillis()

        return (now - lastSent > interval)
            .also { if (it) lastSent = now }
    }
}