package online.afeibaili.module.password.game

import online.afeibaili.LoggerLevel
import online.afeibaili.config
import online.afeibaili.logger
import online.afeibaili.module.password.game.theme.DefaultTheme
import java.awt.Font
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileWriter


/**
 * 密文破译工具类
 *
 *@author AfeiBaili
 *@version 2025/7/29 19:44
 */

val theme = DefaultTheme()
val sentenceWriter = FileWriter(createOrGetSentencesFile())

fun logsSentence(sentence: String) {
    ManageGame.sentenceList.add(sentence)
    sentenceWriter.write(sentence + "\n")
    sentenceWriter.flush()
}

fun createOrGetSentencesFile(): File {
    val sentencesFile = File(System.getProperty("user.dir") + "/data/feifei/sentences.txt")
    if (!sentencesFile.exists()) {
        sentencesFile.parentFile.mkdirs()
        sentencesFile.createNewFile()
        logger(sentencesFile.toString() + "文件不存在，已生成文件")
    }
    return sentencesFile
}

val font: Font = let {
    var fileInputStream: FileInputStream? = null
    try {
        fileInputStream =
            FileInputStream("${System.getProperty("user.dir")}/${config.module.passwordBreakGame.fontPath}")
        Font.createFont(
            Font.TRUETYPE_FONT,
            fileInputStream,
        )
    } catch (e: FileNotFoundException) {
        logger("字体文件找不到请检查配置文件", LoggerLevel.WARN)
        throw e
    } finally {
        fileInputStream?.close()
    }
}

fun <T> let(action: () -> T): T {
    return action.invoke()
}