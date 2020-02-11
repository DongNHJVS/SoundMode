package com.dongnh.autosoundmode.ultil.helper

import android.content.Context
import android.content.SharedPreferences
import com.dongnh.autosoundmode.const.SHARE_PREFERENCE

class SharePreferenceHelper(val context: Context) {

    private var sharePreference = getSharedPreferences(context)

    fun setDataStringKeyValue(key: String, value: String) {
        val edittor: SharedPreferences.Editor = this@SharePreferenceHelper.sharePreference.edit()
        edittor.putString(key, value)
        edittor.apply()
    }

    fun setDataBooleKeyValue(key: String, value: Boolean) {
        val edittor: SharedPreferences.Editor = this@SharePreferenceHelper.sharePreference.edit()
        edittor.putBoolean(key, value)
        edittor.apply()
    }

    fun getDataString(key: String) : String? {
        return this@SharePreferenceHelper.sharePreference.getString(key, "")
    }

    fun getDataBoolen(key: String) : Boolean? {
        return this@SharePreferenceHelper.sharePreference.getBoolean(key, false)
    }

    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARE_PREFERENCE, Context.MODE_PRIVATE)
    }
}