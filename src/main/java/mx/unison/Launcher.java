package mx.unison;

/**
 * Clase de entrada auxiliar requerida para empaquetar aplicaciones JavaFX.
 * Evita el error de componentes JavaFX no encontrados en el classpath de módulos.
 */
public class Launcher {

    /**
     * Constructor principal de la clase de arranque.
     */
    public Launcher() {}

    /**
     * Punto de entrada principal de la JVM.
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}