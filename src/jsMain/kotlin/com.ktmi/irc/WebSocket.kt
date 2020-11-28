package com.ktmi.irc

import org.w3c.dom.WebSocket

/** Wrapper around ws npm package */
@JsModule("ws")
@JsNonModule
external class WebSocket(url: String) : WebSocket