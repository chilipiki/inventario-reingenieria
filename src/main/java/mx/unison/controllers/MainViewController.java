package mx.unison.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import mx.unison.database.DatabaseManager;
import mx.unison.util.UIUtils;

import java.sql.SQLException;

/**
 * Controlador de la vista principal del sistema de inventario.
 *
 * Gestiona la navegación entre las diferentes vistas (Inicio, Productos,
 * Almacenes, Configuración) y la información de sesión del usuario activo.
 */
public class MainViewController {

    /** Referencia al controlador principal de la aplicación. */
    private MainController mainController;

    /** Proveedor de acceso a la base de datos. */
    private DatabaseManager dbManager;

    /** Nombre del usuario autenticado en la sesión actual. */
    private String userName;

    /** Rol del usuario autenticado (ADMIN, PRODUCTOS, ALMACENES). */
    private String userRole;

    // ── Topbar ──────────────────────────────────────────────────────────────

    /** Etiqueta con el nombre del usuario en la barra superior. */
    @FXML private Label userNameLabel;

    /** Etiqueta con el rol del usuario en la barra superior. */
    @FXML private Label userRoleLabel;

    // ── NavBar (dentro de homeView) ─────────────────────────────────────────

    /** Botón Inicio del navbar horizontal. */
    @FXML private Button inicioBtn;

    /** Botón Productos del navbar horizontal. */
    @FXML private Button productosBtn;

    /** Botón Almacenes del navbar horizontal. */
    @FXML private Button almacenesBtn;


    // ── Vistas del StackPane ────────────────────────────────────────────────

    /** Vista de inicio (home). */
    @FXML private VBox homeView;

    /** Vista de productos (BorderPane con header + contenido). */
    @FXML private BorderPane productosView;

    /** Vista de almacenes (BorderPane con header + contenido). */
    @FXML private BorderPane almacenesView;

    // ── Contenedores de contenido dinámico ─────────────────────────────────

    /** Contenedor donde se carga el FXML de productos. */
    @FXML private VBox productosContent;

    /** Contenedor donde se carga el FXML de almacenes. */
    @FXML private VBox almacenesContent;


    /**
     * Constructor del controlador de la vista principal.
     *
     * @param mainController Controlador raíz de la aplicación
     * @param dbManager      Gestor de base de datos
     * @param userName       Nombre del usuario autenticado
     * @param userRole       Rol del usuario autenticado
     */
    public MainViewController(MainController mainController, DatabaseManager dbManager,
                              String userName, String userRole) {
        this.mainController = mainController;
        this.dbManager = dbManager;
        this.userName = userName;
        this.userRole = userRole;
    }

    /**
     * Inicializa el controlador tras cargar el FXML.
     * Configura la sesión, los botones visibles según el rol y las estadísticas.
     */
    @FXML
    public void initialize() {
        userNameLabel.setText("👤 " + userName);
        userRoleLabel.setText("Rol: " + userRole);

        configurarNavegacionPorRol();
        mostrarHome();
    }

    // ── Configuración por rol ───────────────────────────────────────────────

    /**
     * Muestra u oculta los botones del navbar según el rol del usuario.
     */
    private void configurarNavegacionPorRol() {
        // Todos ven productos y almacenes
        setVisible(productosBtn, true);
        setVisible(almacenesBtn, true);
        switch (userRole) {
            case "ADMIN" -> {
                setVisible(productosBtn, true);
                setVisible(almacenesBtn, true);
            }
            case "PRODUCTOS" -> setVisible(productosBtn, true);
            case "ALMACENES" -> setVisible(almacenesBtn, true);
        }
    }

    /** Muestra u oculta un botón del navbar actualizando también su managed. */
    private void setVisible(Button btn, boolean visible) {
        btn.setVisible(visible);
        btn.setManaged(visible);
    }

    // ── Navegación ──────────────────────────────────────────────────────────

    /**
     * Muestra la vista de inicio (home) y actualiza el estado activo del navbar.
     */
    @FXML
    private void handleHome() {
        mostrarVista(homeView);
        activarBoton(inicioBtn);
    }

