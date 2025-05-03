package online.afeibaili

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import online.afeibaili.command.Commands
import online.afeibaili.file.ConfigFile
import online.afeibaili.file.LevelMapFile
import online.afeibaili.module.McModSearch
import online.afeibaili.module.Translation

object FeiFeiBot : KotlinPlugin(
    JvmPluginDescription(
        id = "online.afeibaili.feifeibot",
        name = "FeiFeiBot",
        version = "3.4.1",
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
        loadMcModSearch()
        loadTranslation()
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

    fun loadMcModSearch() {
        if (config.module.isMcModSearch) McModSearch.load()
    }

    fun loadTranslation() {
        if (config.module.isTranslation) Translation.load()
    }
}