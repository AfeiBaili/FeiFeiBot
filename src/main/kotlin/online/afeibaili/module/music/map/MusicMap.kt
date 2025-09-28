package online.afeibaili.module.music.map

import com.fasterxml.jackson.annotation.JsonIgnoreProperties


/**
 * 音乐映射对象
 *
 *@author AfeiBaili
 *@version 2025/9/28 12:47
 */

@JsonIgnoreProperties(ignoreUnknown = true)
class Music {
    lateinit var result: Result
    var code: Int = 0
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Result {
    lateinit var songs: List<Song>
    var hasMore: Boolean = false
    var songCount: Long = 0
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Song {
    var id: Long = 0
    lateinit var name: String
    lateinit var artists: List<Artists>
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Artists {
    var id: Long = 0
    lateinit var name: String
}