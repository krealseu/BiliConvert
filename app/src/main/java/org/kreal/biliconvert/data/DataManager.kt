package org.kreal.biliconvert.data

import android.util.Log
import org.kreal.biliconvert.convert.ConvertTask
import java.io.File

/**
 * Created by lthee on 2018/1/27.
 * 类似system view的想法，还没整理好
 */
class DataManager(biliSourceFolder: File, val outputFolder: File, private val convertTask: ConvertTask) {

    private val films: ArrayList<Film> = arrayListOf()

    var filmSize: Int = 0
        private set

    fun getFilm(index: Int) = films[index]

    fun getState(film: Film): State =
            if (!film.isComplete)
                State.NotComplete
            else if (File(outputFolder, createName(film)).isFile)
                State.Backup
            else when (convertTask.getState(film)) {
                0 -> State.Waiting
                1 -> State.Converting
                else -> State.Completed
            }

    fun createName(film: Film): String {
        return if (film.parent == null)
            film.title + "." + film.format
        else "${film.index} ${film.name}.${film.format}"
    }

    init {
        if (biliSourceFolder.isDirectory)
            biliSourceFolder.listFiles().forEach {
                if (it.isDirectory) {
                    val film = Film(it)
                    if (film.isValid)
                        films.add(film)
                }
            }
        filmSize = films.size
    }

    enum class State {
        NotComplete, Completed, Backup, Waiting, Converting
    }

}