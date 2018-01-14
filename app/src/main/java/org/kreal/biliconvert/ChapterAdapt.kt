package org.kreal.biliconvert

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by lthee on 2018/1/13.
 */
class ChapterAdapt(val film: Film) : RecyclerView.Adapter<ChapterAdapt.ItemView>() {

    override fun getItemCount(): Int = film.chapters.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chapter_detial, parent, false)
        return ItemView(view)
    }

    override fun onBindViewHolder(itemView: ItemView, position: Int) {
        itemView.bindData(film.chapters[position])
    }

    class ItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chapterName: TextView = itemView.findViewById(R.id.chapter_name)
        fun bindData(chapter: Chapter) {
            chapterName.text = chapter.index_title
        }
    }

}