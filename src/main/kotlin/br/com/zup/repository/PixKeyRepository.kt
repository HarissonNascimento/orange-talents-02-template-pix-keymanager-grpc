package br.com.zup.repository

import br.com.zup.model.domain.PixKey
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface PixKeyRepository : JpaRepository<PixKey, UUID> {

    fun existsByKeyValue(keyValue: String?): Boolean

    fun findByIdAndClientId(id: UUID?, clientId: UUID): Optional<PixKey>

}