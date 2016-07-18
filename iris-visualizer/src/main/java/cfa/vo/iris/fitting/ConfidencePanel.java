/*
 * Copyright 2016 Chandra X-Ray Observatory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cfa.vo.iris.fitting;

import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.sherpa.ConfidenceResults;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.swingbinding.JTableBinding;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.beansbinding.Converter;


public class ConfidencePanel extends javax.swing.JPanel {
    private ConfidenceResults confidenceResults;
    private FitController controller;
    private Logger logger = Logger.getLogger(ConfidencePanel.class.getName());

    public static final String PROP_CONTROLLER = "controller";
    public static final String PROP_CONFIDENCERESULTS = "confidenceResults";

    /**
     * Creates new form ConfidencePanel
     */
    public ConfidencePanel() {
        initComponents();
        initBindings();
    }

    public FitController getController() {
        return controller;
    }

    public void setController(FitController controller) {
        FitController old = this.controller;
        this.controller = controller;
        firePropertyChange(PROP_CONTROLLER, old, controller);
    }

    /**
     * Get the value of confidenceResults
     *
     * @return the value of confidenceResults
     */
    public ConfidenceResults getConfidenceResults() {
        return confidenceResults;
    }

    /**
     * Set the value of confidenceResults
     *
     * @param confidenceResults new value of confidenceResults
     */
    void setConfidenceResults(ConfidenceResults confidenceResults) {
        ConfidenceResults oldConfidenceResults = this.confidenceResults;
        this.confidenceResults = confidenceResults;
        firePropertyChange(PROP_CONFIDENCERESULTS, oldConfidenceResults, confidenceResults);
    }

    @SuppressWarnings("unchecked")
    private void initBindings() {
        JTableBinding b = (JTableBinding) bindingGroup.getBinding("table");
        bindingGroup.unbind();
        b.setConverter(new ConfResultsConverter());
        Property parName = BeanProperty.create("name");
        Property parMin = BeanProperty.create("lowerLimit");
        Property parMax = BeanProperty.create("upperLimit");
        b.addColumnBinding(parName).setColumnName("Parameter");
        b.addColumnBinding(parMin).setColumnName("Lower Limit");
        b.addColumnBinding(parMax).setColumnName("Upper Limit");
        bindingGroup.bind();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        sigmaText = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setMinimumSize(new java.awt.Dimension(287, 191));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Confidence Interval: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(17, 12, 0, 0);
        add(jLabel1, gridBagConstraints);

        jTextField1.setName("sigma"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${controller.fit.confidence.config.sigma}"), jTextField1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(jTextField1, gridBagConstraints);

        sigmaText.setText("sigma - 90.00%");
        sigmaText.setName("sigmaPercent"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${confidenceResults.percent}"), sigmaText, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new SigmaConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(17, 12, 0, 12);
        add(sigmaText, gridBagConstraints);

        jButton1.setText("Compute");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doConfidence(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 35, 12, 12);
        add(jButton1, gridBagConstraints);

        jTable1.setName("confidenceTable"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${confidenceResults}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, eLProperty, jTable1, "table");
        jTableBinding.setConverter(new ConfResultsConverter());
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(jTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 239;
        gridBagConstraints.ipady = 79;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 12);
        add(jScrollPane1, gridBagConstraints);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void doConfidence(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doConfidence
        try {
            ConfidenceResults results = controller.computeConfidence();
            setConfidenceResults(results);
        } catch (Exception e) {
            NarrowOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    e.getClass().getSimpleName(),
                    NarrowOptionPane.ERROR_MESSAGE
            );
            logger.log(Level.SEVERE, "Error computing confidence", e);
        }
    }//GEN-LAST:event_doConfidence


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel sigmaText;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private class SigmaConverter extends Converter {

        @Override
        public Object convertForward(Object s) {
            Double sigma = (Double) s;
            return String.format("sigma - %2.2f%%", sigma);
        }

        @Override
        public Object convertReverse(Object t) {
            throw new UnsupportedOperationException("Not supported yet."); // Never called
        }
    }

}
