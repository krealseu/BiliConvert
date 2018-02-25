package org.kreal.biliconvert.data

import org.kreal.biliconvert.convert.ConvertTask
import java.io.File

/**
 * Created by lthee on 2018/1/27.
 */
class DataManager(biliSourceFolder: File, private val convertTask: ConvertTask) {

    private val films: ArrayList<Film> = arrayListOf()

    var filmSize: Int = 0
        private set

    fun getFilm(index: Int) = films[index]

    fun getState(film: Film): State =
            if (!film.isComplete)
                State.NotComplete
            else when (convertTask.getState(film)) {
                0 -> State.Waiting
                1 -> State.Converting
                else -> State.Completed
            }

    init {
        if (biliSourceFolder.isDirectory)
            biliSourceFolder.listFiles().forEach {
                val film = Film(it)
                if (film.isValid)
                    films.add(film)
            }
        filmSize = films.size
    }

    enum class State {
        NotComplete, Completed, Backup, Waiting, Converting
    }

}