package org.kreal.biliconvert.data.bili.json

/**
 * Created by lthee on 2018/1/13.
 */
data class BiliFilmInfo(val is_completed: Boolean, val total_bytes: Int, val type_tag: String,
                        val title: String, val cover: String, val part: String, val page_data: PageData?, val ep: EP?)