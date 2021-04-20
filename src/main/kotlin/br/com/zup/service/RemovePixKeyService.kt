package br.com.zup.service

import br.com.zup.grpc.exception.PixKeyNotFoundException
import br.com.zup.model.domain.PixKey
import br.com.zup.model.request.RemovePixKeyRequest
import br.com.zup.repository.PixKeyRepository
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RemovePixKeyService(
    @Inject val pixKeyRepository: PixKeyRepository
) {

    @Transactional
    fun removePixKey(@Valid removeRequest: RemovePixKeyRequest): PixKey {

        val pixKey = pixKeyRepository.findByIdAndClientId(
            UUID.fromString(removeRequest.pixId),
            UUID.fromString(removeRequest.clientId)
        ).orElseThrow {
            PixKeyNotFoundException("Chave não encontrada!")
        }

        pixKeyRepository.delete(pixKey)

        return pixKey

    }

}