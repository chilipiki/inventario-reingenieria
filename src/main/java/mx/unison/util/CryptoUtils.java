package mx.unison.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Clase para operaciones criptográficas.
 *
 * Actualmente contiene métodos para generar y verificar hashes BCrypt
 * de contraseñas.
 */
public class CryptoUtils {

    /**
     * Constructor privado para evitar instanciación.
     * Esta es una clase de utilidades estáticas.
     */
    private CryptoUtils() {
        throw new IllegalStateException("Clase de utilidades (Utility class)");
    }

    /**
     * Genera un hash BCrypt de una contraseña.
     *
     * @param password Contraseña en texto plano
     * @return Hash BCrypt de la contraseña
     */
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    /**
     * Verifica una contraseña contra su hash BCrypt.
     *
     * @param password Contraseña en texto plano
     * @param hash Hash BCrypt almacenado
     * @return true si la contraseña coincide con el hash, false en caso contrario
     */
    public static boolean verifyPassword(String password, String hash) {
        BCrypt.Result result = BCrypt.verifyer().verify(
                password.toCharArray(),
                hash.toCharArray()
        );
        return result.verified;
    }
}