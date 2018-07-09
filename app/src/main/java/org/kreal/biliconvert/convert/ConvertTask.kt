package org.kreal.biliconvert.convert

import org.kreal.biliconvert.data.bili.Film
import java.io.File

/**
 * Created by lthee on 2018/1/27.
 */
class ConvertTask(private val outputFile: String) : Tasks<Film>() {
    override fun dealTask(task: Film): Int =
            when (task.isComplete) {
                false -> -1
                true -> {
                    val name = createName(task)
                    val file = File("$outputFile/$name.tmp")
                    if (file.exists() && file.isFile)
                        file.delete()
                    val file2 = File("$outputFile/$name")
                    if (file2.exists() && file2.isFile)
                        file2.delete()
                    val sourcePath: Array<String> = Array(task.data.size) { task.data[it].path }
                    when (task.format) {
                        "flv" -> FlvParserUtil.mergerFLV(sourcePath, file.path)
                        "mp4" -> Mp4ParserUtil.mergerMp4(sourcePath, file.path)
                    }
                    if (file.exists() && file.isFile)
                        file.renameTo(file2)
                    0
                }
            }

    override fun submit(task: Film, taskCallback: TaskCallback<Film>) {
        if (getState(task) == -1)
            super.submit(task, taskCallback)
    }

    private fun createName(film: Film): String {
        return if (film.cover == null)
            film.title + "." + film.format
        else "${film.index} ${film.indexTitle}.${film.format}"
    }
}