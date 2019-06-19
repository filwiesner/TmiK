package com.ktmi.irc

data class Tag(val name: String, val value: String?)
data class RawMessage(
    val raw: String,
    val tags: MutableMap<String, String>,
    val prefix: String,
    val commandName: String?,
    val channel: String?,
    val text: String?
)

class RawMessageBuilder(val raw: String) {
    val tags = mutableMapOf<String, String>()

    var channel: String? = null
    var prefix: String = ""
    var command: String? = null
    var text: String? = null

    fun build() = RawMessage(raw, tags, prefix, command, channel, text)
}
fun buildMessage(raw: String, block: RawMessageBuilder.()->Unit ) =
    RawMessageBuilder(raw).apply(block).build()


fun parseMessage(message: String): RawMessage = buildMessage(message.trim()) {
//    println(message)
    var remaining = raw

    // If messages starts with tags
    if (remaining.startsWith('@')) {
        val (first, second) = remaining.divideAtFirst(' ')
        parseTagLine(remaining.substring(first))

        if (second == null) // We've reached the en of the message
            return@buildMessage

        remaining = remaining.substring(second.start)
    }

    // Parse address, command and channel

    remaining = remaining.substring(1) // get rid of ':' at start
    val (first, second) = remaining.divideAtFirst(':')
    parseMetadata(remaining.substring(first))

    if (second == null) // We've reached the en of the message
        return@buildMessage

    remaining = remaining.substring(second.start)

    // the rest is chat message
    if (remaining.isNotBlank() && ignoredCommands.none { it == command })
        text = remaining.trim()
}

private fun RawMessageBuilder.parseMetadata(line: String) {
    val data = line.split(' ')

    prefix = data[0]
    command = data[1]
    channel = data.getOrNull(2)
}

private fun RawMessageBuilder.parseTagLine(line: String): Unit = line
    .substring(1) // get rid of '@' at start
    .split(";")
    .map { it.split("=") }
    .filter { it.size == 2 }
    .forEach { tags[it[0]] = it[1] }


// === HELPERS ===
private fun String.divideAtFirst(char: Char) =
    indexOf(char)
        .let { if (it == -1) length else it}
        .let { Pair(
            0 until it,

            if (it == length) null
            else (it + 1) until length
        ) }
private val ignoredCommands = arrayOf("353")