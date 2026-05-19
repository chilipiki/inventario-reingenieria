package mx.unison;

import mx.unison.database.Database;
import mx.unison.models.Producto;
import mx.unison.views.form.FormProducto;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class FormProductoTest {

    @Test
    void registro_producto_valido() {
        Database db = new Database();
        AtomicBoolean guardado = new AtomicBoolean(false);
        String nombre = "Producto form " + UUID.randomUUID();

        FormProducto form = new FormProducto((Window) null, db, null, () -> guardado.set(true));
        form.getTxtNombre().setText(nombre);
        form.getTxtDescripcion().setText("Producto de prueba");
        form.getTxtCantidad().setText("5");
        form.getTxtPrecio().setText("10.5");
        form.getBtnGuardar().doClick();

        Producto producto = buscarProductoPorNombre(db.listProductos(), nombre);

        assertTrue(guardado.get());
        assertNotNull(producto);

        if (producto != null) {
            db.deleteProducto(producto.id);
        }

    }

    @Test
    void registro_producto_nombre_vacio() {
        Database db = new Database();
        AtomicBoolean guardado = new AtomicBoolean(false);

        FormProducto form = new FormProducto((Window) null, db, null, () -> guardado.set(true));
        form.getTxtNombre().setText("");
        form.getTxtDescripcion().setText("Producto sin nombre");
        form.getTxtCantidad().setText("5");
        form.getTxtPrecio().setText("10.5");

        try {
            form.getBtnGuardar().doClick();
        } catch (HeadlessException ignored) {
        }

        assertFalse(guardado.get());

    }

    @Test
    void registro_producto_cantidad_no_numerica() {
        Database db = new Database();
        AtomicBoolean guardado = new AtomicBoolean(false);
        String nombre = "Producto invalido " + UUID.randomUUID();

        FormProducto form = new FormProducto((Window) null, db, null, () -> guardado.set(true));
        form.getTxtNombre().setText(nombre);
        form.getTxtDescripcion().setText("Cantidad inválida");
        form.getTxtCantidad().setText("abc");
        form.getTxtPrecio().setText("10.5");

        try {
            form.getBtnGuardar().doClick();
        } catch (HeadlessException ignored) {
        }

        assertFalse(guardado.get());
        assertNull(buscarProductoPorNombre(db.listProductos(), nombre));

    }

    @Test
    void registro_producto_precio_no_numerico() {
        Database db = new Database();
        AtomicBoolean guardado = new AtomicBoolean(false);
        String nombre = "Producto precio invalido " + UUID.randomUUID();

        FormProducto form = new FormProducto((Window) null, db, null, () -> guardado.set(true));
        form.getTxtNombre().setText(nombre);
        form.getTxtDescripcion().setText("Precio inválido");
        form.getTxtCantidad().setText("5");
        form.getTxtPrecio().setText("abc");

        try {
            form.getBtnGuardar().doClick();
        } catch (HeadlessException ignored) {
        }

        assertFalse(guardado.get());
        assertNull(buscarProductoPorNombre(db.listProductos(), nombre));

    }

    @Test
    void registro_precio_negativo() {
        Database db = new Database();
        AtomicBoolean guardado = new AtomicBoolean(false);
        String nombre = "Producto " + UUID.randomUUID();

        FormProducto form = new FormProducto((Window) null, db, null, () -> guardado.set(true));
        form.getTxtNombre().setText(nombre);
        form.getTxtDescripcion().setText("Precio inválido");
        form.getTxtCantidad().setText("5");
        form.getTxtPrecio().setText("-1");

        try {
            form.getBtnGuardar().doClick();
        } catch (HeadlessException ignored) {
        }

        assertFalse(guardado.get(), "No debe guardarse el producto con precio negativo");
        assertNull(buscarProductoPorNombre(db.listProductos(), nombre),
                "No debe existir un producto con precio negativo en la base de datos");

    }

    @Test
    void registro_cantidad_negativa() {
        Database db = new Database();
        AtomicBoolean guardado = new AtomicBoolean(false);
        String nombre = "Producto " + UUID.randomUUID();

        FormProducto form = new FormProducto((Window) null, db, null, () -> guardado.set(true));
        form.getTxtNombre().setText(nombre);
        form.getTxtDescripcion().setText("Precio inválido");
        form.getTxtCantidad().setText("-1");
        form.getTxtPrecio().setText("1000.00");

        try {
            form.getBtnGuardar().doClick();
        } catch (HeadlessException ignored) {
        }

        assertFalse(guardado.get(), "No debe guardarse el producto con cantidad negativa");
        assertNull(buscarProductoPorNombre(db.listProductos(), nombre),
                "No debe existir un producto con cantidad negativa en la base de datos");
    }

    private Producto buscarProductoPorNombre(List<Producto> productos, String nombre) {
        for (Producto producto : productos) {
            if (nombre.equals(producto.nombre)) {
                return producto;
            }
        }
        return null;
    }

}