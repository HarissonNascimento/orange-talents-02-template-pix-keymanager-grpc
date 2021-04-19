package br.com.zup.client.itau.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class Owner(
    val id: String,
    @field:JsonProperty("nome")
    val name: String,
    val cpf: String
)