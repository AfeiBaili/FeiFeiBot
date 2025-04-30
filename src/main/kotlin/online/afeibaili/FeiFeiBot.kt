package online.afeibaili

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import online.afeibaili.command.Commands
import online.afeibaili.file.ConfigFile
import online.afeibaili.file.LevelMapFile

object FeiFeiBot : KotlinPlugin(
    JvmPluginDescription(
        id = "online.afeibaili.feifeibot",
        name = "FeiFeiBot",
        version = "3.0.0",
    ) {
        author("AfeiBaili")
    }) {

    val miraiLogger = logger

    override fun onEnable() {
        loadingModule()
        miraiLogger.info("FeiFeiBot加载成功")
    }

    fun loadingModule() {
        loadFile()
        loadCommand()
        Listener.loadingListener()
    }

    fun reloadConfigFile() {
        loadFile()
        loadCommand()
    }

    fun loadFile() {
        configObject = ConfigFile()
        config = configObject.config
        levelObject = LevelMapFile()
        levelMap = levelObject.levelMap
    }

    fun loadCommand() {
        Commands.loadCommands()
        commandsMap = Commands.commandsMap
    }
}