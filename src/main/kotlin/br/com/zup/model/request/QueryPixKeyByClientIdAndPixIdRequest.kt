package br.com.zup.model.request

import br.com.zup.annotation.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class QueryPixKeyByClientIdAndPixIdRequest(
    @field:NotBlank
    @field:ValidUUID
    val clientId: String?,
    @field:NotBlank
    @field:ValidUUID
    val pixId: String?
)