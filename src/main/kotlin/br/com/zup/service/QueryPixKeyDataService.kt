package br.com.zup.service

import br.com.zup.GrpcCreatedAt
import br.com.zup.GrpcKeyType
import br.com.zup.GrpcPixKey
import br.com.zup.client.bcb.BcbClient
import br.com.zup.grpc.exception.PixKeyNotFoundException
import br.com.zup.model.domain.PixKey
import br.com.zup.model.request.ListPixKeyByClientIdRequest
import br.com.zup.model.request.QueryPixKeyByClientIdAndPixIdRequest
import br.com.zup.model.request.QueryPixKeyByKeyValueRequest
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
class QueryPixKeyDataService(
    @Inject val pixKeyRepository: PixKeyRepository,
    @Inject val bcbClient: BcbClient
) {

    @Transactional
    fun queryPixKeyByClientIdAndPixId(@Valid queryRequest: QueryPixKeyByClientIdAndPixIdRequest): PixKey {

        val pixKey = pixKeyRepository.findByIdAndClientId(
            id = UUID.fromString(queryRequest.pixId),
            clientId = UUID.fromString(queryRequest.clientId)
        ).orElseThrow {
            PixKeyNotFoundException("Chave PIX não encontrada!")
        }

        val bcbResponse = bcbClient.findPixKeyByKeyValue(key = pixKey.keyValue)

        if (bcbResponse.status != HttpStatus.OK)
            throw IllegalStateException("Erro ao buscar chave PIX no Banco Central do Brasil (BCB)")

        return pixKey
    }

    @Transactional
    fun queryPixKeyByKeyValue(@Valid queryRequest: QueryPixKeyByKeyValueRequest): PixKey {

        val optionalPixKey = pixKeyRepository.findByKeyValue(queryRequest.keyValue)

        if (optionalPixKey.isPresent)
            return optionalPixKey.get()

        val bcbResponse = bcbClient.findPixKeyByKeyValue(queryRequest.keyValue!!)

        if (bcbResponse.status != HttpStatus.OK)
            throw PixKeyNotFoundException("Chave PIX não encontrada")

        val bcbPixKeyResponse = bcbResponse.body()
            ?: throw IllegalStateException("Erro ao processar resposta do Banco Central do Brasil (BCB)")

        return bcbPixKeyResponse.toPixKey()
    }

    @Transactional
    fun listPixKeyByClientId(@Valid listRequest: ListPixKeyByClientIdRequest): List<GrpcPixKey> {

        val listPixKey = pixKeyRepository.findAllByClientId(UUID.fromString(listRequest.clientId))

        return listPixKey.map {
            GrpcPixKey.newBuilder()
                .setPixId(it.id.toString())
                .setKeyType(GrpcKeyType.valueOf(it.keyType.name))
                .setKeyValue(it.keyValue)
                .setAccountType(it.accountType)
                .setCreatedAt(
                    GrpcCreatedAt.newBuilder()
                        .setDay(it.createdAt.dayOfMonth)
                        .setMonth(it.createdAt.monthValue)
                        .setYear(it.createdAt.year)
                        .setHour(it.createdAt.hour)
                        .setMinute(it.createdAt.minute)
                        .setSecond(it.createdAt.second)
                        .build()
                )
                .build()
        }
    }

}
