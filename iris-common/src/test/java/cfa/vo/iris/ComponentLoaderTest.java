package cfa.vo.iris;

import static org.junit.Assert.*;

import java.net.URL;
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
        List<IrisComponent> components = loader.instantiateComponents();
        
        assertEquals(1, components.size());
        assertTrue(components.get(0) instanceof TestIrisComponent);
        
        assertEquals(1, loader.failures.size());
        assertEquals(BrokenTestIrisComponent.class.getName(), loader.failures.get(0));
    }
    
    @Test
    public void testFailedIORead() {
        ComponentLoader loader = new ComponentLoader(null);
        List<IrisComponent> components = loader.instantiateComponents();
        
        assertEquals(0, components.size());
    }
    
    @Test
    public void testSetOverrideComponentsFile() {
        System.setProperty(ComponentLoader.COMP_OVERRIDE_SYS_PROP, testUrl.getPath());
        
        ComponentLoader loader = new ComponentLoader();
        List<IrisComponent> components = loader.instantiateComponents();
        
        assertEquals(1, components.size());
        assertTrue(components.get(0) instanceof TestIrisComponent);
        
        System.clearProperty(ComponentLoader.COMP_OVERRIDE_SYS_PROP);
    }
    
    @Test
    public void testSetOverrideInvalidePath() {
        System.setProperty(ComponentLoader.COMP_OVERRIDE_SYS_PROP, "invalid");
        
        ComponentLoader loader = new ComponentLoader();
        List<IrisComponent> components = loader.instantiateComponents();
        
        assertEquals(0, components.size());
        
        System.clearProperty(ComponentLoader.COMP_OVERRIDE_SYS_PROP);
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
