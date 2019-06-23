package com.ktmi

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

/**
 * Thread blocking block of code
 */
actual fun runTest(block: suspend () -> Unit) : dynamic = GlobalScope.promise { block() }