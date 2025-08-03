package online.afeibaili.module.password.game.draw.data


/**
 * 处理结果的返回值
 *
 *@author AfeiBaili
 *@version 2025/7/31 13:40
 */

enum class Result() {
    LetterCorrect("字母解析正确"),
    LetterError("字母解析错误"),
    GameVictory("游戏胜利"),
    GameContinue("游戏继续"),
    ;

    constructor(value: Any) : this() {

    }
}