package mx.unison.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para CryptoUtils.
 */
class CryptoUtilsTest {

    @Test
    void hashPassword_deberiaGenerarHashNoNulo() {
        String hash = CryptoUtils.hashPassword("password123");
        assertNotNull(hash);
    }

    @Test
    void hashPassword_deberiaGenerarHashDiferenteAlTextoOriginal() {
        String password = "password123";
        String hash = CryptoUtils.hashPassword(password);
        assertNotEquals(password, hash);
    }

    @Test
    void hashPassword_dosHashesDeMismaPasswordDeberianSerDiferentes() {
        String hash1 = CryptoUtils.hashPassword("password123");
        String hash2 = CryptoUtils.hashPassword("password123");
        assertNotEquals(hash1, hash2);
    }

    @Test
    void verifyPassword_deberiaRetornarTrueConPasswordCorrecta() {
        String password = "password123";
        String hash = CryptoUtils.hashPassword(password);
        assertTrue(CryptoUtils.verifyPassword(password, hash));
    }

    @Test
    void verifyPassword_deberiaRetornarFalseConPasswordIncorrecta() {
        String hash = CryptoUtils.hashPassword("password123");
        assertFalse(CryptoUtils.verifyPassword("passwordIncorrecta", hash));
    }

    @Test
    void verifyPassword_deberiaRetornarFalseConPasswordVacia() {
        String hash = CryptoUtils.hashPassword("password123");
        assertFalse(CryptoUtils.verifyPassword("", hash));
    }

    @Test
    void hashPassword_deberiaFuncinarConCaracteresEspeciales() {
        String password = "p@$$w0rd!#%";
        String hash = CryptoUtils.hashPassword(password);
        assertTrue(CryptoUtils.verifyPassword(password, hash));
    }
}