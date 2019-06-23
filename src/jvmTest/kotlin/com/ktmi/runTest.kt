package com.ktmi

import kotlinx.coroutines.runBlocking

/**
 * Thread blocking block of code
 */
actual fun runTest(block: suspend () -> Unit) = runBlocking { block() }