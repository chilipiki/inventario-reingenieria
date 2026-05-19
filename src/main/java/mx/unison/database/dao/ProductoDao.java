package mx.unison.database.dao;

import com.j256.ormlite.dao.Dao;
import mx.unison.models.Producto;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Producto.
 * Proporciona los métodos CRUD utilizando ORMLite.
 */
public class ProductoDao {
    /** Objeto DAO interno proporcionado por ORMLite. */
    private Dao<Producto, Integer> dao;

    /**
     * Constructor del DAO.
     * @param dao Implementación del DAO inyectada.
     */
    public ProductoDao(Dao<Producto, Integer> dao) {
        this.dao = dao;
    }

    /**
     * Crea un nuevo registro de producto en la base de datos.
     * @param producto Objeto producto a persistir.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public void create(Producto producto) throws SQLException {
        dao.create(producto);
    }

    /**
     * Obtiene un producto por su identificador único.
     * @param id El identificador del producto.
     * @return El objeto Producto, o null si no se encuentra.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public Producto getById(int id) throws SQLException {
        return dao.queryForId(id);
    }

    /**
     * Obtiene una lista con todos los productos registrados.
     * @return Lista de objetos Producto.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<Producto> getAll() throws SQLException {
        return dao.queryForAll();
    }

    /**
     * Actualiza los datos de un producto existente en la base de datos.
     * @param producto El objeto producto con los datos actualizados.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void update(Producto producto) throws SQLException {
        dao.update(producto);
    }

    /**
     * Elimina un producto de la base de datos.
     * @param producto El objeto producto a eliminar.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void delete(Producto producto) throws SQLException {
        dao.delete(producto);
    }

    /**
     * Obtiene una lista de productos asociados a un almacén específico.
     * @param almacenId El identificador del almacén.
     * @return Lista de objetos Producto asociados al almacén.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<Producto> getByAlmacen(int almacenId) throws SQLException {
        return dao.queryForEq("almacen_id", almacenId);
    }
}
