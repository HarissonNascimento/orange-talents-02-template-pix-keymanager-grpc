package br.com.zup.client.bcb.model.request

import br.com.zup.GrpcAccountType
import br.com.zup.client.bcb.model.request.enums.BCBAccountType
import br.com.zup.client.bcb.model.request.enums.BCBKeyType
import br.com.zup.enums.KeyType
import br.com.zup.model.domain.BankAccount
import br.com.zup.model.domain.PixKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class BCBCreatePixKeyRequestTest {

    @Test
    fun `create valid BCBCreatePixKeyRequest when you receive valid PixKey`() {

        val bankAccount = BankAccount(
            institute = "ITAÚ UNIBANCO S.A.",
            ispb = "60701190",
            agency = "0001",
            number = "084329",
            ownerName = "Client Test",
            ownerCpf = "11111111111"
        )

        val pixKey = PixKey(
            clientId = UUID.randomUUID(),
            keyType = KeyType.CHAVE_ALEATORIA,
            keyValue = UUID.randomUUID().toString(),
            accountType = GrpcAccountType.CONTA_CORRENTE,
            linkedBankAccount = bankAccount
        )

        with(BCBCreatePixKeyRequest.of(pixKey)) {
            assertEquals(BCBKeyType.by(pixKey.keyType), this.keyType)
            assertEquals(pixKey.keyValue, this.key)
            assertEquals(pixKey.linkedBankAccount.ispb, this.bankAccount.participant)
            assertEquals(pixKey.linkedBankAccount.agency, this.bankAccount.branch)
            assertEquals(pixKey.linkedBankAccount.number, this.bankAccount.accountNumber)
            assertEquals(BCBAccountType.by(pixKey.accountType), this.bankAccount.accountType)
            assertEquals(pixKey.linkedBankAccount.ownerName, this.owner.name)
            assertEquals(pixKey.linkedBankAccount.ownerCpf, this.owner.taxIdNumber)
        }

    }
}