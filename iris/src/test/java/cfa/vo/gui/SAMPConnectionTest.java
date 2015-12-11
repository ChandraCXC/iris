package cfa.vo.gui;

import cfa.vo.iris.test.IrisUISpecAdapter;
import cfa.vo.iris.test.unit.AbstractUISpecTest;
import org.astrogrid.samp.hub.Hub;
import org.astrogrid.samp.hub.HubServiceMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.UISpec4J;
import org.uispec4j.finder.ComponentMatcher;
import javax.swing.JLabel;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;

public class SAMPConnectionTest extends AbstractUISpecTest {

    private Logger logger = Logger.getLogger(IrisUISpecAdapter.class.getName());
    private ExecutorService executor;
    IrisUISpecAdapter adapter;

    @Before
    public void setUp() throws Exception {
        logger.log(Level.INFO, "setup, instantiating adapter");
        adapter = new IrisUISpecAdapter();
        logger.log(Level.INFO, "setup, verifying windows");
        assertTrue(adapter.getMainWindow().isVisible().isTrue());
        assertTrue(adapter.getSamphub().isVisible().isTrue());
        executor = Executors.newSingleThreadExecutor();
    }

    @After
    public void tearDown() throws Exception {
        executor.shutdown();
        logger.log(Level.INFO, "tearDown, exiting app");
        adapter.getIrisApp().exitApp(0);
        logger.log(Level.INFO, "tearDown, verifying windows are gone");
        assertFalse(adapter.getSamphub().isVisible().isTrue());
        assertFalse(adapter.getMainWindow().isVisible().isTrue());
    }

    // When launched, the Iris Application should launch a SAMP Hub and be connected
    @Test
    public void testBasic() throws Exception {

        assertTrue(adapter.getMainWindow().titleEquals("Iris").isTrue());
        boolean found = false;
        for (int i=0; i<30; i++) {
            JLabel label = (JLabel) adapter.getMainWindow().findSwingComponent(new LabelFinder("SAMP status: connected"));
            if(label != null) {
                found = true;
                break;
            }
            logger.log(Level.INFO, "sleeping for half a second");
            Thread.sleep(500);
        }
        assertTrue(found);
    }

    // If Autorunhub is turned off, the Iris Application should not reconnect
    @Test
    public void testNoAutoRunHub() throws Exception {
        org.uispec4j.Window window = adapter.getMainWindow();
        assertTrue(window.titleEquals("Iris").isTrue());

        logger.log(Level.INFO, "stopping autorunhub");
        window.getMenuBar().getMenu("Interop").getSubMenu("Run Hub Automatically").click();

        logger.log(Level.INFO, "stopping hub");
        final org.uispec4j.Window hub = adapter.getSamphub();

        // why oh why? Should we rather be using windowinterceptor or something?
        executor.submit(new Runnable() {
            @Override
            public void run() {
                hub.getMenuBar().getMenu("File").getSubMenu("Stop Hub").click();
                logger.log(Level.INFO, "stopped hub");
            }
        });

        logger.log(Level.INFO, "making sure Iris is not connected in 2 seconds");
        Thread.sleep(2000);
        JLabel label = (JLabel) window.findSwingComponent(new LabelFinder("SAMP status: connected"));
        assertNull(label);

        // But Iris should reconnect if a new hub is started
        logger.log(Level.INFO, "starting a new hub");
        Hub newHub = Hub.runHub(HubServiceMode.NO_GUI);
        logger.log(Level.INFO, "making sure Iris is connected within few seconds");
        boolean found = false;
        for (int i=0; i<30; i++) {
            label = (JLabel) adapter.getMainWindow().findSwingComponent(new LabelFinder("SAMP status: connected"));
            if(label != null) {
                found = true;
                break;
            }
            logger.log(Level.INFO, "sleeping for half a second");
            Thread.sleep(500);
        }
        logger.log(Level.INFO, "shutting down the new hub");
        newHub.shutdown();
        assertTrue(found);
    }

    private class LabelFinder implements ComponentMatcher {
        private String labelString;

        public LabelFinder(String string) {
            this.labelString = string;
        }

        @Override
        public boolean matches(Component component) {
            if(component instanceof JLabel) {
                JLabel label = (JLabel) component;
                if(labelString.equals(label.getText())) {
                    return true;
                }
            }
            return false;
        }
    }
}
