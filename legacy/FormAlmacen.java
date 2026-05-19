package mx.unison.views.form;

import mx.unison.database.Database;
import mx.unison.models.Almacen;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana de formulario utilizada para agregar o editar almacenes en el sistema.
 *
 * Esta clase crea un cuadro de diálogo modal que permite al usuario ingresar
 * o modificar los datos de un almacén, como su nombre y ubicación.
 *
 * El formulario se comunica con la clase Database para insertar o actualizar
 * registros en la base de datos.
 */
public class FormAlmacen extends JDialog {

    /**
     * Objeto de acceso a la base de datos utilizado para guardar
     * o modificar información de almacenes.
     */
    private Database db;

    /**
     * Objeto Almacen que se está editando.
     * Si es null significa que se está creando un nuevo almacén.
     */
    private Almacen almacen;

    /**
     * Acción que se ejecuta después de guardar el almacén correctamente.
     * Generalmente se utiliza para recargar los datos en la tabla principal.
     */
    private Runnable onSaved;

    /**
     * Campo de texto utilizado para ingresar el nombre del almacén.
     */
    private JTextField txtNombre;

    /**
     * Área de texto utilizada para ingresar la ubicación del almacén.
     */
    private JTextArea txtUbicacion;

    /**
     * Boton para guardar almacen.
     */
    private JButton btnGuardar;

    /**
     * Boton para cancelar.
     *
     * Cierra el formulario al darle clic.
     */
    private JButton btnCancelar;

    /**
     * Constructor del formulario de almacenes.
     *
     * Inicializa la interfaz gráfica y carga los datos del almacén
     * si se está realizando una edición.
     *
     * @param owner Ventana propietaria del diálogo.
     * @param db Objeto de acceso a la base de datos.
     * @param almacen Objeto Almacen que se desea editar o null si se va a crear uno nuevo.
     * @param onSaved Acción que se ejecuta después de guardar el almacén.
     */
    public FormAlmacen(Window owner, Database db, Almacen almacen, Runnable onSaved) {
        super(owner, almacen == null ? "Agregar almacen" : "Editar almacen", ModalityType.APPLICATION_MODAL);
        this.db = db;
        this.almacen = almacen;
        this.onSaved = onSaved;

        initComponents();

        if (almacen != null) {
            loadAlmacen();
        }

        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Inicializa los componentes gráficos del formulario.
     *
     * Crea los campos de texto, etiquetas y botones necesarios
     * para capturar la información del almacén.
     */
    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));

        txtNombre = new JTextField(20);
        txtUbicacion = new JTextArea(4,20);

        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);

        panel.add(new JLabel("Ubicacion:"));
        panel.add(new JScrollPane(txtUbicacion));

        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel botones = new JPanel();
        botones.add(btnGuardar);
        botones.add(btnCancelar);

        setLayout(new BorderLayout(10,10));
        add(panel, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    /**
     * Carga los datos del almacén seleccionado en los campos del formulario.
     *
     * Se utiliza cuando el usuario desea modificar un almacén existente.
     */
    private void loadAlmacen() {
        txtNombre.setText(almacen.nombre);
        txtUbicacion.setText(almacen.ubicacion);
    }

    /**
     * Guarda la información del almacén en la base de datos.
     *
     * Si el almacén ya existe, se actualiza el registro correspondiente.
     * Si no existe, se crea un nuevo registro en la base de datos.
     *
     * También ejecuta la acción onSaved para actualizar la interfaz
     * después de guardar el registro.
     */
    private void guardar() {
        try {
            String nombre = txtNombre.getText().trim();
            String ubicacion = txtUbicacion.getText().trim();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
                return;
            }

            Almacen a = (almacen == null) ? new Almacen() : almacen;
            a.nombre = nombre;
            a.ubicacion = ubicacion;

            String usuario = "ADMIN";

            if (a.id > 0) {
                db.updateAlmacen(a.id,nombre,ubicacion,usuario);
            } else {
                db.insertAlmacen(nombre,ubicacion,usuario);
            }

            if (onSaved != null) {
                onSaved.run();
            }

            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el almacen" + e.getMessage());
        }
    }

    /**
     * Devuelve el campo de texto del nombre del almacén.
     *
     * @return Campo de nombre.
     */
    public JTextField getTxtNombre() {
        return txtNombre;
    }

    /**
     * Devuelve el área de texto de la ubicación del almacén.
     *
     * @return Área de ubicación.
     */
    public JTextArea getTxtUbicacion() {
        return txtUbicacion;
    }

    /**
     * Devuelve el botón para guardar el almacén.
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