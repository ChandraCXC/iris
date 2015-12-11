package cfa.vo.iris.test.unit;

import org.junit.After;
import org.junit.Before;
import org.uispec4j.UISpec4J;
import org.uispec4j.interception.toolkit.UISpecDisplay;

public abstract class AbstractUISpecTest {
    static {
        UISpec4J.init();
    }

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
}
