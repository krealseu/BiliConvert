package org.kreal.biliconvert.loader

import android.content.AsyncTaskLoader
import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.preference.PreferenceManager
import org.kreal.biliconvert.convert.ConvertTask
import org.kreal.biliconvert.data.DataManager
import org.kreal.biliconvert.setting.SettingsKey
import java.io.File

/**
 * Created by lthee on 2018/3/19.
 * 使用AsyncTaskLoader异步加载数据，在老旧设备比较明显，因为bili文件的json解读会很耗时
 */
class BiliSourceLoader(context: Context?) : AsyncTaskLoader<DataManager>(context), SharedPreferences.OnSharedPreferenceChangeListener {
    private var outputFolder: String
    private var biliSourceFolder: String
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        var preferencesChanged = true
        when (key) {
            SettingsKey.OutputFolder -> outputFolder = sharedPreferences.getString(key, SettingsKey.DefaultOutputFolder)
            SettingsKey.CustomFolder, SettingsKey.IsDefault -> biliSourceFolder = if (sharedPreferences.getBoolean(SettingsKey.IsDefault, true)) SettingsKey.DefaultFolder else sharedPreferences.getString(SettingsKey.CustomFolder, SettingsKey.DefaultFolder)
            else -> preferencesChanged = false
        }
        if (preferencesChanged)
            onContentChanged()
    }

    private var data: DataManager? = null
    override fun loadInBackground(): DataManager {
        val result = DataManager(File(biliSourceFolder, "${SettingsKey.biliName}/download"), File(outputFolder), ConvertTask(outputFolder))
        data?.also { onReleaseResources(it) }
        data = result
        return result
    }

    override fun deliverResult(data: DataManager?) {
        if (isReset) {
            data?.also { onReleaseResources(it) }
        }
        if (isStarted) {
            super.deliverResult(data)
        }
    }

    override fun onStartLoading() {
        super.onStartLoading()
//        data?.also { deliverResult(it) }
        if (takeContentChanged() || data == null) {
            forceLoad()
        }
    }

    override fun onReset() {
        super.onReset()
        PreferenceManager.getDefaultSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(this)
        data?.also { onReleaseResources(it) }
        data = null
    }

    init {
        val preference = PreferenceManager.getDefaultSharedPreferences(context)
        preference.registerOnSharedPreferenceChangeListener(this)
        outputFolder = preference.getString(SettingsKey.OutputFolder, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path)
        biliSourceFolder = if (preference.getBoolean(SettingsKey.IsDefault, true)) SettingsKey.DefaultFolder else preference.getString(SettingsKey.CustomFolder, SettingsKey.DefaultFolder)
    }

    private fun onReleaseResources(data: DataManager) {
    }
}