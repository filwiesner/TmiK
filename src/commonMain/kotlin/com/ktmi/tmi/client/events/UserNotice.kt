@file:Suppress("unused")

package com.ktmi.tmi.client.events

import com.ktmi.tmi.dsl.builder.*
import com.ktmi.tmi.events.onTwitchMessage
import com.ktmi.tmi.messages.*

/** Registers a listener for [UserNoticeMessage] with specified notice id */
inline fun TwitchScope.onUserNoticeById(id: String, crossinline action: suspend (UserNoticeMessage) -> Unit) =
    onTwitchMessage<UserNoticeMessage> {
        if (it.noticeId == id) action(it)
    }

/** Registers a listener for [SubMessage] for when user subscribes */
inline fun GlobalContextScope.onSub(crossinline action: suspend UserContext<SubMessage>.() -> Unit) =
    onUserNoticeById("sub") { mess ->
        UserContext(mess.asSubMessage, mess.username, mess.channel).action()
    }
/** Registers a listener for [SubMessage] for when user subscribes */
inline fun ChannelContextScope.onSub(crossinline action: suspend UserContext<SubMessage>.() -> Unit) =
    onUserNoticeById("sub") { mess ->
        UserContext(mess.asSubMessage, mess.username, mess.channel).action()
    }
/** Registers a listener for [SubMessage] for when user subscribes */
inline fun UserContextScope.onSub(crossinline action: suspend UserContext<SubMessage>.() -> Unit) =
    onUserNoticeById("sub") { mess ->
        UserContext(mess.asSubMessage, mess.username, mess.channel).action()
    }
/** Registers a listener for [SubMessage] for when user subscribes */
inline fun UserStateContextScope.onSub(crossinline action: suspend UserContext<SubMessage>.() -> Unit) =
    onUserNoticeById("sub") { mess ->
        UserContext(mess.asSubMessage, mess.username, mess.channel).action()
    }


/** Registers a listener for [SubMessage] for when user subscribes */
inline fun GlobalContextScope.onResub(crossinline action: suspend UserContext<SubMessage>.() -> Unit) =
    onUserNoticeById("resub") { mess ->
        UserContext(mess.asSubMessage, mess.username, mess.channel).action()
    }
/** Registers a listener for [SubMessage] for when user subscribes */
inline fun ChannelContextScope.onResub(crossinline action: suspend UserContext<SubMessage>.() -> Unit) =
    onUserNoticeById("resub") { mess ->
        UserContext(mess.asSubMessage, mess.username, mess.channel).action()
    }
/** Registers a listener for [SubMessage] for when user subscribes */
inline fun UserContextScope.onResub(crossinline action: suspend UserContext<SubMessage>.() -> Unit) =
    onUserNoticeById("resub") { mess ->
        UserContext(mess.asSubMessage, mess.username, mess.channel).action()
    }
/** Registers a listener for [SubMessage] for when user subscribes */
inline fun UserStateContextScope.onResub(crossinline action: suspend UserContext<SubMessage>.() -> Unit) =
    onUserNoticeById("resub") { mess ->
        UserContext(mess.asSubMessage, mess.username, mess.channel).action()
    }

/** Registers a listener for [SubGiftMessage] for when user is gifted subscription */
inline fun GlobalContextScope.onSubGift(crossinline action: suspend UserContext<SubGiftMessage>.() -> Unit) =
    onUserNoticeById("subgift") { mess ->
        UserContext(mess.asSubGift, mess.username, mess.channel).action()
    }
/** Registers a listener for [SubGiftMessage] for when user is gifted subscription */
inline fun ChannelContextScope.onSubGift(crossinline action: suspend UserContext<SubGiftMessage>.() -> Unit) =
    onUserNoticeById("subgift") { mess ->
        UserContext(mess.asSubGift, mess.username, mess.channel).action()
    }
/** Registers a listener for [SubGiftMessage] for when user is gifted subscription */
inline fun UserContextScope.onSubGift(crossinline action: suspend UserContext<SubGiftMessage>.() -> Unit) =
    onUserNoticeById("subgift") { mess ->
        UserContext(mess.asSubGift, mess.username, mess.channel).action()
    }
