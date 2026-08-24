package online.afeibaili

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import online.afeibaili.command.Commands
import online.afeibaili.file.AdminManagerJsonFile
import online.afeibaili.file.ConfigFile
import online.afeibaili.file.KeyWordDataJsonFile
import online.afeibaili.file.LevelMapFile
import online.afeibaili.module.Otto
import online.afeibaili.module.Translation
import online.afeibaili.module.UploadFile
import online.afeibaili.module.echo.cave.EchoCave
import online.afeibaili.module.group.JoinLeaveGroupListener
import online.afeibaili.module.minecraft.Minecraft
import online.afeibaili.module.music.NetEasy
import online.afeibaili.module.password.game.PasswordBreak

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
        loadMinecraft()
        loadTranslation()
        loadPasswordBreakGame()
        loadEchoCave()
        loadNetEasy()
        loadOtto()
        loadTerraria()
        loadUpload()
        loadJoinLeaveGroupMessage()
        loadKeyWord()
        loadAdminManager()

        Listener.loadingListener()
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

    fun loadJoinLeaveGroupMessage() {
        JoinLeaveGroupListener.load()
    }

    //model
    fun loadMinecraft() {
        if (config.module.minecraft.isOpen) Minecraft.load()
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

    fun loadNetEasy() {
        NetEasy.load()
    }

    fun loadOtto() {
        Otto.load()
    }

    fun loadUpload() {
        if (config.module.uploadFile.isOpen) UploadFile.load()
    }

    fun loadKeyWord() {
        KeyWordDataJsonFile.load()
    }

    fun loadAdminManager() {
        AdminManagerJsonFile.load()
    }
}