package br.com.zup.client.itau

import br.com.zup.client.itau.model.response.AccountClientResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${itau.accounts.url}")
interface ItauClient {

    @Get("/api/v1/clientes/{clientId}/contas")
    fun consultItauAccount(
        @PathVariable clientId: String,
        @QueryValue("tipo") type: String?
    ): HttpResponse<AccountClientResponse>

}