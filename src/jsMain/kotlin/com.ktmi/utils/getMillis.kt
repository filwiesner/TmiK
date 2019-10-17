package com.ktmi.utils

import kotlin.js.Date

actual fun getMillis() = Date.now().toLong()