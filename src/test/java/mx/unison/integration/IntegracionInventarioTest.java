package mx.unison.integration;

import mx.unison.TestDatabaseManager;
import mx.unison.models.Almacen;
import mx.unison.models.Producto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para el módulo de inventario.
 * Verifica la interacción entre los DAOs de Producto y Almacen.
 */
class IntegracionInventarioTest {

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
    void flujoCompleto_crearAlmacenYAgregarProductos() throws Exception {
        // Crear almacén
        Almacen almacen = new Almacen("Almacén Central", "Ciudad de México");
        testDb.getAlmacenDao().create(almacen);

        // Agregar productos al almacén
        testDb.getProductoDao().create(new Producto("Laptop", "Laptop Dell", 10, 999.99, almacen));
        testDb.getProductoDao().create(new Producto("Monitor", "Monitor 24", 5, 299.99, almacen));
        testDb.getProductoDao().create(new Producto("Teclado", "Teclado mecánico", 20, 79.99, almacen));

        // Verificar que los productos se asociaron correctamente
        List<Producto> productos = testDb.getProductoDao().getByAlmacen(almacen.getId());
        assertEquals(3, productos.size());
    }

    @Test
    void flujoCompleto_crearActualizarYEliminarProducto() throws Exception {
        // Crear almacén
        Almacen almacen = new Almacen("Almacén Norte", "Monterrey");
        testDb.getAlmacenDao().create(almacen);

        // Crear producto
        Producto producto = new Producto("Laptop", "Laptop Dell", 10, 999.99, almacen);
        testDb.getProductoDao().create(producto);
        assertNotNull(testDb.getProductoDao().getById(producto.getId()));

        // Actualizar producto
        producto.setNombre("Laptop Actualizada");
        producto.setCantidad(15);
        producto.setUltimoUsuario("ADMIN");
        testDb.getProductoDao().update(producto);

        Producto actualizado = testDb.getProductoDao().getById(producto.getId());
        assertEquals("Laptop Actualizada", actualizado.getNombre());
        assertEquals(15, actualizado.getCantidad());

        // Eliminar producto
        testDb.getProductoDao().delete(producto);
        assertNull(testDb.getProductoDao().getById(producto.getId()));
    }

    @Test
    void flujoCompleto_crearActualizarYEliminarAlmacen() throws Exception {
        // Crear almacén
        Almacen almacen = new Almacen("Almacén Sur", "Guadalajara");
        testDb.getAlmacenDao().create(almacen);
        assertNotNull(testDb.getAlmacenDao().getById(almacen.getId()));

        // Actualizar almacén
        almacen.setNombre("Almacén Sur Actualizado");
        almacen.setUbicacion("Zapopan");
        almacen.setUltimoUsuario("ADMIN");
        testDb.getAlmacenDao().update(almacen);

        Almacen actualizado = testDb.getAlmacenDao().getById(almacen.getId());
        assertEquals("Almacén Sur Actualizado", actualizado.getNombre());
        assertEquals("Zapopan", actualizado.getUbicacion());

        // Eliminar almacén
        testDb.getAlmacenDao().delete(almacen);
        assertNull(testDb.getAlmacenDao().getById(almacen.getId()));
    }

    @Test
    void multipleAlmacenes_productosDeberianAsociarseCorrectamente() throws Exception {
        // Crear dos almacenes
        Almacen almacen1 = new Almacen("Almacén 1", "Ubicación 1");
        Almacen almacen2 = new Almacen("Almacén 2", "Ubicación 2");
        testDb.getAlmacenDao().create(almacen1);
        testDb.getAlmacenDao().create(almacen2);

        // Agregar productos a cada almacén
        testDb.getProductoDao().create(new Producto("Producto A", "Desc A", 5, 100.0, almacen1));
        testDb.getProductoDao().create(new Producto("Producto B", "Desc B", 3, 200.0, almacen1));
        testDb.getProductoDao().create(new Producto("Producto C", "Desc C", 1, 300.0, almacen2));

        // Verificar que cada almacén tiene sus productos correctos
        assertEquals(2, testDb.getProductoDao().getByAlmacen(almacen1.getId()).size());
        assertEquals(1, testDb.getProductoDao().getByAlmacen(almacen2.getId()).size());
    }

    @Test
    void stockTotal_deberiaCalcularseCorrectamente() throws Exception {
        Almacen almacen = new Almacen("Almacén Test", "Ubicación Test");
        testDb.getAlmacenDao().create(almacen);

        testDb.getProductoDao().create(new Producto("Producto 1", "Desc 1", 10, 100.0, almacen));
        testDb.getProductoDao().create(new Producto("Producto 2", "Desc 2", 20, 200.0, almacen));
        testDb.getProductoDao().create(new Producto("Producto 3", "Desc 3", 30, 300.0, almacen));

        int stockTotal = testDb.getProductoDao().getAll()
                .stream()
                .mapToInt(Producto::getCantidad)
                .sum();

        assertEquals(60, stockTotal);
    }

    @Test
    void auditoria_deberiaRegistrarUltimoUsuario() throws Exception {
        Almacen almacen = new Almacen("Almacén Test", "Ubicación Test");
        testDb.getAlmacenDao().create(almacen);

        Producto producto = new Producto("Laptop", "Laptop Dell", 10, 999.99, almacen);
        producto.setUltimoUsuario("ADMIN");
        testDb.getProductoDao().create(producto);

        producto.setNombre("Laptop Actualizada");
        producto.setUltimoUsuario("PRODUCTOS");
        testDb.getProductoDao().update(producto);

        Producto actualizado = testDb.getProductoDao().getById(producto.getId());
        assertEquals("PRODUCTOS", actualizado.getUltimoUsuario());
    }
}
