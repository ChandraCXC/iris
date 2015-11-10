package cfa.vo.iris.visualizer;

import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import org.junit.Before;
import org.junit.Test;
import cfa.vo.iris.IrisComponent;

public class VisualizerComponentTest extends AbstractComponentGUITest {
    
    private VisualizerComponent comp = new VisualizerComponent();
    private String windowName;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        windowName = comp.getName();
    }

    @Override
    protected IrisComponent getComponent() {
        return comp;
    }

    @Test
    public void testVisualizerStub() throws Exception {
        
        window.getMenuBar()
            .getMenu("Tools")
            .getSubMenu(windowName)
            .getSubMenu(windowName)
            .click();
        
        desktop.containsWindow(windowName);
        
        org.uispec4j.Button mbButton = desktop.getWindow(windowName).getButton("Metadata");
        mbButton.click();
        
        desktop.containsWindow("Metadata Browser");
    }
}
