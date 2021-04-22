package br.com.zup.client.bcb.model.response

import br.com.zup.GrpcAccountType
import br.com.zup.client.bcb.model.BCBBankAccount
import br.com.zup.client.bcb.model.BCBOwner
import br.com.zup.client.bcb.model.request.enums.BCBAccountType
import br.com.zup.client.bcb.model.request.enums.BCBKeyType
import br.com.zup.enums.KeyType
import br.com.zup.model.domain.BankAccount
import br.com.zup.model.domain.PixKey
import java.time.LocalDateTime

data class BCBGetPixKeyResponse(
    val keyType: BCBKeyType,
    val key: String,
    val bankAccount: BCBBankAccount,
    val owner: BCBOwner,
    val createdAt: LocalDateTime
) {
    fun toPixKey(): PixKey {

        val bankAccount = BankAccount(
            institute = null,
            ispb = bankAccount.participant,
            agency = bankAccount.branch,
            number = bankAccount.accountNumber,
            ownerName = owner.name,
            ownerCpf = owner.taxIdNumber,

            )

        return PixKey(
            clientId = null,
            keyType = when(this.keyType){
                BCBKeyType.CPF -> KeyType.CPF
                BCBKeyType.EMAIL -> KeyType.EMAIL
                BCBKeyType.PHONE -> KeyType.TELEFONE_CELULAR
                BCBKeyType.RANDOM -> KeyType.CHAVE_ALEATORIA
                else -> throw IllegalArgumentException("Key type invalid or not found for ${this.keyType}")
            },
            keyValue = key,
            accountType = when(this.bankAccount.accountType){
                BCBAccountType.CACC -> GrpcAccountType.CONTA_CORRENTE
                BCBAccountType.SVGS -> GrpcAccountType.CONTA_POUPANCA
            },
            linkedBankAccount = bankAccount
        )
    }
}

