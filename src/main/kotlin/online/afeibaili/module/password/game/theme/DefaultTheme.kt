package online.afeibaili.module.password.game.theme

import java.awt.Color

class DefaultTheme : Theme() {
    override val capital: Color = Color(234, 230, 227)
    override val char: Color = Color(87, 63, 57)
    override val line: Color
        get() = char
    override val index: Color = Color(37, 35, 35)
    override val emphasize: Color = Color(68, 214, 61)
    override val secondary: Color = Color(221, 214, 212)
    override val bottomBarFont: Color = Color(135, 100, 93)
    override val bottomBarBorder: Color
        get() = secondary
    override val bottomBarTipsFont: Color
        get() = bottomBarFont
    override val topBarIconCircle: Color = Color(106, 75, 72)
    override val topBarIconRect: Color = Color(220, 100, 93)
}