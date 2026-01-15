package online.afeibaili.command

import net.mamoe.mirai.event.events.MessageEvent


/**
 * 命令基类
 *
 *@author AfeiBaili
 *@version 2026/1/14 11:22
 */

data class Command(
    val name: String,
    val alias: String,
    val level: Int = 0,
    val paramType: ParamType = ParamType.NOTHING,
    val action: suspend Command.(Array<String>, MessageEvent) -> String,
    val childCommand: CommandCollection? = null,
) {
    constructor(
        name: String,
        alias: String,
        action: suspend Command.(Array<String>, MessageEvent) -> String,
    ) : this(name, alias, 0, ParamType.NOTHING, action, null)

    constructor(
        name: String,
        alias: String,
        level: Int,
        paramType: ParamType,
        action: suspend Command.(Array<String>, MessageEvent) -> String,
    ) : this(
        name, alias, level, paramType, action, null,
    )

    constructor(
        name: String,
        alias: String,
        level: Int,
        paramType: ParamType,
        childCommand: CommandCollection?,
        action: suspend Command.(Array<String>, MessageEvent) -> String,
    ) : this(name, alias, level, paramType, action, childCommand)

    fun toDetailString(indent: String = "", isFirstTime: Boolean = true): String {
        val nextIndent = "$indent    "
        return buildString {
            if (isFirstTime) appendLine()
            append(toPrettyString(indent))
            childCommand?.forEach {
                append(it.toDetailString(nextIndent, false))
            }
        }
    }

    fun toPrettyString(indent: String): String {
        val paramText = when (paramType) {
            is ParamType.Multiple -> paramType.params.joinToString("、")
            else -> paramType.toString()
        }

        return buildString {
            appendLine("$indent-> 命令: $name ($alias)")
            appendLine("$indent     等级: $level")
            appendLine("$indent     参数: $paramText")
            if (childCommand != null) {
                appendLine("$indent     子命令: ${childCommand.list.joinToString("、")}")
            }
        }
    }

    override fun toString(): String {
        return buildString {
            append("命令：${name} ($alias)  级别：$level")
            if (childCommand != null) {
                append("子命令：${childCommand.list.joinToString("、")}\\n")
            }
            appendLine()
        }
    }
}