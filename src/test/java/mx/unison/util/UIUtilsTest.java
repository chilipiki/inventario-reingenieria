package mx.unison.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para UIUtils.
 */
class UIUtilsTest {

    @Test
    void constantes_deberianTenerValoresCorrectos() {
        assertEquals("button-primary", UIUtils.BUTTON_PRIMARY);
        assertEquals("button-secondary", UIUtils.BUTTON_SECONDARY);
        assertEquals("button-success", UIUtils.BUTTON_SUCCESS);
        assertEquals("button-danger", UIUtils.BUTTON_DANGER);
        assertEquals("label-title", UIUtils.LABEL_TITLE);
        assertEquals("label-subtitle", UIUtils.LABEL_SUBTITLE);
        assertEquals("label-info", UIUtils.LABEL_INFO);
        assertEquals("card", UIUtils.CARD);
    }

    @Test
    void constructor_deberiaLanzarExcepcionAlInstanciar() {
        assertThrows(Exception.class, () -> {
            var constructor = UIUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }
}