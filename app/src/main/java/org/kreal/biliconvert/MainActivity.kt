package org.kreal.biliconvert

import android.app.LoaderManager
import android.content.Intent
import android.content.Loader
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.kreal.biliconvert.adapter.FilmAdapt
import org.kreal.biliconvert.adapter.OnItemClickListen
import org.kreal.biliconvert.convert.ConvertTask
import org.kreal.biliconvert.convert.Tasks
import org.kreal.biliconvert.data.DataManager
import org.kreal.biliconvert.data.bili.Film
import org.kreal.biliconvert.data.bili.FilmDataLoaclSource
import org.kreal.biliconvert.data.bili.FilmDataSource
import org.kreal.biliconvert.loader.BiliSourceLoader
import org.kreal.biliconvert.setting.SettingsActivity
import org.kreal.biliconvert.setting.SettingsKey
import java.io.File

class MainActivity : AppCompatActivity(), Tasks.TaskCallback<Film>, OnItemClickListen, LoaderManager.LoaderCallbacks<DataManager>, StoragePermissionGrant.PermissionGrantListener {
    override fun onReject() {
        finish()
    }

    override fun onGrant() {
        loaderManager.initLoader(loaderID, null, this)
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<DataManager> {
        return BiliSourceLoader(baseContext)
    }

    override fun onLoadFinished(p0: Loader<DataManager>, dataManager: DataManager) {
        this.dataManager = dataManager
        this.convertTask = dataManager.convertTask
        recycler_view.adapter = FilmAdapt(dataManager, this)
    }

    override fun onLoaderReset(p0: Loader<DataManager>?) {
        recycler_view.adapter = null
    }

    private lateinit var convertTask: ConvertTask
    private lateinit var dataManager: DataManager
    private val handler = Handler()

    private val loaderID: Int = 233

    override fun onClick(i: Int, film: Film) {
        when (dataManager.getState(film)) {
            DataManager.State.Waiting -> convertTask.remove(film)
            DataManager.State.Completed -> convertTask.submit(film, this)
            else -> Unit
        }
    }

    override fun onTaskCompleted(task: Film, result: Int) {
        handler.post {
            recycler_view.adapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycler_view.layoutManager = LinearLayoutManager(baseContext)
        if (StoragePermissionGrant.checkPermissions(baseContext))
            loaderManager.initLoader(loaderID, null, this)
        else
            StoragePermissionGrant().show(fragmentManager, "storage")
        val filmDataSource: FilmDataLoaclSource = FilmDataLoaclSource(File(if (PreferenceManager.getDefaultSharedPreferences(baseContext).getBoolean(SettingsKey.IsDefault, true)) SettingsKey.DefaultFolder else PreferenceManager.getDefaultSharedPreferences(baseContext).getString(SettingsKey.CustomFolder, SettingsKey.DefaultFolder), "${SettingsKey.biliName}/download"))
        filmDataSource.loadFilms(object : FilmDataSource.LoadFilmsCallback {
            override fun onFilmsLoaded(films: Array<Film>) {
                films.forEach {
                    Log.i("asd",it.title)
                }
            }

            override fun onDataNotAvailable() {
            }

        })


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
