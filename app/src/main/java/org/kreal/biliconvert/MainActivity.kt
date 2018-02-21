package org.kreal.biliconvert

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.kreal.biliconvert.adapter.FilmAdapt
import org.kreal.biliconvert.adapter.OnItemClickListen
import org.kreal.biliconvert.convert.ConvertTask
import org.kreal.biliconvert.convert.Tasks
import org.kreal.biliconvert.data.BiliSource
import org.kreal.biliconvert.data.Chapter
import org.kreal.biliconvert.data.DataManager
import java.io.File

class MainActivity : AppCompatActivity(), Tasks.CallBack<Chapter>, OnItemClickListen {
    private lateinit var convertTask: ConvertTask
    private lateinit var biliSource: BiliSource
    private lateinit var dataManager: DataManager
    private val handler = Handler()

    override fun onClick(i: Int, j: Int) {
//        when(dataManager.getState(i,j)){
//            DataManager.State.Waiting->convertTask.remove(dataManager.getChapter(i,j))
//            DataManager.State.Completed->convertTask.submit(dataManager.getChapter(i,j),this)
//        }
    }

    override fun done(task: Chapter, result: Int) {
        Log.i("finisl", task.indexTitle)
        handler.post {
            recycler_view.adapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("WifiManagerLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val p = PreferenceManager.getDefaultSharedPreferences(baseContext).getString("outputfile", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path)
        convertTask = ConvertTask(p)
//        val dd = Film(File("/sdcard/Download/tv.danmaku.bili/download/15892894"))
//        Log.i("d",dd.name)
        biliSource = BiliSource(File("/sdcard/Download/tv.danmaku.bili/download"))
        dataManager = DataManager(biliSource, convertTask)
        recycler_view.adapter = FilmAdapt(dataManager, this)
        recycler_view.layoutManager = LinearLayoutManager(baseContext)
    }
}
