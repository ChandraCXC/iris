package cfa.vo.iris.gui.widgets;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.sherpa.models.Parameter;

import javax.swing.*;

public class ModelParameterPanel extends JPanel {
    private JTextField val;
    private JTextField min;
    private JTextField max;
    private JTextField frozen;
    private JTextField name;

    public ModelParameterPanel() {
        super(new SpringLayout());
        name = addTextField("Name");
        name.setEnabled(false);
        name.setEditable(false);
        name.setName("Par Name");
        val = addTextField("Val");
        min = addTextField("Min");
        max = addTextField("Max");
        frozen = addTextField("Frozen");
        setParameter(null);
        GUIUtils.makeCompactGrid(this, 5, 2, 6, 6, 6, 6);
    }

    public void setParameter(Parameter par) {
        if (par == null) {
            par = SAMPFactory.get(Parameter.class);
        }
        val.setText(getText(par.getVal()));
        min.setText(getText(par.getMin()));
        max.setText(getText(par.getMax()));
        frozen.setText(getText(getBoolean(par.getFrozen())));
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
