package br.com.zup.client.itau.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class Institute(
    @field:JsonProperty("nome")
    val name: String,
    val ispb: String
)