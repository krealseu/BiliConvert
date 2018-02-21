package org.kreal.biliconvert.data

import java.io.File

/**
 * Created by lthee on 2018/1/14.
 */
class BiliSource(val file: File) {
    private val films: ArrayList<Film> = arrayListOf()

    var filmSize: Int = 0
        private set

    fun getFilm(index: Int) = films[index]

    fun getChapter(i: Int, j: Int) = films[i].chapters[j]

    init {
        if (file.isDirectory)
            file.listFiles().forEach {
                films.add(Film(it))
            }
        filmSize = films.size
    }
}