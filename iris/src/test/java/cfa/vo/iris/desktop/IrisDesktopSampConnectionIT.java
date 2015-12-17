package cfa.vo.iris.desktop;

import cfa.vo.iris.test.IrisAppResource;
import cfa.vo.iris.test.unit.AbstractUISpecTest;
import cfa.vo.iris.utils.LabelFinder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.uispec4j.Window;
import javax.swing.*;

import static org.junit.Assert.*;

public class IrisDesktopSampConnectionIT extends AbstractUISpecTest {
    @Rule
    public IrisAppResource appResource = new IrisAppResource(false);
    private Window window;

    private final String SAMP = "SAMP";
    private final String SHERPA = "Sherpa";
    private final String CONNECTED = "connected";
    private final String DISCONNECTED = "disconnected";
    private final String STATUS = " status: ";
    private final int RETRY = 20;
    private final int STEP = 500;

    @Before
    public void setUp() throws Exception {
        window = appResource.getAdapter().getMainWindow();
    }

    @Test
    public void testSampConnection() throws Exception {
        checkLabel(SAMP, true);
    }

    @Test
    public void testSherpaConnection() throws Exception {
        checkLabel(SHERPA, true);
    }

    private void checkLabel(String name, boolean connected) {
        String right = connected ? CONNECTED : DISCONNECTED;
        String wrong = connected? DISCONNECTED : CONNECTED;
        for (int i=0; i<RETRY; i++) {
           try {
               JLabel label = new LabelFinder(name + STATUS + right).find(window);
               assertNotNull(label);
               label = new LabelFinder(name+ STATUS + wrong).find(window);
               assertNull(label);
           } catch (AssertionError ex) {
               try {
                   Thread.sleep(STEP);
               } catch (InterruptedException e) {
                   fail();
               }
           }
        }
    }

}
