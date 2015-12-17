package cfa.vo.iris.test;

import org.junit.rules.ExternalResource;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IrisAppResource extends ExternalResource {
    private IrisUISpecAdapter adapter;
    private boolean withSampHub;
    private Logger logger = Logger.getLogger(IrisAppResource.class.getName());

    public IrisAppResource() {
        this(true);
    }

    public IrisAppResource(boolean withSampHub) {
        this.withSampHub = withSampHub;
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
