package com.vertx.china.vtalk

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class MessageModel(
    val id: String? = null,
    val message: String,
    val time: String? = null,
    val nickname: String? = null,
    val isMine: Boolean = false
)


@JsonClass(generateAdapter = true)
data class SendMessageModel(
    val message: String,
    val nickname: String? = ""
)
