import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper


plugins {
    val kotlinVersion = "1.8.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.16.0"
}

dependencies {
    implementation("org.jsoup:jsoup:1.19.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
}

group = "online.afeibaili"
version = getConfigByKey { "version" }

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}


buildscript {
    dependencies {
        classpath("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    }
}

fun getConfigByKey(key: () -> String): String {
    return sourceSets.main.get().resources.find {
        it.name.contains("config")
    }?.let { config ->
        ObjectMapper().readValue(config, object : TypeReference<Map<String, String>>() {})[key.invoke()]
    }!!
}