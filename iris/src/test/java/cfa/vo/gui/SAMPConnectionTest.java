package cfa.vo.gui;

import cfa.vo.iris.test.IrisUISpecAdapter;
import org.junit.After;
import org.junit.Before;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.finder.ComponentMatcher;

import javax.swing.*;
import java.awt.*;

public class SAMPConnectionTest extends UISpecTestCase {
    IrisUISpecAdapter adapter;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        adapter = new IrisUISpecAdapter();
        setAdapter(adapter);
        assertTrue(adapter.getMainWindow().isVisible());
        assertTrue(adapter.getSamphub().isVisible());
    }

    @After
    public void tearDown() throws Exception {
        adapter.getIrisApp().exitApp(0);
        assertFalse(adapter.getSamphub().isVisible());
        assertFalse(adapter.getMainWindow().isVisible());
        super.tearDown();
    }

    public void testBasic() throws Exception {
        assertTrue(adapter.getMainWindow().titleEquals("Iris"));
        boolean found = false;
        for (int i=0; i<30; i++) {
            JLabel label = (JLabel) adapter.getMainWindow().findSwingComponent(new LabelFinder("SAMP status: connected"));
            if(label != null) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    private class LabelFinder implements ComponentMatcher {
        private String labelString;

        public LabelFinder(String string) {
            this.labelString = string;
        }

        @Override
        public boolean matches(Component component) {
            if(component instanceof JLabel) {
                JLabel label = (JLabel) component;
                return labelString.equals(label.getText());
            }
            return false;
        }
    }
}
