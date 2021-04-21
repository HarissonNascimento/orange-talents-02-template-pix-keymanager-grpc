package br.com.zup.client.bcb.model.request

import br.com.zup.client.bcb.model.BCBBankAccount
import br.com.zup.client.bcb.model.BCBOwner
import br.com.zup.client.bcb.model.request.enums.BCBAccountType
import br.com.zup.client.bcb.model.request.enums.BCBKeyType
import br.com.zup.client.bcb.model.request.enums.BCBOwnerType
import br.com.zup.model.domain.PixKey

data class BCBCreatePixKeyRequest(
    val keyType: BCBKeyType,
    val key: String,
    val bankAccount: BCBBankAccount,
    val owner: BCBOwner
) {

    companion object {

        fun of(pixKey: PixKey): BCBCreatePixKeyRequest {

            val bcbBankAccountRequest = BCBBankAccount(
                participant = pixKey.linkedBankAccount.ispb,
                branch = pixKey.linkedBankAccount.agency,
                accountNumber = pixKey.linkedBankAccount.number,
                accountType = BCBAccountType.by(pixKey.accountType)
            )

            val bcbOwnerRequest = BCBOwner(
                type = BCBOwnerType.NATURAL_PERSON,
                name = pixKey.linkedBankAccount.ownerName,
                taxIdNumber = pixKey.linkedBankAccount.ownerCpf
            )

            return BCBCreatePixKeyRequest(
                keyType = BCBKeyType.by(pixKey.keyType),
                key = pixKey.keyValue,
                bankAccount = bcbBankAccountRequest,
                owner = bcbOwnerRequest
            )
        }

    }

}