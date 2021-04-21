package br.com.zup.service

import br.com.zup.client.bcb.BcbClient
import br.com.zup.client.bcb.model.request.BCBDeletePixKeyRequest
import br.com.zup.grpc.exception.PixKeyNotFoundException
import br.com.zup.model.domain.PixKey
import br.com.zup.model.request.RemovePixKeyRequest
import br.com.zup.repository.PixKeyRepository
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RemovePixKeyService(
    @Inject val pixKeyRepository: PixKeyRepository,
    @Inject val bcbClient: BcbClient
) {

    @Transactional
    fun removePixKey(@Valid removeRequest: RemovePixKeyRequest): PixKey {

        val pixKey = pixKeyRepository.findByIdAndClientId(
            UUID.fromString(removeRequest.pixId),
            UUID.fromString(removeRequest.clientId)
        ).orElseThrow {
            PixKeyNotFoundException("Chave não encontrada!")
        }

        val deleteRequestBody = BCBDeletePixKeyRequest.of(key = pixKey.keyValue, participant = pixKey.linkedBankAccount.ispb)

        val deleteResponse = bcbClient.deletePixKey(deleteRequestBody.key, deleteRequestBody)

        if (deleteResponse.status != HttpStatus.OK)
            throw IllegalStateException("Erro ao deletar chave PIX no Banco Central do Brasil (BCB)")

        pixKeyRepository.delete(pixKey)

        return pixKey

    }

}