    /**
     * Muestra la vista de productos, cargándola dinámicamente si es la primera vez.
     */
    @FXML
    private void handleProductos() {
        mostrarVista(productosView);
        activarBoton(productosBtn);
        if (productosContent.getChildren().isEmpty()) {
            try {
                cargarProductosView();
            } catch (Exception e) {
                UIUtils.showErrorAlert("Error", "Error al cargar productos: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra la vista de almacenes, cargándola dinámicamente si es la primera vez.
     */
    @FXML
    private void handleAlmacenes() {
        mostrarVista(almacenesView);
        activarBoton(almacenesBtn);
        if (almacenesContent.getChildren().isEmpty()) {
            try {
                cargarAlmacenesView();
            } catch (Exception e) {
                UIUtils.showErrorAlert("Error", "Error al cargar almacenes: " + e.getMessage());
            }
        }
    }

    /**
     * Cierra la sesión del usuario actual y regresa a la pantalla de login.
     */
    @FXML
    private void handleLogout() {
        if (UIUtils.showConfirmAlert("Confirmar cierre de sesión", "¿Deseas cerrar sesión?")) {
            try {
                mainController.showLoginViewAgain();
            } catch (Exception e) {
                UIUtils.showErrorAlert("Error", "Error al cerrar sesión: " + e.getMessage());
            }
        }
    }

    // ── Helpers de navegación ───────────────────────────────────────────────

    /**
     * Oculta todas las vistas y muestra únicamente la indicada.
     *
     * @param vista Vista a mostrar
     */
    private void mostrarVista(javafx.scene.Parent vista) {
        homeView.setVisible(false);    homeView.setManaged(false);
        productosView.setVisible(false); productosView.setManaged(false);
        almacenesView.setVisible(false); almacenesView.setManaged(false);

        vista.setVisible(true);
        vista.setManaged(true);
    }

    /** Muestra la vista home por defecto. */
    private void mostrarHome() {
        mostrarVista(homeView);
    }

    /**
     * Marca un botón del navbar como activo y desactiva los demás.
     *
     * @param boton Botón a marcar como activo
     */
    private void activarBoton(Button boton) {
        for (Button b : new Button[]{inicioBtn, productosBtn, almacenesBtn}) {
            b.getStyleClass().removeAll("nav-tab-active", "nav-tab-button");
            b.getStyleClass().add("nav-tab-button");
        }
        boton.getStyleClass().removeAll("nav-tab-active", "nav-tab-button");
        boton.getStyleClass().add("nav-tab-active");
    }

    // ── Carga dinámica de vistas ────────────────────────────────────────────

    /**
     * Carga el FXML de productos e inyecta su contenido en el contenedor correspondiente.
     *
     * @throws Exception Si ocurre un error al cargar el FXML
     */
    private void cargarProductosView() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/productos.fxml"));
        loader.setController(new ProductosViewController(mainController, dbManager, userName, userRole));
        Node content = loader.load();
        VBox.setVgrow(content, Priority.ALWAYS);
        productosContent.getChildren().setAll(content);
        VBox.setVgrow(productosContent, Priority.ALWAYS);
    }

    /**
     * Carga el FXML de almacenes e inyecta su contenido en el contenedor correspondiente.
     *
     * @throws Exception Si ocurre un error al cargar el FXML
     */
    private void cargarAlmacenesView() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/almacenes.fxml"));
        loader.setController(new AlmacenesViewController(mainController, dbManager, userName, userRole));
        Node content = loader.load();
        VBox.setVgrow(content, Priority.ALWAYS);
        almacenesContent.getChildren().setAll(content);
        VBox.setVgrow(almacenesContent, Priority.ALWAYS);
    }

    // ── Getters ─────────────────────────────────────────────────────────────

    /** @return Nombre del usuario autenticado */
    public String getUserName() { return userName; }

    /** @return Rol del usuario autenticado */
    public String getUserRole() { return userRole; }
}
