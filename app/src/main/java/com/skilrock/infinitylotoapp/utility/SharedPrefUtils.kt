package com.skilrock.infinitylotoapp.utility

import android.content.Context

object SharedPrefUtils {

    private const val USER_PREF     = "InfinityLoto"
    private const val GAME_ENGINE   = "GameEngine"
    private const val APP_THEME     = "AppTheme"
    private const val FCM_TOKEN     = "FcmToken"
    const val PLAYER_TOKEN          = "playerToken"
    const val PLAYER_USER_NAME      = "playerUserName"
    const val PLAYER_ID             = "playerId"
    const val PLAYER_MOBILE_NUMBER  = "playerMobileNo"
    const val PLAYER_FIRST_NAME     = "playerFirstName"
    const val PLAYER_LAST_NAME      = "playerLastName"
    const val PLAYER_DATA           = "playerData"
    const val GAME_DGE              = "gameDge"
    const val GAME_SBS              = "gameSbs"
    const val GAME_IGE              = "gameIge"
    const val GAME_SLE              = "gameSle"
    const val GAME_BET_GAMES        = "gameBetGames"
    const val GAME_VIRTUAL_GAMES    = "gameVirtualGames"
    private const val THEME         = "theme"

    fun getString(context: Context, key: String): String {
        return context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).getString(key, "") ?: ""
    }

    fun setString(context: Context, key: String, value: String) {
        context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).edit().putString(key, value).apply()
    }

    fun setInt(context: Context, key: String, value: Int) {
        context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).edit().putInt(key, value).apply()
    }

    fun clearAppSharedPref(context: Context) {
        context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun setGameData(context: Context, key: String, value: String) {
        context.getSharedPreferences(GAME_ENGINE, Context.MODE_PRIVATE).edit().putString(key, value).apply()
    }

    fun getGameData(context: Context, key: String): String {
        return context.getSharedPreferences(GAME_ENGINE, Context.MODE_PRIVATE).getString(key, "") ?: ""
    }

    fun clearGameEngineSharedPref(context: Context) {
        context.getSharedPreferences(GAME_ENGINE, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun setAppTheme(context: Context, value: String) {
        context.getSharedPreferences(APP_THEME, Context.MODE_PRIVATE).edit().putString(THEME, value).apply()
    }

    fun getAppTheme(context: Context): String {
        return context.getSharedPreferences(APP_THEME, Context.MODE_PRIVATE).getString(THEME, THEME_DARK) ?: THEME_DARK
    }

    fun setFcmToken(context: Context, value: String) {
        context.getSharedPreferences(FCM_TOKEN, Context.MODE_PRIVATE).edit().putString("token", value).apply()
    }

    fun getFcmToken(context: Context): String {
        return context.getSharedPreferences(FCM_TOKEN, Context.MODE_PRIVATE).getString("token", "") ?: ""
    }

}