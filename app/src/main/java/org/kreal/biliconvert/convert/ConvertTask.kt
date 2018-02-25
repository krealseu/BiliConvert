package org.kreal.biliconvert.convert

import org.kreal.biliconvert.data.Film
import java.io.File

/**
 * Created by lthee on 2018/1/27.
 */
class ConvertTask(private val outputFile: String) : Tasks<Film>() {
    override fun dealTask(task: Film): Int =
            when (task.isComplete && task.isSingle) {
                false -> -1
                true -> {
                    val file = File("$outputFile/${createName(task)}")
                    if (file.exists() && file.isFile)
                        file.delete()
                    val sourcePath: Array<String> = Array(task.data.size) { task.data[it].path }
                    when (task.format) {
                        "flv" -> FlvParserUtil.mergerFLV(sourcePath, "$outputFile/${createName(task)}")
                        "mp4" -> Mp4ParserUtil.mergerMp4(sourcePath, "$outputFile/${createName(task)}")
                    }
                    0
                }
            }

    override fun submit(task: Film, callBack: CallBack<Film>) {
        if (getState(task) == -1)
            super.submit(task, callBack)
    }

    private fun createName(film: Film): String {
        return if (film.parent == null)
            film.title + "." + film.format
        else "${film.index} ${film.name}.${film.format}"
    }
}