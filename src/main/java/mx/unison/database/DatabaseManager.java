package mx.unison.database;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import mx.unison.models.Almacen;
import mx.unison.models.Producto;
import mx.unison.models.Usuario;
import mx.unison.database.dao.*;
import mx.unison.util.CryptoUtils;

import java.sql.SQLException;

/**
 * Gestor principal de la base de datos.
 * Se encarga de la conexión a SQLite, creación de tablas y provisión de DAOs.
 */
public class DatabaseManager {
    /** URL de conexión JDBC a la base de datos SQLite. */
    private static final String DATABASE_URL = "jdbc:sqlite:Inventario.db";

    /** Fuente de conexión administrada por ORMLite. */
    private ConnectionSource connectionSource;

    /** DAO para la entidad Almacen. */
    private AlmacenDao almacenDao;
    /** DAO para la entidad Producto. */
    private ProductoDao productoDao;
    /** DAO para la entidad Usuario. */
    private UsuarioDao usuarioDao;

    /**
     * Constructor que inicializa la conexión y configura la base de datos.
     * @throws SQLException Si hay problemas de conexión o creación de tablas.
     */
    public DatabaseManager() throws SQLException {
        this.connectionSource = new JdbcConnectionSource(DATABASE_URL);
        initializeDaos();
        initializeTables();
    }

    /**
     * Constructor alternativo que permite especificar una URL de base de datos personalizada.
     * Útil para pruebas unitarias con bases de datos en memoria.
     * @param databaseUrl URL JDBC de la base de datos a conectar.
     * @throws SQLException Si hay problemas de conexión o creación de tablas.
     */
    public DatabaseManager(String databaseUrl) throws SQLException {
        this.connectionSource = new JdbcConnectionSource(databaseUrl);
        initializeDaos();
        initializeTables();
    }

    /**
     * Inicializa los objetos DAO para cada entidad.
     * @throws SQLException Si ocurre un error al crear los DAOs.
     */
    private void initializeDaos() throws SQLException {
        this.almacenDao = new AlmacenDao(
                DaoManager.createDao(connectionSource, Almacen.class)
        );
        this.productoDao = new ProductoDao(
                DaoManager.createDao(connectionSource, Producto.class)
        );
        this.usuarioDao = new UsuarioDao(
                DaoManager.createDao(connectionSource, Usuario.class)
        );
    }

    /**
     * Crea las tablas en la base de datos si no existen.
     * @throws SQLException Si ocurre un error durante la creación.
     */
    private void initializeTables() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, Usuario.class);
        TableUtils.createTableIfNotExists(connectionSource, Almacen.class);
        TableUtils.createTableIfNotExists(connectionSource, Producto.class);

        // Insertar usuarios por defecto
        createDefaultUsers();
    }

    /**
     * Genera los usuarios por defecto del sistema (ej. Administrador inicial).
     * @throws SQLException Si ocurre un error al insertar en la base de datos.
     */
    private void createDefaultUsers() throws SQLException {
        createUserIfNotExists("ADMIN", "admin23", "ADMIN");
        createUserIfNotExists("PRODUCTOS", "productos19", "PRODUCTOS");
        createUserIfNotExists("ALMACENES", "almacenes11", "ALMACENES");
    }

    /**
     * Crea un usuario específico si su nombre no existe en el sistema.
     * @param nombre Nombre del usuario.
     * @param password Contraseña sin encriptar.
     * @param rol Rol del usuario en el sistema.
     * @throws SQLException Si ocurre un error de validación o inserción.
     */
    private void createUserIfNotExists(String nombre, String password, String rol)
            throws SQLException {
        if (usuarioDao.getByNombre(nombre) == null) {
            String hashedPassword = CryptoUtils.hashPassword(password);
            Usuario user = new Usuario(nombre, hashedPassword, rol);
            usuarioDao.create(user);
        }
    }

    /**
     * Obtiene el DAO correspondiente a la entidad Almacen.
     * @return Instancia de AlmacenDao.
     */
    public AlmacenDao getAlmacenDao() { return almacenDao; }

    /**
     * Obtiene el DAO correspondiente a la entidad Producto.
     * @return Instancia de ProductoDao.
     */
    public ProductoDao getProductoDao() { return productoDao; }

    /**
     * Obtiene el DAO correspondiente a la entidad Usuario.
     * @return Instancia de UsuarioDao.
     */
    public UsuarioDao getUsuarioDao() { return usuarioDao; }

    /**
     * Cierra de forma segura la conexión con la base de datos.
     * @throws SQLException Si ocurre un error al cerrar la conexión.
     */
    public void close() throws SQLException {
        try {
            connectionSource.close();
        } catch (Exception e) {
            throw new SQLException("Error al cerrar la conexión a la base de datos", e);
        }
    }
}
