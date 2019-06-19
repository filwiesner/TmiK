package com.ktmi.tmi.messages

import com.ktmi.irc.RawMessage

val String.asChannelName get() =
    if (startsWith("#")) this
    else "#$this"

val String.channelAsUsername get() =
    if (startsWith("#")) substring(1)
    else this

val RawMessage.author get() = prefix
    .indexOfAny(charArrayOf('!','.'))
    .let {
        if (it == -1) prefix
        else prefix.substring(0 until it)
    }

fun RawMessage.assertCommand(command: String?) {
    if (commandName != command)
        throw WrongMessageTypeException("Required $command and received $commandName")
}

fun String.parseTwitchPairSet() = this
    .split(",")
    .map { it.split("/") }
    .filter { it.size == 2 }
    .map { it[0] to it[1].toInt() }
    .toMap()

data class Emote(
    val id: Int,
    val positions: List<Pair<Int, Int>>
)
fun String.parseTwitchEmotes() = this
    .split("/")
    .map { it.split(":") }
    .filter { it.size == 2 }
    .map { emote -> Emote(
        emote[0].toInt(),
        emote[1].split(",")
            .map { it.split("-") }
            .map { it[0].toInt() to it[1].toInt() }
    ) }

//val RawMessage.parsed get() =