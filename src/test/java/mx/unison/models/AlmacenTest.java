package mx.unison.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el modelo Almacen.
 */
class AlmacenTest {

    @Test
    void constructorVacio_deberiaCrearAlmacenSinAtributos() {
        Almacen almacen = new Almacen();
        assertNull(almacen.getNombre());
        assertNull(almacen.getUbicacion());
    }

    @Test
    void constructorParametrizado_deberiaAsignarAtributosCorrectamente() {
        Almacen almacen = new Almacen("Almacén Central", "Ciudad de México");
        assertEquals("Almacén Central", almacen.getNombre());
        assertEquals("Ciudad de México", almacen.getUbicacion());
    }

    @Test
    void constructorParametrizado_deberiaAsignarFechaCreacion() {
        Almacen almacen = new Almacen("Almacén Central", "Ciudad de México");
        assertNotNull(almacen.getFechaHoraCreacion());
    }

    @Test
    void setters_deberianActualizarAtributosCorrectamente() {
        Almacen almacen = new Almacen();
        almacen.setNombre("Almacén Norte");
        almacen.setUbicacion("Monterrey");

        assertEquals("Almacén Norte", almacen.getNombre());
        assertEquals("Monterrey", almacen.getUbicacion());
    }

    @Test
    void setUltimoUsuario_deberiaAsignarUsuarioCorrectamente() {
        Almacen almacen = new Almacen();
        almacen.setUltimoUsuario("ADMIN");
        assertEquals("ADMIN", almacen.getUltimoUsuario());
    }

    @Test
    void setFechaHoraUltimaMod_deberiaAsignarFechaCorrectamente() {
        Almacen almacen = new Almacen();
        String fecha = "2026-04-23T18:21:53";
        almacen.setFechaHoraUltimaMod(fecha);
        assertEquals(fecha, almacen.getFechaHoraUltimaMod());
    }
}