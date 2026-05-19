package mx.unison.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.unison.database.DatabaseManager;

/**
 * Controlador principal de la aplicación.
 *
 * Gestiona la navegación entre vistas y mantiene la referencia
 * al gestor de base de datos.
 */
public class MainController {

    /** Ventana principal (Stage) de la aplicación JavaFX donde se montan las escenas. */
    private Stage primaryStage;

    /** Instancia global del gestor de base de datos compartida entre los controladores. */
    private DatabaseManager dbManager;

    /** Escena que contiene la interfaz gráfica de inicio de sesión. */
    private Scene loginScene;

    /** Escena que contiene la interfaz gráfica del panel principal del sistema. */
    private Scene mainScene;

    /** Ancho predeterminado de la ventana principal al iniciar la aplicación. */
    private static final int WINDOW_WIDTH = 1200;

    /** Alto predeterminado de la ventana principal al iniciar la aplicación. */
    private static final int WINDOW_HEIGHT = 800;

    /** Título mostrado en la barra superior de la ventana de la aplicación. */
    private static final String APP_TITLE = "Sistema de Inventario";

    /**
     * Constructor del controlador principal.
     *
     * @param primaryStage Escena principal de JavaFX
     * @param dbManager Gestor de la base de datos
     */
    public MainController(Stage primaryStage, DatabaseManager dbManager) {
        this.primaryStage = primaryStage;
        this.dbManager = dbManager;

        // Configurar la ventana principal
        this.primaryStage.setTitle(APP_TITLE);
        this.primaryStage.setWidth(WINDOW_WIDTH);
        this.primaryStage.setHeight(WINDOW_HEIGHT);
        this.primaryStage.centerOnScreen();

        // Configurar comportamiento al cerrar
        this.primaryStage.setOnCloseRequest(event -> {
            try {
                dbManager.close();
            } catch (Exception e) {
                System.err.println("Error al cerrar la base de datos: " + e.getMessage());
            }
        });
    }

    /**
     * Muestra la vista de login.
     *
     * @throws Exception Si hay error al cargar el FXML
     */
    public void showLoginView() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/login.fxml")
        );

        LoginController controller = new LoginController(this, dbManager);
        loader.setController(controller);

        loginScene = new Scene(loader.load());
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    /**
     * Muestra la vista principal después del login exitoso.
     *
     * @param userName Nombre del usuario autenticado
     * @param userRole Rol del usuario autenticado
     * @throws Exception Si hay error al cargar el FXML
     */
    public void showMainView(String userName, String userRole) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/main.fxml")
        );

        MainViewController controller = new MainViewController(
                this, dbManager, userName, userRole
        );
        loader.setController(controller);

        mainScene = new Scene(loader.load());
        primaryStage.setScene(mainScene);
    }

    /**
     * Vuelve a mostrar la vista de login.
     * Se utiliza cuando el usuario cierra sesión.
     *
     * @throws Exception Si hay error al cargar el FXML
     */
    public void showLoginViewAgain() throws Exception {
        showLoginView();
    }

    /**
     * Obtiene el gestor de la base de datos.
     *
     * @return DatabaseManager
     */
    public DatabaseManager getDatabaseManager() {
        return dbManager;
    }

    /**
     * Obtiene la escena principal.
     *
     * @return Stage principal
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}