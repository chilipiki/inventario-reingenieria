package mx.unison.database;

import mx.unison.models.Almacen;
import mx.unison.util.CryptoUtils;
import mx.unison.models.Producto;
import mx.unison.models.Usuario;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para hacer conexión con la base de datos.
 * Contiene un metodo para crear tablas e insertar usuarios automaticamente al ejecutar el programa.
 * Contiene un metodo para autenticar usuarios.
 * Contiene métodos con la lógica para realizar operaciones en las tablas de la base de datos.
 *
 */
public class Database {
    /**
     * Cadena de conexión que indica el tipo de base de datos y la ubicación del archivo Inventario.db
     */
    private static final String URL = "jdbc:sqlite:Inventario.db";

    /**
     * Constructor de la clase Datbase.
     * Inicializa la base de datos y crea las tablas necesarias si no existen.
     */
    public Database() {
        init();
    }

    /**
     * Establece una conexión con la base de datos utilizando la URL definida.
     *
     * @return Objeto de conexión a la base de datos.
     * @throws SQLException Excepción que indica un error con la base de datos.
     */
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Metodo para inicializar tablas en la base de datos.
     * Crea las tablas necesarias para el programa y crea registros en la tabla de usuarios.
     * Establece una fecha de creación para productos y almacenes existentes si el campo esta vacío.
     */
    private void init() {
        try (Connection c = connect(); Statement st = c.createStatement()) {
            // Tabla usuarios
            st.execute("CREATE TABLE IF NOT EXISTS usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT,nombre TEXT NOT NULL, password TEXT NOT NULL, fecha_hora_ultimo_inicio TEXT, rol TEXT NOT NULL)");
            // Tabla almacenes
            st.execute("CREATE TABLE IF NOT EXISTS almacenes (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, ubicacion TEXT, fecha_hora_creacion TEXT, fecha_hora_ultima_modificacion TEXT, ultimo_usuario_en_modificar TEXT)");
            // Tabla productos
            st.execute("CREATE TABLE IF NOT EXISTS productos (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, precio REAL DEFAULT 0.0, cantidad INTEGER NOT NULL, almacen_id INTEGER, descripcion TEXT, fecha_hora_creacion INTEGER DEFAULT 0, fecha_hora_ultima_modificacion TEXT, ultimo_usuario_en_modificar TEXT)");
            // Insertar usuarios base si no existen
            insertDefaultUser("ADMIN", "admin23", "ADMIN");
            insertDefaultUser("PRODUCTOS", "productos19", "PRODUCTOS");
            insertDefaultUser("ALMACENES", "almacenes11", "ALMACENES");
            // Establecer fecha de creación para productos/almacenes existentes si están vacíos
            setDefaultFechaCreacionIfEmpty("productos");
            setDefaultFechaCreacionIfEmpty("almacenes");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recibe como parametros los datos del usuario para hacer un INSERT en la tabla de usuarios.
     *
     * @param nombre Nombre del usuario.
     * @param passPlain Contraseña del usuario sin hash.
     * @param rol Rol del usuario en el sistema.
     * @throws SQLException Excepción que indica un error con la base de datos.
     */
     public void insertDefaultUser(String nombre, String passPlain, String rol) throws SQLException {
        String check = "SELECT nombre FROM usuarios WHERE nombre=?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(check)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                String ins = "INSERT INTO usuarios(nombre, password, rol) VALUES(?, ?, ?)";
                try (PreparedStatement ps2 = c.prepareStatement(ins)) {
                    ps2.setString(1, nombre);
                    ps2.setString(2, CryptoUtils.md5(passPlain));
                    ps2.setString(3, rol);
                    ps2.executeUpdate();
                }
            }
        }
    }

    /**
     * Asigna una fecha de creación a registros existentes en caso de que este vacía.
     *
     * @param table Tabla indicada para establecer la fecha de creación en registros existentes.
     * @throws SQLException Excepción que indica un error con la base de datos.
     */
    private void setDefaultFechaCreacionIfEmpty(String table) throws SQLException {
        String checkSql = String.format("SELECT id, fecha_hora_creacion FROM %s", table);
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(checkSql)) {
            ResultSet rs = ps.executeQuery();
            List<Integer> ids = new ArrayList<>();
            while (rs.next()) {
                String f = rs.getString("fecha_hora_creacion");
                if (f == null || f.isEmpty()) ids.add(rs.getInt("id"));
            }
            String upd = String.format("UPDATE %s SET fecha_hora_creacion=? WHERE id=?", table);
            for (Integer id : ids) {
                try (PreparedStatement pu = c.prepareStatement(upd)) {
                    pu.setString(1, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    pu.setInt(2, id);
                    pu.executeUpdate();
                }
            }
        }
    }

