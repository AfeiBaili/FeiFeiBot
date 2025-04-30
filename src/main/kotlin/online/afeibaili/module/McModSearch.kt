package online.afeibaili.module

import net.mamoe.mirai.message.data.ForwardMessageBuilder
import net.mamoe.mirai.message.data.PlainText
import online.afeibaili.command.Command
import online.afeibaili.command.Commands
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.function.Consumer

object McModSearch {
    fun load() {
        Commands.register("搜词条", Command({ param, event ->
            if (param.size != 2) return@Command "搜词条 <mod名 | 关键词>\n使用MC百科搜索"
            try {
                val document = Jsoup.connect(
                    "https://search.mcmod.cn/s?key=" + URLEncoder.encode(
                        param[1],
                        StandardCharsets.UTF_8
                    ) + "&site=&filter=0&mold=0"
                ).get()
                val takeTime = document.getElementsByClass("info").get(0)
                val startTakeChar = takeTime.toString().indexOf('[')
                val endTakeChar = takeTime.toString().indexOf(']')

                val item = document.getElementsByClass("result-item")
                val forwardMessageBuilder = ForwardMessageBuilder(event.subject)
                item.forEach(Consumer { element: Element? ->
                    val message = StringBuilder()
                    val aTag = element!!.getElementsByClass("head").get(0).lastElementChild()
                    if (aTag != null) {
                        message.append("📌").append(aTag.text()).append('\n')
                            .append("🔗").append(aTag.attr("href")).append('\n')
                            .append("📜").append(element.getElementsByClass("body").text())
                    }
                    forwardMessageBuilder.add(event.subject.bot.id, "查询结果", PlainText(message.toString()))
                })
                event.subject.sendMessage(forwardMessageBuilder.build())

                try {
                    return@Command "从MC百科查找到" + item.size + "条结果；" + takeTime.toString()
                        .substring(startTakeChar + 1, endTakeChar)
                } catch (e: StringIndexOutOfBoundsException) {
                    return@Command "搜不到任何信息"
                }
            } catch (e: IOException) {
                return@Command "无法访问MC百科！"
            }

        }))
    }
}