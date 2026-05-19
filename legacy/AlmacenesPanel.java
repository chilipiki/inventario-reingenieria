package mx.unison.views.panel;

import mx.unison.database.Database;
import mx.unison.models.Almacen;
import mx.unison.views.form.FormAlmacen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel gráfico para la gestión de almacenes dentro del sistema de inventario.
 *
 * Esta clase muestra una tabla con los almacenes registrados en la base de datos
 * y permite realizar operaciones básicas como agregar, modificar y eliminar almacenes.
 *
 * Utiliza componentes de Swing para construir la interfaz gráfica y se comunica
 * con la clase Database para realizar las operaciones en la base de datos.
 */
public class AlmacenesPanel extends JPanel {

    /**
     * Objeto de acceso a la base de datos que permite realizar operaciones
     * sobre la tabla de almacenes.
     */
    private final Database db;

    /**
     * Acción que permite regresar a la pantalla anterior del sistema.
     */
    private final Runnable onGoBack;

    /**
     * Tabla utilizada para mostrar los almacenes registrados en el sistema.
     */
    private JTable table;

    /**
     * Modelo de datos utilizado por la tabla para almacenar y mostrar la información.
     */
    private DefaultTableModel model;

    /**
     * Botón que abre el formulario para agregar un nuevo almacén.
     */
    private JButton btnAgregar;

    /**
     * Botón que abre el formulario para modificar el almacén seleccionado.
     */
    private JButton btnModificar;

    /**
     * Botón que elimina el almacén seleccionado.
     */
    private JButton btnEliminar;

    /**
     * Botón que regresa a la pantalla anterior.
     */
    private JButton btnRegresar;

    /**
     * Constructor del panel de almacenes.
     * Inicializa la interfaz gráfica, crea la tabla y carga los datos desde la base de datos.
     *
     * @param db Objeto de conexión con la base de datos.
     * @param onGoBack Acción que se ejecuta cuando el usuario presiona el botón de regresar.
     */
    public AlmacenesPanel(Database db, Runnable onGoBack) {
        this.db = db;
        this.onGoBack = onGoBack;
        setLayout(new BorderLayout());
        initTop();
        initTable();
        loadData();
    }

    /**
     * Inicializa la barra superior del panel con los botones de acción.
     *
     * Los botones disponibles permiten:
     * - Regresar a la pantalla anterior
     * - Agregar un nuevo almacén
     * - Modificar un almacén existente
     * - Eliminar un almacén seleccionado
     */
    private void initTop() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnRegresar = new JButton("Regresar");
        btnRegresar.addActionListener(e -> onGoBack.run());

        btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> openForm(null));

        btnModificar = new JButton("Modificar");
        btnModificar.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                int id = (int) model.getValueAt(r, 0);
                Almacen a = new Almacen();
                a.id = id;
                openForm(a);
            }
        });

        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                int id = (int) model.getValueAt(r, 0);
                if (confirmarEliminacion()) {
                    db.deleteAlmacen(id);
                    loadData();
                }
            }
        });

        top.add(btnRegresar);
        top.add(btnAgregar);
        top.add(btnModificar);
        top.add(btnEliminar);

        add(top, BorderLayout.NORTH);
    }

    /**
     * Inicializa la tabla utilizada para mostrar la información de los almacenes.
     *
     * Define las columnas que se mostrarán en la tabla y evita que las celdas
     * puedan ser editadas directamente por el usuario.
     */
    private void initTable() {
        model = new DefaultTableModel(new Object[]{"ID", "Nombre", "Ubicación", "Creado", "Últ.Mod", "Últ.Usuario"}, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    /**
     * Carga los datos de los almacenes desde la base de datos y los muestra en la tabla.
     *
     * Este método limpia la tabla actual y vuelve a llenarla con los registros
     * obtenidos desde la base de datos.
     */
    private void loadData() {
        model.setRowCount(0);
        List<Almacen> list = db.listAlmacenes();

        for (Almacen a : list) {
            model.addRow(new Object[]{
                    a.id,
                    a.nombre,
                    a.ubicacion,
                    a.fechaHoraCreacion,
                    a.fechaHoraUltimaMod,
                    a.ultimoUsuario
            });
        }
    }

    /**
     * Abre el formulario para agregar o modificar un almacén.
     *
     * Si el parámetro es null, se interpreta como la creación de un nuevo almacén.
     * Si contiene un objeto Almacen, se utilizará para editar el registro existente.
     *
     * @param a Objeto Almacen que se desea editar o null si se va a crear uno nuevo.
     */
    /**
     * Abre el formulario de almacén para alta o edición.
     *
     * Se deja protegido para permitir pruebas simples sin abrir diálogos reales.
     *
     * @param a Almacén a editar o null para crear uno nuevo.
     */
    protected void openForm(Almacen a) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        FormAlmacen form = new FormAlmacen(owner,db,a,this::loadData);
        form.setVisible(true);
    }

    /**
     * Solicita confirmación antes de eliminar un almacén.
     *
     * Se deja protegido para facilitar pruebas unitarias.
     *
     * @return true si el usuario confirma la eliminación, false en caso contrario.
     */
    protected boolean confirmarEliminacion() {
        int opt = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar el almacén?", "Confirmar", JOptionPane.YES_NO_OPTION);
        return opt == JOptionPane.YES_OPTION;
    }

    /**
     * Devuelve la tabla de almacenes del panel.
     *
     * @return Tabla de almacenes.
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Devuelve el botón para agregar almacenes.
     *
     * @return Botón Agregar.
     */
    public JButton getBtnAgregar() {
        return btnAgregar;
    }

    /**
     * Devuelve el botón para modificar almacenes.
     *
     * @return Botón Modificar.
     */
    public JButton getBtnModificar() {
        return btnModificar;
    }

    /**
     * Devuelve el botón para eliminar almacenes.
     *
     * @return Botón Eliminar.
     */
    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    /**
     * Devuelve el botón para regresar a la pantalla anterior.
     *
     * @return Botón Regresar.
     */
    public JButton getBtnRegresar() {
        return btnRegresar;
    }
}