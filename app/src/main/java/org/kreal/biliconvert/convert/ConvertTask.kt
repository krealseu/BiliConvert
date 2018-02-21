package org.kreal.biliconvert.convert

import org.kreal.biliconvert.data.Chapter
import org.kreal.biliconvert.data.Film
import org.kreal.bilitransform.FlvParserUtil
import org.kreal.bilitransform.Mp4ParserUtil

/**
 * Created by lthee on 2018/1/27.
 */
class ConvertTask(private val outputFile: String) : Tasks<Film>() {
    override fun dealTask(task: Film): Int =
            when (task.isComplete) {
                false -> -1
                true -> {
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

    private fun createName(chapter: Film): String {
        return chapter.name + "." + chapter.format
    }
}