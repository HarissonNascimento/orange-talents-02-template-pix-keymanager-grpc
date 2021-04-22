package br.com.zup.endpoint

import br.com.zup.GrpcNewPixKeyRequest
import br.com.zup.GrpcNewPixKeyResponse
import br.com.zup.KeymanagerRegisterServiceGrpc
import br.com.zup.grpc.handler.config.ErrorHandler
import br.com.zup.grpc.util.buildGrpcCreatedAtByLocalDateTime
import br.com.zup.grpc.util.toNewPixKeyRequest
import br.com.zup.service.NewPixKeyService
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class NewPixKeyGrpcEndpoint(@Inject private val newPixKeyService: NewPixKeyService) :
    KeymanagerRegisterServiceGrpc.KeymanagerRegisterServiceImplBase() {

    override fun createNewKey(
        request: GrpcNewPixKeyRequest,
        responseObserver: StreamObserver<GrpcNewPixKeyResponse>
    ) {

        val newPixKeyRequest = request.toNewPixKeyRequest()
        val pixKey = newPixKeyService.createNewKey(newPixKeyRequest)

        responseObserver.onNext(
            GrpcNewPixKeyResponse.newBuilder()
                .setClientId(pixKey.clientId.toString())
                .setPixId(pixKey.id.toString())
                .setCreatedAt(
                    buildGrpcCreatedAtByLocalDateTime(pixKey.createdAt)
                )
                .build()
        )

        responseObserver.onCompleted()

    }

}