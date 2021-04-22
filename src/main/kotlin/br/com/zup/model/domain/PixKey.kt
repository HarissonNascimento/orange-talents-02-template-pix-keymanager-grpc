package br.com.zup.model.domain

import br.com.zup.GrpcAccountType
import br.com.zup.enums.KeyType
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class PixKey(

    @field:NotNull
    @field:Type(type = "uuid-char")
    @Column(nullable = false)
    val clientId: UUID?,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val keyType: KeyType,

    @field:NotBlank
    @Column(unique = true, nullable = false)
    var keyValue: String,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val accountType: GrpcAccountType,

    @field:Valid
    @field:NotNull
    @Embedded
    val linkedBankAccount: BankAccount

) {
    @Id
    @GeneratedValue
    @field:Type(type = "uuid-char")
    val id: UUID? = null

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

    fun updateThisKeyValue(keyValue: String) {
        if (keyType == KeyType.CHAVE_ALEATORIA)
            this.keyValue = keyValue
    }

}
