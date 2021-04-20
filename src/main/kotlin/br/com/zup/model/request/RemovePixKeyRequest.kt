package br.com.zup.model.request

import br.com.zup.annotation.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class RemovePixKeyRequest(

    @field:NotBlank
    @field:ValidUUID
    val pixId: String?,

    @field:NotBlank
    @field:ValidUUID
    val clientId: String?
)
