package online.afeibaili.module.password.game.draw.data

import online.afeibaili.module.password.game.charHeight
import online.afeibaili.module.password.game.charWidth
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage


/**
 * 词类，里面包含多个字
 *
 *@author AfeiBaili
 *@version 2025/7/29 13:20
 */

class Word(val text: Pair<String, String>, val indexStart: Int, val context: Context) : ArrayList<Letter>() {
    val messingLetterList = mutableListOf<Letter>()
    var lastUpdateLetter: Letter? = null

    init {
        for (index in text.first.indices) {

            val letter = Letter(
                Pair(text.first[index], text.second[index]),
                index + indexStart, context
            )
            if (text.second[index] == '*') messingLetterList.add(letter)
            this.add(letter)
        }
    }

    val image = BufferedImage(text.first.length * charWidth, charHeight, BufferedImage.TYPE_INT_ARGB)

    init {
        draw(image.createGraphics())
    }

    fun clearCanvas(graphics2D: Graphics2D) {
        graphics2D.background = Color(0, 0, 0, 0)
        graphics2D.clearRect(0, 0, image.width, image.height)
    }

    fun draw(graphics2D: Graphics2D) = with(graphics2D) {
        clearCanvas(graphics2D)
        forEach { letter ->
            drawImage(letter.image, (letter.index - indexStart) * charWidth, 0, null)
        }
        dispose()
    }

    fun update(index: Int, char: Char): Pair<Result, Int> {
        lastUpdateLetter?.draw(
            lastUpdateLetter!!.image.createGraphics(), lastUpdateLetter!!.charPair.second
        )

        var result: Result = Result.LetterError
        val beDeletedLetterList = mutableListOf<Letter>()
        messingLetterList.forEach { letter ->
            if (index != letter.index) return@forEach
            result = letter.update(char)
            if (result == Result.LetterCorrect) {
                beDeletedLetterList.add(letter)
            }
            lastUpdateLetter = letter
        }
        draw(image.createGraphics())
        messingLetterList.removeAll(beDeletedLetterList)

        return Pair(result, messingLetterList.size)
    }
}
