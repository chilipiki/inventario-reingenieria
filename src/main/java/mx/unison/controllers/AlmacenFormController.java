package mx.unison.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mx.unison.database.DatabaseManager;
import mx.unison.models.Almacen;
import mx.unison.util.UIUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Controlador del formulario de almacén.
 *
 * Maneja la creación y edición de almacenes.
 */
public class AlmacenFormController {

    /** Campo de texto para ingresar o editar el nombre del almacén. */
    @FXML private TextField nombreField;

    /** Área de texto para ingresar o editar la ubicación física del almacén. */
    @FXML private TextArea ubicacionArea;

    /** Botón para guardar los cambios (crear o actualizar) del almacén. */
    @FXML private Button guardarBtn;

    /** Botón para cancelar la operación y cerrar el formulario. */
    @FXML private Button cancelarBtn;

    /** Gestor de la base de datos para realizar operaciones CRUD. */
    private DatabaseManager dbManager;

    /** Nombre del usuario que está realizando la acción. */
    private String usuarioActual;

    /** Almacén que se está editando. Será nulo si se está creando un almacén nuevo. */
    private Almacen almacenActual;

    /** Controlador de la vista principal de almacenes, usado para refrescar la tabla tras guardar. */
    private AlmacenesViewController parentController;

    /**
     * Constructor del controlador del formulario de almacén.
     *
     * @param dbManager Gestor de conexión a la base de datos.
     * @param usuarioActual Nombre del usuario activo en el sistema.
     * @param almacenActual Objeto almacén a editar, o nulo si es una creación nueva.
     * @param parentController Controlador de la vista padre para solicitar la actualización de la tabla.
     */
    public AlmacenFormController(DatabaseManager dbManager, String usuarioActual,
                                 Almacen almacenActual, AlmacenesViewController parentController) {
        this.dbManager = dbManager;
        this.usuarioActual = usuarioActual;
        this.almacenActual = almacenActual;
        this.parentController = parentController;
    }

    /**
     * Método invocado automáticamente por JavaFX después de cargar el archivo FXML.
     * Configura el formulario cargando los datos correspondientes si se trata de una edición.
     */
    @FXML
    public void initialize() {
        // Si es edición, cargar datos del almacén
        if (almacenActual != null) {
            cargarDatosAlmacen();
        }
    }

    /**
     * Carga los datos del almacén para edición.
     */
    private void cargarDatosAlmacen() {
        nombreField.setText(almacenActual.getNombre());
        ubicacionArea.setText(almacenActual.getUbicacion());
    }

    /**
     * Guarda el almacén (creación o actualización).
     */
    @FXML
    private void handleGuardar() {
        // Validaciones
        if (nombreField.getText().isEmpty()) {
            UIUtils.showErrorAlert("Error", "El nombre del almacén es obligatorio");
            return;
        }

        try {
            if (almacenActual == null) {
                // Crear nuevo almacén
                Almacen nuevoAlmacen = new Almacen();
                nuevoAlmacen.setNombre(nombreField.getText());
                nuevoAlmacen.setUbicacion(ubicacionArea.getText());
                nuevoAlmacen.setFechaHoraCreacion(String.valueOf(LocalDateTime.now()));
                nuevoAlmacen.setUltimoUsuario(usuarioActual);

                dbManager.getAlmacenDao().create(nuevoAlmacen);
                UIUtils.showSuccessAlert("Éxito", "Almacén creado correctamente");
            } else {
                // Actualizar almacén existente
                almacenActual.setNombre(nombreField.getText());
                almacenActual.setUbicacion(ubicacionArea.getText());
                almacenActual.setFechaHoraUltimaMod(String.valueOf(LocalDateTime.now()));
                almacenActual.setUltimoUsuario(usuarioActual);

                dbManager.getAlmacenDao().update(almacenActual);
                UIUtils.showSuccessAlert("Éxito", "Almacén actualizado correctamente");
            }

            parentController.recargarAlmacenes();
            cerrarVentana();

        } catch (SQLException e) {
            UIUtils.showErrorAlert("Error", "Error al guardar almacén: " + e.getMessage());
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