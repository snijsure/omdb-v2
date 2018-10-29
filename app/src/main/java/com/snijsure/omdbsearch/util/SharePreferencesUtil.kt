package com.snijsure.omdbsearch.util

import android.app.Application
import android.content.Context
import android.net.NetworkCapabilities
import com.google.gson.Gson
import android.preference.PreferenceManager
import com.google.gson.reflect.TypeToken
import android.content.SharedPreferences
import java.lang.Exception


object SharedPreferencesUtil  {
    const val FAV_LIST = "favlist"
    fun saveArrayList(context: Context, list: ArrayList<String>, key: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()     // This line is IMPORTANT !!!
    }

    fun getArrayList(context: Context, key: String): ArrayList<String> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json = prefs.getString(key, null)

        return try {
            gson.fromJson(json, object : TypeToken<ArrayList<String>>() {
            }.type)
        } catch(exp: Exception) {
            ArrayList()
        }
    }

}