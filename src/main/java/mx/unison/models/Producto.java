package mx.unison.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDateTime;

/**
 * Clase de productos.
 * Contiene los atributos necesarios para guardar productos en la base de datos.
 */
@DatabaseTable(tableName = "productos")
public class Producto {

    /**
     * ID del producto
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * Nombre del producto
     */
    @DatabaseField(canBeNull = false)
    private String nombre;

    /**
     * Descripción del producto
     */
    @DatabaseField
    private String descripcion;

    /**
     * Stock disponible del producto
     */
    @DatabaseField
    private int cantidad;

    /**
     * Precio base del producto
     */
    @DatabaseField
    private double precio;

    /**
     * Almacén al que pertenece el producto
     */
    @DatabaseField(foreign = true, foreignAutoCreate = true)
    private Almacen almacen;

    /**
     * Fecha de creación del producto
     */
    @DatabaseField
    private String fechaCreacion;

    /**
     * Fecha de la última modificación del producto
     *
     * Se actualiza cuando uno o más atributos son modificados
     */
    @DatabaseField
    private String fechaModificacion;

    /**
     * Nombre de usuario del último usuario en modificar el producto
     */
    @DatabaseField
    private String ultimoUsuario;

    /**
     * Constructor por defecto requerido por ORMLite.
     */
    public Producto() {}

    /**
     * Constructor con los campos obligatorios del producto.
     * @param nombre Nombre descriptivo del producto.
     * @param descripcion Descripción detallada del producto.
     * @param cantidad Stock disponible del producto.
     * @param precio Precio base del producto.
     * @param almacen Almacén al que pertenece el producto.
     */
    public Producto(String nombre, String descripcion, int cantidad, double precio, Almacen almacen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.almacen = almacen;
        this.fechaCreacion = String.valueOf(LocalDateTime.now());
    }

    /**
     * Obtiene el ID del producto.
     * @return El ID del producto.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el ID del producto.
     * @param id El ID del producto a establecer.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del producto.
     * @return El nombre del producto.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del producto.
     * @param nombre El nombre del producto a establecer.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción del producto.
     * @return La descripción del producto.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción del producto.
     * @param descripcion La descripción del producto a establecer.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la cantidad disponible del producto.
     * @return La cantidad disponible del producto.
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad disponible del producto.
     * @param cantidad La cantidad disponible del producto a establecer.
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el precio del producto.
     * @return El precio del producto.
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * Establece el precio del producto.
     * @param precio El precio del producto a establecer.
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /**
     * Obtiene el almacén al que pertenece el producto.
     * @return El almacén del producto.
     */
    public Almacen getAlmacen() {
        return almacen;
    }

    /**
     * Establece el almacén al que pertenece el producto.
     * @param almacen El almacén del producto a establecer.
     */
    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    /**
     * Obtiene la fecha de creación del producto.
     * @return La fecha de creación del producto.
     */
    public String getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Establece la fecha de creación del producto.
     * @param fechaCreacion La fecha de creación del producto a establecer.
     */
    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Obtiene la fecha de la última modificación del producto.
     * @return La fecha de la última modificación del producto.
     */
    public String getFechaModificacion() {
        return fechaModificacion;
    }

    /**
     * Establece la fecha de la última modificación del producto.
     * @param fechaModificacion La fecha de la última modificación del producto a establecer.
     */
    public void setFechaModificacion(String fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    /**
     * Obtiene el nombre de usuario del último usuario en modificar el producto.
     * @return El nombre de usuario.
     */
    public String getUltimoUsuario() {
        return ultimoUsuario;
    }

    /**
     * Establece el nombre de usuario del último usuario en modificar el producto.
     * @param ultimoUsuario El nombre de usuario.
     */
    public void setUltimoUsuario(String ultimoUsuario) {
        this.ultimoUsuario = ultimoUsuario;
    }
}
