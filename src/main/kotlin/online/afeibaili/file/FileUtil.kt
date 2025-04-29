package online.afeibaili.file

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import online.afeibaili.FeiFeiBot

fun getJsonPropertyByResource(className: Class<*>, filename: String, property: String): String {
    val map: Map<String, String>? = className.classLoader.getResource(filename)?.let {
        ObjectMapper().readValue(it, object : TypeReference<Map<String, String>>() {})
    }
    return map?.get(property)!!
}

fun getJsonConfigByName(property: String): String {
    return getJsonPropertyByResource(FeiFeiBot::class.java, "config.json", property)
}