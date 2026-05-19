package mx.unison.database.dao;

import com.j256.ormlite.dao.Dao;
import mx.unison.models.Almacen;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Almacen.
 * Proporciona los métodos CRUD utilizando ORMLite.
 */
public class AlmacenDao {
    /**
     * Objeto DAO interno proporcionado por ORMLite.
     */
    private Dao<Almacen, Integer> dao;

    /**
     * Constructor del DAO.
     *
     * @param dao Implementación del DAO inyectada.
     */
    public AlmacenDao(Dao<Almacen, Integer> dao) {
        this.dao = dao;
    }

    /**
     * Crea un nuevo registro de almacén en la base de datos.
     *
     * @param almacen Objeto almacén a persistir.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public void create(Almacen almacen) throws SQLException {
        dao.create(almacen);
    }

    /**
     * Obtiene un almacén por su identificador único.
     * @param id El identificador del almacén.
     * @return El objeto Almacen, o null si no se encuentra.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public Almacen getById(int id) throws SQLException {
        return dao.queryForId(id);
    }

    /**
     * Obtiene una lista con todos los almacenes registrados.
     * @return Lista de objetos Almacen.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<Almacen> getAll() throws SQLException {
        return dao.queryForAll();
    }

    /**
     * Actualiza los datos de un almacén existente en la base de datos.
     * @param almacen El objeto almacén con los datos actualizados.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void update(Almacen almacen) throws SQLException {
        dao.update(almacen);
    }

    /**
     * Elimina un almacén de la base de datos.
     * @param almacen El objeto almacén a eliminar.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void delete(Almacen almacen) throws SQLException {
        dao.delete(almacen);
    }
}
