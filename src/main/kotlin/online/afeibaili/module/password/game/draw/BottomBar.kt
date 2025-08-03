package online.afeibaili.module.password.game.draw

import online.afeibaili.module.password.game.*
import java.awt.*
import java.awt.image.BufferedImage


/**
 * 底部条
 *
 *@author AfeiBaili
 *@version 2025/7/30 12:47
 */

class BottomBar(val width: Int, val height: Int, val letterSet: LinkedHashSet<Char>) {
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).apply {
        createGraphics().apply {
            color = theme.secondary
            fillRect(0, 0, width, height)
        }
    }
    val gameFont: Font = font

    init {
        draw(image.createGraphics())
    }

    fun draw(graphics2D: Graphics2D) {
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        drawLetter(graphics2D)
        drawTips(graphics2D)

        graphics2D.dispose()
    }

    /** 需要绘制的字母 */
    fun drawLetter(graphics2D: Graphics2D) = with(graphics2D) {
        val leftPadding = padding / 2
        val topPadding = padding / 2
        var currentX = leftPadding
        var currentY = topPadding

        val intervalWidth = 5
        val intervalHeight = 10

        letterSet.sorted().forEach { it ->
            if (currentX > width - leftPadding * 2) {
                currentX = leftPadding
                currentY += charHeight + intervalHeight
            }

            drawImage(
                Letter(it).image, currentX, currentY, null
            )
            currentX += intervalWidth + charWidth
        }
    }

    fun drawTips(graphics2D: Graphics2D) = with(graphics2D) {
        font = gameFont.deriveFont(bottomBarTipsSize)
        val metrics1: FontMetrics = getFontMetrics(font)
        val beDrawString = "Tips: 以下为可用字符"
        val stringWidth1: Int = metrics1.stringWidth(beDrawString)
        val stringHeight1: Int = metrics1.height
        val stringAscent1: Int = metrics1.ascent
        color = theme.bottomBarTipsFont
        drawString(
            beDrawString,
            width / 2 - stringWidth1 / 2,
            stringAscent1 + stringHeight1 / 2
        )
    }

    class Letter(val char: Char) {
        val image = BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB)
        val gameFont: Font = font

        init {
            draw(image.createGraphics())
        }

        fun draw(graphics2D: Graphics2D) = with(graphics2D) {
            setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            font = gameFont.deriveFont(charSize).deriveFont(Font.BOLD)
            val metrics: FontMetrics = getFontMetrics(font)
            val stringWidth: Int = metrics.stringWidth(char.toString())
            val stringHeight: Int = metrics.height
            val stringAscent: Int = metrics.ascent
            stroke = BasicStroke(bottomBorderRoundWidth)

            val inset = bottomBorderRoundWidth / 2
            color = theme.capital
            drawRoundRect(
                inset.toInt(),
                inset.toInt(),
                charWidth - bottomBorderRoundWidth.toInt(),
                charHeight - bottomBorderRoundWidth.toInt(),
                15,
                15
            )

            color = theme.bottomBarFont
            drawString(
                char.toString(),
                charWidth / 2 - stringWidth / 2,
                charHeight / 2 - stringHeight / 2 + stringAscent,
            )

            dispose()
        }
    }
}
