package br.com.zup.client.bcb.model.request.enums

import br.com.zup.enums.KeyType
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class BCBKeyTypeTest {

    @Test
    fun `returns BCB key type CPF when receiving key type CPF`() {
        with(BCBKeyType.by(KeyType.CPF)) {
            assertTrue(equals(BCBKeyType.CPF))
        }
    }

    @Test
    fun `returns BCB key type PHONE when receiving key type TELEFONE_CELULAR`() {
        with(BCBKeyType.by(KeyType.TELEFONE_CELULAR)) {
            assertTrue(equals(BCBKeyType.PHONE))
        }
    }

    @Test
    fun `returns BCB key type EMAIL when receiving key type EMAIL`() {
        with(BCBKeyType.by(KeyType.EMAIL)) {
            assertTrue(equals(BCBKeyType.EMAIL))
        }
    }

    @Test
    fun `returns BCB key type RANDOM when receiving key type CHAVE_ALEATORIA`() {
        with(BCBKeyType.by(KeyType.CHAVE_ALEATORIA)) {
            assertTrue(equals(BCBKeyType.RANDOM))
        }
    }

    @Test
    fun `returns IllegalArgumentException when not receiving valid key type`() {
            assertThrows<IllegalArgumentException>{ BCBKeyType.by(KeyType.valueOf("CNPJ"))}
    }

}