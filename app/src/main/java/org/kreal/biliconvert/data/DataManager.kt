package org.kreal.biliconvert.data

import org.kreal.biliconvert.convert.ConvertTask

/**
 * Created by lthee on 2018/1/27.
 */
class DataManager(private val biliSource: BiliSource, private val convertTask: ConvertTask) {
    var filmSize: Int = 0
        get() = biliSource.filmSize

    fun getFilm(index: Int) = biliSource.getFilm(index)

    fun getState(film: Film): State =
        if (!film.isComplete)
             State.NotComplete
        else  when (convertTask.getState(film)) {
            0 -> State.Waiting
            1 -> State.Converting
            else -> State.Completed
        }

    enum class State {
        NotComplete, Completed, Backup, Waiting, Converting
    }

}