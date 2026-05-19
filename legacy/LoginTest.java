package mx.unison;

import mx.unison.views.panel.Login;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    @Test
    void login_correcto() {
        AtomicBoolean loginEjecutado = new AtomicBoolean(false);

        Login login = new Login(usuario -> loginEjecutado.set(true));

        login.getTxtUsuario().setText("ADMIN");
        login.getTxtPassword().setText("admin23");
        login.getLoginBtn().doClick();

        assertTrue(loginEjecutado.get());

    }

    @Test
    void login_credenciales_incorrectas() {
        AtomicBoolean loginEjecutado = new AtomicBoolean(false);

        Login login = new Login(usuario -> loginEjecutado.set(true));

        login.getTxtUsuario().setText("ADMIN");
        login.getTxtPassword().setText("incorrecta");
        login.getLoginBtn().doClick();

        assertFalse(loginEjecutado.get());

    }

    @Test
    void login_campos_vacios() {
        AtomicBoolean loginEjecutado = new AtomicBoolean(false);

        Login login = new Login(usuario -> loginEjecutado.set(true));

        login.getTxtUsuario().setText("");
        login.getTxtPassword().setText("");
        login.getLoginBtn().doClick();

        assertFalse(loginEjecutado.get());
    }

}