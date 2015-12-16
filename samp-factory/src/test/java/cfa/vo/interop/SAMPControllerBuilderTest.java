package cfa.vo.interop;

import org.astrogrid.samp.hub.Hub;
import org.astrogrid.samp.hub.HubServiceMode;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class SAMPControllerBuilderTest {
    private Logger logger = Logger.getLogger(SAMPControllerBuilderTest.class.getName());
    private SAMPController controller;
    private Hub hub;

    @After
    public void after() throws Exception {
        if (controller != null) {
            logger.log(Level.INFO, "shutting down controller");
            controller.stop();
        }

        if (hub != null) {
            logger.log(Level.INFO, "shutting down hub");
            hub.shutdown();
        }
    }

    @Test
    public void testDefaults() {
        SAMPControllerBuilder builder = new SAMPControllerBuilder("aname");

        assertEquals("aname", builder.getName());
        assertEquals("aname", builder.getDescription());
        assertEquals(getClass().getResource("/iris_button_tiny.png"), builder.getIcon());
        assertFalse(builder.isWithResourceServer());

        controller = builder.build();
        assertEquals("aname", controller.getName());
        assertEquals("aname", controller.getMetadata().getName());
        assertEquals("aname", controller.getMetadata().getDescriptionText());
        assertEquals(getClass().getResource("/iris_button_tiny.png"), controller.getMetadata().getIconUrl());
        assertFalse(controller.withServer);
    }

    @Test
    public void testArguments() throws Exception {
        URL testURL = new URL("http://somewhere.org/image.png");

        SAMPControllerBuilder builder = new SAMPControllerBuilder("anothername")
                .withResourceServer("/root")
                .withDescription("a description")
                .withIcon(testURL);

        assertEquals("anothername", builder.getName());
        assertEquals("a description", builder.getDescription());
        assertEquals(testURL, builder.getIcon());
        assertTrue(builder.isWithResourceServer());
        assertEquals("/root", builder.getServerRoot());

        controller = builder.build();
        assertEquals("anothername", controller.getName());
        assertEquals("anothername", controller.getMetadata().getName());
        assertEquals("a description", controller.getMetadata().getDescriptionText());
        assertEquals(testURL, controller.getMetadata().getIconUrl());
        assertTrue(controller.withServer);
    }

    @Test
    public void testConnection() throws Exception {
        logger.log(Level.INFO, "instantiating builder");
        SAMPControllerBuilder builder = new SAMPControllerBuilder("aname");

        logger.log(Level.INFO, "starting hub");
        try {
            hub = Hub.runHub(HubServiceMode.NO_GUI);
        } catch (IOException ex) {
            logger.log(Level.INFO, "a hub is already running. It's good enough for us");
        }

        logger.log(Level.INFO, "starting controller");
        controller = builder.buildAndStart(30000);
        logger.log(Level.INFO, "checking controller is connected");
        assertTrue(controller.isConnected());

    }
}