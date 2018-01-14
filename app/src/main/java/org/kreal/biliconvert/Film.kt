package org.kreal.biliconvert

import android.util.Log
import java.io.File

/**
 * Created by lthee on 2018/1/13.
 */
class Film(val file: File) {
    val chapters: ArrayList<Chapter> = arrayListOf()

    val size: Int

    val title: String

    val cover: String

    init {
        val files = file.listFiles()
        files.forEach {
            if (it.isDirectory)
                chapters.add(Chapter(it))
        }
        size = chapters.sumBy { it.size }
        if (chapters.isEmpty()) {
            title = ""
            cover = ""
        } else {
            title = chapters[0].title
            cover = chapters[0].cover
        }
    }

    fun hasChapter(): Boolean {
        if (chapters.isEmpty())
            return false
        else if (chapters[0].index_title == "P1")
            return false
        return true
    }
}