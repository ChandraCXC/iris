package cfa.vo.iris;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cfa.vo.iris.sdk.AbstractIrisComponent;

public class ComponentLoaderTest {

    private URL testUrl;

    @Before
    public void setUp() {
        testUrl = this.getClass().getResource("/test_components");
    }

    @Test
    public void testLoadComponents() {
        ComponentLoader loader = new ComponentLoader(testUrl);
        List<IrisComponent> components = loader.getComponents();

        assertEquals(1, components.size());
        assertTrue(components.get(0) instanceof TestIrisComponent);

        assertEquals(1, loader.failures.size());
        assertEquals(BrokenTestIrisComponent.class.getName(), loader.failures.get(0));
    }

    @Test
    public void testFailedIORead() throws Exception {
        ComponentLoader loader = new ComponentLoader(new ArrayList());
        List<IrisComponent> components = loader.getComponents();

        assertEquals(0, components.size());
    }

    // Simulated components for testing only.
    public static class TestIrisComponent extends AbstractIrisComponent {
        @Override
        public String getName() {
            return null;
        }
        @Override
        public String getDescription() {
            return null;
        }
        @Override
        public List<IMenuItem> getMenus() {
            return null;
        }
    }
    public static class BrokenTestIrisComponent extends AbstractIrisComponent {
        public BrokenTestIrisComponent() {
            super();
            throw new RuntimeException("I don't work");
        }
        @Override
        public String getName() {
            return null;
        }
        @Override
        public String getDescription() {
            return null;
        }
        @Override
        public List<IMenuItem> getMenus() {
            return null;
        }
    }
}
