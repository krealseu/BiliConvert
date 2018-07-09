package org.kreal.biliconvert.adapter

import org.kreal.biliconvert.data.bili.Film

/**
 * Created by lthee on 2018/1/27.
 */
interface OnItemClickListen {
    fun onClick(i: Int,film: Film)
}