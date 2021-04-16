package br.com.zup.endpoint

import br.com.zup.GrpcNewPixKeyRequest
import br.com.zup.GrpcNewPixKeyResponse
import br.com.zup.KeymanagerServiceGrpc
import br.com.zup.grpc.util.buildByLocalDateTime
import br.com.zup.grpc.util.toNewPixKeyRequest
import br.com.zup.model.domain.PixKey
import br.com.zup.service.NewPixKeyService
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class NewPixKeyGrpcEndpoint(@Inject private val newPixKeyService: NewPixKeyService) :
    KeymanagerServiceGrpc.KeymanagerServiceImplBase() {

    override fun createNewKey(
        request: GrpcNewPixKeyRequest,
        responseObserver: StreamObserver<GrpcNewPixKeyResponse>
    ) {

        val newPixKeyRequest = request.toNewPixKeyRequest()
        val pixKey: PixKey?

        try {

            pixKey = newPixKeyService.createNewKey(newPixKeyRequest)

            responseObserver.onNext(
                GrpcNewPixKeyResponse.newBuilder()
                    .setClientId(pixKey.clientId.toString())
                    .setPixId(pixKey.id.toString())
                    .setCreatedAt(
                        buildByLocalDateTime(pixKey.createdAt)
                    )
                    .build()
            )

            responseObserver.onCompleted()

        } catch (e: StatusRuntimeException) {

            responseObserver.onError(e)

        } catch (e: ConstraintViolationException) {

            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(e.message)
                    .withCause(e)
                    .asRuntimeException()
            )

        } catch (e: Exception) {

            responseObserver.onError(
                Status.INTERNAL
                    .withDescription(e.localizedMessage)
                    .withCause(e)
                    .asRuntimeException()
            )

        }

    }

}