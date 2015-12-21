package cfa.vo.interop;

import org.astrogrid.samp.Metadata;
import org.astrogrid.samp.client.HubConnector;
import org.astrogrid.samp.hub.Hub;
import org.astrogrid.samp.hub.HubServiceMode;
import org.astrogrid.samp.xmlrpc.StandardClientProfile;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SAMPTest {
    private Logger logger = Logger.getLogger(SAMPTest.class.getName());

    @Rule
    public SampResource sampResource = new SampResource();

    @Ignore
    @Test
    public void testGarbageCollection() throws Exception {
        WeakReference<Hub> ref = new WeakReference<>(Hub.runHub(HubServiceMode.MESSAGE_GUI));
        assertTrue(ref.get().getHubService().isHubRunning());
        ref.get().shutdown();
        assertFalse(ref.get().getHubService().isHubRunning());
        System.gc();
        assertNull(ref.get());
    }

    @Ignore
    @Test
    public void testShutdown() throws Exception {
        Hub hub = Hub.runHub(HubServiceMode.MESSAGE_GUI);
        HubConnector conn = new HubConnector(StandardClientProfile.getInstance());
        conn.setAutoconnect(1);
        for (int i=0; i<10; i++)
            try {
                assertTrue(conn.isConnected());
                break;
            } catch (AssertionError e) {
                Thread.sleep(1000);
            }
        assertTrue(conn.isConnected());
        hub.shutdown();

        for (int i=0; i<10; i++)
            try {
                assertFalse(conn.isConnected());
                break;
            } catch (AssertionError e) {
                Thread.sleep(1000);
            }
        assertFalse(conn.isConnected());
    }

    @Test
    public void testSAMPStartup() throws Exception {
        logger.log(Level.INFO, "Testing that SAMP is up...");
        assertTrue(sampResource.getService().isSampUp());
        logger.log(Level.INFO, "...OK");
    }

    @Test
    public void testReloadIfFailure() throws Exception {
        logger.log(Level.INFO, "Testing we start with everything connected");
        assertTrue(sampResource.getService().isSampUp());

        logger.log(Level.INFO, "Registering Mock Listener");
        SAMPConnectionListener mockListener = mock(SAMPConnectionListener.class);
        sampResource.getService().addSampConnectionListener(mockListener);

        logger.log(Level.INFO, "Requesting shutdown of autoRunHub");
        sampResource.getService().setAutoRunHub(false);

        logger.log(Level.INFO, "Requesting shutdown of Hub");
        sampResource.getService().hub.shutdown();
        sampResource.getService().hub = null;

        verify(mockListener, timeout(6000).atLeastOnce()).run(false);

        sampResource.getService().hub = Hub.runHub(HubServiceMode.MESSAGE_GUI);

        verify(mockListener, timeout(6000).atLeastOnce()).run(true);
    }

    private class SampResource extends ExternalResource {
        private Logger logger = Logger.getLogger(SampResource.class.getName());
        private SampService service;
        private boolean autoRunHub;
        private Metadata metadata;

        public SampResource() {
            this.autoRunHub = true;
        }

        public SampResource(boolean autoRunHub) {
            this.autoRunHub = autoRunHub;
        }

        public SampResource(boolean autoRunHub, Metadata metadata) {
            this.autoRunHub = autoRunHub;
            this.metadata = metadata;
        }

        private SampService newSampService() {
            if (metadata == null) {
                return new SampService(autoRunHub);
            } else {
                return new SampService(autoRunHub, metadata);
            }
        }

        @Override
        public void before() {
            logger.log(Level.INFO, "Starting SAMP resource");
            service = newSampService();
            service.start();

            logger.log(Level.INFO, "Verifying resource was correctly started");
            for (int i=0; i<50; i++) {
                try {
                    assertTrue(service.isSampUp());
                    return;
                } catch (AssertionError e) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {}
                }
            }
            fail("Could not start SAMP Service");
        }

        @Override
        public void after() {
            logger.log(Level.INFO, "Shutting down SAMP resource");
            service.shutdown();
        }

        public SampService getService() {
            return service;
        }
    }

}
