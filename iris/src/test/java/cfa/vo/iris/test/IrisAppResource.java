package cfa.vo.iris.test;

import org.junit.rules.ExternalResource;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IrisAppResource extends ExternalResource {
    private IrisUISpecAdapter adapter;
    private boolean withSampHub;
    private boolean withAutorunHub;
    private Logger logger = Logger.getLogger(IrisAppResource.class.getName());

    /**
     * By default, the resource is created with withSampHub and withAutoRunHub set to true;
     */
    public IrisAppResource() {
        this(true, true);
    }

    /**
     * Instantiate a new Iris App Resource.
     *
     * The resource takes care of instantiating the relevant adapter, and it performs preliminary checks that
     * the resource is ready to be used, or otherwise it fails the test.
     *
     * If one does not want the Samp Hub window to be intercepted and exposed by this instance, then
     * set withSampHub to false. This is particularly useful (actually needed) for Integration Tests,
     * since the Hub is started by maven and the interceptor would timeout while trying to intercept a Hub
     * window that would never show up.
     *
     * Also, one can disable the AutoRunHub feature by passing false as withAutoRunHub. This is useful
     * if one wants to make sure that new hubs are not spawned automatically when testing the outcome of shutting
     * down the hub.
     *
     * @param withSampHub if the SAMP hub needs to be intercepted.
     * @param withAutoRunHub if the autoRunHub feature needs to be enabled.
     */
    public IrisAppResource(boolean withSampHub, boolean withAutoRunHub) {
        this.withSampHub = withSampHub;
        this.withAutorunHub = withAutoRunHub;
    }

    @Override
    public void before() {
        logger.log(Level.INFO, "setup, instantiating adapter");
        adapter = new IrisUISpecAdapter(withSampHub);
        logger.log(Level.INFO, "setup, verifying windows");
        assertTrue(adapter.getMainWindow().isVisible().isTrue());
        if (withSampHub) {
            assertTrue(adapter.getSamphub().isVisible().isTrue());
        }
        if (!withAutorunHub) {
            adapter.getMainWindow().getMenuBar().getMenu("Interop").getSubMenu("Run Hub Automatically").click();
        }
    }

    @Override
    public void after() {
        logger.log(Level.INFO, "tearDown, exiting app");
        adapter.getIrisApp().exitApp(0);
        logger.log(Level.INFO, "tearDown, verifying windows are gone");
        if (withSampHub) {
            assertFalse(adapter.getSamphub().isVisible().isTrue());
        }
        assertFalse(adapter.getMainWindow().isVisible().isTrue());
        adapter = null;
    }

    public IrisUISpecAdapter getAdapter() {
        return adapter;
    }
}