    /**
     * Autenticación de usuarios.
     *
     * @param nombre Nombre de usuario.
     * @param passwordPlain Contraseña del usuario sin hash.
     * @return Regresa el Usuario que coincida con los parámetros.
     */
    public Usuario authenticate(String nombre, String passwordPlain) {
        String sql = "SELECT nombre, rol FROM usuarios WHERE nombre=? AND password=?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, CryptoUtils.md5(passwordPlain));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.nombre = rs.getString("nombre");
                u.rol = rs.getString("rol");

                String upd = "UPDATE usuarios SET fecha_hora_ultimo_inicio=? WHERE nombre=?";
                try (PreparedStatement pu = c.prepareStatement(upd)) {
                    pu.setString(1, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    pu.setString(2, nombre);
                    pu.executeUpdate();
                }
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todos los almacenes.
     *
     * @return Regresa una lista de almacenes.
     */
    public List<Almacen> listAlmacenes() {
        List<Almacen> out = new ArrayList<>();
        String sql = "SELECT id, nombre, ubicacion, fecha_hora_creacion, fecha_hora_ultima_modificacion, ultimo_usuario_en_modificar FROM almacenes";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Almacen a = new Almacen();
                a.id = rs.getInt("id");
                a.nombre = rs.getString("nombre");
                a.ubicacion = rs.getString("ubicacion");
                a.fechaHoraCreacion = rs.getString("fecha_hora_creacion");
                a.fechaHoraUltimaMod = rs.getString("fecha_hora_ultima_modificacion");
                a.ultimoUsuario = rs.getString("ultimo_usuario_en_modificar");
                out.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Inserta un nuevo almacén en la tabla almacenes.
     *
     * @param nombre Nombre del almacén.
     * @param ubicacion Ubicación del almacén.
     * @param usuario El usuario que agregó el almacén.
     * @return Regresa el ID del almacén creado.
     */
    public int insertAlmacen(String nombre, String ubicacion, String usuario) {
        String sql = "INSERT INTO almacenes(nombre, ubicacion, fecha_hora_creacion, ultimo_usuario_en_modificar) VALUES(?,?,?,?)";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, ubicacion);
            ps.setString(3, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            ps.setString(4, usuario);
            ps.executeUpdate();
            ResultSet g = ps.getGeneratedKeys();
            if (g.next()) return g.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Modifica el almacén que coincide con los parámetros.
     *
     * @param id ID del almacén.
     * @param nombre Nombre del almacén.
     * @param ubicacion Ubicación del almacén.
     * @param usuario Usuario que modifica.
     */
    public void updateAlmacen(int id, String nombre, String ubicacion, String usuario) {
        String sql = "UPDATE almacenes SET nombre=?, ubicacion=?, fecha_hora_ultima_modificacion=?, ultimo_usuario_en_modificar=? WHERE id=?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, ubicacion);
            ps.setString(3, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            ps.setString(4, usuario);
            ps.setInt(5, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina un almacén utilizando su ID.
     *
     * @param id ID del almacén.
     */
    public void deleteAlmacen(int id) {
        String sql = "DELETE FROM almacenes WHERE id=?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lista todos los productos.
     *
     * @return Devuelve una lista con todos los productos.
     */
    public List<Producto> listProductos() {
        List<Producto> out = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.descripcion, p.cantidad, p.precio, p.almacen_id, a.nombre as almacen_nombre, p.fecha_hora_creacion, p.fecha_hora_ultima_modificacion, p.ultimo_usuario_en_modificar FROM productos p LEFT JOIN almacenes a ON p.almacen_id = a.id";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Producto p = new Producto();
                p.id = rs.getInt("id");
                p.nombre = rs.getString("nombre");
                p.descripcion = rs.getString("descripcion");
                p.cantidad = rs.getInt("cantidad");
                p.precio = rs.getDouble("precio");
                p.almacenId = rs.getInt("almacen_id");
                p.almacenNombre = rs.getString("almacen_nombre");
                p.fechaCreacion = rs.getString("fecha_hora_creacion");
                p.fechaModificacion = rs.getString("fecha_hora_ultima_modificacion");
                p.ultimoUsuario = rs.getString("ultimo_usuario_en_modificar");
                out.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Inserta un nuevo producto en la tabla productos.
     *
     * @param prod Objeto Producto que se va a agregar.
     * @param usuario Usuario que agrega el producto.
     * @return Devuelve el ID del producto creado.
     */
    public int insertProducto(Producto prod, String usuario) {
        String sql = "INSERT INTO productos(nombre, descripcion, cantidad, precio, almacen_id, fecha_hora_creacion, ultimo_usuario_en_modificar) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, prod.nombre);
            ps.setString(2, prod.descripcion);
            ps.setInt(3, prod.cantidad);
            ps.setDouble(4, prod.precio);
            if (prod.almacenId > 0) ps.setInt(5, prod.almacenId);
            else ps.setNull(5, Types.INTEGER);
            ps.setString(6, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            ps.setString(7, usuario);
            ps.executeUpdate();
            ResultSet g = ps.getGeneratedKeys();
            if (g.next()) return g.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Modifica un producto existente.
     *
     * @param prod Producto que se quiere modificar.
     * @param usuario Usuario que hace la modificación.
     */
    public void updateProducto(Producto prod, String usuario) {
        String sql = "UPDATE productos SET nombre=?, descripcion=?, cantidad=?, precio=?, almacen_id=?, fecha_hora_ultima_modificacion=?, ultimo_usuario_en_modificar=? WHERE id=?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, prod.nombre);
            ps.setString(2, prod.descripcion);
            ps.setInt(3, prod.cantidad);
            ps.setDouble(4, prod.precio);
            if (prod.almacenId > 0) ps.setInt(5, prod.almacenId);
            else ps.setNull(5, Types.INTEGER);
            ps.setString(6, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            ps.setString(7, usuario);
            ps.setInt(8, prod.id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina un producto utilizando su ID.
     *
     * @param id ID del producto.
     */
    public void deleteProducto(int id) {
        String sql = "DELETE FROM productos WHERE id=?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


