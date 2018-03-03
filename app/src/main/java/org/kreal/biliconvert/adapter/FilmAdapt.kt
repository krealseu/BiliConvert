package org.kreal.biliconvert.adapter

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import org.kreal.biliconvert.R
import org.kreal.biliconvert.data.DataManager
import java.io.File

/**
 * Created by lthee on 2018/1/13.
 */
class FilmAdapt(private val dataManager: DataManager, var listener: OnItemClickListen? = null) : RecyclerView.Adapter<FilmAdapt.ItemView>() {
    override fun getItemCount(): Int = dataManager.filmSize

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        val itemView = ItemView(view, dataManager)
        itemView.backupButton.setOnClickListener {
            val position = itemView.adapterPosition
            val film = dataManager.getFilm(position)
            when (dataManager.getState(film)) {
                DataManager.State.Backup -> Toast.makeText(it.context, R.string.backup, Toast.LENGTH_SHORT).show()
                DataManager.State.Completed -> {
                    listener?.onClick(position, film)
                    notifyItemChanged(position)
                }
                DataManager.State.NotComplete -> Toast.makeText(it.context, R.string.downloading, Toast.LENGTH_SHORT).show()
                DataManager.State.Waiting -> {
                    listener?.onClick(position, film)
                    notifyItemChanged(position)
                }
                DataManager.State.Converting -> Toast.makeText(it.context, R.string.downloading, Toast.LENGTH_SHORT).show()
            }
        }
        itemView.header.setOnClickListener {
            val position = itemView.adapterPosition
            val film = dataManager.getFilm(position)
            if (dataManager.getState(film) == DataManager.State.Backup) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType( Uri.parse(File(dataManager.outputFolder, dataManager.createName(film)).path),"video/*")
                it.context.startActivity(intent)
            }
        }
        itemView.header.setOnLongClickListener {
            val position = itemView.adapterPosition
            val film = dataManager.getFilm(position)
            return@setOnLongClickListener if (dataManager.getState(film) == DataManager.State.Backup) {
                val dialog = AlertDialog.Builder(it.context)
                dialog.setTitle("Delete")
                dialog.setMessage(film.name)
                dialog.setNegativeButton("Cancel",null)
                dialog.setPositiveButton("Sure"){_, _ ->
                    File(dataManager.outputFolder, dataManager.createName(film)).delete()
                }
                dialog.show()
                true
            }else false
        }
        return itemView
    }

    override fun onBindViewHolder(holder: ItemView, position: Int) {
        val film = dataManager.getFilm(position)
        holder.apply {
            Glide.with(coverImage).load(film.cover).into(coverImage)
            if (!film.isSingle) {
                filmName.text = film.name
                chapterList.visibility = View.VISIBLE
                adapt.listener = listener
                adapt.swipeData(film)
                backupButton.visibility = View.INVISIBLE
                workingBar.visibility = View.INVISIBLE
                header.isClickable = false
            } else {
                filmName.text = film.title
                chapterList.visibility = View.GONE
                backupButton.visibility = View.VISIBLE
                val res = when (dataManager.getState(film)) {
                    DataManager.State.Backup -> R.drawable.data_backup
                    DataManager.State.Completed -> R.drawable.data_un_backup
                    DataManager.State.NotComplete -> R.drawable.download
                    DataManager.State.Waiting -> R.drawable.waiting
                    DataManager.State.Converting -> 0
                }
                when (res) {
                    0 -> {
                        workingBar.visibility = View.VISIBLE
                        backupButton.visibility = View.INVISIBLE
                    }
                    else -> {
                        workingBar.visibility = View.INVISIBLE
                        backupButton.visibility = View.VISIBLE
                        backupButton.setImageResource(res)
                    }
                }
                header.isClickable = true
            }
        }
    }

    class ItemView(itemView: View, dataManager: DataManager) : RecyclerView.ViewHolder(itemView) {
        val coverImage: ImageView = itemView.findViewById(R.id.cover_image)
        val filmName: TextView = itemView.findViewById(R.id.film_name)
        val chapterList: RecyclerView = itemView.findViewById(R.id.chapter_list)
        val backupButton: ImageButton = itemView.findViewById(R.id.backup_button)
        val header: LinearLayout = itemView.findViewById(R.id.item_header)
        val adapt: DetailAdapt = DetailAdapt(dataManager)
        val workingBar: ProgressBar = itemView.findViewById(R.id.working_bar)

        init {
            chapterList.layoutManager = LinearLayoutManager(itemView.context)
            chapterList.addItemDecoration(DividerItemDecoration(itemView.context, DividerItemDecoration.VERTICAL))
            chapterList.adapter = adapt
        }
    }

}