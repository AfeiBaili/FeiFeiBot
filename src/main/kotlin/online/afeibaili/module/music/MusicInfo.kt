package online.afeibaili.module.music


/**
 * 音乐信息
 *
 *@author AfeiBaili
 *@version 2026/3/28 00:49
 */

data class MusicInfo(
    val title: String,
    val desc: String,
    val purl: String,
    val murl: String,
    val jurl: String,
    val source: String,
    val icon: String,
    val appid: Long,
)