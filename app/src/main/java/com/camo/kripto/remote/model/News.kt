package com.camo.kripto.remote.model

data class News(
    val status_updates: List<StatusUpdate>
) {
    data class StatusUpdate(
        val description: String,
        val category: String,
        val created_at: String,
        val user: String?,
        val user_title: String?,
        val pin: Boolean,
        val project: Project
    ) {
        data class Project(
            val type: String,
            val id: String,
            val name: String,
            val symbol: String,
            val image: Image
        ) {
            data class Image(
                val thumb: String,
                val small: String,
                val large: String
            )
        }
    }
}