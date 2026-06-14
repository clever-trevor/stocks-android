package com.example.stocktracker.widget

import android.content.Context

object WidgetPrefs {

    private const val PREFS_NAME = "widget_prefs"
    private const val KEY_SCHEME = "scheme_"
    private const val KEY_NAME = "name_"
    private const val DEFAULT_NAME = "My Portfolio"

    fun saveScheme(context: Context, appWidgetId: Int, schemeId: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_SCHEME + appWidgetId, schemeId).apply()
    }

    fun getSchemeId(context: Context, appWidgetId: Int): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_SCHEME + appWidgetId, 0)

    fun getScheme(context: Context, appWidgetId: Int): WidgetColorScheme =
        WidgetColorSchemes.getById(getSchemeId(context, appWidgetId))

    fun deleteScheme(context: Context, appWidgetId: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().remove(KEY_SCHEME + appWidgetId).apply()
    }

    fun saveName(context: Context, appWidgetId: Int, name: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_NAME + appWidgetId, name).apply()
    }

    fun getName(context: Context, appWidgetId: Int): String =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_NAME + appWidgetId, DEFAULT_NAME) ?: DEFAULT_NAME

    fun deleteName(context: Context, appWidgetId: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().remove(KEY_NAME + appWidgetId).apply()
    }
}
