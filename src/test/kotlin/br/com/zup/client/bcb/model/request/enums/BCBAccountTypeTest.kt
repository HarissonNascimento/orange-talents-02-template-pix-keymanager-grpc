package br.com.zup.client.bcb.model.request.enums

import br.com.zup.GrpcAccountType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class BCBAccountTypeTest{

    @Nested
    inner class CACC {

        @Test
        fun `returns CACC when receiving CONTA_CORRENTE`() {
            with(BCBAccountType.by(GrpcAccountType.CONTA_CORRENTE)) {
                assertTrue(equals(BCBAccountType.CACC))
            }
        }

        @Test
        fun `returns error when not receiving CONTA_CORRENTE`() {
            with(BCBAccountType.by(GrpcAccountType.CONTA_POUPANCA)) {
                assertFalse(equals(BCBAccountType.CACC))
            }
        }
    }

    @Nested
    inner class SVGS {

        @Test
        fun `returns SVGS when receiving CONTA_POUPANCA`() {
            with(BCBAccountType.by(GrpcAccountType.CONTA_POUPANCA)) {
                assertTrue(equals(BCBAccountType.SVGS))
            }
        }

        @Test
        fun `returns error when not receiving CONTA_POUPANCA`() {
            with(BCBAccountType.by(GrpcAccountType.CONTA_CORRENTE)) {
                assertFalse(equals(BCBAccountType.SVGS))
            }
        }
    }
}