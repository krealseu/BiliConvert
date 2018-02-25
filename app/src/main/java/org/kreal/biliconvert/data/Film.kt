package org.kreal.biliconvert.data

import com.google.gson.Gson
import org.kreal.biliconvert.data.json.BiliFilmInfo
import java.io.File
import java.util.*

/**
 * Created by lthee on 2018/1/13.
 */
class Film(val file: File, val parent: Film?) {

    val title: String

    val name: String

    val format: String

    val cover: String

    val index: Int

    val isSingle: Boolean

    val isValid: Boolean

    val isComplete: Boolean

    val data: Array<File>

    val size: Int

    val films: ArrayList<Film> = arrayListOf()

    constructor(file: File) : this(file, null)

    init {
        val jsonFile = File(file, "entry.json")
        size = 0
        when (jsonFile.exists()) {
            true -> {
                isValid = true
                isSingle = true
                val info: BiliFilmInfo = Gson().fromJson(jsonFile.readText(), BiliFilmInfo::class.java)
                val dataFileName: String = info.type_tag
                val dataFile = File(file, (dataFileName ?: ""))
                data = if (dataFile.isDirectory) {
                    val result = dataFile.listFiles { file ->
                        file.name.endsWith(".blv")
                    }
                    Arrays.sort(result) { f1, f2 ->
                        (f1.name.split("."))[0].toInt() - (f2.name.split("."))[0].toInt()
                    }
                    result
                } else arrayOf()
                format = when (dataFileName == null) {
                    true -> ""
                    false -> if (dataFileName.contains("mp4")) "mp4" else if (dataFileName.contains("flv")) "flv" else ""
                }
                title = info.title
                isComplete = info.is_completed
                cover = if (info.ep != null) info.ep.cover else info.cover
                name = if (info.ep != null) info.ep.index_title else info.page_data.part
                index = if (info.ep != null) info.ep.index.toInt() else info.page_data.page
            }
            false -> {
                val files = file.listFiles()
                files.forEach {
                    if (it.isDirectory) {
                        val temp = Film(it, this)
                        if (temp.isValid)
                            films.add(temp)
                    }
                }
                when (films.size) {
                    1 -> {
                        isSingle = true
                        isValid = films[0].isValid
                        title = films[0].title
                        name = films[0].name
                        cover = films[0].cover
                        format = films[0].format
                        data = films[0].data
                        isComplete = films[0].isComplete
                        index = films[0].index
                    }
                    0 -> {
                        isValid = false
                        isSingle = true
                        title = ""
                        name = ""
                        cover = ""
                        format = ""
                        data = arrayOf()
                        isComplete = false
                        index = 0
                    }
                    else -> {
                        isSingle = false
                        isValid = films[0].isValid
                        title = films[0].title
                        name = films[0].title
                        cover = films[0].cover
                        format = films[0].format
                        data = arrayOf()
                        isComplete = true
                        index = films[0].index
                    }
                }
            }
        }
    }

}