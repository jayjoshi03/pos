package com.varitas.gokulpos.tablet.utilities

import com.varitas.gokulpos.tablet.activity.MainApp

object PreferenceData {

    //region To get preference values
    fun getPrefLong(key: String) = Preference.getPref(MainApp.mainApp!!.applicationContext).getLong(key, 0)
    fun getPrefString(key: String) = Preference.getPref(MainApp.mainApp!!.applicationContext).getString(key, "") ?: ""
    fun getPrefInt(key: String) = Preference.getPref(MainApp.mainApp!!.applicationContext).getInt(key, 0)
    fun getPrefBoolean(key: String, defaultValue: Boolean) = Preference.getPref(MainApp.mainApp!!.applicationContext).getBoolean(key, defaultValue)
    //endregion

    //region To set preference values
    fun putPrefBoolean(key: String, value: Boolean) = Preference.getPref(MainApp.mainApp!!.applicationContext).edit().putBoolean(key, value).apply()
    fun putPrefString(key: String, value: String) = Preference.getPref(MainApp.mainApp!!.applicationContext).edit().putString(key, value).apply()
    fun putPrefInt(key: String, value: Int) = Preference.getPref(MainApp.mainApp!!.applicationContext).edit().putInt(key, value).apply()
    fun putPrefLong(key: String, value: Long) = Preference.getPref(MainApp.mainApp!!.applicationContext).edit().putLong(key, value).apply()
    //endregion

}