/** Registers a listener for [SubGiftMessage] for when user is gifted subscription */
inline fun UserStateContextScope.onSubGift(crossinline action: suspend UserContext<SubGiftMessage>.() -> Unit) =
    onUserNoticeById("subgift") { mess ->
        UserContext(mess.asSubGift, mess.username, mess.channel).action()
    }

/** Registers a listener for [SubGiftMessage] for when user is gifted subscription from anonymous user*/
inline fun GlobalContextScope.onAnonymousSubGift(crossinline action: suspend UserContext<SubGiftMessage>.() -> Unit) =
    onUserNoticeById("anonsubgift") { mess ->
        UserContext(mess.asSubGift, mess.username, mess.channel).action()
    }
/** Registers a listener for [SubGiftMessage] for when user is gifted subscription from anonymous user*/
inline fun ChannelContextScope.onAnonymousSubGift(crossinline action: suspend UserContext<SubGiftMessage>.() -> Unit) =
    onUserNoticeById("anonsubgift") { mess ->
        UserContext(mess.asSubGift, mess.username, mess.channel).action()
    }
/** Registers a listener for [SubGiftMessage] for when user is gifted subscription from anonymous user*/
inline fun UserContextScope.onAnonymousSubGift(crossinline action: suspend UserContext<SubGiftMessage>.() -> Unit) =
    onUserNoticeById("anonsubgift") { mess ->
        UserContext(mess.asSubGift, mess.username, mess.channel).action()
    }
/** Registers a listener for [SubGiftMessage] for when user is gifted subscription from anonymous user*/
inline fun UserStateContextScope.onAnonymousSubGift(crossinline action: suspend UserContext<SubGiftMessage>.() -> Unit) =
    onUserNoticeById("anonsubgift") { mess ->
        UserContext(mess.asSubGift, mess.username, mess.channel).action()
    }

