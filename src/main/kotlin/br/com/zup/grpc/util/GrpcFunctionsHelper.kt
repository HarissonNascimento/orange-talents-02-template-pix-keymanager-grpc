package br.com.zup.grpc.util

import br.com.zup.*
import br.com.zup.enums.KeyType
import br.com.zup.model.request.NewPixKeyRequest
import br.com.zup.model.request.RemovePixKeyRequest
import java.time.LocalDateTime

fun GrpcRemovePixKeyRequest.toRemovePixKeyRequest(): RemovePixKeyRequest {
    return RemovePixKeyRequest(
        pixId = this.pixId,
        clientId = this.clientId
    )
}

fun GrpcNewPixKeyRequest.toNewPixKeyRequest(): NewPixKeyRequest {

    return NewPixKeyRequest(
        clientId = this.clientId,
        keyType = when (this.keyType) {
            GrpcKeyType.UNKNOWN_KEY_TYPE -> null
            else -> KeyType.valueOf(this.keyType.name)
        },
        keyValue = this.keyValue,
        accountType = when (this.accountType) {
            GrpcAccountType.UNKNOWN_ACCOUNT_TYPE -> null
            else -> GrpcAccountType.valueOf(this.accountType.name)
        }
    )

}

fun buildByLocalDateTime(localDateTime: LocalDateTime): GrpcCreatedAt {
    return GrpcCreatedAt.newBuilder()
        .setDay(localDateTime.dayOfMonth)
        .setMonth(localDateTime.monthValue)
        .setYear(localDateTime.year)
        .setHour(localDateTime.hour)
        .setMinute(localDateTime.minute)
        .setSecond(localDateTime.second)
        .build()
}