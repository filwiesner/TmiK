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
    val channel: String?
    val badgeInfo: String?
    val badges: Map<String, Int>?
    val color: String?
    val username: String? get() = displayName?.toLowerCase()
    val displayName: String?
    val isMod: Boolean
}



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
        ?: throw CorruptedMessageException(rawMessage, "emote sets are not available")
    val userId get() = _userId
        ?: throw CorruptedMessageException(rawMessage, "user id is not available")
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
        ?: throw CorruptedMessageException(rawMessage, "channel not available")
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
        ?: throw CorruptedMessageException(rawMessage, "channel not available")
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
        ?: throw CorruptedMessageException(rawMessage, "channel not available")
    override val badgeInfo get() = _badgeInfo
    override val badges get() = _badges
    override val color get() = _color
    override val displayName get() = _displayName
    val emoteSets get() = _emoteSets
        ?: throw CorruptedMessageException(rawMessage, "emote sets are not available")
    override val isMod get() = _isMod
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
        ?:throw CorruptedMessageException(rawMessage, "channel is not available")
    val roomId get() = _roomId
        ?:throw CorruptedMessageException(rawMessage, "room id is not available")
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
    ?: throw CorruptedMessageException(rawMessage, "channel not available")
    override val badgeInfo get() = _badgeInfo
    override val badges get() = _badges
    val bits get() = _bits
    override val color get() = _color
    override val username get() = _username
    override val displayName get() = _displayName
    val emotes get() = _emotes
    val messageId get() = _id
        ?: throw CorruptedMessageException(rawMessage, "id is not available")
    override val isMod get() = _isMod
    val timestamp get() = _timestamp
        ?: throw CorruptedMessageException(rawMessage, "timestamp is not available")
    val userId get() = _userId
        ?: throw CorruptedMessageException(rawMessage, "user id is not available")
    val message get() = _text
        ?: throw CorruptedMessageException(rawMessage, "message is not available")
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
        ?: throw CorruptedMessageException(rawMessage, "channel not available")
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
        ?: throw CorruptedMessageException(rawMessage, "channel not available")
    val username get() = _login
        ?: throw CorruptedMessageException(rawMessage, "user not available")
    val message get() = _text
        ?: throw CorruptedMessageException(rawMessage, "message not available")
    val targetMessageId get() = _targetMessageId
        ?: throw CorruptedMessageException(rawMessage, "target message id not available")
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
        ?: throw CorruptedMessageException(rawMessage, "channel not available")
    val message get() = _text
    val noticeId get() = _messageId
        ?: throw CorruptedMessageException(rawMessage, "message id not available")
}

/**
 * Subscription, resubscription, gift subscription to a channel, Incoming raid, Channel ritual
 * Additional information: [https://dev.twitch.tv/docs/irc/tags/#usernotice-twitch-tags]
 * @throws CorruptedMessageException when some property is not present
 * @throws WrongMessageTypeException thrown if command in [RawMessage] does not match given command
 */
class UserNoticeMessage(
    rawMessage: RawMessage
) : TwitchMessage(rawMessage, "USERNOTICE"), UserStateRelated {
    override val channel get() = _channel
        ?: throw CorruptedMessageException(rawMessage, "channel not available")
    val message get() = _text
    override val badgeInfo get() = _badgeInfo
    override val badges get() = _badges
    override val color get() = _color
    override val displayName get() = _displayName
    val emotes get() = _emotes
    val messageId get() = _id
        ?: throw CorruptedMessageException(rawMessage, "id not available")
    override val username get() = _login
        ?: throw CorruptedMessageException(rawMessage, "login not available")
    override val isMod get() = _isMod
    val noticeId get() = _messageId
        ?: throw CorruptedMessageException(rawMessage, "message id (noticeId) not available")
    val roomId get() = _roomId
        ?: throw CorruptedMessageException(rawMessage, "room id not available")
    val systemMessage get() = _systemMessage
        ?: throw CorruptedMessageException(rawMessage, "system message not available")
    val timestamp get() = _timestamp
        ?: throw CorruptedMessageException(rawMessage, "timestamp not available")
    val userId get() = _userId
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
) : Exception("${msg.commandName} message corrupted: $reason")