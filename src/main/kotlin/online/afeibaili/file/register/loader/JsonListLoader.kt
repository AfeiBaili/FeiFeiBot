package online.afeibaili.file.register.loader

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import online.afeibaili.LoggerLevel
import online.afeibaili.logger


/**
 * Json加载器，传入一个类型，返回一个列表类型
 *
 *@author AfeiBaili
 *@version 2025/8/24 11:02
 */

class JsonListLoader<T>() : Loader<List<T>> {
    val objectMapper = ObjectMapper()
    override fun load(string: String): List<T> {
        runCatching {
            val value: List<T> = objectMapper.readValue(string, object : TypeReference<List<T>>() {})
            return value
        }.onFailure {
            logger("无任何回声！", LoggerLevel.WARN)
            return emptyList()
        }
        return emptyList()
    }
}