package online.afeibaili.command

import net.mamoe.mirai.event.events.MessageEvent

object CommandRegistry {
    val commandCollection = CommandCollection()

    fun register(
        commandName: String,
        alias: String,
        level: Int = 0,
        param: ParamType = ParamType.NOTHING,
        action: suspend Command.(Array<String>, MessageEvent) -> String,
    ): Command {
        val command = Command(commandName, alias, level, param, action)
        commandCollection += command
        return command
    }

    fun registerWithChild(
        commandName: String,
        alias: String,
        level: Int = 0,
        param: ParamType = ParamType.NOTHING,
        action: suspend Command.(Array<String>, MessageEvent) -> String,
        vararg commands: Command,
    ): Command {
        val collection = CommandCollection()
        commands.forEach { collection += it }
        val command = Command(commandName, alias, level, param, action, collection)
        commandCollection += command
        return command
    }

    fun register(
        commandName: String,
        alias: String,
        action: suspend Command.(Array<String>, MessageEvent) -> String,
    ): Command {
        return register(commandName, alias, 0, ParamType.NOTHING, action)
    }

    fun register(
        commandName: String,
        alias: String,
        param: ParamType = ParamType.NOTHING,
        action: suspend Command.(Array<String>, MessageEvent) -> String,
    ): Command {
        return register(commandName, alias, 0, param, action)
    }
}