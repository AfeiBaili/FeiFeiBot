package online.afeibaili.file

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File


/**
 * 关键字列表文件对象类
 *
 * @author AfeiBaili
 * @version 2026/6/26 19:10
 */

object KeyWordDataJsonFile {
    lateinit var keywords: KeyWordList
    val jsonFile = File(System.getProperty("user.dir") + "/data/feifei", "keyword.json")
    val jsonMapper: ObjectMapper =
        ObjectMapper().registerModule(KotlinModule.Builder().build())

    fun load() {
        if (!jsonFile.exists()) {
            jsonFile.parentFile.mkdirs()
            jsonFile.createNewFile()
            jsonFile.writeText(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(KeyWordList(hashMapOf())))
        }

        keywords = jsonMapper.readValue(jsonFile, KeyWordList::class.java)
    }

    fun store() {
        jsonFile.writeText(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(keywords))
    }
}


data class KeyWordList(val words: MutableMap<String, MutableSet<Long>>) {
    fun add(keyWord: String, value: Long) {
        synchronized(this) {
            if (words.containsKey(keyWord)) {
                val longs: MutableSet<Long> = words[keyWord]!!
                longs.add(value)
            } else {
                words[keyWord] = mutableSetOf(value)
            }
        }
    }

    fun remove(keyWord: String, value: Long): Boolean {
        synchronized(this) {
            val longs: MutableSet<Long>? = words[keyWord]
            longs?.remove(value)
            if (longs != null && words.containsKey(keyWord) && words[keyWord]!!.isEmpty()) {
                words.remove(keyWord)
                return true
            }
            return false
        }
    }

    fun clear() {
        synchronized(this) { words.clear() }
    }
}