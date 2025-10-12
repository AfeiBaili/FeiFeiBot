package online.afeibaili.bot.json.map

import com.fasterxml.jackson.annotation.JsonIgnoreProperties


/**
 * 千问生图json映射
 *
 *@author AfeiBaili
 *@version 2025/10/12 18:15
 */

class QwenImageGenerateImageRequest(vararg imageUrl: String, promptWords: String) {
    var model = "qwen-image-edit"
    var input = Input()

    init {
        val images: List<Image> = imageUrl.map { Image(it) }
        input.messages[0].content.addAll(images)
        input.messages[0].content.add(Text(promptWords))
    }

    class Input {
        var messages = ArrayList<Message>()

        init {
            messages.add(Message())
        }
    }

    class Message {
        var role = "user"
        var content = ArrayList<Content>()
    }

    open class Content
    class Image(var image: String) : Content() {
    }

    class Text(var text: String) : Content() {
    }
}

class QwenTextGenerateImageRequest(promptWords: String) {
    var model = "qwen-image-plus"
    var input = Input()

    init {
        input.messages[0].content[0].text = promptWords
    }

    class Input {
        var messages = ArrayList<Message>()

        init {
            messages.add(Message())
        }
    }

    class Message {
        var role = "user"
        val content = ArrayList<Content>()

        init {
            content.add(Content())
        }
    }

    class Content {
        var text = ""
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class QwenGenerateImageResponse() {
    var output = Output()

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Output {
        var choices = ArrayList<Choice>()
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Choice {
        var message = Message()
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Message {
        var role = "assistant"
        var content = ArrayList<Content>()
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Content {
        var image = ""
    }
}