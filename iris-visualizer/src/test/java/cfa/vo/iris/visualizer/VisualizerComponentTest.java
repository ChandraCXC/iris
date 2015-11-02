package cfa.vo.iris.visualizer;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.test.unit.AbstractIrisGUITest;

public class VisualizerComponentTest extends AbstractIrisGUITest {
    
    private VisualizerComponent comp;
    private String windowName;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        comp = new VisualizerComponent();
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
        
        org.uispec4j.Button mbButton = desktop.getWindow(windowName).getButton("Metadata");
        mbButton.click();
        
        desktop.containsWindow("Metadata Browser");
    }
}
