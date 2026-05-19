package mx.unison.database.dao;

import mx.unison.TestDatabaseManager;
import mx.unison.models.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para UsuarioDao.
 */
class UsuarioDaoTest {

    private TestDatabaseManager testDb;

    @BeforeEach
    void setUp() throws Exception {
        testDb = new TestDatabaseManager();
        testDb.setUp();
    }

    @AfterEach
    void tearDown() throws Exception {
        testDb.tearDown();
    }

    @Test
    void create_deberiaGuardarUsuarioEnBaseDeDatos() throws Exception {
        Usuario usuario = new Usuario("ADMIN", "hash123", "ADMIN");
        testDb.getUsuarioDao().create(usuario);

        List<Usuario> usuarios = testDb.getUsuarioDao().getAll();
        assertEquals(1, usuarios.size());
        assertEquals("ADMIN", usuarios.get(0).getNombre());
    }

    @Test
    void getAll_conBaseDeDatosVacia_deberiaRetornarListaVacia() throws Exception {
        List<Usuario> usuarios = testDb.getUsuarioDao().getAll();
        assertTrue(usuarios.isEmpty());
    }

    @Test
    void getByNombre_deberiaRetornarUsuarioCorrecto() throws Exception {
        Usuario usuario = new Usuario("ADMIN", "hash123", "ADMIN");
        testDb.getUsuarioDao().create(usuario);

        Usuario encontrado = testDb.getUsuarioDao().getByNombre("ADMIN");
        assertNotNull(encontrado);
        assertEquals("ADMIN", encontrado.getNombre());
    }

    @Test
    void getByNombre_conNombreInexistente_deberiaRetornarNull() throws Exception {
        Usuario encontrado = testDb.getUsuarioDao().getByNombre("NoExiste");
        assertNull(encontrado);
    }

    @Test
    void update_deberiaActualizarUsuarioCorrectamente() throws Exception {
        Usuario usuario = new Usuario("ADMIN", "hash123", "ADMIN");
        testDb.getUsuarioDao().create(usuario);

        usuario.setPassword("nuevoHash456");
        testDb.getUsuarioDao().update(usuario);

        Usuario actualizado = testDb.getUsuarioDao().getByNombre("ADMIN");
        assertEquals("nuevoHash456", actualizado.getPassword());
    }

    @Test
    void delete_deberiaEliminarUsuarioCorrectamente() throws Exception {
        Usuario usuario = new Usuario("ADMIN", "hash123", "ADMIN");
        testDb.getUsuarioDao().create(usuario);

        testDb.getUsuarioDao().delete(usuario);

        List<Usuario> usuarios = testDb.getUsuarioDao().getAll();
        assertTrue(usuarios.isEmpty());
    }
}