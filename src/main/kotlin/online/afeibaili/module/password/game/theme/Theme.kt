package online.afeibaili.module.password.game.theme

import java.awt.Color

/**
 * 抽象主题类
 *
 *@author AfeiBaili
 *@version 2025/7/29 14:01
 */

abstract class Theme {
    abstract val capital: Color
    abstract val char: Color
    abstract val line: Color
    abstract val index: Color
    abstract val emphasize: Color
    abstract val secondary: Color
    abstract val bottomBarFont: Color
    abstract val bottomBarTipsFont: Color
    abstract val bottomBarBorder: Color
    abstract val topBarIconCircle: Color
    abstract val topBarIconRect: Color
}