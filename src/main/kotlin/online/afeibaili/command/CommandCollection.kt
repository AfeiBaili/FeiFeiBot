package online.afeibaili.command


/**
 * 命令集合
 *
 *@author AfeiBaili
 *@version 2026/1/14 11:30
 */

class CommandCollection() : Iterable<Command> {
    val nameMap = LinkedHashMap<String, Command>()
    val aliasMap = LinkedHashMap<String, Command>()

    var index = 0
    val list get() = nameMap.keys

    operator fun plusAssign(command: Command) {
        add(command)
    }

    fun add(command: Command) {
        nameMap[command.name] = command
        aliasMap[command.alias] = command
    }

    operator fun get(commandName: String): Command? {
        nameMap[commandName]?.let { return it }
        aliasMap[commandName]?.let { return it }
        return null
    }

    override fun iterator(): Iterator<Command> {
        return nameMap.values.iterator()
    }

    companion object {
        fun create(vararg commands: Command): CommandCollection {
            val collection = CommandCollection()
            commands.forEach { collection.add(it) }
            return collection
        }
    }
}