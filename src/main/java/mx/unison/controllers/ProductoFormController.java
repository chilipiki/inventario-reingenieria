package mx.unison.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mx.unison.database.DatabaseManager;
import mx.unison.models.Almacen;
import mx.unison.models.Producto;
import mx.unison.util.UIUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador del formulario de producto.
 *
 * Maneja la creación y edición de productos.
 */
public class ProductoFormController {

    /** Campo de texto para ingresar o editar el nombre del producto. */
    @FXML
    private TextField nombreField;

    /** Área de texto para ingresar o editar la descripción detallada del producto. */
    @FXML
    private TextArea descripcionArea;

    /** Control numérico de incremento/decremento para definir la cantidad en stock del producto. */
    @FXML
    private Spinner<Integer> cantidadSpinner;

    /** Campo de texto configurado para aceptar únicamente valores numéricos y decimales para el precio. */
    @FXML
    private TextField precioField;

    /** Menú desplegable que muestra la lista de almacenes disponibles para asignar el producto. */
    @FXML
    private ComboBox<Almacen> almacenCombo;

    /** Botón para confirmar la acción de guardado en la base de datos. */
    @FXML
    private Button guardarBtn;

    /** Botón para descartar los cambios y cerrar el formulario. */
    @FXML
    private Button cancelarBtn;

    /** Gestor de conexiones y DAOs para interactuar con la base de datos SQLite. */
    private DatabaseManager dbManager;

    /** Nombre del usuario activo, utilizado para el registro de auditoría (quién creó/modificó). */
    private String usuarioActual;

    /** Instancia del producto a editar. Si es nulo, significa que el formulario está en modo "Creación". */
    private Producto productoActual;

    /** Referencia al controlador padre para poder solicitar la recarga de la tabla tras guardar. */
    private ProductosViewController parentController;

    /**
     * Constructor del controlador del formulario de productos.
     *
     * @param dbManager Gestor de la base de datos.
     * @param usuarioActual Nombre del usuario que ha iniciado sesión.
     * @param productoActual El producto a editar, o nulo si se va a crear uno nuevo.
     * @param parentController Controlador de la vista principal de productos.
     */
    public ProductoFormController(DatabaseManager dbManager, String usuarioActual,
                                  Producto productoActual, ProductosViewController parentController) {
        this.dbManager = dbManager;
        this.usuarioActual = usuarioActual;
        this.productoActual = productoActual;
        this.parentController = parentController;
    }

    /**
     * Método invocado automáticamente por JavaFX tras cargar la vista FXML.
     * Configura el formateador numérico del precio, los límites del spinner de cantidad,
     * carga la lista de almacenes y llena los campos si está en modo edición.
     */
    @FXML
    public void initialize() {
        // Configurar Spinner de cantidad
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0);
        cantidadSpinner.setValueFactory(valueFactory);

        // Cargar almacenes en ComboBox
        cargarAlmacenes();

        // Si es edición, cargar datos del producto
        if (productoActual != null) {
            cargarDatosProducto();
        }

        // Configurar validación de precio
        precioField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*(\\.\\d*)?")) {
                return change;
            }
            return null;
        }));
    }

    /**
     * Carga la lista de almacenes en el ComboBox.
     */
    private void cargarAlmacenes() {
        try {
            List<Almacen> almacenes = dbManager.getAlmacenDao().getAll();
            almacenCombo.setItems(FXCollections.observableArrayList(almacenes));

            // Mostrar el nombre del almacén
            almacenCombo.setCellFactory(param -> new ListCell<Almacen>() {
                @Override
                protected void updateItem(Almacen item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getNombre());
                }
            });

            almacenCombo.setButtonCell(new ListCell<Almacen>() {
                @Override
                protected void updateItem(Almacen item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getNombre());
                }
            });
        } catch (SQLException e) {
            UIUtils.showErrorAlert("Error", "Error al cargar almacenes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga los datos del producto para edición.
     */
    private void cargarDatosProducto() {
        nombreField.setText(productoActual.getNombre());
        descripcionArea.setText(productoActual.getDescripcion());
        cantidadSpinner.getValueFactory().setValue(productoActual.getCantidad());
        precioField.setText(String.valueOf(productoActual.getPrecio()));

        if (productoActual.getAlmacen() != null) {
            almacenCombo.setValue(productoActual.getAlmacen());
        }
    }

    /**
     * Guarda el producto (creación o actualización).
     */
    @FXML
    private void handleGuardar() {
        // Validaciones
        if (nombreField.getText().isEmpty()) {
            UIUtils.showErrorAlert("Error", "El nombre del producto es obligatorio");
            return;
        }

        if (precioField.getText().isEmpty()) {
            UIUtils.showErrorAlert("Error", "El precio es obligatorio");
            return;
        }

        try {
            double precio = Double.parseDouble(precioField.getText());

            if (productoActual == null) {
                // Crear nuevo producto
                Producto nuevoProducto = new Producto();
                nuevoProducto.setNombre(nombreField.getText());
                nuevoProducto.setDescripcion(descripcionArea.getText());
                nuevoProducto.setCantidad(cantidadSpinner.getValue());
                nuevoProducto.setPrecio(precio);
                nuevoProducto.setAlmacen(almacenCombo.getValue());
                nuevoProducto.setFechaCreacion(String.valueOf(LocalDateTime.now()));
                nuevoProducto.setUltimoUsuario(usuarioActual);

                dbManager.getProductoDao().create(nuevoProducto);
                UIUtils.showSuccessAlert("Éxito", "Producto creado correctamente");
            } else {
                // Actualizar producto existente
                productoActual.setNombre(nombreField.getText());
                productoActual.setDescripcion(descripcionArea.getText());
                productoActual.setCantidad(cantidadSpinner.getValue());
                productoActual.setPrecio(precio);
                productoActual.setAlmacen(almacenCombo.getValue());
                productoActual.setFechaModificacion(String.valueOf(LocalDateTime.now()));
                productoActual.setUltimoUsuario(usuarioActual);

                dbManager.getProductoDao().update(productoActual);
                UIUtils.showSuccessAlert("Éxito", "Producto actualizado correctamente");
            }

            parentController.recargarProductos();
            cerrarVentana();

        } catch (NumberFormatException e) {
            UIUtils.showErrorAlert("Error", "El precio debe ser un número válido");
        } catch (SQLException e) {
            UIUtils.showErrorAlert("Error", "Error al guardar producto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cierra la ventana del formulario.
     */
    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    /**
     * Cierra la ventana actual.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) cancelarBtn.getScene().getWindow();
        stage.close();
    }
}