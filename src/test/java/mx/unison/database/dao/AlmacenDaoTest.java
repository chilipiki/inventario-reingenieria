package mx.unison.database.dao;

import mx.unison.TestDatabaseManager;
import mx.unison.models.Almacen;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para AlmacenDao.
 */
class AlmacenDaoTest {

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
    void create_deberiaGuardarAlmacenEnBaseDeDatos() throws Exception {
        Almacen almacen = new Almacen("Almacén Central", "Ciudad de México");
        testDb.getAlmacenDao().create(almacen);

        List<Almacen> almacenes = testDb.getAlmacenDao().getAll();
        assertEquals(1, almacenes.size());
        assertEquals("Almacén Central", almacenes.get(0).getNombre());
    }

    @Test
    void getAll_conBaseDeDatosVacia_deberiaRetornarListaVacia() throws Exception {
        List<Almacen> almacenes = testDb.getAlmacenDao().getAll();
        assertTrue(almacenes.isEmpty());
    }

    @Test
    void getById_deberiaRetornarAlmacenCorrecto() throws Exception {
        Almacen almacen = new Almacen("Almacén Norte", "Monterrey");
        testDb.getAlmacenDao().create(almacen);

        Almacen encontrado = testDb.getAlmacenDao().getById(almacen.getId());
        assertNotNull(encontrado);
        assertEquals("Almacén Norte", encontrado.getNombre());
    }

    @Test
    void update_deberiaActualizarAlmacenCorrectamente() throws Exception {
        Almacen almacen = new Almacen("Almacén Central", "Ciudad de México");
        testDb.getAlmacenDao().create(almacen);

        almacen.setNombre("Almacén Central Actualizado");
        almacen.setUbicacion("Guadalajara");
        testDb.getAlmacenDao().update(almacen);

        Almacen actualizado = testDb.getAlmacenDao().getById(almacen.getId());
        assertEquals("Almacén Central Actualizado", actualizado.getNombre());
        assertEquals("Guadalajara", actualizado.getUbicacion());
    }

    @Test
    void delete_deberiaEliminarAlmacenCorrectamente() throws Exception {
        Almacen almacen = new Almacen("Almacén Central", "Ciudad de México");
        testDb.getAlmacenDao().create(almacen);

        testDb.getAlmacenDao().delete(almacen);

        List<Almacen> almacenes = testDb.getAlmacenDao().getAll();
        assertTrue(almacenes.isEmpty());
    }

    @Test
    void create_dosAlmacenes_deberiaRetornarDosRegistros() throws Exception {
        testDb.getAlmacenDao().create(new Almacen("Almacén 1", "Ubicación 1"));
        testDb.getAlmacenDao().create(new Almacen("Almacén 2", "Ubicación 2"));

        List<Almacen> almacenes = testDb.getAlmacenDao().getAll();
        assertEquals(2, almacenes.size());
    }
}