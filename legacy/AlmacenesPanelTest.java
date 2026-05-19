package mx.unison;

import mx.unison.database.Database;
import mx.unison.models.Almacen;
import mx.unison.views.panel.AlmacenesPanel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class AlmacenesPanelTest {

    @Test
    void tabla_almacenes_debe_mostrar_registros() {
        Database db = new Database();
        String nombre = "Almacen panel " + UUID.randomUUID();
        int id = db.insertAlmacen(nombre, "Ubicación panel", "ADMIN");

        try {
            AlmacenesPanel panel = new AlmacenesPanel(db, () -> {});

            assertTrue(panel.getTable().getRowCount() > 0);
            assertTrue(existeFilaConId(panel, id));
        } finally {
            db.deleteAlmacen(id);
        }

    }

    @Test
    void mostrar_formulario_agregar_almacen() {
        Database db = new Database();
        AtomicBoolean formularioAbierto = new AtomicBoolean(false);

        AlmacenesPanel panel = new AlmacenesPanel(db, () -> {}) {
            @Override
            protected void openForm(Almacen a) {
                formularioAbierto.set(true);
                assertNull(a);
            }
        };

        panel.getBtnAgregar().doClick();

        assertTrue(formularioAbierto.get());

    }

    @Test
    void eliminar_almacen_desde_tabla() {
        Database db = new Database();
        String nombre = "Almacen eliminar " + UUID.randomUUID();
        int id = db.insertAlmacen(nombre, "Ubicación eliminar", "ADMIN");

        try {
            AlmacenesPanel panel = new AlmacenesPanel(db, () -> {}) {
                @Override
                protected boolean confirmarEliminacion() {
                    return true;
                }
            };

            int fila = buscarFilaPorId(panel, id);
            assertTrue(fila >= 0);

            panel.getTable().setRowSelectionInterval(fila, fila);
            panel.getBtnEliminar().doClick();

            assertNull(buscarAlmacenPorId(db.listAlmacenes(), id));
        } finally {
            Almacen restante = buscarAlmacenPorId(db.listAlmacenes(), id);
            if (restante != null) {
                db.deleteAlmacen(id);
            }
        }

    }

    private boolean existeFilaConId(AlmacenesPanel panel, int id) {
        return buscarFilaPorId(panel, id) >= 0;
    }

    private int buscarFilaPorId(AlmacenesPanel panel, int id) {
        for (int fila = 0; fila < panel.getTable().getRowCount(); fila++) {
            Object valor = panel.getTable().getValueAt(fila, 0);
            if (valor instanceof Integer valorId && valorId == id) {
                return fila;
            }
        }
        return -1;
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