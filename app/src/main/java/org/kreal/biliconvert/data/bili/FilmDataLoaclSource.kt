package org.kreal.biliconvert.data.bili

import java.io.File
import java.util.concurrent.Executors

class FilmDataLoaclSource(private val biliSourceFolder: File) : FilmDataSource {

    private val executors = Executors.newFixedThreadPool(1)

    override fun loadFilms(callback: FilmDataSource.LoadFilmsCallback) = executors.execute {
        val result: MutableList<Film> = arrayListOf()
        if (biliSourceFolder.isDirectory) {
            loadFilms(biliSourceFolder, result)
            callback.onFilmsLoaded(result.toTypedArray())
        } else callback.onDataNotAvailable()
    }

    private fun loadFilms(file: File, result: MutableList<Film>) {
        file.listFiles().forEach {
            if (it.isDirectory) {
                val film: Film? = Film.parses(it)
                if (film != null) result += film
                else loadFilms(it, result)
            }
        }
    }
}