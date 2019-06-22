package com.ktmi.tmi.client

//class FlowDispenserTests {
//    val dispatcher = Dispatchers.Default
//    lateinit var unitChannel: Channel<Unit>
//    lateinit var unitDispenser: FlowDispenser<Unit>

//    @BeforeTest
//    fun prepare() {
//        unitChannel = Channel(Channel.UNLIMITED)
//    }
//
//    @Test
//    fun `requestFlow dispenses working flows`() = runTest {
//        val count = 5
//        val countArr = IntArray(5) { 0 }
//
//        val iterations = Random.nextInt(2..10)
//        repeat(iterations) {
//            unitChannel.send(Unit)
//        }
//
//        GlobalScope.launch {
//            delay(1000)
//            unitChannel.close()
//        }
//
//        val unitDispenser = FlowDispenser(unitChannel, dispatcher)
//        repeat(count) { index ->
//            unitDispenser.requestFlow()
//                .collect { countArr[index] += 1 }
//        }
//
//        assertEquals(count * iterations, countArr.sum())
//    }
//}