package br.com.zup.client.bcb.model

import br.com.zup.client.bcb.model.request.enums.BCBOwnerType

data class BCBOwner(
    val type: BCBOwnerType,
    val name: String,
    val taxIdNumber: String
)
