package org.kreal.biliconvert

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.kreal.biliconvert.adapter.FilmAdapt
import org.kreal.biliconvert.adapter.OnItemClickListen
import org.kreal.biliconvert.convert.ConvertTask
import org.kreal.biliconvert.convert.Tasks
import org.kreal.biliconvert.data.DataManager
import org.kreal.biliconvert.data.Film
import org.kreal.biliconvert.setting.SettingsActivity
import org.kreal.biliconvert.setting.SettingsKey
import java.io.File

class MainActivity : AppCompatActivity(), Tasks.CallBack<Film>, OnItemClickListen {
    private lateinit var convertTask: ConvertTask
    private lateinit var dataManager: DataManager
    private val handler = Handler()
    private var outputFolder: String = ""
    private var biliSourceFolder: String = ""

    override fun onClick(i: Int, film: Film) {
        when (dataManager.getState(film)) {
            DataManager.State.Waiting -> convertTask.remove(film)
            DataManager.State.Completed -> convertTask.submit(film, this)
        }
    }

    override fun done(task: Film, result: Int) {
        handler.post {
            recycler_view.adapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        val preference = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val outputFolderTmp = preference.getString(SettingsKey.OutputFolder, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path)
        val biliSourceFolderTmp = if (preference.getBoolean(SettingsKey.IsDefault, true))
            SettingsKey.DefaultFolder
        else
            PreferenceManager.getDefaultSharedPreferences(baseContext).getString(SettingsKey.CustomFolder, SettingsKey.DefaultFolder)
        if (outputFolderTmp != outputFolder || biliSourceFolder != biliSourceFolderTmp) {
            outputFolder = outputFolderTmp
            biliSourceFolder = biliSourceFolderTmp
            convertTask = ConvertTask(outputFolder)
            dataManager = DataManager(File(biliSourceFolder, "${SettingsKey.biliName}/download"), File(outputFolder), convertTask)

            recycler_view.adapter = FilmAdapt(dataManager, this)
            recycler_view.layoutManager = LinearLayoutManager(baseContext)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        } else
            super.onOptionsItemSelected(item)
    }
}
