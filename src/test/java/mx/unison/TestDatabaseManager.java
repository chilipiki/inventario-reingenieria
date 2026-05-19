package mx.unison;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import mx.unison.database.dao.AlmacenDao;
import mx.unison.database.dao.ProductoDao;
import mx.unison.database.dao.UsuarioDao;
import mx.unison.models.Almacen;
import mx.unison.models.Producto;
import mx.unison.models.Usuario;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;

/**
 * Clase de apoyo para pruebas que provee una base de datos SQLite en memoria.
 * Se usa como base para los tests que requieren acceso a la base de datos.
 */
public class TestDatabaseManager {

    private static final String TEST_DATABASE_URL = "jdbc:sqlite::memory:";

    private ConnectionSource connectionSource;
    private ProductoDao productoDao;
    private AlmacenDao almacenDao;
    private UsuarioDao usuarioDao;

    /**
     * Inicializa la base de datos en memoria y crea las tablas.
     */
    public void setUp() throws SQLException {
        connectionSource = new JdbcConnectionSource(TEST_DATABASE_URL);
        TableUtils.createTableIfNotExists(connectionSource, Usuario.class);
        TableUtils.createTableIfNotExists(connectionSource, Almacen.class);
        TableUtils.createTableIfNotExists(connectionSource, Producto.class);

        productoDao = new ProductoDao(DaoManager.createDao(connectionSource, Producto.class));
        almacenDao = new AlmacenDao(DaoManager.createDao(connectionSource, Almacen.class));
        usuarioDao = new UsuarioDao(DaoManager.createDao(connectionSource, Usuario.class));
    }

    /**
     * Cierra la conexión a la base de datos en memoria.
     */
    public void tearDown() throws Exception {
        if (connectionSource != null) {
            connectionSource.close();
        }
    }

    public ProductoDao getProductoDao() { return productoDao; }
    public AlmacenDao getAlmacenDao() { return almacenDao; }
    public UsuarioDao getUsuarioDao() { return usuarioDao; }
    public ConnectionSource getConnectionSource() { return connectionSource; }
}