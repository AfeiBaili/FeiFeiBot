package online.afeibaili.module

import online.afeibaili.command.Command
import online.afeibaili.command.Commands
import online.afeibaili.translation.Translation

object Translation {
    fun load() {
        Commands.register("翻译", Command({ param, event ->
            if (param.size == 1) return@Command "请填写要翻译的内容"
            val sb = StringBuilder()
            for (i in 1 until param.size) {
                sb.append(param[i]).append(" ")
            }
            sb.deleteCharAt(sb.length - 1)
            val q = sb.toString()

            val counts = Translation.countLettersVsNonLetters(q)
            val letterCount = counts[0]
            val nonLetterCount = counts[1]

            if (letterCount > nonLetterCount) return@Command Translation.parseResult(
                Translation.translate(
                    q,
                    "en",
                    "zh-CHS"
                )
            )
            else return@Command Translation.parseResult(Translation.translate(q, "zh-CHS", "en"))
        }))
    }
}