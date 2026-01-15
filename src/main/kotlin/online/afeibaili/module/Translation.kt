package online.afeibaili.module

import online.afeibaili.command.CommandRegistry
import online.afeibaili.command.ParamType
import online.afeibaili.translation.Translation

object Translation {
    fun load() {
        CommandRegistry.register("翻译", "tran", 0, ParamType.STRING) { p, _ ->
            val q: String = p.joinToString(" ").also { if (it.isEmpty()) return@register "参数不能为空" }

            val counts = Translation.countLettersVsNonLetters(q)
            val letterCount = counts[0]
            val nonLetterCount = counts[1]

            runCatching {
                if (letterCount > nonLetterCount)
                    return@register Translation.parseResult(Translation.translate(q, "en", "zh-CHS"))
                else
                    return@register Translation.parseResult(Translation.translate(q, "zh-CHS", "en"))
            }

            "翻译服务器故障，请重试"
        }
    }
}