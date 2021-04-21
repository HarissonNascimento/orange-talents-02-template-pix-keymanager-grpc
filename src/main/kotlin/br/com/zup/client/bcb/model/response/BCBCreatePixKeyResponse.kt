package br.com.zup.client.bcb.model.response

import br.com.zup.client.bcb.model.BCBBankAccount
import br.com.zup.client.bcb.model.BCBOwner
import br.com.zup.client.bcb.model.request.enums.BCBKeyType
import java.time.LocalDateTime

data class BCBCreatePixKeyResponse(
    val keyType: BCBKeyType,
    val key: String,
    val bankAccount: BCBBankAccount,
    val owner: BCBOwner,
    val createdAt: LocalDateTime
)
