package org.kreal.biliconvert.data

import com.google.gson.Gson
import org.kreal.biliconvert.data.json.BiliFilmInfo
import java.io.File
import java.util.*

/**
 * Created by lthee on 2018/1/13.
 * 视频每集的数据模型
 */
class Chapter(val file: File) : Comparable<Chapter> {

    override fun compareTo(other: Chapter): Int = index - other.index

    private val info: BiliFilmInfo
    val sources: Array<File>
    val title: String
    val isComplete: Boolean
    val indexTitle: String
    val index: Int
    val cover: String
    val size: Int
    val format: String

    init {
        val jsonFile = File(file, "entry.json")
        info = Gson().fromJson(jsonFile.readText(), BiliFilmInfo::class.java)
        val source: File
        if (info.type_tag != null) {
            val it = info.type_tag
            source = File(file, it)
            format = if (it.contains("mp4")) "mp4" else if (it.contains("flv")) "flv" else ""
        } else {
            source = File(file, "")
            format = ""
        }

        sources = if (source.isDirectory) {
            val result = source.listFiles { file ->
                file.name.endsWith(".blv")
            }
            Arrays.sort(result, Comparator { f1, f2 ->
                (f1.name.split("."))[0].toInt() - (f2.name.split("."))[0].toInt()
            })
            result
        } else arrayOf()

        title = info.title
        isComplete = info.is_completed
        indexTitle = if (info.ep != null) info.ep.index_title else info.page_data.part
        index = if (info.ep != null) info.ep.index.toInt() else info.page_data.page
        cover = if (info.ep != null) info.ep.cover else info.cover
        size = info.total_bytes
    }

    override fun equals(other: Any?): Boolean = if (other is Chapter) other.file.path == this.file.path else false
}