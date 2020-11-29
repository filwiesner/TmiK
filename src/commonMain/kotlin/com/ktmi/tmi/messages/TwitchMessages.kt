@file:Suppress("ObjectPropertyName", "unused")

package com.ktmi.tmi.messages

import com.ktmi.irc.RawMessage

/**
 * Parsed message received from Twitch
 * @param rawMessage The original [RawMessage]
 * @param command identifies a command type
 * @throws WrongMessageTypeException thrown if command in [RawMessage] does not match given command
 */
sealed class TwitchMessage(
    val rawMessage: RawMessage,
    command: String?
){ init { rawMessage.assertCommand(command) } }

/**
 * Twitch message which is related to some channel and user
 * This message can be: [UserStateMessage], [TextMessage] or [UserNoticeMessage]
 */
interface UserStateRelated {
    val channel: String
    val badgeInfo: String?
    val badges: Map<String, String>?
    val color: String?
    val username: String? get() = displayName?.toLowerCase()
    val displayName: String?
}
val UserStateRelated.isBroadcaster get() = badges?.containsKey("broadcaster") == true
val UserStateRelated.isMod get() = badges?.containsKey("moderator") == true
val UserStateRelated.isSubscriber get() = badges?.containsKey("subscriber") == true
val UserStateRelated.isModOrBroadcaster get() = isMod || isBroadcaster


private val TwitchMessage._channel get() = rawMessage.channel
private val TwitchMessage._username get() = rawMessage.author
private val TwitchMessage._login get() = rawMessage.tags["login"]
private val TwitchMessage._displayName get() = rawMessage.tags["display-name"]
private val TwitchMessage._badgeInfo get() = rawMessage.tags["badge-info"]
private val TwitchMessage._badges get() = rawMessage.tags["badges"]?.parseTwitchPairSet()
private val TwitchMessage._color get() = rawMessage.tags["color"]
private val TwitchMessage._emoteSets get() = rawMessage.tags["emote-sets"]?.split(",")?.map { it.toInt() }
private val TwitchMessage._userId get() = rawMessage.tags["user-id"]?.toIntOrNull()
private val TwitchMessage._isMod get() = rawMessage.tags["mod"] == "1"
private val TwitchMessage._emoteOnly get() = rawMessage.tags["emote-only"] == "1"
private val TwitchMessage._r9k get() = rawMessage.tags["r9k"] == "1"
private val TwitchMessage._roomId get() = rawMessage.tags["room-id"]?.toIntOrNull()
private val TwitchMessage._slowMode get() = rawMessage.tags["slow"]?.toIntOrNull()
private val TwitchMessage._bits get() = rawMessage.tags["bits"]?.toIntOrNull()
private val TwitchMessage._emotes get() = rawMessage.tags["emotes"]?.parseTwitchEmotes()
private val TwitchMessage._id get() = rawMessage.tags["id"]
private val TwitchMessage._timestamp get() = rawMessage.tags["tmi-sent-ts"]?.toLongOrNull()
private val TwitchMessage._text get() = rawMessage.text
private val TwitchMessage._banDuration get() = rawMessage.tags["ban-duration"]?.toIntOrNull()
private val TwitchMessage._messageId get() = rawMessage.tags["msg-id"]
private val TwitchMessage._targetMessageId get() = rawMessage.tags["target-msg-id"]
private val TwitchMessage._systemMessage get() = rawMessage.tags["system-msg"]


/* Messages for missing required parts of the message*/

private const val channelMissingMessage = "channel not available"
private const val loginMissingMessage = "login not available"
private const val displayMessageMissingMessage = "display name not available"
private const val badgeInfoMissingMessage = "badge info not available"
private const val badgesMissingMessage = "badges not available"
private const val colorMissingMessage = "color not available"
private const val emoteSetsMissingMessage = "emote sets not available"
private const val userIdMissingMessage = "user ID not available"
private const val isModMissingMessage = "moderator tag not available"
private const val emoteOnlyMissingMessage = "emote only tag not available"
private const val r9kMissingMessage = "r9k tag not available"
private const val roomIdMissingMessage = "room ID not available"
private const val slowModeMissingMessage = "slow tag not available"
private const val bitsMissingMessage = "bits not available"
private const val emotesMissingMessage = "emotes not available"
private const val idMissingMessage = "ID not available"
private const val timestampMissingMessage = "timestamp not available"
private const val textMissingMessage = "message text not available"
private const val banDurationMissingMessage = "ban duration not available"
private const val messageIdMissingMessage = "messageId not available"
private const val targetMessageIdMissingMessage = "target message ID not available"
private const val systemMessageMissingMessage = "system message not available"
private const val threadIdMissingMessage = "thread ID not available"
private const val messageMissingMessage = "message not available"


