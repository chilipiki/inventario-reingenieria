package mx.unison.views.panel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel principal del sistema de inventario.
 *
 * Esta clase representa la pantalla inicial de la aplicación y funciona como
 * un menú principal que permite al usuario navegar hacia las secciones
 * de gestión de productos o almacenes.
 *
 * Contiene botones que ejecutan acciones definidas mediante objetos Runnable
 * para abrir las ventanas correspondientes.
 */
public class Home extends JPanel{

    /**
     * Constructor del panel principal del sistema.
     *
     * Inicializa la interfaz gráfica que contiene el título del sistema
     * y los botones de navegación hacia las secciones de productos y almacenes.
     *
     * @param onOpenProductos Acción que se ejecuta al presionar el botón de productos.
     * @param onOpenAlmacenes Acción que se ejecuta al presionar el botón de almacenes.
     */
    public Home(Runnable onOpenProductos, Runnable onOpenAlmacenes) {

        setLayout(new BorderLayout());

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(40,40,40,40));

        JLabel title = new JLabel("Sistema de Inventario");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnProd = new JButton("Productos");
        btnProd.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnProd.addActionListener(e -> onOpenProductos.run());

        JButton btnAlm = new JButton("Almacenes");
        btnAlm.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAlm.addActionListener(e -> onOpenAlmacenes.run());

        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0,20)));
        center.add(btnProd);
        center.add(Box.createRigidArea(new Dimension(0,10)));
        center.add(btnAlm);

        add(center, BorderLayout.CENTER);
    }
}