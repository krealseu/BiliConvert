package org.kreal.biliconvert.setting

import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.preference.SwitchPreference
import android.widget.Toast
import org.kreal.biliconvert.R
import org.kreal.widget.filepickdialog.FilePickDialogFragment
import java.io.File

/**
 * Created by lthee on 2018/3/2.
 * setting fragment
 */

class SettingsFragment : PreferenceFragment(), Preference.OnPreferenceClickListener {
    override fun onPreferenceClick(p0: Preference): Boolean {
        bind2FilePickDialog(p0)
        return true
    }

    private fun bind2FilePickDialog(preference: Preference) {
        FilePickDialogFragment().apply {
            selectFolder = true
            setListener {
                val path = it[0].path
                when (preference.key) {
                    SettingsKey.CustomFolder -> {
                        preference.summary = path
                        PreferenceManager.getDefaultSharedPreferences(preference.context).edit().putString(preference.key, path).apply()
                    }
                    SettingsKey.OutputFolder -> {
                        if (File(path).canWrite()) {
                            preference.summary = path
                            PreferenceManager.getDefaultSharedPreferences(preference.context).edit().putString(preference.key, path).apply()
                        } else {
                            Toast.makeText(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) context else activity, "选择一个有写权限的目录", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }.show(fragmentManager, preference.key)
    }

    private lateinit var customFolder: Preference
    private lateinit var outputFolder: Preference
    private lateinit var defaultFolder: SwitchPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.prf_fragment)
        customFolder = findPreference(SettingsKey.CustomFolder)
        outputFolder = findPreference(SettingsKey.OutputFolder)
        defaultFolder = findPreference(SettingsKey.IsDefault) as SwitchPreference
        defaultFolder.setOnPreferenceChangeListener { _, any ->
            customFolder.isEnabled = any == false
            true
        }
        customFolder.summary = PreferenceManager.getDefaultSharedPreferences(activity).getString(customFolder.key, "/storage/emulated/0/android/data")
        outputFolder.summary = PreferenceManager.getDefaultSharedPreferences(activity).getString(outputFolder.key, "/storage/emulated/0/download")
        customFolder.isEnabled = !defaultFolder.isChecked
        customFolder.onPreferenceClickListener = this
        outputFolder.onPreferenceClickListener = this
        setHasOptionsMenu(true)
    }
}
