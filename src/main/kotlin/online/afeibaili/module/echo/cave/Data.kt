package online.afeibaili.module.echo.cave

class Data() {
    lateinit var message: String
    var id: Long = 0L
    lateinit var nick: String


    constructor(message: String, id: Long, nick: String) : this() {
        this.message = message
        this.id = id
        this.nick = nick
    }
}