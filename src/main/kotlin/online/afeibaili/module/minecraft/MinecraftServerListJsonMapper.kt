package online.afeibaili.module.minecraft

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class MinecraftServerListJsonMapper() {
    var favicon: String = ""
    var players: Players = Players()
    var version: Version = Version()
}

@JsonIgnoreProperties(ignoreUnknown = true)
class MinecraftServerListJsonMapperNew() : MinecraftServerListJsonMapper() {
    var description: String = ""
}

@JsonIgnoreProperties(ignoreUnknown = true)
class MinecraftServerListJsonMapperOld() : MinecraftServerListJsonMapper() {
    var description: Description = Description()
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Description() {
    var translate: String = ""
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Players() {
    var max = 0
    var online = 0
    var sample = listOf<Player>()

    class Player {
        var id: String = ""
        var name: String = ""
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Version {
    var name = ""
    var protocol = 0
}