package online.afeibaili

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import online.afeibaili.command.Commands
import online.afeibaili.file.ConfigFile
import online.afeibaili.file.LevelMapFile
import online.afeibaili.module.Otto
import online.afeibaili.module.Translation
import online.afeibaili.module.UploadFile
import online.afeibaili.module.echo.cave.EchoCave
import online.afeibaili.module.minecraft.Minecraft
import online.afeibaili.module.music.SearchMusicId
import online.afeibaili.module.password.game.PasswordBreak

object FeiFeiBot : KotlinPlugin(
    JvmPluginDescription(
        id = "online.afeibaili.feifeibot",
        name = "FeiFeiBot",
        version = "3.16.0",
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
        loadMinecraft()
        loadTranslation()
        loadPasswordBreakGame()
        loadEchoCave()
        Listener.loadingListener()
        loadSearchMusicId()
        loadOtto()
        loadTerraria()
        loadUpload()
    }

    fun reloadConfigFile() {
        loadFile()
    }

    fun loadFile() {
        configObject = ConfigFile()
        config = configObject.config
        levelObject = LevelMapFile()
        levelMap = levelObject.levelMap
    }

    fun loadCommand() {
        Commands.load()
    }

    //model
    fun loadMinecraft() {
        if (config.module.isEnableMinecraft) Minecraft.load()
    }

    fun loadTerraria() {
//        if (config.module.isEnableTerraria) Terraria.load()
    }

    fun loadTranslation() {
        if (config.module.isTranslation) Translation.load()
    }

    fun loadPasswordBreakGame() {
        if (config.module.passwordBreakGame.isOpen) PasswordBreak.load()
    }

    fun loadEchoCave() {
        if (config.module.isEchoCave) EchoCave.load()
    }

    fun loadSearchMusicId() {
        SearchMusicId.load()
    }

    fun loadOtto() {
        Otto.load()
    }

    fun loadUpload() {
        if (config.module.uploadFile.isOpen) UploadFile.load()
    }
}