package cfa.vo.iris.test;

import org.junit.rules.ExternalResource;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IrisAppResource extends ExternalResource {
    private IrisUISpecAdapter adapter;
    private boolean withSamp;
    private Logger logger = Logger.getLogger(IrisAppResource.class.getName());

    public IrisAppResource() {
        this(true);
    }

    public IrisAppResource(boolean withSamp) {
        this.withSamp = withSamp;
    }

    @Override
    public void before() {
        logger.log(Level.INFO, "setup, instantiating adapter");
        adapter = new IrisUISpecAdapter(withSamp);
        logger.log(Level.INFO, "setup, verifying windows");
        assertTrue(adapter.getMainWindow().isVisible().isTrue());
        assertTrue(adapter.getSamphub().isVisible().isTrue());
    }

    @Override
    public void after() {
        logger.log(Level.INFO, "tearDown, exiting app");
        adapter.getIrisApp().exitApp(0);
        logger.log(Level.INFO, "tearDown, verifying windows are gone");
        assertFalse(adapter.getSamphub().isVisible().isTrue());
        assertFalse(adapter.getMainWindow().isVisible().isTrue());
        adapter = null;
    }

    public IrisUISpecAdapter getAdapter() {
        return adapter;
    }
}
