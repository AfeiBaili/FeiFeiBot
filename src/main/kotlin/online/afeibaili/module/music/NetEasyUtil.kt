package online.afeibaili.module.music

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import online.afeibaili.module.music.map.Music
import java.io.InputStream
import java.math.BigInteger
import java.net.URI
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 * 工具类
 *
 *@author AfeiBaili
 *@version 2026/3/27 21:42
 */

private const val SEARCH_URL = "https://apis.netstart.cn/music/search?keywords="
private const val GET_IDS_URL = "https://music.163.com/weapi/cloudsearch/get/web?csrf_token="
private val client = HttpClient.newHttpClient()
private val JSON = ObjectMapper()

fun queryIds(keyword: String): Music? = runCatching {
    val openConnection: URLConnection =
        URL(SEARCH_URL + URLEncoder.encode(keyword, StandardCharsets.UTF_8)).openConnection()
    val jsonText: String = openConnection.inputStream.reader().use { it.readText() }
    JSON.readValue(jsonText, Music::class.java)
}.getOrNull()

fun getSongUriById(id: String): URI? {
    val request: HttpRequest =
        HttpRequest.newBuilder().uri(URI.create("https://luren.online:2345/proxy/musicUrl?id=${id}&level=standard"))
            .setHeader("Authorization", "7uSi+uq1GB9syMnwzVPKvCK6n+yCe9Y0t4wk/zFS3KM=").GET().build()

    return runCatching {
        val body: String = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        val url: String = JSON.readTree(body).get("url").asText()
        URI.create(url)
    }.getOrNull()
}

fun getSongStreamByUri(songUri: URI): InputStream? {
    val songRequest: HttpRequest = HttpRequest.newBuilder(songUri).GET().build()
    return runCatching {
        client.send(songRequest, HttpResponse.BodyHandlers.ofInputStream()).body()
    }.getOrNull()
}

////////////////////////////////////////////////

fun queryIdsWithCookie(keyword: String): MusicInfo {
    val request: HttpRequest =
        HttpRequest.newBuilder().uri(URI.create(GET_IDS_URL)).setHeader("Referer", "http://music.163.com")
            .setHeader("Cookie", getNetEasyCookie()).setHeader("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(buildBody(keyword))).build()

    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

    val js: JsonNode = JSON.readTree(response.body()).get("result").get("songs").get(0)
    val id = js.get("id").asText()
    val jurl = "http://music.163.com/song/media/outer/url?id=$id.mp3"

    return MusicInfo(
        js.get("name").asText(),
        js.get("ar").get(0).get("name").asText(),
        js.get("al").get("picUrl").asText(),
        "https://y.music.163.com/m/song?id=$id",
        jurl,
        "网易云音乐",
        "",
        100495085
    )
}

private fun buildBody(keyword: String): String {
    val param: List<String> = encryptParam(getJson(keyword))
    val encParams = URLEncoder.encode(param[0], "UTF-8")
    val encSecKey = URLEncoder.encode(param[1], "UTF-8")

    return "params=$encParams&encSecKey=$encSecKey"
}

private fun encryptParam(content: String): List<String> {
    val list = mutableListOf<String>()
    val randomKey = UUID.randomUUID().toString().replace("-", "").take(16)
    val aesEncrypt: String = aesEncrypt(aesEncrypt(content, NetEasyEnum.KEY, NetEasyEnum.IV), randomKey, NetEasyEnum.IV)
    val rsaEncrypt: String = rsaEncrypt(randomKey, NetEasyEnum.modules)
    list += aesEncrypt
    list += rsaEncrypt

    return list
}

private fun rsaEncrypt(randomKey: String, modules: String): String {
    val reversedKey = randomKey.reversed()
    val bytes = reversedKey.toByteArray()
    val hexString = bytes.joinToString("") { "%02x".format(it) }

    val bigText = BigInteger(hexString, 16)
    val bigEx = BigInteger(NetEasyEnum.PUBLIC_KEY, 16)
    val bigMod = BigInteger(modules, 16)
    val bigRet = bigText.modPow(bigEx, bigMod)
    return bigRet.toString(16).padStart(256, '0')
}

private fun aesEncrypt(content: String, key: String, iv: String): String {
    val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding").apply {
        init(
            Cipher.ENCRYPT_MODE, SecretKeySpec(key.toByteArray(), "AES"), IvParameterSpec(iv.toByteArray())
        )
    }
    return Base64.getEncoder().encodeToString(cipher.doFinal(content.toByteArray()))
}

private fun getJson(keyword: String): String {
    return JSON.writeValueAsString(
        JSON.createObjectNode().put("s", keyword).put("type", 1).put("offset", 0).put("limit", 3)
    )
}

private fun getNetEasyCookie() = """
    appver=1.5.0.75771;
    MUSIC_U=
    007247F05C70D1D7A57EE9B5168B7765E9C5DA3
    9F7B1A6E5F1B30EB1CD64475E0FA0DC07FF2C44
    3E8DC50BACAF532D8600375F4C1134BF9842BAD
    CC0AA75CD53920F8272CE8DFFD8335638BEE763
    2BBE945055537D26CD7878B178957410129FC21
    57559EDDCCCF36119D97C86D9B9A95569034F52
    B4FE9E49F3872A2994A8A768BC83F694B90E281
    96450A10FD48B65BEE1FA90E439B0B7271661FB
    10C6F7CF8E94880E16566606516F60E5A244EAF
    A13E8AC43204FDB39D5EE15D6E0490BE11390B4
    844B3235519A84D5EBB6A328319746FDF0DBC7B
    CE605A106DC3CC4DB539D947B380D81ABBB2E4B
    412836E3FCA4F9C5197FA6C90488205BEB8E1E5
    D5A909B3A5B7950F62BB4040EB9C8038ACE0A37
    1F23CC54095B9A7A96B25037221C4FA532F6D40
    4B4D6B0DA0E42615F0D32AB247F6E07ED08AE2C
    15F978E7EF2418A4EA3ABC10DF9F476DBF1FD5F
    60F2E1980E7B520C4112DC6DFC10E344B91315E
    30A7F7ABE57B3636E2DC14C54284139965589F2
    0605F97BBC6C43D3FF526EE2411AB00E6C86AD5
    013BE1C5B818F191F330488BCD3BF3DAC280497
    CC881A64002830B;
""".trimIndent().replace("\n", "")

private object NetEasyEnum {
    const val KEY = "0CoJUm6Qyw8W8jud"
    const val IV = "0102030405060708"
    const val PUBLIC_KEY = "010001"
    val modules = """
        00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace6
        15bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135
        fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c368
        5b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52
        741d546b8e289dc6935b3ece0462db0a22b8e7""".trimIndent().replace("\n", "")
}