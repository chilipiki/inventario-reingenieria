package mx.unison;

import mx.unison.database.Database;
import mx.unison.models.Almacen;
import mx.unison.views.form.FormAlmacen;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class FormAlmacenTest {

    @Test
    void registro_almacen_valido() {
        Database db = new Database();
        AtomicBoolean guardado = new AtomicBoolean(false);
        String nombre = "Almacen form " + UUID.randomUUID();

        FormAlmacen form = new FormAlmacen((Window) null, db, null, () -> guardado.set(true));
        form.getTxtNombre().setText(nombre);
        form.getTxtUbicacion().setText("Ubicación de prueba");
        form.getBtnGuardar().doClick();

        Almacen almacen = buscarAlmacenPorNombre(db.listAlmacenes(), nombre);

        assertTrue(guardado.get());
        assertNotNull(almacen);

        if (almacen != null) {
            db.deleteAlmacen(almacen.id);
        }

    }

    @Test
    void registro_almacen_nombre_vacio() {
        Database db = new Database();
        AtomicBoolean guardado = new AtomicBoolean(false);

        FormAlmacen form = new FormAlmacen((Window) null, db, null, () -> guardado.set(true));
        form.getTxtNombre().setText("");
        form.getTxtUbicacion().setText("Ubicación sin nombre");

        try {
            form.getBtnGuardar().doClick();
        } catch (HeadlessException ignored) {
        }

        assertFalse(guardado.get());

    }

    @Test
    void modificar_almacen_debe_reflejar_cambios() {
        Database db = new Database();
        String nombreInicial = "Almacen inicial " + UUID.randomUUID();
        int id = db.insertAlmacen(nombreInicial, "Ubicación inicial", "ADMIN");

        Almacen almacen = new Almacen();
        almacen.id = id;
        almacen.nombre = nombreInicial;
        almacen.ubicacion = "Ubicación inicial";

        String nombreActualizado = "Almacen actualizado " + UUID.randomUUID();

        try {
            FormAlmacen form = new FormAlmacen((Window) null, db, almacen, null);
            form.getTxtNombre().setText(nombreActualizado);
            form.getTxtUbicacion().setText("Ubicación actualizada");
            form.getBtnGuardar().doClick();

            Almacen almacenActualizado = buscarAlmacenPorId(db.listAlmacenes(), id);

            assertNotNull(almacenActualizado);
            assertEquals(nombreActualizado, almacenActualizado.nombre);
            assertEquals("Ubicación actualizada", almacenActualizado.ubicacion);
        } finally {
            db.deleteAlmacen(id);
        }

    }

    private Almacen buscarAlmacenPorNombre(List<Almacen> almacenes, String nombre) {
        for (Almacen almacen : almacenes) {
            if (nombre.equals(almacen.nombre)) {
                return almacen;
            }
        }
        return null;
    }

    private Almacen buscarAlmacenPorId(List<Almacen> almacenes, int id) {
        for (Almacen almacen : almacenes) {
            if (almacen.id == id) {
                return almacen;
            }
        }
        return null;
    }

}