package com.example.stocktracker.data.local

import android.content.Context

object AppPrefs {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_API_KEY = "alpha_vantage_key"

    fun getApiKey(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_API_KEY, null)

    fun setApiKey(context: Context, key: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_API_KEY, key.trim()).apply()
    }
}
