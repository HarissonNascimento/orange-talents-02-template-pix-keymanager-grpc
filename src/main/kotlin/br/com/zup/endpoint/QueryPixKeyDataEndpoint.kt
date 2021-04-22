package br.com.zup.endpoint

import br.com.zup.*
import br.com.zup.grpc.handler.config.ErrorHandler
import br.com.zup.grpc.util.*
import br.com.zup.service.QueryPixKeyDataService
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class QueryPixKeyDataEndpoint(@Inject val queryDataService: QueryPixKeyDataService) :
    KeymanagerQueryDataServiceGrpc.KeymanagerQueryDataServiceImplBase() {

    override fun queryPixKeyByClientIdAndPixId(
        request: GrpcQueryPixKeyByClientIdAndPixIdRequest,
        responseObserver: StreamObserver<GrpcQueryPixKeyByClientIdAndPixIdResponse>
    ) {

        val queryRequest = request.toQueryPixKeyByClientIdAndPixIdRequest()
        val pixKey = queryDataService.queryPixKeyByClientIdAndPixId(queryRequest)

        responseObserver.onNext(
            GrpcQueryPixKeyByClientIdAndPixIdResponse.newBuilder()
                .setPixId(pixKey.id.toString())
                .setClientId(pixKey.clientId.toString())
                .setKeyType(GrpcKeyType.valueOf(pixKey.keyType.name))
                .setKeyValue(pixKey.keyValue)
                .setOwnerName(pixKey.linkedBankAccount.ownerName)
                .setOwnerCPF(pixKey.linkedBankAccount.ownerCpf)
                .setBankAccount(
                    buildGrpcBankAccountByPixKey(pixKey)
                )
                .setCreatedAt(
                    buildGrpcCreatedAtByLocalDateTime(pixKey.createdAt)
                )
                .build()
        )

        responseObserver.onCompleted()
    }

    override fun queryPixKeyByKeyValue(
        request: GrpcQueryPixKeyByKeyValueRequest,
        responseObserver: StreamObserver<GrpcQueryPixKeyByKeyValueResponse>
    ) {

        val queryRequest = request.toQueryPixKeyByKeyValueRequest()
        val pixKey = queryDataService.queryPixKeyByKeyValue(queryRequest)

        responseObserver.onNext(
            GrpcQueryPixKeyByKeyValueResponse.newBuilder()
                .setKeyType(GrpcKeyType.valueOf(pixKey.keyType.name))
                .setKeyValue(pixKey.keyValue)
                .setOwnerName(pixKey.linkedBankAccount.ownerName)
                .setOwnerCPF(pixKey.linkedBankAccount.ownerCpf)
                .setBankAccount(
                    buildGrpcBankAccountByPixKey(pixKey)
                )
                .setCreatedAt(
                    buildGrpcCreatedAtByLocalDateTime(pixKey.createdAt)
                )
                .build()

        )

        responseObserver.onCompleted()
    }

    override fun listPixKeyByClientId(
        request: GrpcListPixKeyByClientIdRequest,
        responseObserver: StreamObserver<GrpcListPixKeyByClientIdResponse>
    ) {

        val listRequest = request.toListPixKeyByClientIdRequest()
        val listPixKey = queryDataService.listPixKeyByClientId(listRequest)

        responseObserver.onNext(
            GrpcListPixKeyByClientIdResponse.newBuilder()
                .setClientId(listRequest.clientId)
                .addAllPixKeys(listPixKey)
                .build()
        )

        responseObserver.onCompleted()
    }

}