package cfa.vo.iris.visualizer;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.test.unit.AbstractIrisGUITest;

public class FittingToolComponentTest extends AbstractIrisGUITest {
    
    private FittingToolComponent comp;
    private String windowName;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        comp = new FittingToolComponent();
        windowName = comp.getName();
        app.setComponents(Arrays.asList((IrisComponent) comp));
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
