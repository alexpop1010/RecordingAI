package com.tapp.recordingai.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LanguageSet {

    private const val PREFS = "language_prefs"
    private const val KEY_IS_ENGLISH = "is_english"
    fun setEnglish(context: Context) {
        context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_ENGLISH, true)
            .apply()

        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags("en")
        )
    }
    fun setSystemLanguage(context: Context) {
        context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_IS_ENGLISH)
            .apply()

        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.getEmptyLocaleList()
        )
    }

    fun applySavedLanguage(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY_IS_ENGLISH)) {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.getEmptyLocaleList()
            )
            return
        }
        if (prefs.getBoolean(KEY_IS_ENGLISH, false)) {
            setEnglish(context)
        } else {
            setSystemLanguage(context)
        }
    }

}
