package br.com.zup.client.bcb.model.request.enums

import br.com.zup.enums.KeyType

enum class BCBKeyType(val domainType: KeyType?) {
    CPF(KeyType.CPF),
    CNPJ(null),
    PHONE(KeyType.TELEFONE_CELULAR),
    EMAIL(KeyType.EMAIL),
    RANDOM(KeyType.CHAVE_ALEATORIA);

    companion object {

        private val mapping = values().associateBy(BCBKeyType::domainType)

        fun by(domainType: KeyType): BCBKeyType {
            return mapping[domainType] ?: throw IllegalArgumentException("BCBKeyType invalid or not found for $domainType")
        }

    }
}