package br.com.zup.client.bcb

import br.com.zup.client.bcb.model.request.BCBCreatePixKeyRequest
import br.com.zup.client.bcb.model.request.BCBDeletePixKeyRequest
import br.com.zup.client.bcb.model.response.BCBCreatePixKeyResponse
import br.com.zup.client.bcb.model.response.BCBDeletePixKeyResponse
import br.com.zup.client.bcb.model.response.BCBGetPixKeyResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${bcb.pix.url}")
interface BcbClient {

    @Post("/api/v1/pix/keys", produces = [MediaType.APPLICATION_XML], consumes = [MediaType.APPLICATION_XML])
    fun registerPixKey(@Body bcbCreatePixKeyRequest: BCBCreatePixKeyRequest): HttpResponse<BCBCreatePixKeyResponse>

    @Delete("/api/v1/pix/keys/{key}", produces = [MediaType.APPLICATION_XML], consumes = [MediaType.APPLICATION_XML])
    fun deletePixKey(@PathVariable key: String, @Body bcbDeletePixKeyRequest: BCBDeletePixKeyRequest): HttpResponse<BCBDeletePixKeyResponse>

    @Get("/api/v1/pix/keys/{key}", produces = [MediaType.APPLICATION_XML], consumes = [MediaType.APPLICATION_XML])
    fun findPixKeyByKeyValue(@PathVariable key: String): HttpResponse<BCBGetPixKeyResponse>

}