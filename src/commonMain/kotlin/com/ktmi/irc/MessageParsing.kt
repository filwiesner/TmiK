package com.ktmi.irc

/**
 * Represents key-value pair of tag sent in message from [TwitchIRC]
 */
data class Tag(val name: String, val value: String?)

/**
 * Represents parsed message received from [TwitchIRC]
 */
data class RawMessage(

    /**
     *  String message received from Twitch
     */
    val raw: String,

    /**
     * [Map] of tags found at starts of IRC message
     */
    val tags: Map<String, String>,

    /**
     * Origin of the message.
     * For example from **JOIN** message *":ronni!ronni@ronni.tmi.twitch.tv JOIN #dallas"*
     * the prefix is *"ronni!ronni@ronni.tmi.twitch.tv"*
     */
    val prefix: String,

    /**
     * Name of the command. For example **JOIN** or **NOTICE**
     */
    val commandName: String?,

    /**
     * Channel from where the message originated (if available)
     */
    val channel: String?,

    /**
     * The text content of the message. For example from message *":tmi.twitch.tv CLEARMSG #dallas :HeyGuys*"
     * the **text** is *"HeyGuys"*
     */
    val text: String?
)

/**
 * Builder used for parsing and assembling message from [TwitchIRC]
 * @param raw string message received from Twitch
 */
class RawMessageBuilder(val raw: String) {
    val tags = mutableMapOf<String, String>()

    var channel: String? = null
    var prefix: String = ""
    var command: String? = null
    var text: String? = null

    /** Builds [RawMessage] with given information */
    fun build() = RawMessage(raw, tags, prefix, command, channel, text)
}

/**
 * Function used for DSL-like initialization of [RawMessageBuilder]
 * @param raw string message received from Twitch
 * @return [RawMessage] created fom contents of [raw]
 */
fun buildMessage(raw: String, block: RawMessageBuilder.()->Unit ) =
    RawMessageBuilder(raw).apply(block).build()

/**
 * Function used for parsing messages from [TwitchIRC] to [RawMessage].
 * **Don't** use it for parsing commands you write/send.
 * @param message *raw* string received from [TwitchIRC]
 */
fun parseMessage(message: String): RawMessage = buildMessage(message.trim()) {
    var remaining = raw

    // If messages starts with tags
    if (remaining.startsWith('@')) {
        val (first, second) = remaining.divideAtFirst(' ')
        parseTagLine(remaining.substring(first))

        if (second == null) // We've reached the en of the message
            return@buildMessage

        remaining = remaining.substring(second.first)
    }

    // Parse address, command and channel

    remaining = remaining.substring(1) // get rid of ':' at start
    val (first, second) = remaining.divideAtFirst(':')
    parseMetadata(remaining.substring(first))

    if (second == null) // We've reached the en of the message
        return@buildMessage

    remaining = remaining.substring(second.first)

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
private fun String.divideAtFirst(char: Char) = indexOf(char)
    .let { if (it == -1) length else it}
    .let { Pair(
        0 until it,

        if (it == length) null
        else (it + 1) until length
    ) }
private val ignoredCommands = arrayOf("353")