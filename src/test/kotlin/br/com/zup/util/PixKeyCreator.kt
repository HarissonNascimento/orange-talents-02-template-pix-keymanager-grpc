package br.com.zup.util

import br.com.zup.GrpcAccountType
import br.com.zup.GrpcKeyType
import br.com.zup.enums.KeyType
import br.com.zup.model.domain.BankAccount
import br.com.zup.model.domain.PixKey
import br.com.zup.util.PixKeyCreator.Companion.CLIENT_ID
import java.util.*

class PixKeyCreator {

    companion object {
        val CLIENT_ID: UUID = UUID.randomUUID()
    }

}

fun createValidPixKey(): PixKey {
    return PixKey(
        clientId = CLIENT_ID,
        keyType = KeyType.valueOf(GrpcKeyType.EMAIL.name),
        keyValue = "test@email.com",
        accountType = GrpcAccountType.CONTA_CORRENTE,
        linkedBankAccount = createValidBankAccount()
    )
}

fun createValidBankAccount(): BankAccount{
    return BankAccount(
        institute = "Institute Test",
        ispb = "00000000",
        agency = "0000",
        number = "000000",
        ownerName = "Owner Test",
        ownerCpf = "00000000000"
    )
}