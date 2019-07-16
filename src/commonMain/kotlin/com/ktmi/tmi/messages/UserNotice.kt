package com.ktmi.tmi.messages

/** Subscription plan */
enum class SubPlan { Prime, Tier1, Tier2, Tier3;
    companion object {
        fun parse(plan: String): SubPlan = when(plan) {
            "Prime" -> Prime
            "1000" -> Tier1
            "2000" -> Tier2
            "3000" -> Tier3
            else -> throw IllegalStateException("Unknown subscription plan")
        }
    }
}

/** When user subscribes to a channel */
class SubMessage(message: UserNoticeMessage) : UserNoticeMessage(message.rawMessage) {
    val cumulativeMonths: Int get() = rawMessage.tags["msg-param-cumulative-months"]?.toInt()
        ?: throw CorruptedMessageException(rawMessage, "cumulative months not found or not a number")
    val streakShared: Boolean get() = rawMessage.tags["msg-param-should-share-streak"] == "1"
    val streak: Int? get() = rawMessage.tags["msg-param-streak-months"]?.toInt()
    val subPlan: SubPlan get() = SubPlan.parse(rawMessage.tags["msg-param-sub-plan"] ?: "")
    val subPlanName: String get() = rawMessage.tags["msg-param-sub-plan-name"]
        ?: throw CorruptedMessageException(rawMessage, "sub plan name not found")
}
val UserNoticeMessage.asSubMessage get() = SubMessage(this)

/** When user gifts a subscription */
class SubGiftMessage(message: UserNoticeMessage) : UserNoticeMessage(message.rawMessage) {
    val cumulativeMonths: Int get() = rawMessage.tags["msg-param-months"]?.toInt()
        ?: throw CorruptedMessageException(rawMessage, "months not found or not a number")
    val recipientUsername: String get() = rawMessage.tags["msg-param-recipient-user-name"]
        ?: throw CorruptedMessageException(rawMessage, "username not found")
    val recipientDisplayName: String get() = rawMessage.tags["msg-param-recipient-display-name"]
        ?: throw CorruptedMessageException(rawMessage, "display name not found")
    val recipientId: String get() = rawMessage.tags["msg-param-recipient-id"]
        ?: throw CorruptedMessageException(rawMessage, "recipient id not found")
    val subPlan: SubPlan get() = SubPlan.parse(rawMessage.tags["msg-param-sub-plan"] ?: "")
    val subPlanName: String get() = rawMessage.tags["msg-param-sub-plan-name"]
        ?: throw CorruptedMessageException(rawMessage, "sub plan name not found")
}
val UserNoticeMessage.asSubGift get() = SubGiftMessage(this)

/** When user upgrades gifted subscription */
class UpgradeGiftMessage(message: UserNoticeMessage) : UserNoticeMessage(message.rawMessage) {
    val giftTotal: Int get() = rawMessage.tags["msg-param-promo-gift-total"]?.toInt()
        ?: throw CorruptedMessageException(rawMessage, "months not found or not a number")
    val promoName: String? get() = rawMessage.tags["msg-param-promo-name"]
    val senderUsername: String? get() = rawMessage.tags["msg-param-sender-login"]
    val senderDisplayName: String? get() = rawMessage.tags["msg-param-sender-name"]
}
val UserNoticeMessage.asUpgradeGift get() = UpgradeGiftMessage(this)

/** When channel is raided by other channel */
class RaidMessage(message: UserNoticeMessage) : UserNoticeMessage(message.rawMessage) {
    val sourceUsername: String get() = rawMessage.tags["msg-param-login"]
        ?: throw CorruptedMessageException(rawMessage, "username (login) not found")
    val sourceDisplayName: String get() = rawMessage.tags["msg-param-displayName"]
        ?: throw CorruptedMessageException(rawMessage, "display name not found")
    val viewerCount: Int get() = rawMessage.tags["msg-param-viewerCount"]?.toInt()
        ?: throw CorruptedMessageException(rawMessage, "viewer count not found or not a number")
}
val UserNoticeMessage.asRaid get() = RaidMessage(this)

/** When user sends a 'ritual message' (now only first message) */
class RitualMessage(message: UserNoticeMessage) : UserNoticeMessage(message.rawMessage) {
    val name: String get() = rawMessage.tags["msg-param-ritual-name"]
        ?: throw CorruptedMessageException(rawMessage, "ritual name not found")
}
val UserNoticeMessage.asRitual get() = RitualMessage(this)

/** When user passes next bit badge rank */
class BitsBadgeMessage(message: UserNoticeMessage) : UserNoticeMessage(message.rawMessage) {
    val threshold: Int get() = rawMessage.tags["msg-param-threshold"]?.toInt()
        ?: throw CorruptedMessageException(rawMessage, "threshold not found or not a number")
}
val UserNoticeMessage.asBitsBadge get() = BitsBadgeMessage(this)