/**
 * Message received on successful login. Contains information about logged user
 * @throws CorruptedMessageException when some property is not present
 * @throws WrongMessageTypeException thrown if command in [RawMessage] does not match given command
 */
class GlobalUserStateMessage(
    rawMessage: RawMessage
) : TwitchMessage(rawMessage, "GLOBALUSERSTATE") {
    val badgeInfo get() = _badgeInfo
    val badges get() = _badges
    val color get() = _color
    val displayName get() = _displayName
    val emoteSets get() = _emoteSets
        ?: throw CorruptedMessageException(rawMessage, emoteSetsMissingMessage)
    val userId get() = _userId
        ?: throw CorruptedMessageException(rawMessage, userIdMissingMessage)
}

/**
 * Message received when user joined a channel
 * @throws CorruptedMessageException when some property is not present
 * @throws WrongMessageTypeException thrown if command in [RawMessage] does not match given command
 */
class JoinMessage(
    rawMessage: RawMessage
) : TwitchMessage(rawMessage, "JOIN") {
    val channel get() = _channel
        ?: throw CorruptedMessageException(rawMessage, channelMissingMessage)
    val username get() = _username
}

/**
 * Message received when user left a channel
 * @throws CorruptedMessageException when some property is not present
 * @throws WrongMessageTypeException thrown if command in [RawMessage] does not match given command
 */
class LeaveMessage(
    rawMessage: RawMessage
) : TwitchMessage(rawMessage, "PART") {
    val channel get() = _channel
        ?: throw CorruptedMessageException(rawMessage, channelMissingMessage)
    val username get() = _username
}

/**
 * Identifies a user’s chat settings or properties. Received when user joins a channel
 * @throws CorruptedMessageException when some property is not present
 * @throws WrongMessageTypeException thrown if command in [RawMessage] does not match given command
 */
class UserStateMessage(
    rawMessage: RawMessage
) : TwitchMessage(rawMessage, "USERSTATE"), UserStateRelated {
    override val channel get() = _channel
        ?: throw CorruptedMessageException(rawMessage, channelMissingMessage)
    override val badgeInfo get() = _badgeInfo
    override val badges get() = _badges
    override val color get() = _color
    override val displayName get() = _displayName
    val emoteSets get() = _emoteSets
        ?: throw CorruptedMessageException(rawMessage, emoteSetsMissingMessage)
}

/**
 * Identifies the channel’s chat settings. Received when user joins a channel
 * @throws CorruptedMessageException when some property is not present
 * @throws WrongMessageTypeException thrown if command in [RawMessage] does not match given command
 */
class RoomStateMessage(
    rawMessage: RawMessage
) : TwitchMessage(rawMessage, "ROOMSTATE") {
    val channel get() = _channel
        ?: throw CorruptedMessageException(rawMessage, channelMissingMessage)
    val roomId get() = _roomId
        ?: throw CorruptedMessageException(rawMessage, roomIdMissingMessage)
    val emoteOnly get() = _emoteOnly
    val r9k get() = _r9k
    val slowMode get() = _slowMode
}

/**
 * Identifies text message sent by some user in some channel
 * @throws CorruptedMessageException when some property is not present
 * @throws WrongMessageTypeException thrown if command in [RawMessage] does not match given command
 */
class TextMessage(
    rawMessage: RawMessage
) : TwitchMessage(rawMessage, "PRIVMSG"), UserStateRelated {
    override val channel get() = _channel
        ?: throw CorruptedMessageException(rawMessage, channelMissingMessage)
    override val badgeInfo get() = _badgeInfo
    override val badges get() = _badges
    val bits get() = _bits
    override val color get() = _color
    override val username get() = _username
    override val displayName get() = _displayName
    val emotes get() = _emotes
    val messageId get() = _id
        ?: throw CorruptedMessageException(rawMessage, idMissingMessage)
    val timestamp get() = _timestamp
        ?: throw CorruptedMessageException(rawMessage, timestampMissingMessage)
    val userId get() = _userId
        ?: throw CorruptedMessageException(rawMessage, userIdMissingMessage)
    val message get() = _text
        ?: throw CorruptedMessageException(rawMessage, textMissingMessage)
}

/**
 * Received when user is purged, typically after a user is banned from chat or timed out
 * @throws CorruptedMessageException when some property is not present
 * @throws WrongMessageTypeException thrown if command in [RawMessage] does not match given command
 */
class ClearChatMessage(
    rawMessage: RawMessage
) : TwitchMessage(rawMessage, "CLEARCHAT") {
    val channel get() = _channel
        ?: throw CorruptedMessageException(rawMessage, channelMissingMessage)
    val banDuration get() = _banDuration
    val bannedUser get() = _text
    val permanent get() = banDuration == null
}

