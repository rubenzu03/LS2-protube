package com.tecnocampus.LS2.protube_back;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProtubeBackApplicationTests {

    @Test
    void shouldStartApp() {
        // Test simple que verifica que la clase de aplicación existe y se puede instanciar
        ProtubeBackApplication app = new ProtubeBackApplication();
        Assertions.assertNotNull(app);
    }

}
