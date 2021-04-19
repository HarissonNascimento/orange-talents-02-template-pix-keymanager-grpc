package br.com.zup.client.itau.model.response

import br.com.zup.model.domain.BankAccount
import com.fasterxml.jackson.annotation.JsonProperty

data class AccountClientResponse(
    @field:JsonProperty("tipo")
    val type: String,
    @field:JsonProperty("instituicao")
    val institute: Institute,
    @field:JsonProperty("agencia")
    val agency: String,
    @field:JsonProperty("numero")
    val number: String,
    @field:JsonProperty("titular")
    val owner: Owner
) {
    fun toBankAccount(): BankAccount {
        return BankAccount(
            institute = institute.name,
            ispb = institute.ispb,
            agency = agency,
            number = number,
            ownerName = owner.name,
            ownerCpf = owner.cpf
        )
    }
}

