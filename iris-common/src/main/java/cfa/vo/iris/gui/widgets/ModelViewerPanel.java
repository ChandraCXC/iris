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

import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.fitting.FitConfiguration;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sherpa.models.*;
import org.jdesktop.beansbinding.Converter;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

public final class ModelViewerPanel extends javax.swing.JPanel implements SedListener, PropertyChangeListener {

    private Logger logger = Logger.getLogger(ModelViewerPanel.class.getName());
    private boolean editable = false;
    public static final String FIT_SUCCEEDED = "Fit Succeeded";
    public static final String FIT_FAILED = "Fit Failed";
    public static final String PROP_EDITABLE = "editable";
    public static final String PROP_FIT = "fit";
    private ExtSed sed;
    private FitConfiguration fit;

    public ModelViewerPanel() {
        initComponents();
        SedEvent.getInstance().add(this);
        initModelsTree();
        revalidate();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        boolean old_editable = this.editable;
        this.editable = editable;
        firePropertyChange(PROP_EDITABLE, old_editable, editable);
    }

    private void setFit(@Nonnull FitConfiguration fit) {
        this.fit = fit;
        firePropertyChange(PROP_FIT, null, fit);
        fit.addPropertyChangeListener(this);
    }

    public FitConfiguration getFit() {
        return fit;
    }

    public void setSelectedParameter(Parameter selectedParameter) {
        paramPanel.setParameter(selectedParameter);
    }

    JTree getModelsTree() {
        return modelsTree;
    }

    private void initModelsTree() {
        modelsTree.setPreferredSize(null);
        ModelViewerMouseAdapter adapter = makeMouseListener();
        modelsTree.addMouseListener(adapter);
        modelsTree.addTreeSelectionListener(adapter);
    }

    private ModelViewerMouseAdapter makeMouseListener() {
        return new ModelViewerMouseAdapter(this);
    }

    void removeModelComponent(Model model) {
        fit.removeModel(model);
    }

    public void freezeAll(Model model) {
        for (Parameter par : model.getPars()) {
            par.setFrozen(1);
        }
        setSelectedParameter(paramPanel.getParameter());
    }

    public void freezeAll() {
        for(Model model : this.fit.getModel().getParts()) {
            for (Parameter par : model.getPars()) {
                par.setFrozen(1);
            }
        }
        setSelectedParameter(paramPanel.getParameter());
    }

    public void thawAll(Model model) {
        for (Parameter par : model.getPars()) {
            if (! new Integer(1).equals(par.getAlwaysfrozen())) {
                par.setFrozen(0);
            }
        }
        setSelectedParameter(paramPanel.getParameter());
    }

    public void thawAll() {
        for(Model model : this.fit.getModel().getParts()) {
            for (Parameter par : model.getPars()) {
                if (!new Integer(1).equals(par.getAlwaysfrozen())) {
                    par.setFrozen(0);
                }
            }
        }
        setSelectedParameter(paramPanel.getParameter());
    }

    public void setSed(ExtSed sed) {
        this.sed = sed;
        FitConfiguration fitConf = sed.getFit();
        setFit(fitConf);
    }

    public void fitResult(boolean result) {
        String msg = result ? FIT_SUCCEEDED : FIT_FAILED;
        statusField.setText(msg);
    }

    @Override
    public void process(ExtSed source, SedCommand payload) {
        if (SedCommand.SELECTED.equals(payload) ||
                SedCommand.CHANGED.equals(payload) && source.equals(sed)) {
            setSed(source);
        }
    }

    @Override
    public void updateUI() {
        if (paramPanel != null) {
            paramPanel.updateUI();
        }
        super.updateUI();
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        firePropertyChange(PROP_FIT, null, propertyChangeEvent.getSource());
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
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jLabel1 = new javax.swing.JLabel();
        modelExpressionField = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        paramPanel = new cfa.vo.iris.gui.widgets.ModelParameterPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        modelsTree = new javax.swing.JTree();
        statusPanel = new javax.swing.JPanel();
        statusField = new javax.swing.JLabel();

        setMinimumSize(null);
        setPreferredSize(new java.awt.Dimension(450, 250));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Model Expression: ");
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(17, 12, 0, 0);
        add(jLabel1, gridBagConstraints);

        modelExpressionField.setInputVerifier(new Verifier());
        modelExpressionField.setName("modelExpressionField"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${fit.expression}"), modelExpressionField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${editable}"), modelExpressionField, org.jdesktop.beansbinding.BeanProperty.create("editable"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 5);
        add(modelExpressionField, gridBagConstraints);

        jSplitPane1.setDividerLocation(160);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        paramPanel.setName("paramPanel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${editable}"), paramPanel, org.jdesktop.beansbinding.BeanProperty.create("editable"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(paramPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(paramPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanel2);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        modelsTree.setName("modelsTree"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${fit.treeModel}"), modelsTree, org.jdesktop.beansbinding.BeanProperty.create("model"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(modelsTree);

        jSplitPane1.setLeftComponent(jScrollPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        add(jSplitPane1, gridBagConstraints);

        statusPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setPreferredSize(new java.awt.Dimension(4, 14));
        statusPanel.setLayout(new java.awt.GridBagLayout());

        statusField.setText("Status");
        statusField.setMaximumSize(null);
        statusField.setMinimumSize(null);
        statusField.setName("statusField"); // NOI18N
        statusField.setPreferredSize(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("${fit.modelValid}"), statusField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new StatusConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        statusPanel.add(statusField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        add(statusPanel, gridBagConstraints);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField modelExpressionField;
    private javax.swing.JTree modelsTree;
    private cfa.vo.iris.gui.widgets.ModelParameterPanel paramPanel;
    private javax.swing.JLabel statusField;
    private javax.swing.JPanel statusPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    class Verifier extends InputVerifier {
        ModelExpressionVerifier v = new ModelExpressionVerifier();

        @Override
        public boolean verify(JComponent jComponent) {
            return v.verify(fit);
        }

        @Override
        public boolean shouldYieldFocus(JComponent input) {
            verify(input);
            return super.shouldYieldFocus(input);
        }
    }

    class StatusConverter extends Converter {

        @Override
        public Object convertForward(Object o) {
            Boolean modelValid = (Boolean) o;
            return modelValid ? "" : "Invalid Model Expression";
        }

        @Override
        public Object convertReverse(Object o) {
            // read only
            throw new UnsupportedOperationException("");
        }
    }

}
