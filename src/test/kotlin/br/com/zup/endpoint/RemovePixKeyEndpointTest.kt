package br.com.zup.endpoint

import br.com.zup.GrpcAccountType
import br.com.zup.GrpcKeyType
import br.com.zup.GrpcRemovePixKeyRequest
import br.com.zup.KeymanagerRemoveServiceGrpc
import br.com.zup.client.bcb.BcbClient
import br.com.zup.client.bcb.model.request.BCBDeletePixKeyRequest
import br.com.zup.client.bcb.model.response.BCBDeletePixKeyResponse
import br.com.zup.enums.KeyType
import br.com.zup.model.domain.BankAccount
import br.com.zup.model.domain.PixKey
import br.com.zup.repository.PixKeyRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RemovePixKeyEndpointTest(
    private val pixKeyRepository: PixKeyRepository,
    private val grpcClient: KeymanagerRemoveServiceGrpc.KeymanagerRemoveServiceBlockingStub
) {

    @Inject
    lateinit var bcbClient: BcbClient

    private lateinit var savedPixKey: PixKey

    @BeforeEach
    fun setUp() {
        pixKeyRepository.deleteAll()

        savedPixKey = pixKeyRepository.save(
            PixKey(
                clientId = NewPixKeyGrpcEndpointTest.CLIENT_ID,
                keyType = KeyType.valueOf(GrpcKeyType.EMAIL.name),
                keyValue = "test@email.com",
                accountType = GrpcAccountType.CONTA_CORRENTE,
                linkedBankAccount = BankAccount(
                    institute = "Institute Test",
                    ispb = "00000000",
                    agency = "0000",
                    number = "000000",
                    ownerName = "Owner Test",
                    ownerCpf = "00000000000"
                )
            )
        )
    }

    @Test
    fun `remove pix key when given valid pixId and clientId`() {

        val bcbDeletePixKeyRequest =
            BCBDeletePixKeyRequest.of(key = savedPixKey.keyValue, participant = savedPixKey.linkedBankAccount.ispb)

        `when`(
            bcbClient.deletePixKey(
                key = bcbDeletePixKeyRequest.key,
                bcbDeletePixKeyRequest = bcbDeletePixKeyRequest
            )
        )
            .thenReturn(HttpResponse.ok(bcbDeletePixKeyResponse(bcbDeletePixKeyRequest)))

        val removePixKeyResponse = grpcClient.removePixKey(
            GrpcRemovePixKeyRequest.newBuilder()
                .setClientId(savedPixKey.clientId.toString())
                .setPixId(savedPixKey.id.toString())
                .build()
        )

        with(removePixKeyResponse) {
            assertEquals(savedPixKey.clientId.toString(), clientId)
            assertEquals(savedPixKey.id.toString(), pixId)
        }

    }

    @Test
    fun `dont remove pix key when cannot remove with BCB`() {

        val bcbDeletePixKeyRequest =
            BCBDeletePixKeyRequest.of(key = savedPixKey.keyValue, participant = savedPixKey.linkedBankAccount.ispb)

        `when`(
            bcbClient.deletePixKey(
                key = bcbDeletePixKeyRequest.key,
                bcbDeletePixKeyRequest = bcbDeletePixKeyRequest
            )
        )
            .thenReturn(HttpResponse.notFound())

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.removePixKey(
                GrpcRemovePixKeyRequest.newBuilder()
                    .setClientId(savedPixKey.clientId.toString())
                    .setPixId(savedPixKey.id.toString())
                    .build()
            )
        }

        with(thrown)
        {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
        }

    }

    @Test
    fun `dont remove pix key when given invalid pixId`() {

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.removePixKey(
                GrpcRemovePixKeyRequest.newBuilder()
                    .setClientId(savedPixKey.clientId.toString())
                    .setPixId(UUID.randomUUID().toString())
                    .build()
            )
        }

        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada!", status.description)
        }
    }

    @Test
    fun `dont remove pix key when given invalid clientId`() {

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.removePixKey(
                GrpcRemovePixKeyRequest.newBuilder()
                    .setClientId(UUID.randomUUID().toString())
                    .setPixId(savedPixKey.id.toString())
                    .build()
            )
        }

        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada!", status.description)
        }
    }

    @Test
    fun `dont remove pix key when given invalid UUID`() {

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.removePixKey(
                GrpcRemovePixKeyRequest.newBuilder()
                    .setClientId("1")
                    .setPixId("1")
                    .build()
            )
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @MockBean(BcbClient::class)
    fun bcbClient(): BcbClient {
        return Mockito.mock(BcbClient::class.java)
    }

    @Factory
    class RemoveClient {
        @Bean
        fun removeBlockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerRemoveServiceGrpc.KeymanagerRemoveServiceBlockingStub {
            return KeymanagerRemoveServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun bcbDeletePixKeyResponse(bcbDeleteRequest: BCBDeletePixKeyRequest): BCBDeletePixKeyResponse {
        return BCBDeletePixKeyResponse(
            key = bcbDeleteRequest.key,
            participant = bcbDeleteRequest.participant,
            deletedAt = LocalDateTime.now()
        )
    }
}