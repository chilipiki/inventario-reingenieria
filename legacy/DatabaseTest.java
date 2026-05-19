package mx.unison;

import static org.junit.jupiter.api.Assertions.*;

import mx.unison.database.Database;
import mx.unison.models.Producto;
import mx.unison.models.Usuario;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

class DatabaseTest {

	@Test
	void conexion_correcta_base_datos() throws Exception {

		Database db = new Database();

		Connection connection = db.connect();

		assertNotNull(connection, "La conexión no debe ser null");
		assertFalse(connection.isClosed(), "La conexión debe estar abierta");

		connection.close();
	}

	@Test
	void insercion_usuario_default_debe_crearse() throws Exception {

		Database db = new Database();
		String nombre = "User_Default" + UUID.randomUUID();

		db.insertDefaultUser(nombre, "pass123", "PRODUCTOS");

		Usuario usuario = db.authenticate(nombre, "pass123");

		assertNotNull(usuario, "El usuario insertado debe existir y autenticarse");
		assertEquals(nombre, usuario.nombre);
		assertEquals("PRODUCTOS", usuario.rol);
	}

	@Test
	void autenticacion_usuario_valida_debe_retornar_usuario() {
		Database db = new Database();

		Usuario usuario = db.authenticate("ADMIN", "admin23");

		assertNotNull(usuario, "Se esperaba un usuario válido con credenciales correctas");
		assertEquals("ADMIN", usuario.nombre);
		assertEquals("ADMIN", usuario.rol);
	}

	@Test
	void autenticacion_credenciales_incorrectas_debe_retornar_null() {
		Database db = new Database();

		Usuario usuario = db.authenticate("ADMIN", "contrasena_incorrecta");

		assertNull(usuario, "Se esperaba null con credenciales inválidas");
	}

	@Test
	void insercion_producto_debe_aparecer_en_lista() {
		Database db = new Database();
		Producto producto = crearProductoPrueba();
		int idInsertado = -1;

		try {
			idInsertado = db.insertProducto(producto, "ADMIN");

			assertTrue(idInsertado > 0, "El ID generado del producto debe ser mayor que 0");

			List<Producto> productos = db.listProductos();
			Producto encontrado = buscarProductoPorId(productos, idInsertado);

			assertNotNull(encontrado, "El producto insertado debe existir en la lista");
			assertEquals(producto.nombre, encontrado.nombre);
			assertEquals(producto.descripcion, encontrado.descripcion);
			assertEquals(producto.cantidad, encontrado.cantidad);
			assertEquals(producto.precio, encontrado.precio, 0.0001);
		} finally {
			if (idInsertado > 0) {
				db.deleteProducto(idInsertado);
			}
		}
	}

	@Test
	void actualizacion_producto_debe_reflejar_cambios() {
		Database db = new Database();
		Producto producto = crearProductoPrueba();
		int idInsertado = -1;

		try {
			idInsertado = db.insertProducto(producto, "ADMIN");
			assertTrue(idInsertado > 0);

			producto.id = idInsertado;
			producto.nombre = "Producto actualizado " + UUID.randomUUID();
			producto.descripcion = "Descripción actualizada";
			producto.cantidad = 99;
			producto.precio = 123.45;

			db.updateProducto(producto, "ADMIN");

			Producto actualizado = buscarProductoPorId(db.listProductos(), idInsertado);
			assertNotNull(actualizado, "El producto actualizado debe seguir existiendo");
			assertEquals(producto.nombre, actualizado.nombre);
			assertEquals(producto.descripcion, actualizado.descripcion);
			assertEquals(producto.cantidad, actualizado.cantidad);
			assertEquals(producto.precio, actualizado.precio, 0.0001);
		} finally {
			if (idInsertado > 0) {
				db.deleteProducto(idInsertado);
			}
		}
	}

	@Test
	void eliminacion_producto_debe_quitarlo_de_lista() {
		Database db = new Database();
		Producto producto = crearProductoPrueba();

		int idInsertado = db.insertProducto(producto, "ADMIN");
		assertTrue(idInsertado > 0);

		db.deleteProducto(idInsertado);

		Producto eliminado = buscarProductoPorId(db.listProductos(), idInsertado);
		assertNull(eliminado, "El producto eliminado no debe aparecer en la lista");
	}

	@Test
	void consulta_productos_debe_retornar_lista_con_informacion() {
		Database db = new Database();
		Producto p1 = crearProductoPrueba();
		Producto p2 = crearProductoPrueba();
		int id1 = -1;
		int id2 = -1;

		try {
			id1 = db.insertProducto(p1, "ADMIN");
			id2 = db.insertProducto(p2, "ADMIN");

			List<Producto> productos = db.listProductos();

			assertNotNull(productos, "La lista de productos no debe ser null");
			assertFalse(productos.isEmpty(), "La lista de productos no debe estar vacía");
			assertNotNull(buscarProductoPorId(productos, id1), "Debe incluir el primer producto insertado");
			assertNotNull(buscarProductoPorId(productos, id2), "Debe incluir el segundo producto insertado");
		} finally {
			if (id1 > 0) {
				db.deleteProducto(id1);
			}
			if (id2 > 0) {
				db.deleteProducto(id2);
			}
		}
	}

	private Producto crearProductoPrueba() {
		Producto producto = new Producto();
		producto.nombre = "Producto test " + UUID.randomUUID();
		producto.descripcion = "Descripción de prueba";
		producto.cantidad = 10;
		producto.precio = 50.0;
		producto.almacenId = 0;
		return producto;
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