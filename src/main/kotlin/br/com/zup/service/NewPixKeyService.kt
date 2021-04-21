package br.com.zup.service

import br.com.zup.client.bcb.BcbClient
import br.com.zup.client.bcb.model.request.BCBCreatePixKeyRequest
import br.com.zup.client.itau.ItauClient
import br.com.zup.enums.KeyType
import br.com.zup.grpc.exception.CPFDontMatchException
import br.com.zup.grpc.exception.PixKeyAlreadyExistsException
import br.com.zup.grpc.exception.PixKeyNotFoundException
import br.com.zup.model.domain.BankAccount
import br.com.zup.model.domain.PixKey
import br.com.zup.model.request.NewPixKeyRequest
import br.com.zup.repository.PixKeyRepository
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class NewPixKeyService(
    @Inject val pixKeyRepository: PixKeyRepository,
    @Inject val itauClient: ItauClient,
    @Inject val bcbClient: BcbClient
) {

    @Transactional
    fun createNewKey(@Valid newPixKeyRequest: NewPixKeyRequest): PixKey {

        if (pixKeyRepository.existsByKeyValue(newPixKeyRequest.keyValue)) {
            throw PixKeyAlreadyExistsException("Chave PIX já registrada: ${newPixKeyRequest.keyValue}")

        }

        val itauResponseBody =
            consultBankAccount(newPixKeyRequest.clientId.toString(), newPixKeyRequest.accountType!!.name)

        if (newPixKeyRequest.keyType!! == KeyType.CPF) {
            if (!newPixKeyRequest.keyValue.equals(itauResponseBody.ownerCpf))
                throw CPFDontMatchException("O CPF fornecido não pertence a está conta!")

        }

        val pixKey = newPixKeyRequest.toPixKey(itauResponseBody)

        val bcbResponse = bcbClient.registerPixKey(BCBCreatePixKeyRequest.of(pixKey))

        if (bcbResponse.status != HttpStatus.CREATED)
            throw IllegalStateException("Erro ao registrar chave PIX no Banco Central do Brasil (BCB)")

        pixKey.updateThisKeyValue(bcbResponse.body()!!.key)

        pixKeyRepository.save(pixKey)

        return pixKey

    }

    private fun consultBankAccount(clientId: String, accountType: String): BankAccount {
        val itauConsultResponse =
            itauClient.consultItauAccount(clientId, accountType)

        return itauConsultResponse.body()?.toBankAccount()
            ?: throw PixKeyNotFoundException("Não foi possivel encontrar o cliente!")

    }

}