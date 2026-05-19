package mx.unison.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDateTime;

/**
 * Clase de almacenes.
 * Contiene los atributos necesarios para guardar almacenes en la base de datos.
 */
@DatabaseTable(tableName = "almacenes")
public class Almacen {

    /**
     * ID del almacén
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * Nombre del almacén
     */
    @DatabaseField(canBeNull = false)
    private String nombre;

    /**
     * Ubicación del almacén
     */
    @DatabaseField
    private String ubicacion;

    /**
     * Fecha y hora en la que fue creado el almacén
     */
    @DatabaseField
    private String fechaHoraCreacion;

    /**
     * Fecha y hora en la que se modificó por última vez algún atributo del almacén
     */
    @DatabaseField
    private String fechaHoraUltimaMod;

    /**
     * Nombre de usuario que modificó el almacén la última vez
     */
    @DatabaseField
    private String ultimoUsuario;

    /**
     * Constructor por defecto requerido por ORMLite.
     */
    public Almacen() {}

    /**
     * Constructor con los campos obligatorios del almacén.
     * @param nombre Nombre descriptivo del almacén.
     * @param ubicacion Dirección o ubicación física del almacén.
     */
    public Almacen (String nombre, String ubicacion) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.fechaHoraCreacion = String.valueOf(LocalDateTime.now());
    }

    /**
     * Obtiene el identificador único del almacén.
     * @return El ID del almacén.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el identificador único del almacén.
     * @param id El nuevo ID a asignar.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del almacén.
     * @return El nombre actual.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del almacén.
     * @param nombre El nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la ubicación física del almacén.
     * @return La ubicación.
     */
    public String getUbicacion() {
        return ubicacion;
    }

    /**
     * Establece la ubicación física del almacén.
     * @param ubicacion La nueva ubicación.
     */
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    /**
     * Obtiene la fecha y hora en que se registró el almacén.
     * @return Cadena con la fecha y hora de creación.
     */
    public String getFechaHoraCreacion() {
        return fechaHoraCreacion;
    }

    /**
     * Establece la fecha y hora de creación del almacén.
     * @param fechaHoraCreacion La fecha y hora a registrar.
     */
    public void setFechaHoraCreacion(String fechaHoraCreacion) {
        this.fechaHoraCreacion = fechaHoraCreacion;
    }

    /**
     * Obtiene la fecha y hora de la última modificación del registro.
     * @return Cadena con la fecha y hora de modificación.
     */
    public String getFechaHoraUltimaMod() {
        return fechaHoraUltimaMod;
    }

    /**
     * Establece la fecha y hora de la última modificación.
     * @param fechaHoraUltimaMod La nueva fecha y hora.
     */
    public void setFechaHoraUltimaMod(String fechaHoraUltimaMod) {
        this.fechaHoraUltimaMod = fechaHoraUltimaMod;
    }

    /**
     * Obtiene el nombre del último usuario que modificó este registro.
     * @return El nombre del usuario.
     */
    public String getUltimoUsuario() {
        return ultimoUsuario;
    }

    /**
     * Establece el usuario que realizó la última modificación.
     * @param ultimoUsuario Nombre o identificador del usuario.
     */
    public void setUltimoUsuario(String ultimoUsuario) {
        this.ultimoUsuario = ultimoUsuario;
    }
}
