package org.kreal.biliconvert.setting

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.preference.SwitchPreference
import org.kreal.biliconvert.R
import org.kreal.widget.filepickdialog.FilePickDialogFragment

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
                preference.summary = it[0]
                PreferenceManager.getDefaultSharedPreferences(preference.context).edit().putString(preference.key, it[0]).commit()
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
        defaultFolder.setOnPreferenceChangeListener { preference, any ->
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
