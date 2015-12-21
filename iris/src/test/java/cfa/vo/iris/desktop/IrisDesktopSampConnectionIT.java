package cfa.vo.iris.desktop;

import cfa.vo.iris.test.IrisAppResource;
import cfa.vo.iris.test.unit.AbstractUISpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.uispec4j.Window;

public class IrisDesktopSampConnectionIT extends AbstractUISpecTest {
    @Rule
    public IrisAppResource appResource = new IrisAppResource(false, false);
    private Window window;

    private final String SAMP = "SAMP";
    private final String SHERPA = "Sherpa";

    @Before
    public void setUp() throws Exception {
        window = appResource.getAdapter().getMainWindow();
        this.RETRY = 60;
    }

    @Test
    public void testSampConnection() throws Exception {
        checkLabel(window, SAMP, true);
    }

    @Test
    public void testSherpaConnection() throws Exception {
        checkLabel(window, SHERPA, true);
    }

}
