package org.kreal.biliconvert

import java.io.File

/**
 * Created by lthee on 2018/1/14.
 */
class BiliSource(val file: File) {
    private val films: ArrayList<Film> = arrayListOf()
    var size: Int = 0
        private set

    fun getIndex(index: Int) = films[index]

    init {
        if (file.isDirectory)
            file.listFiles().forEach {
                films.add(Film(it))
            }
        size = films.size
    }
}