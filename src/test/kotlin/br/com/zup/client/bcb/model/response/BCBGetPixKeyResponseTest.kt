package br.com.zup.client.bcb.model.response

import br.com.zup.client.bcb.model.BCBBankAccount
import br.com.zup.client.bcb.model.BCBOwner
import br.com.zup.client.bcb.model.request.enums.BCBAccountType
import br.com.zup.client.bcb.model.request.enums.BCBKeyType
import br.com.zup.client.bcb.model.request.enums.BCBOwnerType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

internal class BCBGetPixKeyResponseTest {

    @Test
    fun `must return valid pixkey`() {

        val bcbResponse = generateValidBCBGetPixKeyResponse()

        with(bcbResponse.toPixKey()) {
            assertNull(this.clientId)
            assertEquals(bcbResponse.keyType, BCBKeyType.by(this.keyType))
            assertEquals(bcbResponse.key, this.keyValue)
            assertEquals(bcbResponse.bankAccount.accountType, BCBAccountType.by(this.accountType))
            assertNull(this.linkedBankAccount.institute)
            assertEquals(bcbResponse.bankAccount.participant, this.linkedBankAccount.ispb)
            assertEquals(bcbResponse.bankAccount.branch, this.linkedBankAccount.agency)
            assertEquals(bcbResponse.bankAccount.accountNumber, this.linkedBankAccount.number)
            assertEquals(bcbResponse.owner.name, this.linkedBankAccount.ownerName)
            assertEquals(bcbResponse.owner.taxIdNumber, this.linkedBankAccount.ownerCpf)
        }
    }

    private fun generateValidBCBGetPixKeyResponse(): BCBGetPixKeyResponse {
        return BCBGetPixKeyResponse(
            keyType = BCBKeyType.RANDOM,
            key = UUID.randomUUID().toString(),
            bankAccount = BCBBankAccount(
                participant = "60701190",
                branch = "0001",
                accountNumber = "084329",
                accountType = BCBAccountType.CACC
            ),
            owner = BCBOwner(
                type = BCBOwnerType.NATURAL_PERSON,
                name = "Client Test",
                taxIdNumber = "11111111111"
            ),
            createdAt = LocalDateTime.now()
        )
    }

}