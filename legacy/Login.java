package mx.unison.views.panel;

import mx.unison.database.Database;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Panel de inicio de sesión del sistema de inventario.
 *
 * Esta clase representa la interfaz gráfica donde el usuario puede
 * ingresar sus credenciales para acceder al sistema.
 *
 * El panel solicita el nombre de usuario y la contraseña y utiliza
 * la clase Database para verificar si las credenciales son válidas.
 * Si la autenticación es exitosa, se ejecuta una acción definida
 * mediante un objeto Consumer que recibe el nombre del usuario autenticado.
 */
public class Login extends JPanel{

    /**
     * Campo de texto donde se ingresa el nombre de usuario.
     */
    private JTextField txtUsuario;

    /**
     * Campo de contraseña donde se ingresa la clave del usuario.
     */
    private JPasswordField txtPassword;

    /**
     * Botón que ejecuta la autenticación del usuario.
     */
    private JButton loginBtn;

    /**
     * Constructor del panel de inicio de sesión.
     *
     * Inicializa los componentes gráficos del formulario de login
     * y define la acción que se ejecutará cuando el usuario se
     * autentique correctamente en el sistema.
     *
     * @param onLogin Acción que se ejecuta cuando el usuario inicia sesión
     * correctamente. Recibe como parámetro el nombre del usuario autenticado.
     */
    public Login(Consumer<String> onLogin) {

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(320, 380));
        card.setBackground(new Color(224, 240, 255));
        card.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 2, true));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Inicio de sesión");
        title.setFont(new Font("Consolas", Font.PLAIN, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));

        JPanel fields = new JPanel();
        fields.setLayout(new GridLayout(4, 1, 10, 10));
        fields.setBackground(new Color(224, 240, 255));
        fields.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        txtUsuario = new JTextField();
        txtPassword = new JPasswordField();

        fields.add(new JLabel("Usuario:"));
        fields.add(txtUsuario);
        fields.add(new JLabel("Contraseña:"));
        fields.add(txtPassword);

        loginBtn = new JButton("Iniciar sesión");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setFocusPainted(false);
        loginBtn.setBackground(new Color(198, 243, 213));
        loginBtn.setBorder(BorderFactory.createLineBorder(new Color(63, 185, 80), 2, true));
        loginBtn.setPreferredSize(new Dimension(160, 40));

        loginBtn.addActionListener(e -> {

            Database db = new Database();
            var usr = db.authenticate(txtUsuario.getText(), new String(txtPassword.getPassword()));

            if (usr != null) {
                onLogin.accept(usr.nombre);
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales inválidas",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(title);
        card.add(fields);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(loginBtn);

        add(card, gbc);
    }

    /**
     * Devuelve el campo de texto del usuario.
     *
     * @return Campo de usuario.
     */

    public JTextField getTxtUsuario() {
        return txtUsuario;
    }

    /**
     * Devuelve el campo de contraseña.
     *
     * @return Campo de contraseña.
     */
    public JPasswordField getTxtPassword() {
        return txtPassword;
    }

    /**
     * Devuelve el botón de inicio de sesión.
     *
     * @return Botón de login.
     */
    public JButton getLoginBtn() {
        return loginBtn;
    }
}