package com.ktmi

/**
 * Thread blocking block of code
 */
expect fun runTest(block: suspend () -> Unit)