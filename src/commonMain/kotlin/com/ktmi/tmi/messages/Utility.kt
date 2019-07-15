package com.ktmi.tmi.messages

import com.ktmi.irc.RawMessage
import com.ktmi.irc.TwitchIRC

/**
 * Makes string valid channel name in [TwitchIRC].
 * That means turning it lower-case and putting **'#'** at the start (if needed)
 */
val String.asChannelName get() = this.toLowerCase().let {
    if (startsWith("#")) it
    else "#$it"
}

/**
 * Turns channel name to username.
 * That means stripping **'#'** at the start (if needed)
 */
val String.channelAsUsername get() =
    if (startsWith("#")) substring(1)
    else this

/**
 * Cuts the username from [RawMessage.prefix] (if possible)
 */
val RawMessage.author get() = prefix
    .indexOfAny(charArrayOf('!','.'))
    .let {
        if (it == -1) prefix
        else prefix.substring(0 until it)
    }

/**
 * Makes sure that [RawMessage] is given command
 * @param command command name (for example *JOIN* or *PRIVMSG*)
 * @throws WrongMessageTypeException thrown if command in [RawMessage] does not match given [command]
 */
fun RawMessage.assertCommand(command: String?) {
    if (commandName != command)
        throw WrongMessageTypeException("Required $command and received $commandName")
}

/**
 * Parses Twitch par set received form [TwitchIRC].
 * For example this *"staff/1,premium/1"* turns into map with two keys
 * ('staff' and 'premium') with values *'1'* and *'1'*
 */
fun String.parseTwitchPairSet() = this
    .split(",")
    .map { it.split("/") }
    .filter { it.size == 2 }
    .map { it[0] to it[1].toInt() }
    .toMap()

/**
 * Specifies emote in [TextMessage] that specifies id of the emote and positions (start and end)
 * in the [TextMessage.message]
 */
data class Emote(
    val id: Int,
    val positions: List<Pair<Int, Int>>
)

/**
 * Parses [Emote]s from message received from [TwitchIRC].
 * For example this *"25:0-4,12-16/1902:6-10"* turns into list of two emotes with id *25* and *1902* respectively
 */
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