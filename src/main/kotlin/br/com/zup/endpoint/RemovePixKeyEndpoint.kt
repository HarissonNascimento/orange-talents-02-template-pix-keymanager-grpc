package br.com.zup.endpoint

import br.com.zup.GrpcRemovePixKeyRequest
import br.com.zup.GrpcRemovePixKeyResponse
import br.com.zup.KeymanagerRemoveServiceGrpc
import br.com.zup.grpc.handler.config.ErrorHandler
import br.com.zup.grpc.util.toRemovePixKeyRequest
import br.com.zup.service.RemovePixKeyService
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RemovePixKeyEndpoint(@Inject val removeService: RemovePixKeyService) :
    KeymanagerRemoveServiceGrpc.KeymanagerRemoveServiceImplBase() {

    override fun removePixKey(
        request: GrpcRemovePixKeyRequest,
        responseObserver: StreamObserver<GrpcRemovePixKeyResponse>
    ) {

        val removePixKeyRequest = request.toRemovePixKeyRequest()
        val pixKey = removeService.removePixKey(removePixKeyRequest)

        responseObserver.onNext(
            GrpcRemovePixKeyResponse.newBuilder()
                .setClientId(pixKey.clientId.toString())
                .setPixId(pixKey.id.toString())
                .build()
        )

        responseObserver.onCompleted()

    }

}