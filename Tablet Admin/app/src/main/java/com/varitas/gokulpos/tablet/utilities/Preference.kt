package com.varitas.gokulpos.tablet.utilities

import android.content.Context
import android.content.SharedPreferences

object Preference {

    private const val PREFERENCE_FILE: String = "GOKUL"
    private const val PASSWORD: String = "GOKUL"

    @Synchronized fun getPref(context: Context): SharedPreferences {
        return ObscuredSharedPreferences(context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE), PASSWORD)
    }
}