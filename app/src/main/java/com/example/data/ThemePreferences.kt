package com.example.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Local persistent storage for user theme preferences (Dark / Light mode).
 * Preserves user choice across app restarts and avoids theme flashing on startup.
 */
class ThemePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "orami_theme_preferences"
        private const val KEY_DARK_MODE = "is_dark_mode"
        private const val KEY_IS_USER_SET = "is_user_theme_set"

        @Volatile
        private var instance: ThemePreferences? = null

        fun getInstance(context: Context): ThemePreferences {
            return instance ?: synchronized(this) {
                instance ?: ThemePreferences(context).also { instance = it }
            }
        }
    }

    /**
     * Checks if the user has explicitly selected a theme preference.
     */
    fun isUserThemeSet(): Boolean = prefs.getBoolean(KEY_IS_USER_SET, false)

    /**
     * Returns true if dark mode is enabled, defaulting to system theme if unset.
     */
    fun isDarkMode(systemDefault: Boolean): Boolean {
        return if (prefs.contains(KEY_DARK_MODE)) {
            prefs.getBoolean(KEY_DARK_MODE, systemDefault)
        } else {
            systemDefault
        }
    }

    /**
     * Saves user's theme selection into persistent storage.
     */
    fun setDarkMode(isDark: Boolean) {
        prefs.edit()
            .putBoolean(KEY_DARK_MODE, isDark)
            .putBoolean(KEY_IS_USER_SET, true)
            .apply()
    }
}
