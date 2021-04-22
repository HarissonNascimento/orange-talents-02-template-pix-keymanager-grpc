package br.com.zup.model.request

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
data class QueryPixKeyByKeyValueRequest(
    @field:NotBlank
    @field:Size(max=77)
    val keyValue: String?
)