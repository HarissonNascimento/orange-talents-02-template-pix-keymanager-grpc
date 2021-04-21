package br.com.zup.client.bcb.model.response

import java.time.LocalDateTime

data class BCBDeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
)
