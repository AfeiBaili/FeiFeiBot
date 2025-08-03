package online.afeibaili.module.password.game


/**
 * 设置图片信息，大小等
 *
 *@author AfeiBaili
 *@version 2025/7/29 13:55
 */


//宽高
const val width = 1000
const val height = 1600

//主容器内边距
const val padding = 100

//一行最大的字符数为16 实际 14
const val maxChars = 16
const val charLineCount = 14

//字体大小
const val charSize = 64f
const val indexSize = 16f
const val bottomBarTipsSize = 24f

//单个字符的宽度（需要注意是否可被整除）
const val charWidth = width / maxChars

//单个字符的宽度
const val charHeight = 100

//顶部条
const val topHeight = 200
const val topIconHeight = 70
const val topIconRectWidth = 90
const val topIconRectHeight = 15
const val topIconCircleWidth = 10

//主容器
const val mainContainerHeight = 1100

//底部条
const val bottomHeight = 300

//底部字母圆角边框大小
const val bottomBorderRoundWidth = 3f