/** Registers a listener for [UserNoticeMessage] for when user is gifted subscription from mystery user*/
inline fun GlobalContextScope.onMysterySubGift(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onUserNoticeById("submysterygift") { mess ->
        UserContext(mess, mess.username, mess.channel).action()
    }
/** Registers a listener for [UserNoticeMessage] for when user is gifted subscription from mystery user*/
inline fun ChannelContextScope.onMysterySubGift(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onUserNoticeById("submysterygift") { mess ->
        UserContext(mess, mess.username, mess.channel).action()
    }
/** Registers a listener for [UserNoticeMessage] for when user is gifted subscription from mystery user*/
inline fun UserContextScope.onMysterySubGift(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onUserNoticeById("submysterygift") { mess ->
        UserContext(mess, mess.username, mess.channel).action()
    }
/** Registers a listener for [UserNoticeMessage] for when user is gifted subscription from mystery user*/
inline fun UserStateContextScope.onMysterySubGift(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onUserNoticeById("submysterygift") { mess ->
        UserContext(mess, mess.username, mess.channel).action()
    }

/** Registers a listener for [UpgradeGiftMessage] for when user continues his's gifted sub*/
inline fun GlobalContextScope.onUpgradeGift(crossinline action: suspend UserContext<UpgradeGiftMessage>.() -> Unit) =
    onUserNoticeById("giftpaidupgrade") { mess ->
        UserContext(mess.asUpgradeGift, mess.username, mess.channel).action()
    }
/** Registers a listener for [UpgradeGiftMessage] for when user continues his's gifted sub*/
inline fun ChannelContextScope.onUpgradeGift(crossinline action: suspend UserContext<UpgradeGiftMessage>.() -> Unit) =
    onUserNoticeById("giftpaidupgrade") { mess ->
        UserContext(mess.asUpgradeGift, mess.username, mess.channel).action()
    }
/** Registers a listener for [UpgradeGiftMessage] for when user continues his's gifted sub*/
inline fun UserContextScope.onUpgradeGift(crossinline action: suspend UserContext<UpgradeGiftMessage>.() -> Unit) =
    onUserNoticeById("giftpaidupgrade") { mess ->
        UserContext(mess.asUpgradeGift, mess.username, mess.channel).action()
    }
/** Registers a listener for [UpgradeGiftMessage] for when user continues his's gifted sub*/
inline fun UserStateContextScope.onUpgradeGift(crossinline action: suspend UserContext<UpgradeGiftMessage>.() -> Unit) =
    onUserNoticeById("giftpaidupgrade") { mess ->
        UserContext(mess.asUpgradeGift, mess.username, mess.channel).action()
    }

/** Registers a listener for [UpgradeGiftMessage] for when user continues his's anonymous gifted sub */
inline fun GlobalContextScope.onAnonymousUpgradeGift(crossinline action: suspend UserContext<UpgradeGiftMessage>.() -> Unit) =
    onUserNoticeById("anongiftpaidupgrade") { mess ->
        UserContext(mess.asUpgradeGift, mess.username, mess.channel).action()
    }
/** Registers a listener for [UpgradeGiftMessage] for when user continues his's anonymous gifted sub */
inline fun ChannelContextScope.onAnonymousUpgradeGift(crossinline action: suspend UserContext<UpgradeGiftMessage>.() -> Unit) =
    onUserNoticeById("anongiftpaidupgrade") { mess ->
        UserContext(mess.asUpgradeGift, mess.username, mess.channel).action()
    }
/** Registers a listener for [UpgradeGiftMessage] for when user continues his's anonymous gifted sub */
inline fun UserContextScope.onAnonymousUpgradeGift(crossinline action: suspend UserContext<UpgradeGiftMessage>.() -> Unit) =
    onUserNoticeById("anongiftpaidupgrade") { mess ->
        UserContext(mess.asUpgradeGift, mess.username, mess.channel).action()
    }
/** Registers a listener for [UpgradeGiftMessage] for when user continues his's anonymous gifted sub */
inline fun UserStateContextScope.onAnonymousUpgradeGift(crossinline action: suspend UserContext<UpgradeGiftMessage>.() -> Unit) =
    onUserNoticeById("anongiftpaidupgrade") { mess ->
        UserContext(mess.asUpgradeGift, mess.username, mess.channel).action()
    }

/** Registers a listener for [UserNoticeMessage] with id 'rewardgift' */
inline fun GlobalContextScope.onRewardGift(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onUserNoticeById("rewardgift") { mess ->
        UserContext(mess, mess.username, mess.channel).action()
    }
/** Registers a listener for [UserNoticeMessage] with id 'rewardgift' */
inline fun ChannelContextScope.onRewardGift(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onUserNoticeById("rewardgift") { mess ->
        UserContext(mess, mess.username, mess.channel).action()
    }
/** Registers a listener for [UserNoticeMessage] with id 'rewardgift' */
inline fun UserContextScope.onRewardGift(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onUserNoticeById("rewardgift") { mess ->
        UserContext(mess, mess.username, mess.channel).action()
    }
/** Registers a listener for [UserNoticeMessage] with id 'rewardgift' */
inline fun UserStateContextScope.onRewardGift(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onUserNoticeById("rewardgift") { mess ->
        UserContext(mess, mess.username, mess.channel).action()
    }

/** Registers a listener for [RaidMessage] for when channel is raided */
inline fun GlobalContextScope.onRaid(crossinline action: suspend UserContext<RaidMessage>.() -> Unit) =
    onUserNoticeById("raid") { mess ->
        UserContext(mess.asRaid, mess.username, mess.channel).action()
    }
/** Registers a listener for [RaidMessage] for when channel is raided */
inline fun ChannelContextScope.onRaid(crossinline action: suspend UserContext<RaidMessage>.() -> Unit) =
    onUserNoticeById("raid") { mess ->
        UserContext(mess.asRaid, mess.username, mess.channel).action()
    }
/** Registers a listener for [RaidMessage] for when channel is raided */
inline fun UserContextScope.onRaid(crossinline action: suspend UserContext<RaidMessage>.() -> Unit) =
    onUserNoticeById("raid") { mess ->
        UserContext(mess.asRaid, mess.username, mess.channel).action()
    }
/** Registers a listener for [RaidMessage] for when channel is raided */
inline fun UserStateContextScope.onRaid(crossinline action: suspend UserContext<RaidMessage>.() -> Unit) =
    onUserNoticeById("raid") { mess ->
        UserContext(mess.asRaid, mess.username, mess.channel).action()
    }

/** Registers a listener for [RaidMessage] for when channel stops raiding */
inline fun GlobalContextScope.onRaidEnded(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onUserNoticeById("unraid") { mess ->
        UserContext(mess, mess.username, mess.channel).action()
    }
/** Registers a listener for [RaidMessage] for when channel stops raiding */
inline fun ChannelContextScope.onRaidEnded(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onUserNoticeById("unraid") { mess ->
        UserContext(mess, mess.username, mess.channel).action()
    }
/** Registers a listener for [RaidMessage] for when channel stops raiding */
inline fun UserContextScope.onRaidEnded(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onUserNoticeById("unraid") { mess ->
        UserContext(mess, mess.username, mess.channel).action()
    }
/** Registers a listener for [RaidMessage] for when channel stops raiding */
inline fun UserStateContextScope.onRaidEnded(crossinline action: suspend UserContext<UserNoticeMessage>.() -> Unit) =
    onUserNoticeById("unraid") { mess ->
        UserContext(mess, mess.username, mess.channel).action()
    }

/** Registers a listener for [RaidMessage] for when user sends ritual message */
inline fun GlobalContextScope.onRitual(crossinline action: suspend UserContext<RitualMessage>.() -> Unit) =
    onUserNoticeById("ritual") { mess ->
        UserContext(mess.asRitual, mess.username, mess.channel).action()
    }
/** Registers a listener for [RaidMessage] for when user sends ritual message */
inline fun ChannelContextScope.onRitual(crossinline action: suspend UserContext<RitualMessage>.() -> Unit) =
    onUserNoticeById("ritual") { mess ->
        UserContext(mess.asRitual, mess.username, mess.channel).action()
    }
/** Registers a listener for [RaidMessage] for when user sends ritual message */
inline fun UserContextScope.onRitual(crossinline action: suspend UserContext<RitualMessage>.() -> Unit) =
    onUserNoticeById("ritual") { mess ->
        UserContext(mess.asRitual, mess.username, mess.channel).action()
    }
/** Registers a listener for [RaidMessage] for when user sends ritual message */
inline fun UserStateContextScope.onRitual(crossinline action: suspend UserContext<RitualMessage>.() -> Unit) =
    onUserNoticeById("ritual") { mess ->
        UserContext(mess.asRitual, mess.username, mess.channel).action()
    }

/** Registers a listener for [RaidMessage] for when user reaches new bit badge */
inline fun GlobalContextScope.onBitsBadgeTier(crossinline action: suspend UserContext<BitsBadgeMessage>.() -> Unit) =
    onUserNoticeById("bitsbadgetier") { mess ->
        UserContext(mess.asBitsBadge, mess.username, mess.channel).action()
    }
/** Registers a listener for [RaidMessage] for when user reaches new bit badge */
inline fun ChannelContextScope.onBitsBadgeTier(crossinline action: suspend UserContext<BitsBadgeMessage>.() -> Unit) =
    onUserNoticeById("bitsbadgetier") { mess ->
        UserContext(mess.asBitsBadge, mess.username, mess.channel).action()
    }
/** Registers a listener for [RaidMessage] for when user reaches new bit badge */
inline fun UserContextScope.onBitsBadgeTier(crossinline action: suspend UserContext<BitsBadgeMessage>.() -> Unit) =
    onUserNoticeById("bitsbadgetier") { mess ->
        UserContext(mess.asBitsBadge, mess.username, mess.channel).action()
    }
/** Registers a listener for [RaidMessage] for when user reaches new bit badge */
inline fun UserStateContextScope.onBitsBadgeTier(crossinline action: suspend UserContext<BitsBadgeMessage>.() -> Unit) =
    onUserNoticeById("bitsbadgetier") { mess ->
        UserContext(mess.asBitsBadge, mess.username, mess.channel).action()
    }