package org.example;

import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    private Tomcat tomcat;

    @AfterEach
    void tearDown() throws Exception {
        if (tomcat != null) {
            tomcat.stop();
            tomcat.destroy();
            tomcat = null;
        }
    }

    @Test
    void startTomcat_startsEmbeddedServer() throws Exception {
        tomcat = Main.startTomcat(0);

        assertNotNull(tomcat);
        assertNotNull(tomcat.getServer());
        assertNotNull(tomcat.getHost());
        assertTrue(tomcat.getHost().findChildren().length > 0);
    }
}
