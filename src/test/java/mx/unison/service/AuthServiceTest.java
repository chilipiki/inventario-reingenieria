package mx.unison.service;

import mx.unison.TestDatabaseManager;
import mx.unison.models.Usuario;
import mx.unison.util.CryptoUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para AuthService.
 */
class AuthServiceTest {

    private TestDatabaseManager testDb;
    private AuthService authService;

    @BeforeEach
    void setUp() throws Exception {
        testDb = new TestDatabaseManager();
        testDb.setUp();
        authService = new AuthService(testDb.getUsuarioDao());

        // Crear usuario de prueba
        String hash = CryptoUtils.hashPassword("password123");
        Usuario usuario = new Usuario("ADMIN", hash, "ADMIN");
        testDb.getUsuarioDao().create(usuario);
    }

    @AfterEach
    void tearDown() throws Exception {
        testDb.tearDown();
    }

    @Test
    void authenticate_conCredencialesCorrectas_deberiaRetornarUsuario() throws Exception {
        Usuario resultado = authService.authenticate("ADMIN", "password123");
        assertNotNull(resultado);
        assertEquals("ADMIN", resultado.getNombre());
    }

    @Test
    void authenticate_conPasswordIncorrecta_deberiaRetornarNull() throws Exception {
        Usuario resultado = authService.authenticate("ADMIN", "passwordIncorrecta");
        assertNull(resultado);
    }

    @Test
    void authenticate_conUsuarioInexistente_deberiaRetornarNull() throws Exception {
        Usuario resultado = authService.authenticate("NoExiste", "password123");
        assertNull(resultado);
    }

    @Test
    void authenticate_conCamposVacios_deberiaRetornarNull() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> authService.authenticate("", ""));
    }

    @Test
    void authenticate_deberiaActualizarFechaUltimoInicio() throws Exception {
        authService.authenticate("ADMIN", "password123");
        Usuario usuario = testDb.getUsuarioDao().getByNombre("ADMIN");
        assertNotNull(usuario.getFechaHoraUltimoInicio());
    }
}