package br.com.zup.grpc.handler

import br.com.zup.grpc.exception.CPFDontMatchException
import br.com.zup.grpc.handler.config.ExceptionHandler
import br.com.zup.grpc.handler.config.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class CPFDontMatchExceptionHandler : ExceptionHandler<CPFDontMatchException> {
    override fun handle(e: CPFDontMatchException): StatusWithDetails {
        return StatusWithDetails(
            Status.INVALID_ARGUMENT
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is CPFDontMatchException
    }
}