/**
 * Single message removal on a channel. This is triggered via /delete <target-msg-id> on IRC
 * @throws CorruptedMessageException when some property is not present
 * @throws WrongMessageTypeException thrown if command in [RawMessage] does not match given command
 */
class ClearMessage(
    rawMessage: RawMessage
) : TwitchMessage(rawMessage, "CLEARMSG") {
    val channel get() = _channel
        ?: throw CorruptedMessageException(rawMessage, channelMissingMessage)
    /** Name of user who wrote deleted message */
    val username get() = _login
        ?: throw CorruptedMessageException(rawMessage, loginMissingMessage)
    val message get() = _text
        ?: throw CorruptedMessageException(rawMessage, textMissingMessage)
    val targetMessageId get() = _targetMessageId
        ?: throw CorruptedMessageException(rawMessage, targetMessageIdMissingMessage)
}

// TODO Implement HOSTTARGET?

/**
 * General notices from the server
 * All possible noticeIds: [https://dev.twitch.tv/docs/irc/msg-id/#msg-id-tags-for-notice]
 * @throws CorruptedMessageException when some property is not present
 * @throws WrongMessageTypeException thrown if command in [RawMessage] does not match given command
 */
class NoticeMessage(
    rawMessage: RawMessage
) : TwitchMessage(rawMessage, "NOTICE") {
    val channel get() = _channel
        ?: throw CorruptedMessageException(rawMessage, channelMissingMessage)
    val message get() = _text
    val noticeId get() = _messageId
        ?: throw CorruptedMessageException(rawMessage, messageIdMissingMessage)
}

/**
 * Subscription, resubscription, gift subscription to a channel, Incoming raid, Channel ritual
 * Additional information: [https://dev.twitch.tv/docs/irc/tags/#usernotice-twitch-tags]
 * @throws CorruptedMessageException when some property is not present
 * @throws WrongMessageTypeException thrown if command in [RawMessage] does not match given command
 */
open class UserNoticeMessage(
    rawMessage: RawMessage
) : TwitchMessage(rawMessage, "USERNOTICE"), UserStateRelated {
    override val channel get() = _channel
        ?: throw CorruptedMessageException(rawMessage, channelMissingMessage)
    val message get() = _text
    override val badgeInfo get() = _badgeInfo
    override val badges get() = _badges
    override val color get() = _color
    override val displayName get() = _displayName
    val emotes get() = _emotes
    val messageId get() = _id
        ?: throw CorruptedMessageException(rawMessage, idMissingMessage)
    /** The name of the user who sent the notice */
    override val username get() = _login
        ?: throw CorruptedMessageException(rawMessage, loginMissingMessage)
    val noticeId get() = _messageId
        ?: throw CorruptedMessageException(rawMessage, messageIdMissingMessage)
    val roomId get() = _roomId
        ?: throw CorruptedMessageException(rawMessage, roomIdMissingMessage)
    val systemMessage get() = _systemMessage
        ?: throw CorruptedMessageException(rawMessage, systemMessageMissingMessage)
    val timestamp get() = _timestamp
        ?: throw CorruptedMessageException(rawMessage, timestampMissingMessage)
    val userId get() = _userId
}

/**
 * Identifies whisper sent by some user
 * @throws CorruptedMessageException when some property is not present
 * @throws WrongMessageTypeException thrown if command in [RawMessage] does not match given command
 */
class WhisperMessage(
    rawMessage: RawMessage
) : TwitchMessage(rawMessage, "WHISPER") {
    val badges get() = _badges
    val color get() = _color
    val displayName get() = _displayName
    val emotes get() = _emotes
    val messageId get() = rawMessage.tags["message-id"]
        ?: throw CorruptedMessageException(rawMessage, messageIdMissingMessage)
    val threadId get() = rawMessage.tags["thread-id"]?.toLongOrNull()
        ?: throw CorruptedMessageException(rawMessage, threadIdMissingMessage)
    val userId get() = _userId
        ?: throw CorruptedMessageException(rawMessage, userIdMissingMessage)
    val message get() = _text
        ?: throw CorruptedMessageException(rawMessage, messageMissingMessage)
    val username get() = _username
}

/**
 * Message that wasn't identified
 */
class UndefinedMessage(rawMessage: RawMessage) : TwitchMessage(rawMessage, rawMessage.commandName)

/**
 * thrown if command in [RawMessage] does not match given command
 */
class WrongMessageTypeException(msg: String) : Exception(msg)

/**
 * thrown when some property in [TwitchMessage] is not present
 */
class CorruptedMessageException(
    val msg: RawMessage,
    reason: String
) : Exception("${msg.commandName} message corrupted: $reason \n $msg")
