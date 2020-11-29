package com.ktmi.utils

import kotlin.js.Date

internal actual fun getMillis() = Date.now().toLong()