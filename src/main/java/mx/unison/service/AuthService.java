package mx.unison.service;

import mx.unison.database.dao.UsuarioDao;
import mx.unison.models.Usuario;
import mx.unison.util.CryptoUtils;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Servicio de autenticación del sistema.
 *
 * Encapsula la lógica de autenticación de usuarios,
 * verificación de contraseñas y actualización de registros de login.
 */
public class AuthService {
    /** DAO encargado de gestionar los usuarios para la autenticación. */
    private UsuarioDao usuarioDao;

    /**
     * Constructor del servicio de autenticación.
     * @param usuarioDao El DAO inyectado para realizar consultas de usuarios.
     */
    public AuthService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    /**
     * Autentica un usuario con su nombre y contraseña.
     *
     * @param nombre Nombre del usuario
     * @param password Contraseña en texto plano
     * @return Usuario autenticado, o null si la autenticación falla
     * @throws SQLException Si hay error en la base de datos
     */
    public Usuario authenticate(String nombre, String password) throws SQLException {
        if (nombre == null || nombre.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("El usuario y la contraseña no pueden estar vacíos");
        }

        Usuario usuario = usuarioDao.getByNombre(nombre);

        if (usuario == null) {
            return null;
        }

        // Verificar contraseña usando BCrypt
        if (!CryptoUtils.verifyPassword(password, usuario.getPassword())) {
            return null;
        }

        // Actualizar fecha de último login
        usuario.setFechaHoraUltimoInicio(String.valueOf(LocalDateTime.now()));
        usuarioDao.update(usuario);

        return usuario;
    }

    /**
     * Obtiene un usuario por su nombre sin autenticación.
     *
     * @param nombre Nombre del usuario
     * @return Usuario encontrado, o null si no existe
     * @throws SQLException Si hay error en la base de datos
     */
    public Usuario getUserByNombre(String nombre) throws SQLException {
        return usuarioDao.getByNombre(nombre);
    }
}