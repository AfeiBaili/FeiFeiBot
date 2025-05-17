import net.mamoe.mirai.console.gradle.BuildMiraiPluginV2

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
    //本地库
    implementation(fileTree("lib"))
}

group = "online.afeibaili"
version = "3.5.2"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

afterEvaluate {
    tasks.named<BuildMiraiPluginV2>("buildPlugin") {
        from(fileTree("lib").map { zipTree(it) })
    }
}