package mx.unison.database.dao;

import com.j256.ormlite.dao.Dao;
import mx.unison.models.Usuario;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Usuario.
 * Proporciona los métodos CRUD utilizando ORMLite.
 */
public class UsuarioDao {
    /** Objeto DAO interno proporcionado por ORMLite. */
    private Dao<Usuario, Integer> dao;

    /**
     * Constructor del DAO.
     * @param dao Implementación del DAO inyectada.
     */
    public UsuarioDao(Dao<Usuario, Integer> dao) {
        this.dao = dao;
    }

    /**
     * Crea un nuevo registro de usuario en la base de datos.
     * @param usuario Objeto usuario a persistir.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public void create(Usuario usuario) throws SQLException {
        dao.create(usuario);
    }

    /**
     * Obtiene un usuario por su identificador único.
     * @param nombre El nombre del usuario.
     * @return El objeto Usuario, o null si no se encuentra.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public Usuario getByNombre(String nombre) throws SQLException {
        List<Usuario> usuarios = dao.queryForEq("nombre", nombre);
        return usuarios.isEmpty() ? null : usuarios.get(0);
    }

    /**
     * Obtiene una lista con todos los usuarios registrados.
     * @return Lista de objetos Usuario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<Usuario> getAll() throws SQLException {
        return dao.queryForAll();
    }

    /**
     * Actualiza los datos de un usuario existente en la base de datos.
     * @param usuario El objeto usuario con los datos actualizados.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void update(Usuario usuario) throws SQLException {
        dao.update(usuario);
    }

    /**
     * Elimina un usuario de la base de datos.
     * @param usuario El objeto usuario a eliminar.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void delete(Usuario usuario) throws SQLException {
        dao.delete(usuario);
    }
}
