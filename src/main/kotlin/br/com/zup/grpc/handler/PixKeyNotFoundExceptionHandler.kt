package br.com.zup.grpc.handler

import br.com.zup.grpc.exception.PixKeyNotFoundException
import br.com.zup.grpc.handler.config.ExceptionHandler
import br.com.zup.grpc.handler.config.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class PixKeyNotFoundExceptionHandler : ExceptionHandler<PixKeyNotFoundException> {

    override fun handle(e: PixKeyNotFoundException): StatusWithDetails {
        return StatusWithDetails(
            Status.NOT_FOUND
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is PixKeyNotFoundException
    }
}