/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJInternalFrame.java
 *
 * Created on Nov 14, 2014, 12:56:11 PM
 */
package cfa.vo.iris.gui.widgets;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sherpa.IFitConfiguration;
import cfa.vo.sherpa.models.CompositeModel;
import cfa.vo.sherpa.models.CompositeModelTreeModel;
import cfa.vo.sherpa.models.Parameter;
import cfa.vo.sherpa.models.UserModel;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ModelViewerPanel extends javax.swing.JPanel implements SedListener {

    private IFitConfiguration fit;
    
    private final String[] values = new String[]{"Val", "Min", "Max", "Frozen"};

    /**
     * Creates new form NewJInternalFrame
     */
    public ModelViewerPanel(ExtSed sed, IFitConfiguration fitConfig) {
        initComponents();
        setSed(sed);
        setFitConfiguration(fitConfig);
        SedEvent.getInstance().add(this);
        modelsTree.setPreferredSize(null);
        modelsTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreePath selPath = modelsTree.getPathForLocation(e.getX(), e.getY());
                if (selPath != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                    if (node.isLeaf()) {
                        Parameter par = (Parameter) node.getUserObject();
                        setSelectedParameter(par);
                    }
                }

            }
        });
    }

    public IFitConfiguration getFitConfiguration() {
        return fit;
    }
    
    private java.util.List<UserModel> userModels;
    
    private void setFitConfiguration(IFitConfiguration fit) {
        try {
            this.fit = fit;
            CompositeModel m = fit.getModel();
            userModels = fit.getUserModelList();
            setModel(m);
            setExpression(m.getName());
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, null, ex);
            setModel(SAMPFactory.get(CompositeModel.class));
            setExpression("No Model");
            setSelectedParameter(null);
        }
    }

    private String getValue(Parameter par, String name) {
        try {
            Method m = Parameter.class.getMethod("get" + name);
            String typeStr = "%s";
            Object value = m.invoke(par);
            if (name.equals("Frozen")) {
                Integer v = (Integer) value;
                value = v == 0 ? false : true;
            }
            return String.format(typeStr, value);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            return "";
        }
    }
    
    private Parameter selectedParameter;
    public static final String PROP_SELECTEDPARAMETER = "selectedParameter";

    /**
     * Get the value of selectedParameter
     *
     * @return the value of selectedParameter
     */
    public Parameter getSelectedParameter() {
        return selectedParameter;
    }

    /**
     * Set the value of selectedParameter
     *
     * @param selectedParameter new value of selectedParameter
     */
    public void setSelectedParameter(final Parameter selectedParameter) {
        Parameter oldSelectedParameter = this.selectedParameter;
        this.selectedParameter = selectedParameter;
        firePropertyChange(PROP_SELECTEDPARAMETER, oldSelectedParameter, selectedParameter);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JPanel panel;

                if (selectedParameter != null) {

                    panel = new JPanel(new SpringLayout());


                    for (String name : values) {
                        JLabel l = new JLabel(name, JLabel.TRAILING);
                        panel.add(l);
                        JTextField textField = new JTextField();
                        l.setLabelFor(textField);
                        textField.setName(name);
                        textField.setEditable(false);
                        textField.setText(getValue(selectedParameter, name));
                        panel.add(textField);
                    }


                    GUIUtils.makeCompactGrid(panel, values.length, 2, 6, 6, 6, 6);

                } else {
                    panel = new JPanel(new GridLayout());
                    panel.add(new JLabel("No Parameter Selected."));
                }

                jSplitPane1.setBottomComponent(panel);
            }

        });
    }

    private ExtSed sed;

    public static final String PROP_SED = "sed";

    /**
     * Get the value of sed
     *
     * @return the value of sed
     */
    public ExtSed getSed() {
        return sed;
    }

    /**
     * Set the value of sed
     *
     * @param sed new value of sed
     */
    private void setSed(ExtSed sed) {
        ExtSed oldSed = this.sed;
        this.sed = sed;
        firePropertyChange(PROP_SED, oldSed, sed);
    }


    private CompositeModel model;
    public static final String PROP_MODEL = "model";

    /**
     * Get the value of model
     *
     * @return the value of model
     */
    public CompositeModel getModel() {
        return model;
    }

    /**
     * Set the value of model
     *
     * @param model new value of model
     */
    public void setModel(CompositeModel model) {
        CompositeModel oldModel = this.model;
        this.model = model;
        firePropertyChange(PROP_MODEL, oldModel, model);
        setTreeModel(new CompositeModelTreeModel(model, userModels));
    }
    
    private CompositeModelTreeModel treeModel;
    public static final String PROP_TREEMODEL = "treeModel";

    /**
     * Get the value of treeModel
     *
     * @return the value of treeModel
     */
    public CompositeModelTreeModel getTreeModel() {
        return treeModel;
    }

    /**
     * Set the value of treeModel
     *
     * @param treeModel new value of treeModel
     */
    public void setTreeModel(CompositeModelTreeModel treeModel) {
        CompositeModelTreeModel oldTreeModel = this.treeModel;
        this.treeModel = treeModel;
        firePropertyChange(PROP_TREEMODEL, oldTreeModel, treeModel);
    }


    private String expression;
    public static final String PROP_EXPRESSION = "expression";

    /**
     * Get the value of expression
     *
     * @return the value of expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Set the value of expression
     *
     * @param expression new value of expression
     */
    public void setExpression(String expression) {
        String oldExpression = this.expression;
        this.expression = expression;
        firePropertyChange(PROP_EXPRESSION, oldExpression, expression);
    }

    @Override
    public void process(ExtSed source, SedCommand payload) {
        if (SedCommand.SELECTED.equals(payload) || SedCommand.CHANGED.equals(payload) && source.equals(sed)) {
            IFitConfiguration fitConf = (IFitConfiguration) source.getAttachment("fit.model");
            setFitConfiguration(fitConf);
            setSed(source);
        }
    }


    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jLabel1 = new javax.swing.JLabel();
        modelExpressionField = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        modelsTree = new javax.swing.JTree();

        jLabel1.setText("Model Expression: ");
        jLabel1.setName("jLabel1"); // NOI18N

        modelExpressionField.setEditable(false);
        modelExpressionField.setName("modelExpressionField"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${expression}"), modelExpressionField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jSplitPane1.setDividerLocation(150);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 467, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 181, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanel2);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        modelsTree.setName("modelsTree"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${treeModel}"), modelsTree, org.jdesktop.beansbinding.BeanProperty.create("model"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(modelsTree);

        jSplitPane1.setLeftComponent(jScrollPane1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jSplitPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 546, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(modelExpressionField)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(modelExpressionField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField modelExpressionField;
    private javax.swing.JTree modelsTree;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
