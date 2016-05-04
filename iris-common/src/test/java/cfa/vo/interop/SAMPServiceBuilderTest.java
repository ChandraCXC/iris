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

public class SAMPServiceBuilderTest {
    private Logger logger = Logger.getLogger(SAMPServiceBuilderTest.class.getName());
    private SampService sampService;
    private Hub hub;

    @After
    public void after() throws Exception {
        if (sampService != null) {
            logger.log(Level.INFO, "shutting down controller");
            sampService.shutdown();
        }

        if (hub != null) {
            logger.log(Level.INFO, "shutting down hub");
            hub.shutdown();
        }
    }

    @Test
    public void testDefaults() {
        SAMPServiceBuilder builder = new SAMPServiceBuilder("aname");

        assertEquals("aname", builder.getName());
        assertEquals("aname", builder.getDescription());
        assertEquals(getClass().getResource("/iris_button_tiny.png"), builder.getIcon());
        assertFalse(builder.isWithResourceServer());

        sampService = builder.build();
        sampService.start();
        assertEquals("aname", sampService.getSampClient().getMetadata().getName());
        assertEquals("aname", sampService.getSampClient().getMetadata().getName());
        assertEquals("aname", sampService.getSampClient().getMetadata().getDescriptionText());
        assertEquals(getClass().getResource("/iris_button_tiny.png"), sampService.getSampClient().getMetadata().getIconUrl());
        assertNull(sampService.resourceServer);
    }

    @Test
    public void testArguments() throws Exception {
        URL testURL = new URL("http://somewhere.org/image.png");

        SAMPServiceBuilder builder = new SAMPServiceBuilder("anothername")
                .withResourceServer("/root")
                .withDescription("a description")
                .withIcon(testURL);

        assertEquals("anothername", builder.getName());
        assertEquals("a description", builder.getDescription());
        assertEquals(testURL, builder.getIcon());
        assertTrue(builder.isWithResourceServer());
        assertEquals("/root", builder.getServerRoot());

        sampService = builder.build();
        sampService.start();
        assertEquals("anothername", sampService.getSampClient().getMetadata().getName());
        assertEquals("anothername", sampService.getSampClient().getMetadata().getName());
        assertEquals("a description", sampService.getSampClient().getMetadata().getDescriptionText());
        assertEquals(testURL, sampService.getSampClient().getMetadata().getIconUrl());
        assertNotNull(sampService.resourceServer);
    }

    @Test
    public void testConnection() throws Exception {
        logger.log(Level.INFO, "instantiating builder");
        SAMPServiceBuilder builder = new SAMPServiceBuilder("aname");

        logger.log(Level.INFO, "starting hub");
        try {
            hub = Hub.runHub(HubServiceMode.MESSAGE_GUI);
        } catch (IOException ex) {
            logger.log(Level.INFO, "a hub is already running. It's good enough for us");
        }

        logger.log(Level.INFO, "starting controller");
        sampService = builder.build();
        sampService.start();
        logger.log(Level.INFO, "checking controller is connected");
        boolean connected = false;
        for (int i=0; i<20; i++) {
            try {
                assertTrue(sampService.getSampClient().isConnected());
                connected = true;
                break;
            } catch (AssertionError e) {
                Thread.sleep(500);
            }
        }
        assertTrue(connected);
    }
}