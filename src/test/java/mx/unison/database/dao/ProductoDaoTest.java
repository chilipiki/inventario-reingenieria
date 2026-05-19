package mx.unison.database.dao;

import mx.unison.TestDatabaseManager;
import mx.unison.models.Almacen;
import mx.unison.models.Producto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para ProductoDao.
 */
class ProductoDaoTest {

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
    void create_deberiaGuardarProductoEnBaseDeDatos() throws Exception {
        Almacen almacen = new Almacen("Almacén Test", "Ubicación Test");
        testDb.getAlmacenDao().create(almacen);

        Producto producto = new Producto("Laptop", "Laptop Dell", 10, 999.99, almacen);
        testDb.getProductoDao().create(producto);

        List<Producto> productos = testDb.getProductoDao().getAll();
        assertEquals(1, productos.size());
        assertEquals("Laptop", productos.get(0).getNombre());
    }

    @Test
    void getAll_conBaseDeDatosVacia_deberiaRetornarListaVacia() throws Exception {
        List<Producto> productos = testDb.getProductoDao().getAll();
        assertTrue(productos.isEmpty());
    }

    @Test
    void getById_deberiaRetornarProductoCorrecto() throws Exception {
        Almacen almacen = new Almacen("Almacén Test", "Ubicación Test");
        testDb.getAlmacenDao().create(almacen);

        Producto producto = new Producto("Monitor", "Monitor 24", 5, 299.99, almacen);
        testDb.getProductoDao().create(producto);

        Producto encontrado = testDb.getProductoDao().getById(producto.getId());
        assertNotNull(encontrado);
        assertEquals("Monitor", encontrado.getNombre());
    }

    @Test
    void update_deberiaActualizarProductoCorrectamente() throws Exception {
        Almacen almacen = new Almacen("Almacén Test", "Ubicación Test");
        testDb.getAlmacenDao().create(almacen);

        Producto producto = new Producto("Laptop", "Laptop Dell", 10, 999.99, almacen);
        testDb.getProductoDao().create(producto);

        producto.setNombre("Laptop Actualizada");
        producto.setCantidad(20);
        testDb.getProductoDao().update(producto);

        Producto actualizado = testDb.getProductoDao().getById(producto.getId());
        assertEquals("Laptop Actualizada", actualizado.getNombre());
        assertEquals(20, actualizado.getCantidad());
    }

    @Test
    void delete_deberiaEliminarProductoCorrectamente() throws Exception {
        Almacen almacen = new Almacen("Almacén Test", "Ubicación Test");
        testDb.getAlmacenDao().create(almacen);

        Producto producto = new Producto("Laptop", "Laptop Dell", 10, 999.99, almacen);
        testDb.getProductoDao().create(producto);

        testDb.getProductoDao().delete(producto);

        List<Producto> productos = testDb.getProductoDao().getAll();
        assertTrue(productos.isEmpty());
    }

    @Test
    void getByAlmacen_deberiaRetornarProductosDelAlmacen() throws Exception {
        Almacen almacen1 = new Almacen("Almacén 1", "Ubicación 1");
        Almacen almacen2 = new Almacen("Almacén 2", "Ubicación 2");
        testDb.getAlmacenDao().create(almacen1);
        testDb.getAlmacenDao().create(almacen2);

        testDb.getProductoDao().create(new Producto("Producto 1", "Desc 1", 5, 100.0, almacen1));
        testDb.getProductoDao().create(new Producto("Producto 2", "Desc 2", 3, 200.0, almacen1));
        testDb.getProductoDao().create(new Producto("Producto 3", "Desc 3", 1, 300.0, almacen2));

        List<Producto> productosAlmacen1 = testDb.getProductoDao().getByAlmacen(almacen1.getId());
        assertEquals(2, productosAlmacen1.size());
    }
}