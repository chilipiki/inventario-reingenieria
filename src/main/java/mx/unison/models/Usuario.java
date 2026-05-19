package mx.unison.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDateTime;

/**
 * Clase que representa a un usuario del sistema.
 *
 * Esta clase almacena la información básica de un usuario
 * autenticado dentro del sistema de inventario, incluyendo
 * su nombre y el rol que posee dentro de la aplicación.
 */
@DatabaseTable(tableName = "usuarios")
public class Usuario {

    /**
     * ID del usuario, generado automáticamente por la base de datos.
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * Nombre del usuario.
     */
    @DatabaseField(canBeNull = false, unique = true)
    private String nombre;

    /**
     * Contraseña del usuario.
     */
    @DatabaseField(canBeNull = false)
    private String password;

    /**
     * Fecha y hora del último inicio de sesión del usuario.
     */
    @DatabaseField
    private String fechaHoraUltimoInicio;

    /**
     * Rol del usuario dentro del sistema.
     * Puede utilizarse para definir permisos o niveles de acceso.
     */
    @DatabaseField(canBeNull = false)
    private String rol;

    /**
     * Constructor vacío requerido por ORMLite.
     */
    public Usuario() {}

    /**
     * Constructor para crear un nuevo usuario con los datos proporcionados.
     *
     * @param nombre   El nombre del usuario.
     * @param password La contraseña del usuario.
     * @param rol      El rol del usuario dentro del sistema.
     */
    public Usuario(String nombre, String password, String rol) {
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
        this.fechaHoraUltimoInicio = String.valueOf(LocalDateTime.now());
    }

    /**
     * Obtiene el ID del usuario.
     * @return El ID del usuario.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el ID del usuario.
     * @param id El ID a establecer.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del usuario.
     * @return El nombre del usuario.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     * @param nombre El nombre a establecer.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la contraseña del usuario.
     * @return La contraseña del usuario.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña del usuario.
     * @param password La contraseña a establecer.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obtiene la fecha y hora del último inicio de sesión del usuario.
     * @return La fecha y hora del último inicio de sesión.
     */
    public String getFechaHoraUltimoInicio() {
        return fechaHoraUltimoInicio;
    }

    /**
     * Establece la fecha y hora del último inicio de sesión del usuario.
     * @param fechaHoraUltimoInicio La fecha y hora a establecer.
     */
    public void setFechaHoraUltimoInicio(String fechaHoraUltimoInicio) {
        this.fechaHoraUltimoInicio = fechaHoraUltimoInicio;
    }

    /**
     * Obtiene el rol del usuario dentro del sistema.
     * @return El rol del usuario.
     */
    public String getRol() {
        return rol;
    }

    /**
     * Establece el rol del usuario dentro del sistema.
     * @param rol El rol a establecer.
     */
    public void setRol(String rol) {
        this.rol = rol;
    }
}
