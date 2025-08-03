package online.afeibaili.module.password.game.draw

import online.afeibaili.module.password.game.Game
import online.afeibaili.module.password.game.draw.data.Result
import online.afeibaili.module.password.game.draw.data.Sentence
import online.afeibaili.module.password.game.padding
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage


/**
 * 主容器
 *
 *@author AfeiBaili
 *@version 2025/7/30 12:44
 */

class MainContainer(val width: Int, val height: Int, val sentence: Sentence) {
    val image: BufferedImage =
        BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

    init {
        draw(image.createGraphics())
    }

    fun clearCanvas(graphics: Graphics2D) = with(graphics) {
        graphics.background = Color(0, 0, 0, 0)
        graphics.clearRect(0, 0, this@MainContainer.width, this@MainContainer.height)
    }

    fun draw(graphics: Graphics2D) = with(graphics) {
        clearCanvas(graphics)
        drawImage(sentence.image, padding, 0, null)
        dispose()
    }

    fun update(index: Int, char: Char, gameSetting: Game.Companion.GameSetting): Pair<Result, Result> {
        val result = sentence.update(index, char, gameSetting)
        draw(image.createGraphics())
        return result
    }
}