@file:Suppress("DEPRECATION")

package com.example.chatfirebase

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleHelper {
    fun setLocale(activity: Activity, language: String) {
        saveLanguage(activity, language)

        val updatedContext = updateResources(activity, language)
        activity.baseContext.resources.updateConfiguration(
            updatedContext.resources.configuration,
            updatedContext.resources.displayMetrics
        )
    }


    fun getSavedLanguage(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("My_Lang", Locale.getDefault().language) ?: "en"
    }

    private fun saveLanguage(context: Context, lang: String) {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("My_Lang", lang).apply()
    }

    fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }
}
