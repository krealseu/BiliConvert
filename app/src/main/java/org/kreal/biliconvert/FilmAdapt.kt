package org.kreal.biliconvert

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.io.File

/**
 * Created by lthee on 2018/1/13.
 */
class FilmAdapt(val file: File) : RecyclerView.Adapter<FilmAdapt.ItemView>() {

    private val films: ArrayList<Film> = arrayListOf()

    override fun getItemCount(): Int = films.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ItemView(view)
    }

    override fun onBindViewHolder(holder: ItemView, position: Int) {
        holder.bindData(films[position])
    }

    init {
        if (file.isDirectory)
            file.listFiles().forEach {
                if (it.isDirectory)
                    films.add(Film(it))
            }
    }

    class ItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverImage: ImageView = itemView.findViewById(R.id.cover_image)
        private val filmName: TextView = itemView.findViewById(R.id.film_name)
        private val chapterList: RecyclerView = itemView.findViewById(R.id.chapter_list)

        init {
            chapterList.layoutManager = LinearLayoutManager(itemView.context)
        }

        fun bindData(film: Film) {
            filmName.text = film.title
            Glide.with(coverImage).load(film.cover).into(coverImage)
            if (film.hasChapter()) {
                chapterList.visibility = View.VISIBLE
                chapterList.adapter = ChapterAdapt(film)
                chapterList.adapter.notifyDataSetChanged()
            } else chapterList.visibility = View.GONE

        }
    }
}