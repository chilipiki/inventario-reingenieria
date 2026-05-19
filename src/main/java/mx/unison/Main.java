package mx.unison;

import javafx.application.Application;
import javafx.stage.Stage;
import mx.unison.controllers.MainController;
import mx.unison.database.DatabaseManager;

/**
 * Clase principal de JavaFX que inicia el ciclo de vida de la aplicación.
 */
public class Main extends Application {

    /**
     * Constructor por defecto requerido por el framework JavaFX.
     * No debe ser invocado manualmente.
     */
    public Main() {}

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Inicializar la base de datos
            DatabaseManager dbManager = new DatabaseManager();

            // Crear controlador principal
            MainController mainController = new MainController(primaryStage, dbManager);

            // Mostrar vista de login
            mainController.showLoginView();

        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Punto de entrada de la aplicación.
     *
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
}