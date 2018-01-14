package org.kreal.biliconvert

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.kreal.biliconvert.R.id.recycler_view
import org.kreal.bilitransform.FlvParserUtil
import org.kreal.bilitransform.Mp4ParserUtil
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread {
            Log.i("kk", "kkkk")
            val ff = arrayOf("/sdcard/0.blv", "/sdcard/1.blv", "/sdcard/2.blv", "/sdcard/3.blv", "/sdcard/4.blv")
            FlvParserUtil.mergerFLV(ff, "/sdcard/ss.flv")
            Log.i("kk", "kkkk2")
            val ss = arrayOf("/sdcard/22.blv", "/sdcard/22.blv", "/sdcard/22.blv", "/sdcard/22.blv")
            Mp4ParserUtil.mergerMp4(ss, "/sdcard/gg.mp4")
            Log.i("kk", "kkkk3")
        }
        setContentView(R.layout.activity_main)
        val file = File("/sdcard/Download/tv.danmaku.bili/download/9957018")
        val k = Film(file)
        k.chapters.forEach {
            Log.i("df", it.index_title)
        }
        recycler_view.adapter = FilmAdapt(File("/sdcard/Download/tv.danmaku.bili/download"))
        recycler_view.layoutManager = LinearLayoutManager(baseContext)

    }
}
