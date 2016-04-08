package cfa.vo.iris.gui.widgets;

import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.sherpa.models.Parameter;
import org.jdesktop.beansbinding.*;

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
    private BindingGroup group;

    private Parameter parameter;

    public final static String PROP_PARAMETER = "parameter";

    public ModelParameterPanel() {
        super(new SpringLayout());
        initComponents();
        initBindings();
    }

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

    private void initBindings() {
        group = new BindingGroup();
        createBinding("name", name, "text");
        createBinding("val", val, "text");
        createBinding("min", min, "text");
        createBinding("max", max, "text");
        createBinding("frozen", frozen, "selected");
        Binding name = group.getBinding("name");
        name.setSourceUnreadableValue("No Parameter Selected");
        group.bind();
    }

    private void createBinding(String propertyName, JComponent comp, String compPropertyName) {
        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create(String.format("${parameter.%s}", propertyName)),
                comp,
                BeanProperty.create(compPropertyName), propertyName);

        group.addBinding(binding);
    }

    private void initComponents() {
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

        GUIUtils.makeCompactGrid(this, 5, 2, 6, 6, 6, 6);
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
