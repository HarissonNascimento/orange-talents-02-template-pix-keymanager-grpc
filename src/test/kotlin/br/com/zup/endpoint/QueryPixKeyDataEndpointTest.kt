package br.com.zup.endpoint

import br.com.zup.GrpcQueryPixKeyByClientIdAndPixIdRequest
import br.com.zup.GrpcQueryPixKeyByKeyValueRequest
import br.com.zup.KeymanagerQueryDataServiceGrpc
import br.com.zup.client.bcb.BcbClient
import br.com.zup.client.bcb.model.BCBBankAccount
import br.com.zup.client.bcb.model.BCBOwner
import br.com.zup.client.bcb.model.request.enums.BCBAccountType
import br.com.zup.client.bcb.model.request.enums.BCBKeyType
import br.com.zup.client.bcb.model.request.enums.BCBOwnerType
import br.com.zup.client.bcb.model.response.BCBGetPixKeyResponse
import br.com.zup.model.domain.PixKey
import br.com.zup.repository.PixKeyRepository
import br.com.zup.util.createValidPixKey
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
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class QueryPixKeyDataEndpointTest(
    private val pixKeyRepository: PixKeyRepository,
    private val grpcClient: KeymanagerQueryDataServiceGrpc.KeymanagerQueryDataServiceBlockingStub
) {

    @Inject
    lateinit var bcbClient: BcbClient

    private lateinit var savedPixKey: PixKey

    @BeforeEach
    fun setUp() {
        pixKeyRepository.deleteAll()

        savedPixKey = pixKeyRepository.save(
            createValidPixKey()
        )
    }

    @Test
    fun `must query data of the pix key by clientId and pixId when receiving valid clientId and pixId`() {

        val grpcRequest = GrpcQueryPixKeyByClientIdAndPixIdRequest.newBuilder()
            .setClientId(savedPixKey.clientId.toString())
            .setPixId(savedPixKey.id.toString())
            .build()

        `when`(bcbClient.findPixKeyByKeyValue(savedPixKey.keyValue))
            .thenReturn(HttpResponse.ok(generateBCBGetPixKeyResponse()))

        val queryPixKeyByClientIdAndPixId = grpcClient.queryPixKeyByClientIdAndPixId(
            grpcRequest
        )

        with(queryPixKeyByClientIdAndPixId) {
            assertEquals(savedPixKey.keyType.name, this.keyType.name)
            assertEquals(savedPixKey.keyValue, this.keyValue)
            assertEquals(savedPixKey.linkedBankAccount.ownerName, this.ownerName)
            assertEquals(savedPixKey.linkedBankAccount.ownerCpf, this.ownerCPF)
            assertEquals(savedPixKey.linkedBankAccount.ispb, this.bankAccount.participant)
            assertEquals(savedPixKey.linkedBankAccount.agency, this.bankAccount.branch)
            assertEquals(savedPixKey.linkedBankAccount.number, this.bankAccount.accountNumber)
            assertEquals(savedPixKey.accountType.name, this.bankAccount.accountType.name)
            assertEquals(savedPixKey.createdAt.dayOfMonth, this.createdAt.day)
            assertEquals(savedPixKey.createdAt.monthValue, this.createdAt.month)
            assertEquals(savedPixKey.createdAt.year, this.createdAt.year)
            assertEquals(savedPixKey.createdAt.hour, this.createdAt.hour)
            assertEquals(savedPixKey.createdAt.minute, this.createdAt.minute)
            assertEquals(savedPixKey.createdAt.second, this.createdAt.second)
        }
    }

    @Test
    fun `must query data of the pix key by keyValue when receiving valid keyValue`() {

        val grpcRequest = GrpcQueryPixKeyByKeyValueRequest.newBuilder()
            .setKeyValue(savedPixKey.keyValue)
            .build()

        `when`(bcbClient.findPixKeyByKeyValue(savedPixKey.keyValue))
            .thenReturn(HttpResponse.ok(generateBCBGetPixKeyResponse()))

        val queryPixKeyByClientIdAndPixId = grpcClient.queryPixKeyByKeyValue(
            grpcRequest
        )

        with(queryPixKeyByClientIdAndPixId) {
            assertEquals(savedPixKey.keyType.name, this.keyType.name)
            assertEquals(savedPixKey.keyValue, this.keyValue)
            assertEquals(savedPixKey.linkedBankAccount.ownerName, this.ownerName)
            assertEquals(savedPixKey.linkedBankAccount.ownerCpf, this.ownerCPF)
            assertEquals(savedPixKey.linkedBankAccount.ispb, this.bankAccount.participant)
            assertEquals(savedPixKey.linkedBankAccount.agency, this.bankAccount.branch)
            assertEquals(savedPixKey.linkedBankAccount.number, this.bankAccount.accountNumber)
            assertEquals(savedPixKey.accountType.name, this.bankAccount.accountType.name)
            assertEquals(savedPixKey.createdAt.dayOfMonth, this.createdAt.day)
            assertEquals(savedPixKey.createdAt.monthValue, this.createdAt.month)
            assertEquals(savedPixKey.createdAt.year, this.createdAt.year)
            assertEquals(savedPixKey.createdAt.hour, this.createdAt.hour)
            assertEquals(savedPixKey.createdAt.minute, this.createdAt.minute)
            assertEquals(savedPixKey.createdAt.second, this.createdAt.second)
        }
    }

    @Test
    fun `should not return the key data if are not registered with the BCB`() {
        val grpcRequest = GrpcQueryPixKeyByClientIdAndPixIdRequest.newBuilder()
            .setClientId(savedPixKey.clientId.toString())
            .setPixId(savedPixKey.id.toString())
            .build()

        `when`(bcbClient.findPixKeyByKeyValue(savedPixKey.keyValue))
            .thenReturn(HttpResponse.notFound())

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.queryPixKeyByClientIdAndPixId(
                grpcRequest
            )
        }

        with(thrown){
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Erro ao buscar chave PIX no Banco Central do Brasil (BCB)", status.description)
        }
    }

    @Test
    fun `should consult BCB when no record is found in our system`(){
        val grpcRequest = GrpcQueryPixKeyByKeyValueRequest.newBuilder()
            .setKeyValue(savedPixKey.keyValue)
            .build()

        `when`(bcbClient.findPixKeyByKeyValue(savedPixKey.keyValue))
            .thenReturn(HttpResponse.ok(generateBCBGetPixKeyResponse()))

        pixKeyRepository.deleteAll()

        val queryPixKeyByClientIdAndPixId = grpcClient.queryPixKeyByKeyValue(
            grpcRequest
        )

        with(queryPixKeyByClientIdAndPixId) {
            assertEquals(savedPixKey.keyType.name, this.keyType.name)
            assertEquals(savedPixKey.keyValue, this.keyValue)
            assertEquals(savedPixKey.linkedBankAccount.ownerName, this.ownerName)
            assertEquals(savedPixKey.linkedBankAccount.ownerCpf, this.ownerCPF)
            assertEquals(savedPixKey.linkedBankAccount.ispb, this.bankAccount.participant)
            assertEquals(savedPixKey.linkedBankAccount.agency, this.bankAccount.branch)
            assertEquals(savedPixKey.linkedBankAccount.number, this.bankAccount.accountNumber)
            assertEquals(savedPixKey.accountType.name, this.bankAccount.accountType.name)
            assertEquals(savedPixKey.createdAt.dayOfMonth, this.createdAt.day)
            assertEquals(savedPixKey.createdAt.monthValue, this.createdAt.month)
            assertEquals(savedPixKey.createdAt.year, this.createdAt.year)
            assertEquals(savedPixKey.createdAt.hour, this.createdAt.hour)
            assertEquals(savedPixKey.createdAt.minute, this.createdAt.minute)
        }
    }

    @MockBean(BcbClient::class)
    fun bcbClient(): BcbClient {
        return Mockito.mock(BcbClient::class.java)
    }

    @Factory
    class RegisterClient {
        @Bean
        fun queryBlockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerQueryDataServiceGrpc.KeymanagerQueryDataServiceBlockingStub {
            return KeymanagerQueryDataServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun generateBCBGetPixKeyResponse(): BCBGetPixKeyResponse {
        return BCBGetPixKeyResponse(
            keyType = BCBKeyType.by(savedPixKey.keyType),
            key = savedPixKey.keyValue,
            bankAccount = BCBBankAccount(
                participant = savedPixKey.linkedBankAccount.ispb,
                branch = savedPixKey.linkedBankAccount.agency,
                accountNumber = savedPixKey.linkedBankAccount.number,
                accountType = BCBAccountType.by(savedPixKey.accountType)
            ),
            owner = BCBOwner(
                type = BCBOwnerType.NATURAL_PERSON,
                name = savedPixKey.linkedBankAccount.ownerName,
                taxIdNumber = savedPixKey.linkedBankAccount.ownerCpf
            ),
            createdAt = savedPixKey.createdAt
        )
    }
}