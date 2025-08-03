package online.afeibaili.module.password.game.draw.data

import online.afeibaili.module.password.game.*
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage


/**
 * 字类
 *
 *@author AfeiBaili
 *@version 2025/7/29 13:20
 */

data class Letter(var charPair: Pair<Char, Char>, val index: Int, val context: Context) {
    val gameFont: Font = font
    val image = BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB)

    init {
        draw(image.createGraphics(), charPair.second)
    }

    fun getIndex(char: Char): Int = context.letterRandomMap[char] ?: -1

    fun update(char: Char): Result {
        if (charPair.first == char) {
            charPair = Pair(char, char)
            draw(image.createGraphics(), char, Color(0, 255, 100, 120))
            return Result.LetterCorrect
        } else {
            draw(image.createGraphics(), char, Color(255, 0, 50, 120))
            return Result.LetterError
        }
    }

    fun clearCanvas(graphics2D: Graphics2D) {
        graphics2D.background = Color(0, 0, 0, 0)
        graphics2D.clearRect(0, 0, charWidth, charHeight)
    }

    fun draw(graphics2D: Graphics2D, letter: Char, background: Color = Color(0, 0, 0, 0)) {
        clearCanvas(graphics2D)
        drawLetter(graphics2D, letter)
        if (getIndex(charPair.first) == -1) return
        drawLine(graphics2D)
        drawIndex(graphics2D)
        drawBackground(graphics2D, background)

        graphics2D.dispose()
    }

    fun drawLetter(graphics2D: Graphics2D, letter: Char) = with(graphics2D) {
        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        font = gameFont.deriveFont(charSize).deriveFont(Font.BOLD)
        color = theme.char

        //绘制字母
        drawString(
            if (letter == '*') "" else letter.toString(),
            charWidth / 4,
            charSize.toInt()
        )
    }

    fun drawLine(graphics2D: Graphics2D) = with(graphics2D) {
        color = theme.line
        //10的线内边距
        val linePadding = 10
        fillRect(linePadding, charSize.toInt() + 1, charWidth - linePadding * 2, 3)
    }

    fun drawIndex(graphics2D: Graphics2D) = with(graphics2D) {
        val centerLineWidth = 3
        val centerLineHeight = 10
        val charWidthDivTwo: Int = charWidth / 2

        //索引号
        color = theme.index
        font = gameFont.deriveFont(indexSize).deriveFont(Font.BOLD)
        drawString(
            index.toString(), charWidthDivTwo / 3, (charSize + 5 + indexSize).toInt()
        )

        //标识号
        color = theme.char
        drawString(
            getIndex(charPair.first).toString(),
            charWidthDivTwo + charWidthDivTwo / 4,
            (charSize + 5 + indexSize).toInt()
        )

        color = theme.line
        fillRect(
            charWidthDivTwo - centerLineWidth / 2,
            charSize.toInt() + centerLineHeight,
            centerLineWidth,
            centerLineHeight
        )
    }

    fun drawBackground(graphics2D: Graphics2D, background: Color) = with(graphics2D) {
        color = background
        fillRoundRect(0, 0, charWidth, charHeight, 15, 15)
    }
}