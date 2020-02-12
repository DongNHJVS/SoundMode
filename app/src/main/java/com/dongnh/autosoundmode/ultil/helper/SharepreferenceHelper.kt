package com.dongnh.autosoundmode.ultil.helper

import android.content.Context
import android.content.SharedPreferences
import com.dongnh.autosoundmode.const.SHARE_PREFERENCE

class SharePreferenceHelper(context: Context) {

    private var sharePreference = getSharedPreferences(context)

    fun setDataStringKeyValue(key: String, value: String) {
        val editor: SharedPreferences.Editor = this@SharePreferenceHelper.sharePreference.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun setDataIntKeyValue(key: String, value: Int) {
        val editor: SharedPreferences.Editor = this@SharePreferenceHelper.sharePreference.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun setDataBooleKeyValue(key: String, value: Boolean) {
        val editor: SharedPreferences.Editor = this@SharePreferenceHelper.sharePreference.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getDataString(key: String) : String? {
        return this@SharePreferenceHelper.sharePreference.getString(key, "")
    }

    fun getDataBoolean(key: String) : Boolean? {
        return this@SharePreferenceHelper.sharePreference.getBoolean(key, false)
    }

    fun getDataInt(key: String) : Int? {
        return this@SharePreferenceHelper.sharePreference.getInt(key, -1)
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARE_PREFERENCE, Context.MODE_PRIVATE)
    }
}