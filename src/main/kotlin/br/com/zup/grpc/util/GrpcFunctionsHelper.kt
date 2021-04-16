package br.com.zup.grpc.util

import br.com.zup.GrpcAccountType
import br.com.zup.GrpcCreatedAt
import br.com.zup.GrpcKeyType
import br.com.zup.GrpcNewPixKeyRequest
import br.com.zup.enums.KeyType
import br.com.zup.model.request.NewPixKeyRequest
import java.time.LocalDateTime

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