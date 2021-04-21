package br.com.zup.client.bcb.model

import br.com.zup.client.bcb.model.request.enums.BCBAccountType

data class BCBBankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: BCBAccountType
)
