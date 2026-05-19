package mx.unison;

import mx.unison.database.Database;
import mx.unison.models.Producto;
import mx.unison.views.panel.PanelProductos;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class PanelProductosTest {

    @Test
    void tabla_productos_debe_mostrar_registros() {
        Database db = new Database();
        Producto producto = new Producto();
        producto.nombre = "Producto panel " + UUID.randomUUID();
        producto.descripcion = "Registro para tabla";
        producto.cantidad = 3;
        producto.precio = 9.5;
        producto.almacenId = 0;

        int id = db.insertProducto(producto, "ADMIN");

        try {
            PanelProductos panel = new PanelProductos(db, () -> {});

            assertTrue(panel.getTable().getRowCount() > 0);
            assertTrue(existeFilaConId(panel, id));
        } finally {
            db.deleteProducto(id);
        }

    }

    @Test
    void mostrar_formulario_agregar_producto() {
        Database db = new Database();
        AtomicBoolean formularioAbierto = new AtomicBoolean(false);

        PanelProductos panel = new PanelProductos(db, () -> {}) {
            @Override
            protected void openForm(Producto p) {
                formularioAbierto.set(true);
                assertNull(p);
            }
        };

        panel.getBtnAgregar().doClick();

        assertTrue(formularioAbierto.get());

    }

    @Test
    void eliminar_producto_desde_tabla() {
        Database db = new Database();
        Producto producto = new Producto();
        producto.nombre = "Producto eliminar " + UUID.randomUUID();
        producto.descripcion = "Eliminar desde panel";
        producto.cantidad = 4;
        producto.precio = 15.0;
        producto.almacenId = 0;

        int id = db.insertProducto(producto, "ADMIN");

        try {
            PanelProductos panel = new PanelProductos(db, () -> {}) {
                @Override
                protected boolean confirmarEliminacion() {
                    return true;
                }
            };

            int fila = buscarFilaPorId(panel, id);
            assertTrue(fila >= 0);

            panel.getTable().setRowSelectionInterval(fila, fila);
            panel.getBtnEliminar().doClick();

            assertNull(buscarProductoPorId(db.listProductos(), id));
        } finally {
            Producto restante = buscarProductoPorId(db.listProductos(), id);
            if (restante != null) {
                db.deleteProducto(id);
            }
        }

    }

    private boolean existeFilaConId(PanelProductos panel, int id) {
        return buscarFilaPorId(panel, id) >= 0;
    }

    private int buscarFilaPorId(PanelProductos panel, int id) {
        for (int fila = 0; fila < panel.getTable().getRowCount(); fila++) {
            Object valor = panel.getTable().getValueAt(fila, 0);
            if (valor instanceof Integer valorId && valorId == id) {
                return fila;
            }
        }
        return -1;
    }

    private Producto buscarProductoPorId(List<Producto> productos, int id) {
        for (Producto producto : productos) {
            if (producto.id == id) {
                return producto;
            }
        }
        return null;
    }

}