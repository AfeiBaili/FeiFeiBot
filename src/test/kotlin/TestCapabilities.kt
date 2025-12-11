import online.afeibaili.file.ConfigFile
import online.afeibaili.file.json.JsonConfigMap
import online.afeibaili.translation.Translation
import org.junit.Before
import kotlin.test.Test

/**
 * 测试功能
 *
 *@author AfeiBaili
 *@version 2025/12/3 18:09
 */

class TestCapabilities {
    @Before
    fun before() {
        val config: JsonConfigMap = ConfigFile().config
        online.afeibaili.config = config
    }

    @Test
    fun test1() {
        println(Translation.parseResult(Translation.translate("啊", "zh-CHS", "en")))
    }
}