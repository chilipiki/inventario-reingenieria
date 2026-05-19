package mx.unison.controllers;

import mx.unison.database.Database;
import mx.unison.views.panel.AlmacenesPanel;
import mx.unison.views.panel.Home;
import mx.unison.views.panel.Login;
import mx.unison.views.panel.PanelProductos;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del sistema de inventario.
 *
 * Esta clase administra las diferentes pantallas de la aplicación
 * utilizando un CardLayout, permitiendo cambiar entre la vista de
 * inicio de sesión, el menú principal, el panel de productos y
 * el panel de almacenes.
 *
 * También mantiene una instancia de la base de datos utilizada
 * por los diferentes paneles del sistema.
 */
public class Vistas extends JFrame {

    /**
     * Administrador de diseño que permite cambiar entre
     * diferentes paneles dentro de la aplicación.
     */
    private final CardLayout cardLayout = new CardLayout();

    /**
     * Contenedor principal donde se almacenan las diferentes vistas
     * del sistema.
     */
    private final JPanel container = new JPanel(cardLayout);

    /**
     * Instancia de acceso a la base de datos utilizada por
     * los diferentes paneles del sistema.
     */
    private final Database db = new Database();

    /**
     * Constructor de la ventana principal del sistema.
     *
     * Inicializa las diferentes vistas del sistema y las
     * registra dentro del CardLayout para permitir la
     * navegación entre ellas.
     */
    public Vistas() {

        setTitle("Sistema de Inventario - Cliente");
        setSize(1000, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Login login = new Login(user -> showHome(user));
        Home home = new Home(() -> showPanel("PRODUCTOS"), () -> showPanel("ALMACENES"));
        PanelProductos productos = new PanelProductos(db, () -> showPanel("INICIO"));
        AlmacenesPanel almacenes = new AlmacenesPanel(db, () -> showPanel("INICIO"));

        container.add(login, "LOGIN");
        container.add(home, "INICIO");
        container.add(productos, "PRODUCTOS");
        container.add(almacenes, "ALMACENES");

        add(container);

        cardLayout.show(container, "LOGIN");
    }

    /**
     * Cambia la vista al menú principal después de que el
     * usuario ha iniciado sesión correctamente.
     *
     * @param usuarioNombre Nombre del usuario autenticado.
     */
    private void showHome(String usuarioNombre) {
        cardLayout.show(container, "INICIO");
    }

    /**
     * Cambia la vista actual al panel especificado.
     *
     * @param name Nombre del panel que se desea mostrar.
     */
    private void showPanel(String name) {
        cardLayout.show(container, name);
    }
}