package com.ktmi.tmi.client.commands

import com.ktmi.tmi.client.builder.TwitchScope

suspend fun TwitchScope.reconnect() = sendRaw("RECONNECT")