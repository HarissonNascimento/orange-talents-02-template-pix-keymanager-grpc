package br.com.zup.endpoint

import br.com.zup.GrpcRemovePixKeyRequest
import br.com.zup.GrpcRemovePixKeyResponse
import br.com.zup.KeymanagerRemoveServiceGrpc
import br.com.zup.grpc.util.toRemovePixKeyRequest
import br.com.zup.model.domain.PixKey
import br.com.zup.service.RemovePixKeyService
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class RemovePixKeyEndpoint(@Inject val removeService: RemovePixKeyService) :
    KeymanagerRemoveServiceGrpc.KeymanagerRemoveServiceImplBase() {

    override fun removePixKey(
        request: GrpcRemovePixKeyRequest,
        responseObserver: StreamObserver<GrpcRemovePixKeyResponse>
    ) {

        val removePixKeyRequest = request.toRemovePixKeyRequest()
        val pixKey: PixKey?

        try {
            pixKey = removeService.removePixKey(removePixKeyRequest)

            responseObserver.onNext(
                GrpcRemovePixKeyResponse.newBuilder()
                    .setClientId(pixKey.clientId.toString())
                    .setPixId(pixKey.id.toString())
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