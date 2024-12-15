package com.bibitdev.storyapps.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.bibitdev.storyapps.model.DataUser

class PreferencesHelper (context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(UserPreferences.PREFS_NAME, Context.MODE_PRIVATE)

    fun loadUser(): DataUser? {
        return prefs.run {
            val userId = getString(UserPreferences.USER_ID, null)
            val name = getString(UserPreferences.USER_NAME, null)
            val token = getString(UserPreferences.TOKEN, null)
            if (!userId.isNullOrEmpty() && !name.isNullOrEmpty() && !token.isNullOrEmpty()) {
                DataUser(userId, name, token)
            } else {
                null
            }
        }
    }

    fun saveUser(user: DataUser) {
        prefs.edit().apply {
            putString(UserPreferences.USER_ID, user.userId)
            putString(UserPreferences.USER_NAME, user.name)
            putString(UserPreferences.TOKEN, user.token)
        }.apply()
    }


    fun clear() {
        Log.d("PreferencesManager", "Clearing All Preferences")
        prefs.edit().clear().apply()
    }
}
