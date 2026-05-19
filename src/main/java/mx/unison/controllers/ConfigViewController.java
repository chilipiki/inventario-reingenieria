package mx.unison.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import mx.unison.database.DatabaseManager;
import mx.unison.models.Usuario;
import mx.unison.util.UIUtils;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador para la vista de configuración del sistema.
 *
 * Maneja la gestión de usuarios, cambio de contraseña
 * y configuración general del sistema. Solo accesible para ADMIN.
 */
public class ConfigViewController {

    /** Tabla que muestra la lista de usuarios registrados en el sistema. */
    @FXML private TableView<Usuario> usuariosTable;

    /** Columna de la tabla correspondiente al identificador único del usuario. */
    @FXML private TableColumn<Usuario, Integer> idUsuarioColumn;

    /** Columna de la tabla correspondiente al nombre del usuario. */
    @FXML private TableColumn<Usuario, String> nombreUsuarioColumn;

    /** Columna de la tabla correspondiente al rol (ej. ADMIN, USUARIO). */
    @FXML private TableColumn<Usuario, String> rolColumn;

    /** Columna de la tabla correspondiente a la fecha y hora de su último inicio de sesión. */
    @FXML private TableColumn<Usuario, String> ultimoInicioColumn;

    /** Campo de texto encriptado para introducir la contraseña actual. */
    @FXML private PasswordField passwordActualField;

    /** Campo de texto encriptado para introducir la nueva contraseña deseada. */
    @FXML private PasswordField passwordNuevaField;

    /** Campo de texto encriptado para confirmar la nueva contraseña. */
    @FXML private PasswordField passwordConfirmarField;

    /** Etiqueta que muestra el nombre del usuario que está utilizando el sistema. */
    @FXML private Label usuarioActivoLabel;

    /** Controlador principal usado para gestionar la navegación global (si fuera necesario). */
    private MainController mainController;

    /** Gestor de conexiones y DAOs para interactuar con la base de datos. */
    private DatabaseManager dbManager;

    /** Nombre del usuario activo en la sesión. */
    private String usuarioActual;

    /** Lista observable que vincula los datos de los usuarios con la interfaz gráfica de la tabla. */
    private ObservableList<Usuario> usuariosObservable;

    /**
     * Constructor del controlador de configuración.
     *
     * @param mainController Controlador de navegación principal.
     * @param dbManager Gestor de conexión a la base de datos SQLite.
     * @param usuarioActual Nombre del usuario administrador actual.
     */
    public ConfigViewController(MainController mainController, DatabaseManager dbManager, String usuarioActual) {
        this.mainController = mainController;
        this.dbManager = dbManager;
        this.usuarioActual = usuarioActual;
    }

