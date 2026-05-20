package mx.unison.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;
import mx.unison.database.DatabaseManager;
import mx.unison.models.Almacen;
import mx.unison.models.Producto;
import mx.unison.util.UIUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para la vista de gestión de productos.
 *
 * Maneja la visualización, búsqueda, creación, edición y eliminación
 * de productos en el sistema.
 */
public class ProductosViewController {

    /** Filtro por nombre del producto. */
    @FXML private TextField filtroNombre;

    /** Filtro por descripción del producto. */
    @FXML private TextField filtroDescripcion;

    /** Filtro por nombre del almacén. */
    @FXML private TextField filtroAlmacen;

    /** Filtro de cantidad mínima. */
    @FXML private TextField filtroCantidadMin;

    /** Filtro de cantidad máxima. */
    @FXML private TextField filtroCantidadMax;

    /** Filtro de precio mínimo. */
    @FXML private TextField filtroPrecioMin;

    /** Filtro de precio máximo. */
    @FXML private TextField filtroPrecioMax;

    /** Filtro de fecha de creación desde. */
    @FXML private DatePicker filtroFechaDesde;

    /** Filtro de fecha de creación hasta. */
    @FXML private DatePicker filtroFechaHasta;

    /** Tabla principal que muestra el inventario de productos. */
    @FXML
    private TableView<Producto> productosTable;

    /** Columna de la tabla correspondiente al ID único del producto. */
    @FXML
    private TableColumn<Producto, Integer> idColumn;

    /** Columna de la tabla correspondiente al nombre del producto. */
    @FXML
    private TableColumn<Producto, String> nombreColumn;

    /** Columna de la tabla correspondiente a la descripción del producto. */
    @FXML
    private TableColumn<Producto, String> descripcionColumn;

    /** Columna de la tabla correspondiente a la cantidad de unidades en stock. */
    @FXML
    private TableColumn<Producto, Integer> cantidadColumn;

    /** Columna de la tabla correspondiente al precio unitario del producto. */
    @FXML
    private TableColumn<Producto, Double> precioColumn;

    /** Columna de la tabla correspondiente al nombre del almacén donde está ubicado el producto. */
    @FXML
    private TableColumn<Producto, String> almacenColumn;

    /** Columna de la tabla correspondiente a la fecha de creación del producto. */
    @FXML
    private TableColumn<Producto, String> fechaCreacionColumn;

    /** Columna de la tabla correspondiente a la fecha de última modificación del producto. */
    @FXML
    private TableColumn<Producto, String> fechaModificacionColumn;

    /** Columna de la tabla correspondiente al último usuario que modificó el producto. */
    @FXML
    private TableColumn<Producto, String> ultimoUsuarioColumn;

    /** Botón para abrir el formulario y registrar un nuevo producto. */
    @FXML
    private Button agregarBtn;

    /** Botón para abrir el formulario de edición con el producto seleccionado en la tabla. */
    @FXML
    private Button editarBtn;

    /** Botón para eliminar permanentemente el producto seleccionado en la tabla. */
    @FXML
    private Button eliminarBtn;

    /** Botón para recargar los datos de la base de datos manualmente. */
    @FXML
    private Button actualizarBtn;

    /** Controlador de nivel superior encargado de la navegación global. */
    private MainController mainController;

    /** Gestor de la base de datos para realizar las operaciones CRUD de los productos. */
    private DatabaseManager dbManager;

    /** Nombre del usuario activo actual. */
    private String usuarioActual;

    /** Rol del usuario actual. */
    private String userRole;

    /** Lista observable enlazada a la tabla para actualización automática de la vista. */
    private ObservableList<Producto> productosObservable;

    /**
     * Constructor del controlador de la vista de productos.
     *
     * @param mainController Controlador principal de la aplicación.
     * @param dbManager Gestor de conexión a la base de datos.
     * @param usuarioActual Nombre del usuario que ha iniciado sesión.
     * @param userRole Rol del usuario actual.
     */
    public ProductosViewController(MainController mainController,
                                   DatabaseManager dbManager, String usuarioActual, String userRole) {
        this.mainController = mainController;
        this.dbManager = dbManager;
        this.usuarioActual = usuarioActual;
        this.userRole = userRole;
    }

