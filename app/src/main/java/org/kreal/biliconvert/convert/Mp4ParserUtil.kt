package org.kreal.bilitransform

import android.util.Log
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import java.io.File
import java.io.RandomAccessFile
import java.util.*

/**
 * Created by lthee on 2017/12/16.
 */
object Mp4ParserUtil {
    fun mergerMp4(mp4PathList: Array<String>,outputfile:String) {
        val mp4MovieList = arrayOfNulls<Movie>(mp4PathList.size)
        mp4PathList.forEachIndexed { index, path ->
            mp4MovieList[index] = MovieCreator.build(path)
        }
        val audioTracks = LinkedList<Track>()
        val videoTracks = LinkedList<Track>()
        mp4MovieList.forEach {
            it?.tracks?.forEach {
                when (it.handler) {
                    "soun" -> audioTracks.add(it)
                    "vide" -> videoTracks.add(it)
                }
            }
        }
        val resultMovie = Movie()
        if (!audioTracks.isEmpty())
            resultMovie.addTrack(AppendTrack(*audioTracks.toTypedArray()))
        if (!videoTracks.isEmpty())
            resultMovie.addTrack(AppendTrack(*videoTracks.toTypedArray()))
        val outContainer = DefaultMp4Builder().build(resultMovie)
        val fileChannel = RandomAccessFile(outputfile,"rw").channel
        outContainer.writeContainer(fileChannel)
        fileChannel.close()
    }
}