package cfa.vo.gui;

import cfa.vo.iris.test.IrisAppResource;
import cfa.vo.iris.test.IrisUISpecAdapter;
import cfa.vo.iris.test.unit.AbstractUISpecTest;
import org.astrogrid.samp.hub.Hub;
import org.astrogrid.samp.hub.HubServiceMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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
    }

    @After
    public void tearDown() throws Exception {
        executor.shutdown();
    }

    // When launched, the Iris Application should launch a SAMP Hub and be connected
    @Test
    public void testBasic() throws Exception {
        checkLabel(mainWindow, "SAMP", true);

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

        // check that a new hub comes up and Iris reconnects
        checkLabel(mainWindow, "SAMP", false);
    }

    // If Autorunhub is turned off, the Iris Application should not reconnect
    @Test
    public void testNoAutoRunHub() throws Exception {

        logger.log(Level.INFO, "stopping autorunhub");
        mainWindow.getMenuBar().getMenu("Interop").getSubMenu("Run Hub Automatically").click();

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
}