    /**
     * Método invocado automáticamente por JavaFX tras cargar el archivo FXML.
     * Configura el mapeo de datos en las columnas, los anchos visuales,
     * el evento de doble clic para editar y el sistema de filtrado.
     */
    @FXML
    public void initialize() {
        // Configurar columnas existentes
        idColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nombreColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        descripcionColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescripcion()));
        cantidadColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject());
        precioColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrecio()).asObject());
        almacenColumn.setCellValueFactory(cellData -> {
            Almacen almacen = cellData.getValue().getAlmacen();
            String almacenNombre = almacen != null ? almacen.getNombre() : "Sin almacén";
            return new javafx.beans.property.SimpleStringProperty(almacenNombre);
        });

        // Configurar columnas nuevas
        fechaCreacionColumn.setCellValueFactory(cellData -> {
            String fecha = cellData.getValue().getFechaCreacion();
            if (fecha == null || fecha.isEmpty()) return new javafx.beans.property.SimpleStringProperty("");
            return new javafx.beans.property.SimpleStringProperty(fecha.substring(0, 16).replace("T", " "));
        });
        fechaModificacionColumn.setCellValueFactory(cellData -> {
            String fecha = cellData.getValue().getFechaModificacion();
            if (fecha == null || fecha.isEmpty()) return new javafx.beans.property.SimpleStringProperty("");
            return new javafx.beans.property.SimpleStringProperty(fecha.substring(0, 16).replace("T", " "));
        });
        ultimoUsuarioColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUltimoUsuario()));

        // Cargar productos
        cargarProductos();

        // Conectar filtros en tiempo real
        filtroNombre.textProperty().addListener((obs, o, n) -> aplicarFiltros());
        filtroDescripcion.textProperty().addListener((obs, o, n) -> aplicarFiltros());
        filtroAlmacen.textProperty().addListener((obs, o, n) -> aplicarFiltros());
        filtroCantidadMin.textProperty().addListener((obs, o, n) -> aplicarFiltros());
        filtroCantidadMax.textProperty().addListener((obs, o, n) -> aplicarFiltros());
        filtroPrecioMin.textProperty().addListener((obs, o, n) -> aplicarFiltros());
        filtroPrecioMax.textProperty().addListener((obs, o, n) -> aplicarFiltros());
        filtroFechaDesde.valueProperty().addListener((obs, o, n) -> aplicarFiltros());
        filtroFechaHasta.valueProperty().addListener((obs, o, n) -> aplicarFiltros());

        // Doble clic para editar
        productosTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !productosTable.getSelectionModel().isEmpty()) {
                handleEditar();
            }
        });

        productosTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        boolean puedeEditar = userRole.equals("ADMIN") || userRole.equals("PRODUCTOS");
        agregarBtn.setVisible(puedeEditar);   agregarBtn.setManaged(puedeEditar);
        editarBtn.setVisible(puedeEditar);    editarBtn.setManaged(puedeEditar);
        eliminarBtn.setVisible(puedeEditar);  eliminarBtn.setManaged(puedeEditar);
    }

    /**
     * Carga todos los productos de la base de datos.
     */
    private void cargarProductos() {
        try {
            List<Producto> productos = dbManager.getProductoDao().getAll();
            productosObservable = FXCollections.observableArrayList(productos);
            productosTable.setItems(productosObservable);
        } catch (SQLException e) {
            UIUtils.showErrorAlert("Error", "Error al cargar productos: " + e.getMessage());
            System.err.println("Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Aplica todos los filtros activos sobre la lista de productos.
     */
    private void aplicarFiltros() {
        String nombre = filtroNombre.getText().toLowerCase();
        String descripcion = filtroDescripcion.getText().toLowerCase();
        String almacen = filtroAlmacen.getText().toLowerCase();

        int cantMin = parsearEntero(filtroCantidadMin.getText(), 0);
        int cantMax = parsearEntero(filtroCantidadMax.getText(), Integer.MAX_VALUE);
        double precioMin = parsearDecimal(filtroPrecioMin.getText(), 0.0);
        double precioMax = parsearDecimal(filtroPrecioMax.getText(), Double.MAX_VALUE);

        java.time.LocalDate fechaDesde = filtroFechaDesde.getValue();
        java.time.LocalDate fechaHasta = filtroFechaHasta.getValue();

        ObservableList<Producto> filtrados = productosObservable.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre))
                .filter(p -> p.getDescripcion() == null || p.getDescripcion().toLowerCase().contains(descripcion))
                .filter(p -> {
                    String nomAlmacen = p.getAlmacen() != null ? p.getAlmacen().getNombre().toLowerCase() : "";
                    return nomAlmacen.contains(almacen);
                })
                .filter(p -> p.getCantidad() >= cantMin && p.getCantidad() <= cantMax)
                .filter(p -> p.getPrecio() >= precioMin && p.getPrecio() <= precioMax)
                .filter(p -> {
                    if (fechaDesde == null || p.getFechaCreacion() == null || p.getFechaCreacion().isEmpty()) return true;
                    java.time.LocalDate fechaProd = java.time.LocalDate.parse(p.getFechaCreacion().substring(0, 10));
                    return !fechaProd.isBefore(fechaDesde);
                })
                .filter(p -> {
                    if (fechaHasta == null || p.getFechaCreacion() == null || p.getFechaCreacion().isEmpty()) return true;
                    java.time.LocalDate fechaProd = java.time.LocalDate.parse(p.getFechaCreacion().substring(0, 10));
                    return !fechaProd.isAfter(fechaHasta);
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        productosTable.setItems(filtrados);
    }

    /**
     * Parsea un String a entero, retornando un valor por defecto si falla.
     */
    private int parsearEntero(String texto, int valorDefault) {
        try { return Integer.parseInt(texto.trim()); }
        catch (NumberFormatException e) { return valorDefault; }
    }

    /**
     * Parsea un String a double, retornando un valor por defecto si falla.
     */
    private double parsearDecimal(String texto, double valorDefault) {
        try { return Double.parseDouble(texto.trim()); }
        catch (NumberFormatException e) { return valorDefault; }
    }

    /**
     * Abre el diálogo para agregar un nuevo producto.
     */
    @FXML
    private void handleAgregar() {
        try {
            abrirFormularioProducto(null);
        } catch (Exception e) {
            UIUtils.showErrorAlert("Error", "Error al abrir formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Abre el diálogo para editar el producto seleccionado.
     */
    @FXML
    private void handleEditar() {
        Producto productoSeleccionado = productosTable.getSelectionModel().getSelectedItem();

        if (productoSeleccionado == null) {
            UIUtils.showErrorAlert("Error", "Selecciona un producto para editar");
            return;
        }

        try {
            abrirFormularioProducto(productoSeleccionado);
        } catch (Exception e) {
            UIUtils.showErrorAlert("Error", "Error al abrir formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Elimina el producto seleccionado.
     */
    @FXML
    private void handleEliminar() {
        Producto productoSeleccionado = productosTable.getSelectionModel().getSelectedItem();

        if (productoSeleccionado == null) {
            UIUtils.showErrorAlert("Error", "Selecciona un producto para eliminar");
            return;
        }

        if (UIUtils.showConfirmAlert(
                "Confirmar Eliminación",
                "¿Estás seguro de que deseas eliminar el producto '" + productoSeleccionado.getNombre() + "'?"
        )) {
            try {
                dbManager.getProductoDao().delete(productoSeleccionado);
                cargarProductos();
                UIUtils.showSuccessAlert("Éxito", "Producto eliminado correctamente");
            } catch (SQLException e) {
                UIUtils.showErrorAlert("Error", "Error al eliminar producto: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Actualiza la lista de productos.
     */
    @FXML
    private void handleActualizar() {
        cargarProductos();
        UIUtils.showSuccessAlert("Éxito", "Lista actualizada");
    }

    /**
     * Abre el formulario de producto en una ventana modal.
     *
     * @param producto Producto a editar, o null para crear uno nuevo
     * @throws Exception Si hay error al cargar el FXML
     */
    private void abrirFormularioProducto(Producto producto) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/formProducto.fxml")
        );

        ProductoFormController controller = new ProductoFormController(
                dbManager, usuarioActual, producto, this
        );
        loader.setController(controller);

        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setTitle(producto == null ? "Nuevo Producto" : "Editar Producto");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setWidth(500);
        stage.setHeight(600);
        stage.showAndWait();
    }

    /**
     * Limpia todos los filtros y muestra todos los productos.
     */
    @FXML
    private void handleLimpiarFiltros() {
        filtroNombre.clear();
        filtroDescripcion.clear();
        filtroAlmacen.clear();
        filtroCantidadMin.clear();
        filtroCantidadMax.clear();
        filtroPrecioMin.clear();
        filtroPrecioMax.clear();
        filtroFechaDesde.setValue(null);
        filtroFechaHasta.setValue(null);
        productosTable.setItems(productosObservable);
    }

    /**
     * Recarga la lista de productos después de crear/editar uno.
     */
    public void recargarProductos() {
        cargarProductos();
    }
}