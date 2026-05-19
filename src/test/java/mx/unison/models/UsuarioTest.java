package mx.unison.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el modelo Usuario.
 */
class UsuarioTest {

    @Test
    void constructorVacio_deberiaCrearUsuarioSinAtributos() {
        Usuario usuario = new Usuario();
        assertNull(usuario.getNombre());
        assertNull(usuario.getPassword());
        assertNull(usuario.getRol());
    }

    @Test
    void constructorParametrizado_deberiaAsignarAtributosCorrectamente() {
        Usuario usuario = new Usuario("ADMIN", "hash123", "ADMIN");
        assertEquals("ADMIN", usuario.getNombre());
        assertEquals("hash123", usuario.getPassword());
        assertEquals("ADMIN", usuario.getRol());
    }

    @Test
    void constructorParametrizado_deberiaAsignarFechaUltimoInicio() {
        Usuario usuario = new Usuario("ADMIN", "hash123", "ADMIN");
        assertNotNull(usuario.getFechaHoraUltimoInicio());
    }

    @Test
    void setters_deberianActualizarAtributosCorrectamente() {
        Usuario usuario = new Usuario();
        usuario.setNombre("PRODUCTOS");
        usuario.setPassword("hash456");
        usuario.setRol("PRODUCTOS");

        assertEquals("PRODUCTOS", usuario.getNombre());
        assertEquals("hash456", usuario.getPassword());
        assertEquals("PRODUCTOS", usuario.getRol());
    }

    @Test
    void setFechaHoraUltimoInicio_deberiaActualizarFecha() {
        Usuario usuario = new Usuario();
        String fecha = "2026-04-23T18:21:53";
        usuario.setFechaHoraUltimoInicio(fecha);
        assertEquals(fecha, usuario.getFechaHoraUltimoInicio());
    }
}