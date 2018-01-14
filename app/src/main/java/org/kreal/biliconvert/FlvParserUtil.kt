package org.kreal.bilitransform

import android.util.Log
import org.kreal.FLVParser.FLV
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

/**
 * Created by lthee on 2017/12/16.
 */
object FlvParserUtil {
    fun mergerFLV(flvPathList: Array<String>, outputfile: String) {
        val flvList = arrayListOf<FLV>()
        val flvDurations = arrayListOf<Double>()
        var totalDuration = 0.0
        flvPathList.forEach {
            val flv = FLV(it)
            flvList.add(flv)
            val duration = getDuration(flv)
            totalDuration += duration
            flvDurations.add(getDuration(flv))
        }
        val fileChannel = RandomAccessFile(outputfile, "rw").channel

        var addDuration = 0
        flvList.forEachIndexed { i, flv ->
            if (i == 0) {
                fileChannel.write(flv.flvHeader.data)
                fileChannel.write(ByteBuffer.wrap(byteArrayOf(0x00, 0x00, 0x00, 0x00)))
                flv.forEach {
                    if (it.header.type.toInt() == 0x12 ) {
                        fileChannel.write(it.header.data)
                        val tagData = it.getdate()
                        val pos = findDuration(tagData, 0)
                        if (pos != -1) {
                            tagData.putDouble(totalDuration)
                        }
                        tagData.position(0)
                        fileChannel.write(tagData)
                        fileChannel.write(ByteBuffer.allocate(4).putInt(it.header.dataSize + 11))
                    } else
                        it.writeTo(fileChannel)
                }
            } else {
                addDuration += (flvDurations[i - 1] * 1000).toInt()
                flv.forEach {
                    it.header.timestamp += addDuration
//                    fileChannel.write(it.header.data)
//                    fileChannel.write(it.getdate())
//                    fileChannel.write(ByteBuffer.allocate(4).putInt(it.header.dataSize+11).flip() as ByteBuffer)
                    it.writeTo(fileChannel)
                }
            }
        }

        flvList.forEach {
            it.close()
        }
        fileChannel.close()
    }

    private fun getDuration(flv: FLV): Double {
        for (tag in flv) {
            if (tag.header.type.toInt() == 0x12 ) {
                val data = tag.getdate()
                val pos = findDuration(data, 0)
                return if (pos == -1) {
                    0.0
                } else {
                    data.getDouble(pos)
                }
            }
        }
        return 0.0
    }

    private val Duration = byteArrayOf(0x64, 0x75, 0x72, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x00)   //  "duration "

    private fun findDuration(byteBuffer: ByteBuffer, position: Int): Int {
        byteBuffer.position(position)
        while (byteBuffer.get() != Duration[0]) {
            if (byteBuffer.remaining() < 18) {
                return -1
            }
        }
        val pos = byteBuffer.position()
        return if (byteBuffer.get() == Duration[1] && byteBuffer.get() == Duration[2] && byteBuffer.get() == Duration[3]
                && byteBuffer.get() == Duration[4] && byteBuffer.get() == Duration[5] && byteBuffer.get() == Duration[6]
                && byteBuffer.get() == Duration[7] && byteBuffer.get() == Duration[8])
            byteBuffer.position()
        else
            findDuration(byteBuffer, pos)
    }
}