package online.afeibaili.module.password.game.draw

import online.afeibaili.module.password.game.*
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage


/**
 * 顶部条部件
 *
 *@author AfeiBaili
 *@version 2025/7/30 12:37
 */

class TopBar(val width: Int, val height: Int, val allNumber: Int) {
    val image: BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

    init {
        draw(image.createGraphics(), allNumber)
    }

    fun clearCanvas(graphics2D: Graphics2D) {
        graphics2D.background = Color(0, 0, 0, 0)
        graphics2D.clearRect(0, 0, image.width, image.height)
    }

    fun draw(graphics2D: Graphics2D, availableNumber: Int) = with(graphics2D) {
        clearCanvas(graphics2D)
        val marginTop = 10
        val intervalWidth = 5
        val topIconWidth: Int = topHeight / 2

        val iconCenter: Int = topIconWidth * allNumber + intervalWidth * (allNumber - 1)
        val startX = (width - iconCenter) / 2

        var currentX = startX
        val errored: Int = allNumber - availableNumber
        repeat(errored) { i ->
            drawImage(
                Icon(topIconWidth, topHeight, false).image,
                currentX, marginTop, null
            )

            currentX += intervalWidth + topIconWidth
        }
        repeat(availableNumber) { i ->
            drawImage(
                Icon(topIconWidth, topHeight, true).image,
                currentX, marginTop, null
            )

            currentX += intervalWidth + topIconWidth
        }
        dispose()
    }

    fun update(availableNumber: Int) {
        draw(image.createGraphics(), availableNumber)
    }

    class Icon(val width: Int, val height: Int, val isAvailable: Boolean) {
        val image: BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

        init {
            val graphics: Graphics2D = image.createGraphics()
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            drawCircle(graphics)
            if (!isAvailable) {
                drawRect(graphics, 45.0)
                drawRect(graphics, -45.0)
            }

            graphics.dispose()
        }

        fun drawCircle(graphics2D: Graphics2D) = with(graphics2D) {
            color = theme.topBarIconCircle
            stroke = BasicStroke(topIconCircleWidth.toFloat())

            drawOval(
                (width - topIconHeight) / 2,
                (height - topIconHeight) / 2,
                topIconHeight,
                topIconHeight
            )
        }

        fun drawRect(graphics2D: Graphics2D, radians: Double) {
            val oldTransform: AffineTransform = graphics2D.transform
            graphics2D.rotate(Math.toRadians(radians), width / 2.0, height / 2.0)
            graphics2D.color = theme.topBarIconRect
            graphics2D.fillRoundRect(
                (width - topIconRectWidth) / 2,
                (height - topIconRectHeight) / 2,
                topIconRectWidth,
                topIconRectHeight,
                40,
                40,
            )
            graphics2D.transform = oldTransform
        }
    }
}