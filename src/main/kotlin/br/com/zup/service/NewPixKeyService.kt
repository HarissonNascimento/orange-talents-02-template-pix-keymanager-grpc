package br.com.zup.service

import br.com.zup.client.itau.ItauClient
import br.com.zup.model.domain.BankAccount
import br.com.zup.model.domain.PixKey
import br.com.zup.model.request.NewPixKeyRequest
import br.com.zup.repository.PixKeyRepository
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class NewPixKeyService(
    @Inject val pixKeyRepository: PixKeyRepository,
    @Inject val itauClient: ItauClient
) {

    @Transactional
    fun createNewKey(@Valid newPixKeyRequest: NewPixKeyRequest): PixKey {

        if (pixKeyRepository.existsByKeyValue(newPixKeyRequest.keyValue)) {
            throw StatusRuntimeException(
                Status
                    .ALREADY_EXISTS
                    .withDescription("Chave PIX já registrada: ${newPixKeyRequest.keyValue}")
            )
        }

        val itauResponseBody =
            consultBankAccount(newPixKeyRequest.clientId.toString(), newPixKeyRequest.accountType!!.name)

        val pixKey = newPixKeyRequest.toPixKey(itauResponseBody)

        pixKeyRepository.save(pixKey)

        return pixKey

    }

    private fun consultBankAccount(clientId: String, accountType: String): BankAccount {
        val itauConsultResponse =
            itauClient.consultItauAccount(clientId, accountType)

        return itauConsultResponse.body()?.toBankAccount() ?: throw StatusRuntimeException(
            Status
                .NOT_FOUND
                .withDescription("Não foi possivel encontrar o cliente!")
        )
    }

}