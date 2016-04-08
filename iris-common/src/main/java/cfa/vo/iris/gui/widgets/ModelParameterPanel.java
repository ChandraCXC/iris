package cfa.vo.iris.gui.widgets;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.sherpa.models.Parameter;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

public class ModelParameterPanel extends JPanel {
    private JTextField val;
    private JTextField min;
    private JTextField max;
    private JCheckBox frozen;
    private JTextField name;
    private Set<JTextField> editableSet;

    public ModelParameterPanel() {
        super(new SpringLayout());
        name = addTextField("Name");
        name.setEnabled(false);
        name.setEditable(false);
        name.setName("Par Name");
        val = addTextField("Val");
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

        setParameter(null);
        GUIUtils.makeCompactGrid(this, 5, 2, 6, 6, 6, 6);
    }

    public void setEditable(boolean editable) {
        for (JTextField comp : editableSet) {
            comp.setEditable(editable);
        }
        frozen.setEnabled(editable);
    }

    public void setParameter(Parameter par) {
        if (par == null) {
            par = SAMPFactory.get(Parameter.class);
        }
        val.setText(getText(par.getVal()));
        min.setText(getText(par.getMin()));
        max.setText(getText(par.getMax()));
        frozen.setSelected(getBoolean(par.getFrozen()));
        name.setText(par.getName() != null? par.getName() : "No Parameter Selected");
    }

    private String getText(Object val) {
        return String.format("%s", val);
    }

    private boolean getBoolean(Integer val) {
        return val != null && val != 0;
    }

    private JTextField addTextField(String name) {
        JLabel l = new JLabel(name, JLabel.TRAILING);
        add(l);
        JTextField textField = new JTextField();
        l.setLabelFor(textField);
        textField.setName(name);
        textField.setEditable(false);
        add(textField);
        return textField;
    }
}
