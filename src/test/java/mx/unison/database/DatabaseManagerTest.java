package mx.unison.database;

import mx.unison.models.Almacen;
import mx.unison.models.Producto;
import mx.unison.models.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para DatabaseManager.
 */
class DatabaseManagerTest {

    private DatabaseManager dbManager;

    @BeforeEach
    void setUp() throws Exception {
        dbManager = new DatabaseManager("jdbc:sqlite::memory:");
    }

    @AfterEach
    void tearDown() throws Exception {
        dbManager.close();
    }

    @Test
    void constructor_deberiaInicializarDaosCorrectamente() {
        assertNotNull(dbManager.getProductoDao());
        assertNotNull(dbManager.getAlmacenDao());
        assertNotNull(dbManager.getUsuarioDao());
    }

    @Test
    void constructor_deberiaCrearUsuariosPorDefecto() throws Exception {
        Usuario admin = dbManager.getUsuarioDao().getByNombre("ADMIN");
        Usuario productos = dbManager.getUsuarioDao().getByNombre("PRODUCTOS");
        Usuario almacenes = dbManager.getUsuarioDao().getByNombre("ALMACENES");

        assertNotNull(admin);
        assertNotNull(productos);
        assertNotNull(almacenes);
    }

    @Test
    void constructor_usuariosPorDefecto_deberianTenerRolesCorrrectos() throws Exception {
        Usuario admin = dbManager.getUsuarioDao().getByNombre("ADMIN");
        Usuario productos = dbManager.getUsuarioDao().getByNombre("PRODUCTOS");
        Usuario almacenes = dbManager.getUsuarioDao().getByNombre("ALMACENES");

        assertEquals("ADMIN", admin.getRol());
        assertEquals("PRODUCTOS", productos.getRol());
        assertEquals("ALMACENES", almacenes.getRol());
    }

    @Test
    void constructor_deberiaCrearTablasCorrectamente() throws Exception {
        List<Producto> productos = dbManager.getProductoDao().getAll();
        List<Almacen> almacenes = dbManager.getAlmacenDao().getAll();

        assertNotNull(productos);
        assertNotNull(almacenes);
    }

    @Test
    void constructor_noDeberiaCrearUsuariosDuplicados() throws Exception {
        // Crear segunda instancia con la misma base de datos
        DatabaseManager dbManager2 = new DatabaseManager("jdbc:sqlite::memory:");

        List<Usuario> usuarios = dbManager2.getUsuarioDao().getAll();
        assertEquals(3, usuarios.size());

        dbManager2.close();
    }

    @Test
    void close_deberiaRerrarConexionSinErrores() {
        assertDoesNotThrow(() -> dbManager.close());
    }
}