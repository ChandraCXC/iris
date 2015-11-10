package cfa.vo.iris.visualizer;

import cfa.vo.iris.test.unit.AbstractComponentGUITest;
import org.junit.Before;
import org.junit.Test;
import cfa.vo.iris.IrisComponent;

public class FittingToolComponentTest extends AbstractComponentGUITest {

    private FittingToolComponent comp = new FittingToolComponent();
    private String windowName;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        windowName = comp.getName();
    }

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
    }
}
