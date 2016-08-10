package cfa.vo.iris.gui.widgets;

import cfa.vo.iris.gui.GUIUtils;
import org.jdesktop.beansbinding.*;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractGridPanel extends JPanel {
    protected BindingGroup group;

    public AbstractGridPanel() {
        super(new SpringLayout());
        group = new BindingGroup();
        initComponents();
        makeGrid(getRows(), getCols());
        initBindings();
        group.bind();
        setPreferredSize(null);
    }

    protected void createBinding(String propertyName, JComponent comp, String compPropertyName) {
        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create(String.format("${%s.%s}", getBindingRoot(), propertyName)),
                comp,
                BeanProperty.create(compPropertyName), propertyName);

        group.addBinding(binding);
    }

    protected JTextField addTextField(String name) {
        JLabel l = new JLabel(name, JLabel.TRAILING);
        add(l);
        JTextField textField = new JTextField();
        l.setLabelFor(textField);
        textField.setName(name);
        textField.setEditable(false);
        add(textField);
        return textField;
    }

    protected void makeGrid(int rows, int cols) {
        GUIUtils.makeCompactGrid(this, rows, cols, 6, 6, 6, 6);
    }

    protected abstract void initComponents();

    protected abstract void initBindings();

    protected abstract int getRows();

    protected abstract int getCols();

    protected abstract String getBindingRoot();
}
