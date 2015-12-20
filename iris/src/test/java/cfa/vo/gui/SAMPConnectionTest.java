package cfa.vo.gui;

import cfa.vo.iris.test.IrisAppResource;
import cfa.vo.iris.test.IrisUISpecAdapter;
import cfa.vo.iris.test.unit.AbstractUISpecTest;
import org.astrogrid.samp.hub.Hub;
import org.astrogrid.samp.hub.HubServiceMode;
import org.junit.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uispec4j.*;
import static org.junit.Assert.*;

public class SAMPConnectionTest extends AbstractUISpecTest {

    private Logger logger = Logger.getLogger(IrisUISpecAdapter.class.getName());
    private ExecutorService executor;
    private Window mainWindow;

    @Rule
    public IrisAppResource irisApp = new IrisAppResource();

    @Before
    public void setUp() throws Exception {
        executor = Executors.newSingleThreadExecutor();
        IrisUISpecAdapter adapter = irisApp.getAdapter();
        mainWindow = adapter.getMainWindow();
        assertTrue(mainWindow.titleEquals("Iris").isTrue());
        this.RETRY = 60;
    }

    @After
    public void tearDown() throws Exception {
        executor.shutdown();
    }

    // When launched, the Iris Application should launch a SAMP Hub and be connected
    @Test
    public void testBasic() throws Exception {
        checkLabel(mainWindow, "SAMP", true);

        // avoid autorunhub to start before we can check that Iris disconnected.
        logger.log(Level.INFO, "disabling autorunhub");
        switchAutoRunHub();

        // If we stop the hub a new one should come up
        logger.log(Level.INFO, "stopping hub");
        final Window hub = irisApp.getAdapter().getSamphub();

        // why oh why? Should we rather be using windowinterceptor or something?
        executor.submit(new Runnable() {
            @Override
            public void run() {
                hub.getMenuBar().getMenu("File").getSubMenu("Stop Hub").click();
                logger.log(Level.INFO, "stopped hub");
            }
        });

        // check that Iris disconnects
        checkLabel(mainWindow, "SAMP", false);

        logger.log(Level.INFO, "enabling autorunhub");
        switchAutoRunHub();

        // check that a new hub comes up and Iris reconnects
        checkLabel(mainWindow, "SAMP", true);
    }

    // If Autorunhub is turned off, the Iris Application should not reconnect
    @Test
    public void testNoAutoRunHub() throws Exception {
        logger.log(Level.INFO, "disabling autorunhub");
        switchAutoRunHub();

        logger.log(Level.INFO, "stopping hub");
        final Window hub = irisApp.getAdapter().getSamphub();

        // why oh why? Should we rather be using windowinterceptor or something?
        executor.submit(new Runnable() {
            @Override
            public void run() {
                hub.getMenuBar().getMenu("File").getSubMenu("Stop Hub").click();
                logger.log(Level.INFO, "stopped hub");
            }
        });

        logger.log(Level.INFO, "making sure Iris disconnects");
        checkLabel(mainWindow, "SAMP", false);

        // But Iris should reconnect if a new hub is started
        logger.log(Level.INFO, "starting a new hub");
        Hub newHub = Hub.runHub(HubServiceMode.NO_GUI);
        logger.log(Level.INFO, "making sure Iris is connected within few seconds");
        checkLabel(mainWindow, "SAMP", true);
        logger.log(Level.INFO, "shutting down the new hub");
        newHub.shutdown();
    }

    private void switchAutoRunHub() {
        logger.log(Level.INFO, "switching autorunhub");
        mainWindow.getMenuBar().getMenu("Interop").getSubMenu("Run Hub Automatically").click();
    }
}
