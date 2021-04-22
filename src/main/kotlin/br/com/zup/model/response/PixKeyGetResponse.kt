package br.com.zup.model.response

import br.com.zup.client.itau.model.response.Owner
import br.com.zup.model.domain.BankAccount
import java.time.LocalDateTime

class PixKeyGetResponse(
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
)
