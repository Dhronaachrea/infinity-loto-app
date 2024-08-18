package com.skilrock.infinitylotoapp.utility

import com.skilrock.infinitylotoapp.BuildConfig

const val LOGIN_TOKEN               = "infinity"
const val DEVICE_TYPE               = "MOBILE"
const val LOGIN_DEVICE              = "ANDROID_APP_CASH"
const val REQUEST_IP                = "127.0.0.1"
const val TAG_DGE                   = "DGE"
const val TAG_SBS                   = "SBS"
const val TAG_IGE                   = "IGE"
const val TAG_SLE                   = "SLE"
const val TAG_BET_GAMES             = "BETGAMES"
const val TAG_VIRTUAL_GAMES         = "VIRTUAL_GAMES"
const val MOBILE_PLATFORM           = 1
const val GAME_ID_SABANZURI         = "powerball"
const val GAME_ID_LUCKY_TWELVE      = "twelvebytwentyfour"
const val GAME_CODE_LUCKY_TWELVE    = "1"
const val GAME_CODE_SABANZURI       = "2"
const val GAME_CODE_MONEY_BEE       = "101"
const val GAME_CODE_TREASURE_HUNT   = "105"
const val GAME_CODE_TIC_TAC_TOE     = "103"
const val GAME_CODE_BIG_FIVE        = "110"
const val GAME_CODE_FUN_TIME        = "111"
const val GAME_TYPE_DGE             = "dge"
const val GAME_TYPE_IGE             = "ige"
const val GAME_TYPE_SBS             = "sbs"
const val GAME_TYPE_SLE             = "sle"
const val GAME_TYPE_VIRTUAL_GAMES   = "sbs"
const val COUNTRY_CODE_KENYA        = "+254"
const val CLIENT_SABANZURI          = "SABANZURI"
const val THEME_DARK                = "ThemeDark"
const val THEME_LIGHT               = "ThemeLight"
const val CONTENT_TYPE              = "Content-Type: application/json"
const val MERCHANT_CODE             = "merchantCode: infiniti"
const val LOGIN_URL                 = "Weaver/service/rest/playerLogin"
const val VERSION_URL               = "Weaver/service/rest/versionControl"
const val FORGOT_PASSWORD_URL       = "Weaver/service/rest/forgotPassword"
const val RESET_PASSWORD_URL        = "Weaver/service/rest/resetPassword"
const val REGISTRATION_URL          = "Weaver/service/rest/v1/playerRegistration"
const val REGISTRATION_OTP_URL      = "Weaver/service/rest/registrationOTP"
const val MY_TRANSACTIONS_URL       = "Weaver/service/rest/transactionDetails"
const val MY_TICKETS_URL            = "Weaver/service/rest/ticketDetails"
const val CHANGE_PASSWORD_URL       = "Weaver/service/rest/changePassword"
const val UPDATE_PROFILE_URL        = "Weaver/service/rest/updatePlayerProfile"
const val UPDATE_BALANCE_URL        = "Weaver/service/rest/getBalance"
const val FUND_TRANSFER_URL         = "Weaver/service/rest/ola/txn/FundsTransferFromWallet"
const val LOGOUT_URL                = "Weaver/service/rest/playerLogout"
const val UPLOAD_AVATAR_URL         = "Weaver/service/rest/uploadAvatar"
const val PLAYER_INBOX_URL          = "Weaver/service/rest/playerInbox"
const val PLAYER_INBOX_ACTIVITY_URL = "Weaver/service/rest/inboxActivity"
const val EMAIL_OTP_URL             = "Weaver/service/rest/sendVerificationCode"
const val EMAIL_VERIFY_URL          = "Weaver/service/rest/verifyPlayer"

const val JACKPOT_TIMER_URL         = "${BuildConfig.DRAWER_URL}index.php/component/weaver/?task=api.getGamesInfo"
const val HOW_PLAY_SABANZURI_URL    = "${BuildConfig.DRAWER_URL}mobile-pages/how-to-play-sabanzuri-lotto"
const val HOW_PLAY_LUCKY_TWELVE_URL = "${BuildConfig.DRAWER_URL}mobile-pages/how-to-play-lucky-twelve"
const val HOW_PLAY_TREASURE_URL     = "${BuildConfig.DRAWER_URL}mobile-pages/treasure-hunt"
const val HOW_PLAY_TIC_TAC_TOE_URL  = "${BuildConfig.DRAWER_URL}mobile-pages/tic-tac-toe"
const val HOW_PLAY_MONEY_BEE_URL    = "${BuildConfig.DRAWER_URL}mobile-pages/money-bee"
const val HOW_PLAY_BIG_FIVE_URL     = "${BuildConfig.DRAWER_URL}mobile-pages/big-5"
const val HOW_PLAY_FUN_TIME_URL     = "${BuildConfig.DRAWER_URL}mobile-pages/fun-time"
const val FAQ_URL                   = "${BuildConfig.DRAWER_URL}mobile-pages/mobile-faq"
const val RESPONSIBLE_GAMING_URL    = "${BuildConfig.DRAWER_URL}mobile-pages/mobile-responsible-gaming"
const val TNC_URL                   = "${BuildConfig.DRAWER_URL}mobile-pages/mobile-terms-conditions"
const val PRIVACY_POLICY_URL        = "${BuildConfig.DRAWER_URL}mobile-pages/mobile-privacy-policy"
const val CONTACT_US_URL            = "${BuildConfig.DRAWER_URL}mobile-pages/mobile-contact-us"

const val DEPOSIT_UI_URL            = "${BuildConfig.CASHIER_URL}player/payment/options"
const val PENDING_TRANSACTIONS_URL  = "${BuildConfig.CASHIER_URL}player/payment/transactionDetails"
const val ADD_NEW_ACCOUNT_URL       = "${BuildConfig.CASHIER_URL}player/payment/accounts/add"
const val DEPOSIT_REQUEST_URL       = "${BuildConfig.CASHIER_URL}player/payment/depositRequest"
const val WITHDRAWAL_REQUEST_URL    = "${BuildConfig.CASHIER_URL}player/payment/withdrawalRequest"

const val SESSION_EXPIRE_CODE       = 203
const val PARSE_TYPE                = "text/plain"
const val DATE_TYPE_DESIRED         = "dd-MM-yyyy"
const val DATE_TIME_TYPE_DESIRED    = "dd/MM/yyyy HH:mm:ss"

const val NOTIFICATION_CHANNEL_TRANSACTIONAL    = "TRANSACTIONAL"
const val NOTIFICATION_CHANNEL_ALERT            = "ALERT"
const val NOTIFICATION_CHANNEL_PROMOTIONAL      = "PROMOTIONAL"