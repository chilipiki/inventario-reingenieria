package mx.unison.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el modelo Producto.
 */
class ProductoTest {

    @Test
    void constructorVacio_deberiaCrearProductoSinAtributos() {
        Producto producto = new Producto();
        assertNull(producto.getNombre());
        assertNull(producto.getDescripcion());
        assertEquals(0, producto.getCantidad());
        assertEquals(0.0, producto.getPrecio());
    }

    @Test
    void constructorParametrizado_deberiaAsignarAtributosCorrectamente() {
        Almacen almacen = new Almacen("Almacén Central", "Ciudad de México");
        Producto producto = new Producto("Laptop", "Laptop Dell", 10, 999.99, almacen);

        assertEquals("Laptop", producto.getNombre());
        assertEquals("Laptop Dell", producto.getDescripcion());
        assertEquals(10, producto.getCantidad());
        assertEquals(999.99, producto.getPrecio());
        assertEquals(almacen, producto.getAlmacen());
    }

    @Test
    void constructorParametrizado_deberiaAsignarFechaCreacion() {
        Almacen almacen = new Almacen("Almacén Central", "Ciudad de México");
        Producto producto = new Producto("Laptop", "Laptop Dell", 10, 999.99, almacen);
        assertNotNull(producto.getFechaCreacion());
    }

    @Test
    void setters_deberianActualizarAtributosCorrectamente() {
        Producto producto = new Producto();
        producto.setNombre("Monitor");
        producto.setDescripcion("Monitor 24 pulgadas");
        producto.setCantidad(5);
        producto.setPrecio(299.99);

        assertEquals("Monitor", producto.getNombre());
        assertEquals("Monitor 24 pulgadas", producto.getDescripcion());
        assertEquals(5, producto.getCantidad());
        assertEquals(299.99, producto.getPrecio());
    }

    @Test
    void setAlmacen_deberiaAsignarAlmacenCorrectamente() {
        Producto producto = new Producto();
        Almacen almacen = new Almacen("Almacén Norte", "Monterrey");
        producto.setAlmacen(almacen);
        assertEquals(almacen, producto.getAlmacen());
    }

    @Test
    void setUltimoUsuario_deberiaAsignarUsuarioCorrectamente() {
        Producto producto = new Producto();
        producto.setUltimoUsuario("ADMIN");
        assertEquals("ADMIN", producto.getUltimoUsuario());
    }
}