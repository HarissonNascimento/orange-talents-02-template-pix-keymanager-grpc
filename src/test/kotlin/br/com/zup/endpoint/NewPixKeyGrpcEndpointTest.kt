package br.com.zup.endpoint

import br.com.zup.GrpcAccountType
import br.com.zup.GrpcKeyType
import br.com.zup.GrpcNewPixKeyRequest
import br.com.zup.KeymanagerRegisterServiceGrpc
import br.com.zup.client.itau.ItauClient
import br.com.zup.client.itau.model.response.AccountClientResponse
import br.com.zup.client.itau.model.response.Institute
import br.com.zup.client.itau.model.response.Owner
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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class NewPixKeyGrpcEndpointTest(
    private val pixKeyRepository: PixKeyRepository,
    private val grpcClient: KeymanagerRegisterServiceGrpc.KeymanagerRegisterServiceBlockingStub
) {

    @Inject
    lateinit var itauClient: ItauClient

    companion object {
        val CLIENT_ID: UUID = UUID.randomUUID()
    }

    @BeforeEach
    fun setUp() {
        pixKeyRepository.deleteAll()
    }

    @Test
    fun `register new pix key when given valid data`() {
        `when`(
            itauClient.consultItauAccount(
                clientId = CLIENT_ID.toString(),
                type = GrpcAccountType.CONTA_CORRENTE.name
            )
        )
            .thenReturn(HttpResponse.ok(generatedAccountClientResponse()))


        val createNewKeyResponse = grpcClient.createNewKey(
            GrpcNewPixKeyRequest.newBuilder()
                .setClientId(CLIENT_ID.toString())
                .setKeyType(GrpcKeyType.EMAIL)
                .setKeyValue("teste@email.com")
                .setAccountType(GrpcAccountType.CONTA_CORRENTE)
                .build()
        )

        with(createNewKeyResponse) {
            assertEquals(CLIENT_ID.toString(), clientId)
            assertNotNull(pixId)
        }
    }

    @Test
    fun `dont register new pix key when given invalid client id`() {
        `when`(
            itauClient.consultItauAccount(
                clientId = CLIENT_ID.toString(),
                type = GrpcAccountType.CONTA_CORRENTE.name
            )
        )
            .thenReturn(HttpResponse.notFound())

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.createNewKey(
                GrpcNewPixKeyRequest.newBuilder()
                    .setClientId(CLIENT_ID.toString())
                    .setKeyType(GrpcKeyType.EMAIL)
                    .setKeyValue("teste@email.com")
                    .setAccountType(GrpcAccountType.CONTA_CORRENTE)
                    .build()
            )
        }

        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Não foi possivel encontrar o cliente!", status.description)
        }
    }

    @Test
    fun `dont register new pix key when given existing key value`() {

        val pixKeyToBeSaved = PixKey(
            clientId = CLIENT_ID,
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

        pixKeyRepository.save(pixKeyToBeSaved)

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.createNewKey(
                GrpcNewPixKeyRequest.newBuilder()
                    .setClientId(pixKeyToBeSaved.clientId.toString())
                    .setKeyType(GrpcKeyType.valueOf(pixKeyToBeSaved.keyType.name))
                    .setKeyValue(pixKeyToBeSaved.keyValue)
                    .setAccountType(pixKeyToBeSaved.accountType)
                    .build()
            )
        }

        with(thrown) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave PIX já registrada: ${pixKeyToBeSaved.keyValue}", status.description)
        }

    }

    @Test
    fun `dont register new pix key when given different CPF from account`() {

        `when`(
            itauClient.consultItauAccount(
                clientId = CLIENT_ID.toString(),
                type = GrpcAccountType.CONTA_CORRENTE.name
            )
        )
            .thenReturn(HttpResponse.ok(generatedAccountClientResponse()))

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.createNewKey(
                GrpcNewPixKeyRequest.newBuilder()
                    .setClientId(CLIENT_ID.toString())
                    .setKeyType(GrpcKeyType.CPF)
                    .setKeyValue("22222222222")
                    .setAccountType(GrpcAccountType.CONTA_CORRENTE)
                    .build()
            )
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("O CPF fornecido não pertence a está conta!", status.description)
        }
    }

    @Test
    fun `dont register new pix key when given invalid CPF`() {

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.createNewKey(
                GrpcNewPixKeyRequest.newBuilder()
                    .setClientId(CLIENT_ID.toString())
                    .setKeyType(GrpcKeyType.CPF)
                    .setKeyValue("INVALID CPF")
                    .setAccountType(GrpcAccountType.CONTA_CORRENTE)
                    .build()
            )
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `dont register new pix key when given invalid email`() {

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.createNewKey(
                GrpcNewPixKeyRequest.newBuilder()
                    .setClientId(CLIENT_ID.toString())
                    .setKeyType(GrpcKeyType.EMAIL)
                    .setKeyValue("INVALID EMAIL")
                    .setAccountType(GrpcAccountType.CONTA_CORRENTE)
                    .build()
            )
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `dont register new pix key when given invalid phone number`() {

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.createNewKey(
                GrpcNewPixKeyRequest.newBuilder()
                    .setClientId(CLIENT_ID.toString())
                    .setKeyType(GrpcKeyType.TELEFONE_CELULAR)
                    .setKeyValue("INVALID PHONE NUMBER")
                    .setAccountType(GrpcAccountType.CONTA_CORRENTE)
                    .build()
            )
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }


    @MockBean(ItauClient::class)
    fun itauClient(): ItauClient {
        return Mockito.mock(ItauClient::class.java)
    }

    @Factory
    class RegisterClient {
        @Bean
        fun registerBlockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerRegisterServiceGrpc.KeymanagerRegisterServiceBlockingStub {
            return KeymanagerRegisterServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun generatedAccountClientResponse(): AccountClientResponse {
        return AccountClientResponse(
            type = GrpcAccountType.CONTA_CORRENTE.name,
            institute = Institute(
                name = "ITAÚ UNIBANCO S.A.",
                ispb = "60701190"
            ),
            agency = "0001",
            number = "084329",
            owner = Owner(
                id = "de95a228-1f27-4ad2-907e-e5a2d816e9bc",
                name = "Client Test",
                cpf = "11111111111"
            )
        )
    }

}