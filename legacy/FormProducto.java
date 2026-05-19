package mx.unison.views.form;

import mx.unison.database.Database;
import mx.unison.models.Producto;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana de formulario utilizada para agregar o editar productos dentro del sistema de inventario.
 *
 * Esta clase crea un cuadro de diálogo modal que permite al usuario ingresar
 * o modificar la información de un producto, como su nombre, descripción,
 * cantidad disponible y precio.
 *
 * El formulario se comunica con la clase Database para insertar o actualizar
 * los registros correspondientes en la base de datos.
 */
public class FormProducto extends JDialog {

    /**
     * Objeto de acceso a la base de datos utilizado para guardar
     * o modificar la información de los productos.
     */
    private final Database db;

    /**
     * Objeto Producto que se está editando.
     * Si es null significa que se está creando un nuevo producto.
     */
    private final Producto producto;

    /**
     * Acción que se ejecuta después de guardar el producto correctamente.
     * Generalmente se utiliza para recargar los datos en la tabla principal.
     */
    private final Runnable onSaved;

    /**
     * Campo de texto utilizado para ingresar el nombre del producto.
     */
    private JTextField txtNombre;

    /**
     * Área de texto utilizada para ingresar la descripción del producto.
     */
    private JTextArea txtDescripcion;

    /**
     * Campo de texto utilizado para ingresar la cantidad disponible del producto.
     */
    private JTextField txtCantidad;

    /**
     * Campo de texto utilizado para ingresar el precio del producto.
     */
    private JTextField txtPrecio;

    /**
     * Boton para guardar producto.
     */
    private JButton btnGuardar;

    /**
     * Boton para cancelar.
     *
     * Cierra el formulario al darle clic.
     */
    private JButton btnCancelar;

    /**
     * Constructor del formulario de productos.
     *
     * Inicializa la interfaz gráfica del formulario y carga los datos
     * del producto en caso de que se esté realizando una edición.
     *
     * @param owner Ventana propietaria del diálogo.
     * @param db Objeto de acceso a la base de datos.
     * @param producto Objeto Producto que se desea editar o null si se va a crear uno nuevo.
     * @param onSaved Acción que se ejecuta después de guardar el producto.
     */
    public FormProducto(Window owner, Database db, Producto producto, Runnable onSaved) {
        super(owner, producto == null ? "Agregar producto" : "Editar producto", ModalityType.APPLICATION_MODAL);
        this.db = db;
        this.producto = producto;
        this.onSaved = onSaved;

        initComponents();
        if (producto != null) {
            loadProducto();
        }

        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Inicializa los componentes gráficos del formulario.
     *
     * Crea los campos de texto, etiquetas y botones necesarios
     * para capturar la información del producto.
     */
    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));

        txtNombre = new JTextField(20);
        txtDescripcion = new JTextArea(4, 20);
        txtCantidad = new JTextField(20);
        txtPrecio = new JTextField(20);

        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);

        panel.add(new JLabel("Descripción:"));
        panel.add(new JScrollPane(txtDescripcion));

        panel.add(new JLabel("Cantidad:"));
        panel.add(txtCantidad);

        panel.add(new JLabel("Precio:"));
        panel.add(txtPrecio);

        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel botones = new JPanel();
        botones.add(btnGuardar);
        botones.add(btnCancelar);

        setLayout(new BorderLayout(10, 10));
        add(panel, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    /**
     * Carga la información del producto seleccionado en los campos del formulario.
     *
     * Este método se utiliza cuando el usuario desea modificar un producto existente.
     */
    private void loadProducto() {
        txtNombre.setText(producto.nombre);
        txtDescripcion.setText(producto.descripcion);
        txtCantidad.setText(String.valueOf(producto.cantidad));
        txtPrecio.setText(String.valueOf(producto.precio));
    }

    /**
     * Guarda la información del producto en la base de datos.
     *
     * Si el producto ya existe, se actualiza el registro correspondiente.
     * Si no existe, se crea un nuevo registro en la base de datos.
     *
     * También ejecuta la acción onSaved para actualizar la interfaz
     * después de guardar el producto.
     */
    private void guardar() {
        try {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            double precio = Double.parseDouble(txtPrecio.getText().trim());

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
                return;
            }

            if (cantidad < 0 || precio < 0) {
                JOptionPane.showMessageDialog(this,"Cantidad y precio deben ser positivos");
                return;
            }

            Producto p = (producto == null) ? new Producto() : producto;
            p.nombre = nombre;
            p.descripcion = descripcion;
            p.cantidad = cantidad;
            p.precio = precio;
            p.almacenId = 0;

            String usuario = "ADMIN";

            if (p.id > 0) {
                db.updateProducto(p, usuario);
            } else {
                db.insertProducto(p, usuario);
            }

            if (onSaved != null) {
                onSaved.run();
            }

            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad y precio deben ser numéricos");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el producto: " + ex.getMessage());
        }
    }

    /**
     * Devuelve el campo de texto del nombre del producto.
     *
     * @return Campo de nombre.
     */
    public JTextField getTxtNombre() {
        return txtNombre;
    }

    /**
     * Devuelve el área de texto de la descripción del producto.
     *
     * @return Área de descripción.
     */
    public JTextArea getTxtDescripcion() {
        return txtDescripcion;
    }

    /**
     * Devuelve el campo de texto de la cantidad del producto.
     *
     * @return Campo de cantidad.
     */
    public JTextField getTxtCantidad() {
        return txtCantidad;
    }

    /**
     * Devuelve el campo de texto del precio del producto.
     *
     * @return Campo de precio.
     */
    public JTextField getTxtPrecio() {
        return txtPrecio;
    }

    /**
     * Devuelve el botón para guardar el producto.
     *
     * @return Botón Guardar.
     */
    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    /**
     * Devuelve el botón para cancelar y cerrar el formulario.
     *
     * @return Botón Cancelar.
     */
    public JButton getBtnCancelar() {
        return btnCancelar;
    }
}