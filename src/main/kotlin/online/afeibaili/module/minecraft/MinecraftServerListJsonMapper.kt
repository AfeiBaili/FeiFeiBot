package online.afeibaili.module.minecraft

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class MinecraftServerListJsonMapper() {
    var favicon: String = ""
    var description: Description = Description()
    var players: Players = Players()
    var version: Version = Version()

    class Players() {
        var max = 0
        var online = 0
        var sample = listOf<Player>()

        class Player {
            var id: String = ""
            var name: String = ""
        }
    }

    class Description {
        var text = ""
    }

    class Version {
        var name = ""
        var protocol = 0
    }
}