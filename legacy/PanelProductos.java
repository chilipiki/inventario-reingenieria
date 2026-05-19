package mx.unison.views.panel;

import mx.unison.database.Database;
import mx.unison.models.Producto;
import mx.unison.views.form.FormProducto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel que permite visualizar y gestionar los productos del sistema de inventario.
 *
 * Esta clase muestra una tabla con todos los productos registrados en la base
 * de datos y proporciona opciones para agregar, modificar o eliminar productos.
 *
 * También permite regresar al panel anterior mediante un botón de navegación.
 */
public class PanelProductos extends JPanel{

    /**
     * Objeto de acceso a la base de datos utilizado para realizar
     * operaciones de consulta, inserción, actualización y eliminación
     * de productos.
     */
    private final Database db;

    /**
     * Acción que se ejecuta cuando el usuario presiona el botón "Regresar".
     * Generalmente se utiliza para volver al menú principal.
     */
    private final Runnable onGoBack;

    /**
     * Tabla utilizada para mostrar la lista de productos.
     */
    private JTable table;

    /**
     * Modelo de datos de la tabla que contiene la información
     * de los productos.
     */
    private DefaultTableModel model;

    /**
     * Botón que abre el formulario para agregar un nuevo producto.
     */
    private JButton btnAgregar;

    /**
     * Botón que abre el formulario para modificar el producto seleccionado.
     */
    private JButton btnModificar;

    /**
     * Botón que elimina el producto seleccionado de la tabla.
     */
    private JButton btnEliminar;

    /**
     * Botón que regresa al panel anterior.
     */
    private JButton btnRegresar;

    /**
     * Constructor del panel de productos.
     *
     * Inicializa los componentes del panel, incluyendo la barra de
     * acciones superior y la tabla de productos. Posteriormente carga
     * los datos desde la base de datos.
     *
     * @param db Objeto de acceso a la base de datos.
     * @param onGoBack Acción que se ejecuta cuando el usuario desea regresar.
     */
    public PanelProductos(Database db, Runnable onGoBack) {
        this.db = db;
        this.onGoBack = onGoBack;
        setLayout(new BorderLayout());
        initTop();
        initTable();
        loadData();
    }

    /**
     * Inicializa la barra superior del panel.
     *
     * Contiene los botones para regresar, agregar, modificar y eliminar
     * productos.
     */
    private void initTop() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> onGoBack.run());

        btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> onAgregar());

        btnModificar = new JButton("Modificar");
        btnModificar.addActionListener(e -> onModificar());

        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> onEliminar());

        top.add(btnRegresar);
        top.add(btnAgregar);
        top.add(btnModificar);
        top.add(btnEliminar);

        add(top, BorderLayout.NORTH);
    }

    /**
     * Inicializa la tabla donde se mostrarán los productos.
     *
     * Define las columnas del modelo de tabla y configura la tabla
     * para que las celdas no sean editables directamente.
     */
    private void initTable() {

        model = new DefaultTableModel(
                new Object[]{"ID","Nombre","Descripción","Cantidad","Precio","Almacén","Creado","Últ.Mod","Últ.Usuario"}, 0) {

            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    // --- Acciones ---

    protected void onAgregar() {openForm(null);}

    protected void onModificar() {
        int r = table.getSelectedRow();
        if (r >= 0) {
            int id = (int) model.getValueAt(r, 0);
            Producto p = new Producto();
            p.id = id; // simplificado, en el formulario se volverán a cargar los datos
            openForm(p);
        }
    }

    protected void onEliminar() {
        int r = table.getSelectedRow();
        if (r >= 0) {
            int id = (int) model.getValueAt(r, 0);

            if (confirmarEliminacion()) {
                db.deleteProducto(id);
                loadData();
            }
        }
    }

    /**
     * Carga los productos desde la base de datos y los muestra
     * en la tabla del panel.
     */
    private void loadData() {

        model.setRowCount(0);

        List<Producto> productos = db.listProductos();

        for (Producto p : productos) {

            model.addRow(new Object[]{
                    p.id,
                    p.nombre,
                    p.descripcion,
                    p.cantidad,
                    p.precio,
                    p.almacenNombre,
                    p.fechaCreacion,
                    p.fechaModificacion,
                    p.ultimoUsuario
            });
        }
    }

    /**
     * Abre el formulario para agregar o modificar un producto.
     *
     * @param p Producto que se desea editar. Si es null se abrirá
     * un formulario para crear un nuevo producto.
     */
    /**
     * Abre el formulario de producto.
     *
     * Se deja protegido para permitir pruebas simples sin abrir diálogos reales.
     *
     * @param p Producto a editar o null para crear uno nuevo.
     */
    protected void openForm(Producto p) {

        Window owner = SwingUtilities.getWindowAncestor(this);

        FormProducto form = new FormProducto(owner, db, p, this::loadData);

        form.setVisible(true);
    }

    /**
     * Solicita confirmación antes de eliminar un producto.
     *
     * Se deja protegido para facilitar pruebas unitarias.
     *
     * @return true si el usuario confirma la eliminación, false en caso contrario.
     */
    protected boolean confirmarEliminacion() {
        int opt = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea eliminar el producto?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);
        return opt == JOptionPane.YES_OPTION;
    }

    /**
     * Devuelve la tabla de productos del panel.
     *
     * @return Tabla de productos.
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Devuelve el botón para agregar productos.
     *
     * @return Botón Agregar.
     */
    public JButton getBtnAgregar() {
        return btnAgregar;
    }

    /**
     * Devuelve el botón para modificar productos.
     *
     * @return Botón Modificar.
     */
    public JButton getBtnModificar() {
        return btnModificar;
    }

    /**
     * Devuelve el botón para eliminar productos.
     *
     * @return Botón Eliminar.
     */
    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    /**
     * Devuelve el botón para regresar al panel anterior.
     *
     * @return Botón Regresar.
     */
    public JButton getBtnRegresar() {
        return btnRegresar;
    }
}