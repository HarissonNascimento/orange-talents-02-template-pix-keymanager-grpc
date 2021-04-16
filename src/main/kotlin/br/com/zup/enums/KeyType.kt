package br.com.zup.enums

import io.micronaut.validation.validator.constraints.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class KeyType {

    CPF {
        override fun isValidKey(keyValue: String?): Boolean {
            if (keyValue.isNullOrBlank()) return false

            if (!keyValue.matches("[0-9]+".toRegex())) return false

            return CPFValidator().run {
                initialize(null)
                isValid(keyValue, null)
            }
        }
    },
    TELEFONE_CELULAR {
        override fun isValidKey(keyValue: String?): Boolean {
            if (keyValue.isNullOrBlank()) return false

            return keyValue.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL {
        override fun isValidKey(keyValue: String?): Boolean {
            if (keyValue.isNullOrBlank()) return false

            return EmailValidator().run {
                initialize(null)
                isValid(keyValue, null)
            }
        }
    },
    CHAVE_ALEATORIA {
        override fun isValidKey(keyValue: String?) = keyValue.isNullOrBlank()
    };

    abstract fun isValidKey(keyValue: String?): Boolean
}