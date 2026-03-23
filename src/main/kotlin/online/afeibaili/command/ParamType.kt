package online.afeibaili.command

sealed class ParamType(val description: String) {
    open class SingleParam(description: String) : ParamType(description)
    class AT : SingleParam("@QQ")
    class STRING : SingleParam("字符串")
    class INT : SingleParam("整形")
    class LONG : SingleParam("长整形")
    class FLOAT : SingleParam("浮点数")
    class REFERENCE : SingleParam("引用消息")

    class NOTHING : ParamType("无")

    class Multiple(val params: List<SingleParam>) : ParamType("多个参数")

    companion object {
        val AT = AT()
        val STRING = STRING()
        val INT = INT()
        val LONG = LONG()
        val FLOAT = FLOAT()
        val NOTHING = NOTHING()
        val REFERENCE = REFERENCE()
    }

    override fun toString(): String {
        return this.description
    }
}