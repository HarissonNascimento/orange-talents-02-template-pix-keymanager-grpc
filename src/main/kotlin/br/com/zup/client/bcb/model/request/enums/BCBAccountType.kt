package br.com.zup.client.bcb.model.request.enums

import br.com.zup.GrpcAccountType

enum class BCBAccountType {
    CACC,
    SVGS;

    companion object {
        fun by(domainType: GrpcAccountType): BCBAccountType {
            return when(domainType) {
                GrpcAccountType.CONTA_CORRENTE -> CACC
                GrpcAccountType.CONTA_POUPANCA -> SVGS
                else -> throw IllegalArgumentException("BCBAccountType invalid or not found for $domainType")
            }
        }
    }
}
