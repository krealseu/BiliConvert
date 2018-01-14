package org.kreal.biliconvert

import android.util.Log
import com.google.gson.Gson
import org.kreal.biliconvert.json.BiliFilmInfo
import java.io.File

/**
 * Created by lthee on 2018/1/13.
 */
class Chapter(val file: File) : Comparable<Chapter> {

    override fun compareTo(other: Chapter): Int = index - other.index

    private val info: BiliFilmInfo
    val sources: Array<File>
    val title: String
    val isCompelet: Boolean
    val index_title: String?
    val index: Int
    val cover: String
    val size: Int

    init {
        val jsonFile = File(file, "entry.json")
        info = Gson().fromJson(jsonFile.readText(), BiliFilmInfo::class.java)
        Log.i("kk", file.path)
        val source = File(file, info.type_tag ?: "")
        sources = if (source.isDirectory) {
            source.listFiles { file ->
                file.name.endsWith(".blv")
            }.sortedArray()
        } else arrayOf()
        title = info.title
        isCompelet = info.is_completed
        index_title = if (info.ep != null) info.ep.index_title else info.page_data.part
        index = if (info.ep != null) info.ep.index.toInt() else info.page_data.page
        cover = if (info.ep != null) info.ep.cover else info.cover
        size = info.total_bytes
    }
}