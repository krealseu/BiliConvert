package org.kreal.biliconvert.data.bili

interface FilmDataSource {
    interface LoadFilmsCallback {

        fun onFilmsLoaded(films: Array<Film>)

        fun onDataNotAvailable()
    }

    fun loadFilms(callback: LoadFilmsCallback)

}