package com.ktmi.irc

import kotlin.coroutines.CoroutineContext

/** Actual implementation of TwitchIRC interface for Javascript */
class WsIrcClient(
    token: String,
    username: String,
    secure: Boolean,
    context: CoroutineContext
) : IrcClient(token, username, secure, context) {

    private var ws: WebSocket? = null

    override fun connectSocket(url: String, token: String, username: String) {
        ws = WebSocket(url).apply {
            onopen = { onConnectionOpened() }
            onclose = { onClosed() }
            onmessage = { onMessageReceived(it.data as String) }
            onerror = { onClosed() }
        }
    }

    override fun disconnectSocket() {
        ws?.apply {
            onopen = null
            onclose = null
            onmessage = null
            onerror = null
        }
        ws?.close(1000)
    }

    override fun sendMessage(message: String) {
        ws?.send(message)
    }
}

actual fun initIrcClient(
    token: String,
    username: String,
    secure: Boolean,
    context: CoroutineContext
): TwitchIRC = WsIrcClient(token, username, secure, context)