package com.oppalab.moca.util

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    val PREFERENCE_NAME = "moca_preferene"
    val DEFAULT_VALUE_BOOLEAN = false
    val DEFAULT_VALUE_INT = -1
    val DEFAULT_VALUE_LONG = -1L
    val DEFAULT_VALUE_FLOAT = -1F

    fun getPrefernece(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun setLong(context: Context, key: String, value: Long) {
        val prefs = getPrefernece(context)
        val editor = prefs.edit()
        editor.putLong(key, value)
        editor.commit()
    }

    fun getLong(context: Context, key: String): Long {
        val prefs = getPrefernece(context)
        return prefs.getLong(key, DEFAULT_VALUE_LONG)
    }

}