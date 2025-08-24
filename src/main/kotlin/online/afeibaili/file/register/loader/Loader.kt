package online.afeibaili.file.register.loader


/**
 * 文件加载器，用于编写加载指定逻辑
 *
 *@author AfeiBaili
 *@version 2025/8/24 10:47
 */

interface Loader<T> {
    fun load(string: String): T
}