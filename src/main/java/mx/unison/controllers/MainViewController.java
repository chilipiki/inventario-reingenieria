package mx.unison.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import mx.unison.database.DatabaseManager;
import mx.unison.util.UIUtils;

import java.sql.SQLException;

/**
 * Controlador de la vista principal.
 *
 * Gestiona la interfaz de usuario principal después del login
 * y la navegación entre los diferentes módulos del sistema.
 */
public class MainViewController {

    /** Contenedor principal que estructura el diseño general de la ventana (Sidebar, Topbar y Contenido). */
    @FXML
    private BorderPane rootPane;

    // ================= Sidebar =================

    /** Contenedor vertical del menú lateral izquierdo. */
    @FXML
    private VBox sidebar;

    /** Botón de navegación hacia el panel principal (Dashboard). */
    @FXML
    private Button dashboardBtn;

    /** Botón de navegación hacia el módulo de inventario de productos. */
    @FXML
    private Button productosBtn;

    /** Botón de navegación hacia el módulo de gestión de almacenes. */
    @FXML
    private Button almacenesBtn;

    /** Botón de navegación hacia el módulo de configuración del sistema. */
    @FXML
    private Button configBtn;

    /** Etiqueta que muestra el nombre del usuario activo en el menú lateral. */
    @FXML
    private Label userNameLabel;

    /** Etiqueta que muestra el rol (ej. ADMIN, USUARIO) del usuario activo en el menú lateral. */
    @FXML
    private Label userRoleLabel;

    // ================= Topbar =================

    /** Etiqueta de título principal en la barra superior dinámica según la vista actual. */
    @FXML
    private Label pageTitle;

    /** Etiqueta de subtítulo o descripción en la barra superior. */
    @FXML
    private Label pageSubtitle;

    // ================= Content Area =================

    /** Contenedor central (Stack) donde se superponen e intercambian las distintas vistas hijas. */
    @FXML
    private StackPane contentStackPane;

    /** Vista del panel de estadísticas o inicio. */
    @FXML
    private VBox dashboardView;

    /** Contenedor donde se inyecta la vista de gestión de productos. */
    @FXML
    private VBox productosView;

    /** Contenedor donde se inyecta la vista de gestión de almacenes. */
    @FXML
    private VBox almacenesView;

    /** Contenedor donde se inyecta la vista de configuración y usuarios. */
    @FXML
    private VBox configView;

    // ================= Stats Labels =================

    /** Etiqueta en el dashboard que muestra la cantidad total de productos distintos. */
    @FXML
    private Label productosCountLabel;

    /** Etiqueta en el dashboard que muestra la cantidad total de almacenes registrados. */
    @FXML
    private Label almacenesCountLabel;

    /** Etiqueta en el dashboard que muestra la suma total de unidades físicas en stock. */
    @FXML
    private Label stockTotalLabel;

    /** Controlador de nivel superior encargado de la navegación global de ventanas. */
    private MainController mainController;

    /** Proveedor de los DAOs para interactuar con la base de datos. */
    private DatabaseManager dbManager;

    /** Nombre del usuario validado en la sesión actual. */
    private String userName;

    /** Rol o nivel de acceso del usuario para restringir módulos. */
    private String userRole;

    /**
     * Constructor del controlador de la vista principal.
     *
     * @param mainController Controlador principal de la aplicación
     * @param dbManager Gestor de la base de datos
     * @param userName Nombre del usuario autenticado
     * @param userRole Rol del usuario autenticado
     */
    public MainViewController(MainController mainController, DatabaseManager dbManager,
                              String userName, String userRole) {
        this.mainController = mainController;
        this.dbManager = dbManager;
        this.userName = userName;
        this.userRole = userRole;
    }

    /**
     * Inicializa el controlador después de cargar el FXML.
     */
    @FXML
    public void initialize() {
        // Configurar información del usuario
        userNameLabel.setText("👤 " + userName);
        userRoleLabel.setText("Rol: " + userRole);

        // Mostrar botones según el rol
        configureNavigationByRole();

        // Cargar estadísticas
        loadStatistics();

        // Establecer Dashboard como vista inicial
        showDashboard();
    }

    /**
     * Configura la visibilidad de los botones según el rol del usuario.
     */
    private void configureNavigationByRole() {
        switch (userRole) {
            case "ADMIN" -> {
                // Admin ve todo
                productosBtn.setVisible(true);
                productosBtn.setManaged(true);
                almacenesBtn.setVisible(true);
                almacenesBtn.setManaged(true);
                configBtn.setVisible(true);
                configBtn.setManaged(true);
            }
            case "PRODUCTOS" -> {
                // Solo ve productos
                productosBtn.setVisible(true);
                productosBtn.setManaged(true);
            }
            case "ALMACENES" -> {
                // Solo ve almacenes
                almacenesBtn.setVisible(true);
                almacenesBtn.setManaged(true);
            }
            default -> {
                // Vista limitada
            }
        }
    }

