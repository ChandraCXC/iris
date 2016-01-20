package cfa.vo.iris.test.unit;

import cfa.vo.iris.utils.LabelFinder;
import org.junit.After;
import org.junit.Before;
import org.uispec4j.UISpec4J;
import org.uispec4j.Window;
import org.uispec4j.interception.toolkit.UISpecDisplay;

import javax.swing.*;

import static org.junit.Assert.*;

public abstract class AbstractUISpecTest {
    static {
        UISpec4J.init();
    }

    private final String CONNECTED = "connected";
    private final String DISCONNECTED = "disconnected";
    private final String STATUS = " status: ";
    protected int RETRY = 20;
    protected int STEP = 500;

    /**
     * Initializes the resources needed by the test case.<br>
     */
    @Before
    public final void uispecBefore() throws Exception {
        UISpecDisplay.instance().reset();
    }

    /**
     * Checks whether an unexpected exception had occurred, and releases the test resources.
     */
    @After
    public final void uispecAfter() throws Exception {
        UISpecDisplay.instance().rethrowIfNeeded();
        UISpecDisplay.instance().reset();
    }

    protected void checkLabel(Window window, String name, boolean connected) {
        String right = connected ? CONNECTED : DISCONNECTED;
        String wrong = connected? DISCONNECTED : CONNECTED;
        boolean shouldFail = true;
        for (int i=0; i<RETRY; i++) {
           try {
               JLabel label = new LabelFinder(name + STATUS + right).find(window);
               assertNotNull(label);
               label = new LabelFinder(name+ STATUS + wrong).find(window);
               assertNull(label);
               shouldFail = false;
               break;
           } catch (AssertionError ex) {
               try {
                   Thread.sleep(STEP);
               } catch (InterruptedException e) {
                   fail();
               }
           }
        }
        assertFalse(shouldFail);
    }
}
