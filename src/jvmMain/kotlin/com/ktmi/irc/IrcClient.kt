package com.ktmi.irc

import okhttp3.*
import kotlin.coroutines.CoroutineContext

/** Actual implementation of TwitchIRC interface for JVM */
class WsIrcClient constructor(
    token: String,
    username: String,
    secure: Boolean,
    context: CoroutineContext
) : IrcClient(token, username, secure, context) {

    private val client = OkHttpClient()
    private var ws: WebSocket? = null

    override fun connectSocket(url: String, token: String, username: String) {
        val request = Request.Builder().apply {
            url(url)
        }.build()

        ws = client.newWebSocket(request, listener)
    }

    override fun disconnectSocket() {
        ws?.close(1000, null)
    }

    override fun sendMessage(message: String) {
        ws?.send(message)
    }


    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            onConnectionOpened()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            onClosed()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            // When network signal is lost two errors are thrown. Ignore one of them so only one DISCONNECT event is dispatched
            if (t.localizedMessage != "Network is unreachable: connect")
                onClosed()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            onMessageReceived(text)
        }
    }
}

actual fun initIrcClient(
    token: String,
    username: String,
    secure: Boolean,
    context: CoroutineContext
): TwitchIRC = WsIrcClient(token, username, secure, context)