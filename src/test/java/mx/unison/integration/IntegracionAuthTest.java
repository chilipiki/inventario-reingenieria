package mx.unison.integration;

import mx.unison.TestDatabaseManager;
import mx.unison.models.Usuario;
import mx.unison.service.AuthService;
import mx.unison.util.CryptoUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para el módulo de autenticación.
 * Verifica la interacción entre AuthService, UsuarioDao y CryptoUtils.
 */
class IntegracionAuthTest {

    private TestDatabaseManager testDb;
    private AuthService authService;

    @BeforeEach
    void setUp() throws Exception {
        testDb = new TestDatabaseManager();
        testDb.setUp();
        authService = new AuthService(testDb.getUsuarioDao());

        // Crear usuarios de prueba
        testDb.getUsuarioDao().create(new Usuario("ADMIN", CryptoUtils.hashPassword("admin123"), "ADMIN"));
        testDb.getUsuarioDao().create(new Usuario("PRODUCTOS", CryptoUtils.hashPassword("productos123"), "PRODUCTOS"));
        testDb.getUsuarioDao().create(new Usuario("ALMACENES", CryptoUtils.hashPassword("almacenes123"), "ALMACENES"));
    }

    @AfterEach
    void tearDown() throws Exception {
        testDb.tearDown();
    }

    @Test
    void flujoCompleto_loginExitoso_deberiaRetornarUsuario() throws Exception {
        Usuario resultado = authService.authenticate("ADMIN", "admin123");
        assertNotNull(resultado);
        assertEquals("ADMIN", resultado.getNombre());
        assertEquals("ADMIN", resultado.getRol());
    }

    @Test
    void flujoCompleto_loginFallido_deberiaRetornarNull() throws Exception {
        Usuario resultado = authService.authenticate("ADMIN", "passwordIncorrecta");
        assertNull(resultado);
    }

    @Test
    void flujoCompleto_loginExitoso_deberiaActualizarFechaUltimoInicio() throws Exception {
        String fechaAntes = testDb.getUsuarioDao().getByNombre("ADMIN").getFechaHoraUltimoInicio();

        // Pequeña pausa para asegurar diferencia en la fecha
        Thread.sleep(100);

        authService.authenticate("ADMIN", "admin123");
        String fechaDespues = testDb.getUsuarioDao().getByNombre("ADMIN").getFechaHoraUltimoInicio();

        assertNotEquals(fechaAntes, fechaDespues);
    }

    @Test
    void flujoCompleto_cambioDePassword_deberiaPermitirNuevoLogin() throws Exception {
        // Login con password original
        assertNotNull(authService.authenticate("ADMIN", "admin123"));

        // Cambiar password
        Usuario admin = testDb.getUsuarioDao().getByNombre("ADMIN");
        admin.setPassword(CryptoUtils.hashPassword("nuevaPassword123"));
        testDb.getUsuarioDao().update(admin);

        // Login con password antigua debería fallar
        assertNull(authService.authenticate("ADMIN", "admin123"));

        // Login con nueva password debería funcionar
        assertNotNull(authService.authenticate("ADMIN", "nuevaPassword123"));
    }

    @Test
    void flujoCompleto_multipleUsuarios_deberianAutenticarseIndependientemente() throws Exception {
        Usuario admin = authService.authenticate("ADMIN", "admin123");
        Usuario productos = authService.authenticate("PRODUCTOS", "productos123");
        Usuario almacenes = authService.authenticate("ALMACENES", "almacenes123");

        assertNotNull(admin);
        assertNotNull(productos);
        assertNotNull(almacenes);

        assertEquals("ADMIN", admin.getRol());
        assertEquals("PRODUCTOS", productos.getRol());
        assertEquals("ALMACENES", almacenes.getRol());
    }

    @Test
    void flujoCompleto_crearUsuario_deberiaPoderAutenticarse() throws Exception {
        // Crear nuevo usuario
        Usuario nuevoUsuario = new Usuario("NUEVO", CryptoUtils.hashPassword("nuevo123"), "PRODUCTOS");
        testDb.getUsuarioDao().create(nuevoUsuario);

        // Verificar que puede autenticarse
        Usuario resultado = authService.authenticate("NUEVO", "nuevo123");
        assertNotNull(resultado);
        assertEquals("NUEVO", resultado.getNombre());
    }

    @Test
    void flujoCompleto_eliminarUsuario_noDeberiaPoderAutenticarse() throws Exception {
        // Verificar que puede autenticarse
        assertNotNull(authService.authenticate("PRODUCTOS", "productos123"));

        // Eliminar usuario
        Usuario productos = testDb.getUsuarioDao().getByNombre("PRODUCTOS");
        testDb.getUsuarioDao().delete(productos);

        // Verificar que ya no puede autenticarse
        assertNull(authService.authenticate("PRODUCTOS", "productos123"));
    }
}