    /**
     * Método invocado automáticamente por JavaFX tras cargar la vista FXML.
     * Configura el mapeo de las columnas de la tabla de usuarios y carga la información inicial.
     */
    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        idUsuarioColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nombreUsuarioColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        rolColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRol()));
        ultimoInicioColumn.setCellValueFactory(cellData -> {
            String fecha = cellData.getValue().getFechaHoraUltimoInicio();
            if (fecha == null || fecha.isEmpty()) return new javafx.beans.property.SimpleStringProperty("");
            return new javafx.beans.property.SimpleStringProperty(fecha.substring(0,16).replace("T", " "));
        });
        // Mostrar usuario activo
        usuarioActivoLabel.setText(usuarioActual);

        // Cargar usuarios
        cargarUsuarios();
    }

    /**
     * Carga todos los usuarios de la base de datos y actualiza la tabla.
     */
    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = dbManager.getUsuarioDao().getAll();
            usuariosObservable = FXCollections.observableArrayList(usuarios);
            usuariosTable.setItems(usuariosObservable);
        } catch (SQLException e) {
            UIUtils.showErrorAlert("Error", "Error al cargar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Abre un cuadro de diálogo nativo de JavaFX para agregar un nuevo usuario,
     * encripta su contraseña y lo guarda en la base de datos.
     */
    @FXML
    private void handleAgregarUsuario() {
        // Diálogo simple para crear usuario
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Usuario");
        dialog.setHeaderText("Crear nuevo usuario");

        ButtonType crearButtonType = new ButtonType("Crear", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(crearButtonType, ButtonType.CANCEL);

        // Formulario
        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre de usuario");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");
        ComboBox<String> rolCombo = new ComboBox<>();
        rolCombo.getItems().addAll("ADMIN", "USUARIO");
        rolCombo.setValue("USUARIO");

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.getChildren().addAll(
                new Label("Nombre:"), nombreField,
                new Label("Contraseña:"), passwordField,
                new Label("Rol:"), rolCombo
        );
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == crearButtonType) {
                String hashedPassword = BCrypt.withDefaults().hashToString(12, passwordField.getText().toCharArray());
                return new Usuario(nombreField.getText(), hashedPassword, rolCombo.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(usuario -> {
            try {
                if (usuario.getNombre().isEmpty()) {
                    UIUtils.showErrorAlert("Error", "El nombre no puede estar vacío");
                    return;
                }
                dbManager.getUsuarioDao().create(usuario);
                cargarUsuarios();
                UIUtils.showSuccessAlert("Éxito", "Usuario creado correctamente");
            } catch (SQLException e) {
                UIUtils.showErrorAlert("Error", "Error al crear usuario: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Elimina el usuario actualmente seleccionado en la tabla previa validación.
     * No permite que el usuario activo elimine su propia cuenta.
     */
    @FXML
    private void handleEliminarUsuario() {
        Usuario usuarioSeleccionado = usuariosTable.getSelectionModel().getSelectedItem();

        if (usuarioSeleccionado == null) {
            UIUtils.showErrorAlert("Error", "Selecciona un usuario para eliminar");
            return;
        }

        if (usuarioSeleccionado.getNombre().equals(usuarioActual)) {
            UIUtils.showErrorAlert("Error", "No puedes eliminar tu propio usuario");
            return;
        }

        if (UIUtils.showConfirmAlert(
                "Confirmar Eliminación",
                "¿Estás seguro de que deseas eliminar al usuario '" + usuarioSeleccionado.getNombre() + "'?"
        )) {
            try {
                dbManager.getUsuarioDao().delete(usuarioSeleccionado);
                cargarUsuarios();
                UIUtils.showSuccessAlert("Éxito", "Usuario eliminado correctamente");
            } catch (SQLException e) {
                UIUtils.showErrorAlert("Error", "Error al eliminar usuario: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Acción vinculada al botón de actualizar para refrescar la lista de usuarios.
     */
    @FXML
    private void handleActualizarUsuarios() {
        cargarUsuarios();
    }

    /**
     * Verifica la contraseña actual, valida que la nueva coincida con la confirmación
     * y aplica el cambio utilizando un nuevo hash Bcrypt.
     */
    @FXML
    private void handleCambiarPassword() {
        String actual = passwordActualField.getText();
        String nueva = passwordNuevaField.getText();
        String confirmar = passwordConfirmarField.getText();

        if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
            UIUtils.showErrorAlert("Error", "Todos los campos son obligatorios");
            return;
        }

        if (!nueva.equals(confirmar)) {
            UIUtils.showErrorAlert("Error", "Las contraseñas nuevas no coinciden");
            return;
        }

        if (nueva.length() < 6) {
            UIUtils.showErrorAlert("Error", "La contraseña debe tener al menos 6 caracteres");
            return;
        }

        try {
            Usuario usuario = dbManager.getUsuarioDao().getByNombre(usuarioActual);

            if (usuario == null) {
                UIUtils.showErrorAlert("Error", "Usuario no encontrado");
                return;
            }

            // Verificar contraseña actual
            BCrypt.Result result = BCrypt.verifyer().verify(actual.toCharArray(), usuario.getPassword());
            if (!result.verified) {
                UIUtils.showErrorAlert("Error", "La contraseña actual es incorrecta");
                return;
            }

            // Actualizar contraseña
            String nuevaHasheada = BCrypt.withDefaults().hashToString(12, nueva.toCharArray());
            usuario.setPassword(nuevaHasheada);
            usuario.setFechaHoraUltimoInicio(String.valueOf(LocalDateTime.now()));
            dbManager.getUsuarioDao().update(usuario);

            // Limpiar campos
            passwordActualField.clear();
            passwordNuevaField.clear();
            passwordConfirmarField.clear();

            UIUtils.showSuccessAlert("Éxito", "Contraseña cambiada correctamente");
        } catch (SQLException e) {
            UIUtils.showErrorAlert("Error", "Error al cambiar contraseña: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Muestra un cuadro de diálogo para confirmar la limpieza de la base de datos.
     * (Actualmente es una simulación visual).
     */
    @FXML
    private void handleLimpiarDb() {
        if (UIUtils.showConfirmAlert(
                "Confirmar Limpieza",
                "¿Estás seguro de que deseas limpiar la base de datos? Esta acción no se puede deshacer."
        )) {
            UIUtils.showSuccessAlert("Éxito", "Base de datos limpiada correctamente");
        }
    }
}