package org.kreal.biliconvert.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import org.kreal.biliconvert.R
import org.kreal.biliconvert.data.DataManager
import org.kreal.biliconvert.data.Film

/**
 * Created by lthee on 2018/1/13.
 */
class DetailAdapt(val dataManager: DataManager, var listener: OnItemClickListen? = null) : RecyclerView.Adapter<DetailAdapt.ItemView>() {
    private var filmIndex: Int = 0
    private var film: Film? = null

    fun swipeData(film: Film) {
        this.film = film
        notifyDataSetChanged()
    }

    override fun getItemCount() = film?.films?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chapter_detial, parent, false)
        val itemView = ItemView(view)
        itemView.backupButton.setOnClickListener {
            val position = itemView.adapterPosition
            val film = film?.films?.get(position) ?: return@setOnClickListener
            when (dataManager.getState(film)) {
                DataManager.State.Backup -> Toast.makeText(it.context, R.string.backup, Toast.LENGTH_SHORT).show()
                DataManager.State.Completed -> {
                    listener?.onClick(filmIndex, film)
                    notifyItemChanged(position)
                }
                DataManager.State.NotComplete -> Toast.makeText(it.context, R.string.downloading, Toast.LENGTH_SHORT).show()
                DataManager.State.Waiting -> {
                    listener?.onClick(filmIndex, film)
                    notifyItemChanged(position)
                }
                DataManager.State.Converting -> Toast.makeText(it.context, R.string.downloading, Toast.LENGTH_SHORT).show()
            }
        }
        return itemView
    }

    override fun onBindViewHolder(itemView: ItemView, position: Int) {
        val filmTmp = film?.films?.get(position) ?: return
        val res = when (dataManager.getState(filmTmp)) {
            DataManager.State.Backup -> R.drawable.data_backup
            DataManager.State.Completed -> R.drawable.data_un_backup
            DataManager.State.NotComplete -> R.drawable.download
            DataManager.State.Waiting -> R.drawable.waiting
            DataManager.State.Converting -> 0
        }
        itemView.apply {
            chapterName.text = filmTmp.name
            when (res) {
                0 -> {
                    workingBar.visibility = View.VISIBLE
                    backupButton.visibility = View.GONE
                }
                else -> {
                    workingBar.visibility = View.GONE
                    backupButton.visibility = View.VISIBLE
                    backupButton.setImageResource(res)
                }
            }
        }

    }

    class ItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chapterName: TextView = itemView.findViewById(R.id.chapter_name)
        val backupButton: ImageButton = itemView.findViewById(R.id.backup_button)
        val workingBar: ProgressBar = itemView.findViewById(R.id.working_bar)
    }

}