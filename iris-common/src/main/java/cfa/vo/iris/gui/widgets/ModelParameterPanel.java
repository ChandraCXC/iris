package cfa.vo.iris.gui.widgets;

import cfa.vo.sherpa.models.Parameter;
import org.jdesktop.beansbinding.*;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

public class ModelParameterPanel extends AbstractGridPanel {
    private JTextField val;
    private JTextField min;
    private JTextField max;
    private JCheckBox frozen;
    private JTextField name;
    private Set<JTextField> editableSet;

    private Parameter parameter;

    public final static String PROP_PARAMETER = "parameter";

    public void setEditable(boolean editable) {
        for (JTextField comp : editableSet) {
            comp.setEditable(editable);
        }
        frozen.setEnabled(editable);
    }

    public void setParameter(Parameter par) {
        Parameter oldPar = this.parameter;
        this.parameter = par;
        firePropertyChange(PROP_PARAMETER, oldPar, par);
    }

    public Parameter getParameter() {
        return parameter;
    }

    @Override
    public void updateUI() {
        firePropertyChange(PROP_PARAMETER, null, parameter);
        super.updateUI();
    }

    @Override
    protected void initBindings() {
        createBinding("name", name, "text");
        createBinding("val", val, "text");
        createBinding("min", min, "text");
        createBinding("max", max, "text");
        createBinding("frozen", frozen, "selected");
        group.getBinding("name").setSourceUnreadableValue("No Parameter Selected");
        group.getBinding("val").setSourceUnreadableValue("");
        group.getBinding("min").setSourceUnreadableValue("");
        group.getBinding("max").setSourceUnreadableValue("");
        group.getBinding("frozen").setSourceUnreadableValue(false);
    }

    @Override
    protected int getRows() {
        return 5;
    }

    @Override
    protected int getCols() {
        return 2;
    }

    @Override
    protected String getBindingRoot() {
        return "parameter";
    }

    @Override
    protected void initComponents() {
        name = addTextField("Name");
        name.setEnabled(false);
        name.setEditable(false);
        name.setName("Par Name");
        val = addTextField("Value");
        val.setName("Par Val");
        min = addTextField("Min");
        min.setName("Par Min");
        max = addTextField("Max");
        max.setName("Par Max");
        add(new JLabel("Frozen"));
        frozen = new JCheckBox();
        frozen.setName("Par Frozen");
        add(frozen);

        editableSet = new HashSet<>();
        editableSet.add(val);
        editableSet.add(min);
        editableSet.add(max);
    }

}
