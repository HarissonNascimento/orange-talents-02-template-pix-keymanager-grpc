package br.com.zup.model.request


import br.com.zup.GrpcAccountType
import br.com.zup.annotation.ValidPixKey
import br.com.zup.annotation.ValidUUID
import br.com.zup.enums.KeyType
import br.com.zup.model.domain.BankAccount
import br.com.zup.model.domain.PixKey
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
@ValidPixKey
data class NewPixKeyRequest(
    @field:NotBlank
    @field:ValidUUID
    val clientId: String?,
    @field:NotNull
    val keyType: KeyType?,
    @field:Size(max = 77)
    val keyValue: String?,
    @field:NotNull
    val accountType: GrpcAccountType?
) {

    fun toPixKey(linkedBankAccount: BankAccount): PixKey {
        return PixKey(
            clientId = UUID.fromString(this.clientId),
            keyType = KeyType.valueOf(this.keyType!!.name),
            keyValue = if (this.keyType == KeyType.CHAVE_ALEATORIA) UUID.randomUUID().toString() else this.keyValue!!,
            accountType = GrpcAccountType.valueOf(this.accountType!!.name),
            linkedBankAccount = linkedBankAccount
        )
    }

}