    /**
     * Carga las estadísticas del dashboard consultando la base de datos.
     */
    private void loadStatistics() {
        try {
            // Contar productos
            int productosCount = dbManager.getProductoDao().getAll().size();
            productosCountLabel.setText(String.valueOf(productosCount));

            // Contar almacenes
            int almacenesCount = dbManager.getAlmacenDao().getAll().size();
            almacenesCountLabel.setText(String.valueOf(almacenesCount));

            // Calcular stock total
            int stockTotal = dbManager.getProductoDao().getAll()
                    .stream()
                    .mapToInt(p -> p.getCantidad())
                    .sum();
            stockTotalLabel.setText(String.valueOf(stockTotal));

        } catch (SQLException e) {
            System.err.println("Error al cargar estadísticas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================================================================
    // MANEJADORES DE NAVEGACIÓN
    // ============================================================================

    /**
     * Navega a la vista de panel principal (Dashboard).
     */
    @FXML
    private void handleDashboard() {
        showView(dashboardView, dashboardBtn, "Dashboard", "Bienvenido al sistema");
    }

    /**
     * Navega a la vista de inventario, cargando dinámicamente el controlador de productos si es necesario.
     */
    @FXML
    private void handleProductos() {
        showView(productosView, productosBtn, "Gestión de Productos", "Administra tus productos");

        // Cargar vista de productos si no está cargada
        if (productosView.getChildren().isEmpty()) {
            try {
                loadProductosView();
            } catch (Exception e) {
                UIUtils.showErrorAlert("Error", "Error al cargar vista de productos: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Carga la vista de productos desde el FXML instanciando su controlador correspondiente.
     * @throws Exception Si el archivo FXML no se encuentra o contiene errores de sintaxis.
     */
    private void loadProductosView() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/productos.fxml")
        );

        ProductosViewController controller = new ProductosViewController(
                mainController, dbManager, userName
        );
        loader.setController(controller);

        productosView.getChildren().clear();
        productosView.getChildren().add(loader.load());
    }

    /**
     * Navega a la vista de almacenes, cargando el sub-módulo de forma dinámica la primera vez que se accede.
     */
    @FXML
    private void handleAlmacenes() {
        showView(almacenesView, almacenesBtn, "Gestión de Almacenes", "Administra tus almacenes");

        // Cargar vista de almacenes si no está cargada
        if (almacenesView.getChildren().isEmpty()) {
            try {
                loadAlmacenesView();
            } catch (Exception e) {
                UIUtils.showErrorAlert("Error", "Error al cargar vista de almacenes: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Carga la vista de almacenes desde el FXML instanciando su controlador correspondiente.
     * @throws Exception Si el archivo FXML no se encuentra o no se puede cargar.
     */
    private void loadAlmacenesView() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/almacenes.fxml")
        );

        AlmacenesViewController controller = new AlmacenesViewController(
                mainController, dbManager, userName
        );
        loader.setController(controller);

        almacenesView.getChildren().clear();
        almacenesView.getChildren().add(loader.load());
    }

    /**
     * Navega a la vista de configuración general del sistema.
     */
    @FXML
    private void handleConfig() {
        showView(configView, configBtn, "Configuración", "Administración del sistema");
        if (configView.getChildren().isEmpty()) {
            try {
                loadConfigView();
            } catch (Exception e) {
                UIUtils.showErrorAlert("Error", "Error al cargar vista de configuración: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Carga la vista de configuración desde el archivo FXML y asigna su controlador.
     * @throws Exception Si ocurre un fallo en la carga del recurso.
     */
    private void loadConfigView() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/config.fxml")
        );
        ConfigViewController controller = new ConfigViewController(
                mainController, dbManager, userName
        );
        loader.setController(controller);
        configView.getChildren().clear();
        configView.getChildren().add(loader.load());
    }

    /**
     * Muestra una vista específica en el StackPane ocultando las demás.
     *
     * @param view Vista a mostrar
     * @param button Botón de navegación asociado
     * @param title Título de la página
     * @param subtitle Subtítulo de la página
     */
    private void showView(VBox view, Button button, String title, String subtitle) {
        // Ocultar todas las vistas
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        productosView.setVisible(false);
        productosView.setManaged(false);
        almacenesView.setVisible(false);
        almacenesView.setManaged(false);
        configView.setVisible(false);
        configView.setManaged(false);

        // Remover estado activo de todos los botones
        dashboardBtn.getStyleClass().remove("nav-button-active");
        productosBtn.getStyleClass().remove("nav-button-active");
        almacenesBtn.getStyleClass().remove("nav-button-active");
        configBtn.getStyleClass().remove("nav-button-active");

        // Mostrar la vista seleccionada
        view.setVisible(true);
        view.setManaged(true);

        // Marcar botón como activo
        if (!button.getStyleClass().contains("nav-button-active")) {
            button.getStyleClass().add("nav-button-active");
        }

        // Actualizar topbar
        pageTitle.setText(title);
        pageSubtitle.setText(subtitle);
    }

    /**
     * Cierra la sesión y vuelve a mostrar el login.
     */
    @FXML
    private void handleLogout() {
        if (UIUtils.showConfirmAlert(
                "Confirmar Cierre de Sesión",
                "¿Deseas cerrar sesión?"
        )) {
            try {
                mainController.showLoginViewAgain();
            } catch (Exception e) {
                UIUtils.showErrorAlert("Error", "Error al cerrar sesión: " + e.getMessage());
                System.err.println("Error al cerrar sesión: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Muestra el dashboard por defecto.
     */
    private void showDashboard() {
        showView(dashboardView, dashboardBtn, "Dashboard", "Bienvenido al sistema");
    }

    /**
     * Obtiene el nombre del usuario autenticado.
     *
     * @return Nombre del usuario
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Obtiene el rol del usuario autenticado.
     *
     * @return Rol del usuario
     */
    public String getUserRole() {
        return userRole;
    }
}