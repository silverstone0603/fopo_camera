package com.teamfopo.fopo.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.preference.*
import android.util.Log
import android.widget.Toast
import com.teamfopo.fopo.*
import com.teamfopo.fopo.R
import com.teamfopo.fopo.module.FOPOService
import com.teamfopo.fopo.module.modAuthProcess
import com.teamfopo.fopo.module.modDBMS

class SettingPreferenceFragment: PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    companion object {

    }

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.settings_preference)

        val setting_logout = findPreference("key_logout")
        val setting_myinfo = findPreference("key_myinfo")
        val about_application = findPreference("key_about")
        val changetest = findPreference("key_push")

        changetest.onPreferenceChangeListener = this

        setting_logout.setOnPreferenceClickListener {
            var logout = modAuthProcess().logout()
            var tempVO = logout.execute("${FOPOService.dataMemberVO!!.token}").get()

            var dbms = modDBMS(MainActivity.mContext)
            dbms.clearMember()

            var intent = Intent(context, SplashActivity::class.java)
            startActivity(intent)

            System.exit(0)

            true
        }

        setting_myinfo.setOnPreferenceClickListener {
            var intent = Intent(context, MyInfoActivity::class.java)
            startActivity(intent)

            true
        }

        about_application.setOnPreferenceClickListener {
            var intent = Intent(context, AboutActivity::class.java)
            startActivity(intent)

            true
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference?.key){
            "key_push" -> {
                //Log.d("asdasd", newValue.toString()
                // get View of preference value
                //val sps = PreferenceManager.getDefaultSharedPreferences(MainActivity.mContext)
                FOPOService.setting_push = newValue as Boolean
                Toast.makeText(context, "${FOPOService.setting_push}", Toast.LENGTH_LONG).show()
            }
        }
        return true
    }

}