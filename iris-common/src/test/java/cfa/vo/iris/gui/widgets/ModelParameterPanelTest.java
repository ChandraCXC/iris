package cfa.vo.iris.gui.widgets;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.sedlib.Param;
import cfa.vo.sherpa.models.Parameter;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.CheckBox;
import org.uispec4j.ComboBox;
import org.uispec4j.Panel;
import org.uispec4j.TextBox;
import org.uispec4j.assertion.UISpecAssert;

import static org.junit.Assert.*;

public class ModelParameterPanelTest {
    private Parameter parameter;
    private ModelParameterPanel panel;
    private TextBox name;
    private TextBox min;
    private TextBox max;
    private TextBox val;
    private CheckBox frozen;
    private Panel p;


    @Before
    public void setUp() throws Exception {
        parameter = SAMPFactory.get(Parameter.class);
        parameter.setName("name");
        parameter.setVal(1.0);
        parameter.setMin(-3.0);
        parameter.setMax(3.0);
        parameter.setFrozen(1);

        panel = new ModelParameterPanel();
        p = new Panel(panel);
        name = p.getTextBox("Par Name");
        val = p.getTextBox("Par Val");
        min = p.getTextBox("Par Min");
        max = p.getTextBox("Par Max");
        frozen = p.getCheckBox("Par Frozen");
    }

    @Test
    public void testSetEditable() throws Exception {
        UISpecAssert.not(name.isEditable());
        UISpecAssert.not(val.isEditable());
        UISpecAssert.not(min.isEditable());
        UISpecAssert.not(max.isEditable());
        UISpecAssert.not(frozen.isEnabled());

        panel.setEditable(true);

        UISpecAssert.not(name.isEditable());
        val.isEditable().check();
        min.isEditable().check();
        max.isEditable().check();
        frozen.isEnabled().check();
    }

    @Test
    public void testSetParameter() throws Exception {
        name.textEquals("No Parameter Selected").check();
        val.textIsEmpty().check();
        min.textIsEmpty().check();
        max.textIsEmpty().check();
        UISpecAssert.not(frozen.isSelected());

        panel.setParameter(parameter);

        name.textEquals("name").check();
        min.textEquals("-3.0").check();
        max.textEquals("3.0").check();
        frozen.isSelected().check();
    }

    @Test
    public void testBinding() throws Exception {
        assertNull(panel.getParameter());
        panel.setParameter(parameter);

        Parameter observed = panel.getParameter();
        assertNotNull(observed);

        assertEquals("name", observed.getName());
        assertEquals(1.0, observed.getVal(), Double.MIN_VALUE);
        assertEquals(-3.0, observed.getMin(), Double.MIN_VALUE);
        assertEquals(3.0, observed.getMax(), Double.MIN_VALUE);
        assertEquals(1, (long)observed.getFrozen());

        panel.setEditable(true);

        min.setText("-1");
        max.setText("1");
        val.setText("0.5");
        frozen.unselect();

        assertEquals(0.5, observed.getVal(), Double.MIN_VALUE);
        assertEquals(-1.0, observed.getMin(), Double.MIN_VALUE);
        assertEquals(1.0, observed.getMax(), Double.MIN_VALUE);
        assertEquals(0, (long)observed.getFrozen());
    }
}