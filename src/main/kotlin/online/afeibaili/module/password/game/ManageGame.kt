package online.afeibaili.module.password.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.FileReader
import java.util.*


object ManageGame {
    private val gameMap = mutableMapOf<Long, Game>()
    val random: Random = Random()
    val sentenceList get() = loadSentenceList()
    val sendMassageScope = CoroutineScope(Dispatchers.Default)

    fun play(groupId: Long): String {
        val sentences = sentenceList
        gameMap[groupId]?.let { return "当前群里游戏未结束" }
        gameMap[groupId] = Game(groupId, sentences[random.nextInt(0, sentences.size)])

        return "密文破译游戏已创建"
    }

    fun remove(groupId: Long): String {
        val removedGame: Game? = gameMap.remove(groupId)
        removedGame ?: return "当前没有游戏创建"
        removedGame.over()

        return "已移除此局游戏"
    }

    fun loadSentenceList(): MutableList<String> {
        val lines: MutableList<String> = FileReader(createOrGetSentencesFile()).use { reader ->
            reader.readLines().filter { it.isNotEmpty() }
        }.toMutableList()
        if (lines.isEmpty())
            lines.add("I love ice cream, especially on a hot summer day when it's cold and sweet, melting in my mouth.")
        return lines
    }
}