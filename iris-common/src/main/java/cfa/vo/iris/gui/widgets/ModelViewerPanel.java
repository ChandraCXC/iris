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
import cfa.vo.sherpa.models.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ModelViewerPanel extends javax.swing.JPanel implements SedListener {

    private Logger logger = Logger.getLogger(ModelViewerPanel.class.getName());
    private boolean modelValid;
    public final String PROP_MODELVALID = "modelValid";

    private Verifier verifier;
    private final String[] values = new String[]{"Val", "Min", "Max", "Frozen"};

    /**
     * Creates new form NewJInternalFrame
     */
    public ModelViewerPanel(ExtSed sed) {
        initComponents();
        initVerifier();
        setSed(sed);
        SedEvent.getInstance().add(this);
        initModelsTree();
    }

    public void setEditable(boolean isEditable) {
        modelExpressionField.setEditable(isEditable);
    }

    public boolean isModelValid() {
        return modelValid;
    }

    public void setModelValid(boolean modelValid) {
        boolean oldModelValid = this.modelValid;
        this.modelValid = modelValid;
        firePropertyChange(PROP_MODELVALID, oldModelValid, modelValid);
        setStatus(modelValid);
    }

    private java.util.List<UserModel> userModels;
    
    public void setFitConfiguration(IFitConfiguration fit) {

        if (fit != null) {
            CompositeModel m = fit.getModel();
            userModels = fit.getUserModelList();
            if (m != null) {
                logger.info("setting model to: "+m);
                setModel(m);
            } else {
                logger.info("null model, instantiating new one");
                setModel(SAMPFactory.get(CompositeModel.class));
            }
            String expression = getModel().getName();
            if (expression != null && !expression.isEmpty()) {
                setExpression(getModel().getName());
            } else {
                setExpression("No Model");
                setSelectedParameter(null);
            }
        } else {
            setFitConfiguration(SAMPFactory.get(IFitConfiguration.class));
        }
    }

    private void setStatus(final boolean valid) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String msg = valid ? "" : "Invalid Model Expression";
                statusField.setText(msg);
            }
        });
    }

    private void initModelsTree() {
        modelsTree.setPreferredSize(null);
//        modelsTree.setComponentPopupMenu(makePopupMenu());
        modelsTree.addMouseListener(makeSelectedParamMouseListener());
//        modelsTree.addMouseListener(makeRightButtonMouseListener());
    }

    private MouseAdapter makeSelectedParamMouseListener() {
        return new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                checkPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                checkPopup(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                TreePath selPath = modelsTree.getPathForLocation(e.getX(), e.getY());
                if (selPath != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                    if (node.isLeaf()) {
                        Parameter par = (Parameter) node.getUserObject();
                        setSelectedParameter(par);
                    } else {
                        checkPopup(e);
                    }
                }
            }

            private void checkPopup(MouseEvent e) {
                TreePath selPath = modelsTree.getPathForLocation(e.getX(), e.getY());
                if (selPath != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                    Object obj = node.getUserObject();
                    if (!node.isLeaf() && obj instanceof Model && e.isPopupTrigger()) {
                        makePopupMenu().show(modelsTree, e.getX(), e.getY());
                    }
                }
            }
        };
    }

    private JPopupMenu makePopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem item = new JMenuItem("Remove");
//        item.addActionListener(makeDeleteActionListener());
        menu.add(item);
        return menu;
    }

//    private ActionListener makeDeleteActionListener() {
//        return new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent arg0) {
//                if(selectedNode != null){
//                    logger.info("Deleting " + selectedNode);
//                    DefaultMutableTreeNode n = new DefaultMutableTreeNode("added");
//                    selectedNode.add(n);
//                    modelsTree.repaint();
//                    modelsTree.updateUI();
//                }
//            }
//        };
//    }

    private void initVerifier() {
        verifier = new Verifier();
        modelExpressionField.setInputVerifier(verifier);
        modelExpressionField.addActionListener(verifier);
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

    /**
     * Set the value of sed
     *
     * @param sed new value of sed
     */
    private void setSed(ExtSed sed) {
        this.sed = sed;
        IFitConfiguration fitConf = (IFitConfiguration) sed.getAttachment("fit.model");
        setFitConfiguration(fitConf);
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
        logger.info("setting expression to: "+expression);
        String oldExpression = this.expression;
        this.expression = expression;
        firePropertyChange(PROP_EXPRESSION, oldExpression, expression);
        verifier.verify(expression);
    }

    @Override
    public void process(ExtSed source, SedCommand payload) {
        if (SedCommand.SELECTED.equals(payload) ||
                SedCommand.CHANGED.equals(payload) && source.equals(sed)) {
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
        statusPanel = new javax.swing.JPanel();
        statusField = new javax.swing.JLabel();

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
            .add(0, 167, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanel2);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        modelsTree.setName("modelsTree"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${treeModel}"), modelsTree, org.jdesktop.beansbinding.BeanProperty.create("model"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(modelsTree);

        jSplitPane1.setLeftComponent(jScrollPane1);

        statusPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        statusPanel.setName("statusPanel"); // NOI18N

        statusField.setName("statusField"); // NOI18N

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
        );

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
            .add(statusPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(modelExpressionField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 167, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    private javax.swing.JLabel statusField;
    private javax.swing.JPanel statusPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    class Verifier extends InputVerifier implements ActionListener {
        cfa.vo.iris.gui.widgets.ModelExpressionVerifier v = new cfa.vo.iris.gui.widgets.ModelExpressionVerifier();

        @Override
        public boolean verify(JComponent jComponent) {
            JTextField field = (JTextField) jComponent;
            String expression = field.getText();
            return verify(expression);
        }

        @Override
        public boolean shouldYieldFocus(JComponent input) {
            verify(input);
            return super.shouldYieldFocus(input);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField source = (JTextField)e.getSource();
            shouldYieldFocus(source);
        }

        public boolean verify(String expression) {
            boolean retVal = v.verify(expression, model.getParts());
            setModelValid(retVal);
            return retVal;
        }

    }

}
