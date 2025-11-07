package online.afeibaili.bot

import net.mamoe.mirai.contact.Contact

interface Stream {
    suspend fun sendAsStream(message: String, contact: Contact, role: String = "user")
}