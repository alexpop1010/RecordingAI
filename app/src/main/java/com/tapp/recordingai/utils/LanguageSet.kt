package com.tapp.recordingai.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LanguageSet {

    private const val PREFS = "language_prefs"
    private const val KEY_MODE = "language_mode"
    private const val KEY_IS_ENGLISH_LEGACY = "is_english"

    const val MODE_SYSTEM = "system"
    const val MODE_RUSSIAN = "ru"
    const val MODE_ENGLISH = "en"

    fun getMode(context: Context): String {
        val prefs = prefs(context)
        migrateLegacyIfNeeded(prefs)
        return prefs.getString(KEY_MODE, MODE_SYSTEM) ?: MODE_SYSTEM
    }

    fun setMode(context: Context, mode: String) {
        prefs(context).edit().putString(KEY_MODE, mode).apply()
        applyAppLocales(mode)
    }

    fun applySavedLanguage(context: Context) {
        applyAppLocales(getMode(context))
    }

    private fun applyAppLocales(mode: String) {
        val locales = when (mode) {
            MODE_RUSSIAN -> LocaleListCompat.forLanguageTags("ru")
            MODE_ENGLISH -> LocaleListCompat.forLanguageTags("en")
            else -> LocaleListCompat.getEmptyLocaleList()
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun migrateLegacyIfNeeded(prefs: SharedPreferences) {
        if (prefs.contains(KEY_MODE)) return
        if (!prefs.contains(KEY_IS_ENGLISH_LEGACY)) {
            prefs.edit().putString(KEY_MODE, MODE_SYSTEM).apply()
            return
        }
        val legacyEnglish = prefs.getBoolean(KEY_IS_ENGLISH_LEGACY, false)
        val mode = if (legacyEnglish) MODE_ENGLISH else MODE_SYSTEM
        prefs.edit()
            .putString(KEY_MODE, mode)
            .remove(KEY_IS_ENGLISH_LEGACY)
            .apply()
    }

    /**
     * BCP-47 для [android.speech.RecognizerIntent.EXTRA_LANGUAGE] — совпадает с режимом UI
     * (русский / английский) или с языком системы в режиме «как в устройстве».
     */
    fun getSpeechLanguageTag(context: Context): String {
        return when (getMode(context)) {
            MODE_RUSSIAN -> "ru-RU"
            MODE_ENGLISH -> "en-US"
            else -> normalizeSpeechTag(Locale.getDefault().toLanguageTag())
        }
    }

    private fun normalizeSpeechTag(tag: String): String {
        if (tag.isBlank()) return "en-US"
        val lower = tag.lowercase(Locale.ROOT)
        return when {
            lower == "ru" || lower.startsWith("ru-") -> if (lower == "ru") "ru-RU" else tag
            lower == "en" || lower.startsWith("en-") -> if (lower == "en") "en-US" else tag
            else -> tag
        }
    }
}
