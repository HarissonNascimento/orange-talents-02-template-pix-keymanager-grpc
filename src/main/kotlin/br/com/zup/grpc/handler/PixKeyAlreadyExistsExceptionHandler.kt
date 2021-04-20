package br.com.zup.grpc.handler

import br.com.zup.grpc.exception.PixKeyAlreadyExistsException
import br.com.zup.grpc.handler.config.ExceptionHandler
import br.com.zup.grpc.handler.config.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class PixKeyAlreadyExistsExceptionHandler : ExceptionHandler<PixKeyAlreadyExistsException> {
    override fun handle(e: PixKeyAlreadyExistsException): StatusWithDetails {
        return StatusWithDetails(
            Status.ALREADY_EXISTS
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is PixKeyAlreadyExistsException
    }
}