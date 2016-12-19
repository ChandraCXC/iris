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

import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.fitting.custom.DefaultCustomModel;
import cfa.vo.iris.fitting.custom.ModelsListener;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.IrisVisualizer;
import cfa.vo.iris.visualizer.preferences.SedModel;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.iris.visualizer.preferences.VisualizerDataStore;
import cfa.vo.sherpa.FitResults;
import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.optimization.OptimizationMethod;
import cfa.vo.sherpa.stats.Statistic;
import org.astrogrid.samp.client.SampException;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FittingMainView extends JInternalFrame implements SedListener {
    
    private SedModel sedModel;
    private FitController controller;
    private JFileChooser chooser;
    private VisualizerDataStore dataStore;
    private VisualizerComponentPreferences preferences;
    
    private FittingRangesFrame fittingRangesFrame;
    
    public final String DEFAULT_DESCRIPTION = "Double click on a Component to add it to the list of selected Components.";
    public final String CUSTOM_DESCRIPTION = "User Model";
    
    public static final String PROP_SEDMODEL = "sedModel";

    public FittingMainView() {
        initComponents();
        setPreferredSize(null);
        SedEvent.getInstance().add(this);
    }

    public FittingMainView(VisualizerComponentPreferences preferences, JFileChooser chooser, FitController controller) {
        this();
        this.controller = controller;
        this.chooser = chooser;
        this.dataStore = preferences.getDataStore();
        this.preferences = preferences;
        setSedModel(controller.getSedModel());
        initController();
        setUpAvailableModelsTree();
        setUpModelViewerPanel();
        setUpMenuBar();
        confidencePanel.setController(controller);
        revalidate();
        pack();
    }

    public SedModel getSedModel() {
        return sedModel;
    }

    public void setSedModel(SedModel sedModel) {
        this.sedModel = sedModel;
        firePropertyChange(PROP_SEDMODEL, null, sedModel);
        controller.setSedModel(sedModel);
    }

    @Override
    public void process(ExtSed source, SedCommand payload) {
        if (SedCommand.SELECTED.equals(payload) || 
                (SedCommand.CHANGED.equals(payload) && source.equals(sedModel.getSed()))) {
            setSedModel(dataStore.getSedModel(source));
        }
    }

    private void initController() {
        controller.addListener((ModelsListener) availableTree);
    }

    private void setUpMenuBar() {
        saveTextMenuItem.setAction(new SaveTextAction());
        saveJsonMenuItem.setAction(new SaveJsonAction());
        loadJsonMenuItem.setAction(new LoadJsonAction());
    }

    private void setUpModelViewerPanel() {
        modelViewerPanel.setSed(sedModel.getSed());
        modelViewerPanel.setEditable(true);
    }

    private void setUpAvailableModelsTree() {
        ((CustomJTree) availableTree).register();
        availableTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        availableTree.addMouseListener(new AvailableModelsMouseAdapter());
        availableTree.setModel(controller.getModelsTreeModel());
        rootPane.setDefaultButton(searchButton);
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

        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane4 = new javax.swing.JSplitPane();
        modelPanel = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        optimizationCombo = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        statisticCombo = new javax.swing.JComboBox();
        openFittingRangesButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        busyFit = new org.jdesktop.swingx.JXBusyLabel();
        fitButton = new javax.swing.JButton();
        stopFitButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        modelViewerPanel = new cfa.vo.iris.gui.widgets.ModelViewerPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        resultsContainer = new javax.swing.JPanel();
        resultsPanel = new cfa.vo.iris.fitting.FitResultsPanel();
        confidenceContainer = new javax.swing.JPanel();
        confidencePanel = new cfa.vo.iris.fitting.ConfidencePanel();
        jPanel4 = new javax.swing.JPanel();
        availableComponents = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        availableTree = new CustomJTree();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionArea = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        currentSedField = new javax.swing.JTextField();
        currentSedLabel = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        loadJsonMenuItem = new javax.swing.JMenuItem();
        saveTextMenuItem = new javax.swing.JMenuItem();
        saveJsonMenuItem = new javax.swing.JMenuItem();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Fitting Tool");
        setAutoscrolls(true);
        setMinimumSize(null);
        getContentPane().setLayout(new java.awt.GridLayout(1, 1));

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jSplitPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Fit Configuration"));

        jSplitPane4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        modelPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jSplitPane3.setDividerLocation(450);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Optimization Method:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(jLabel1, gridBagConstraints);

        optimizationCombo.setModel(new DefaultComboBoxModel<>(OptimizationMethod.values()));
        optimizationCombo.setName("optimizationCombo"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sedModel.sed.fit.method}"), optimizationCombo, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        jPanel5.add(optimizationCombo, gridBagConstraints);

        jLabel2.setText("Statistic:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(jLabel2, gridBagConstraints);

        statisticCombo.setModel(new DefaultComboBoxModel<>(Statistic.values()));
        statisticCombo.setName("statisticCombo"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sedModel.sed.fit.stat}"), statisticCombo, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        jPanel5.add(statisticCombo, gridBagConstraints);

        openFittingRangesButton.setText("Add Ranges...");
        openFittingRangesButton.setToolTipText("Define fitting ranges for the model");
        openFittingRangesButton.setName("addFittingRange"); // NOI18N
        openFittingRangesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFittingRangesButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 82;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 0);
        jPanel5.add(openFittingRangesButton, gridBagConstraints);

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));
        jPanel6.add(busyFit);

        fitButton.setText("Fit");
        fitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doFit(evt);
            }
        });
        jPanel6.add(fitButton);

        stopFitButton.setText("Stop");
        stopFitButton.setEnabled(false);
        stopFitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopFitButtonActionPerformed(evt);
            }
        });
        jPanel6.add(stopFitButton);

        clearButton.setText("Clear All");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        jPanel6.add(clearButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel5.add(jPanel6, gridBagConstraints);

        jSplitPane3.setRightComponent(jPanel5);

        modelViewerPanel.setMinimumSize(null);
        modelViewerPanel.setPreferredSize(null);
        jSplitPane3.setLeftComponent(modelViewerPanel);

        javax.swing.GroupLayout modelPanelLayout = new javax.swing.GroupLayout(modelPanel);
        modelPanel.setLayout(modelPanelLayout);
        modelPanelLayout.setHorizontalGroup(
            modelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
        );
        modelPanelLayout.setVerticalGroup(
            modelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
        );

        jSplitPane4.setLeftComponent(modelPanel);

        jSplitPane2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        resultsPanel.setMinimumSize(null);
        resultsPanel.setPreferredSize(null);

        javax.swing.GroupLayout resultsContainerLayout = new javax.swing.GroupLayout(resultsContainer);
        resultsContainer.setLayout(resultsContainerLayout);
        resultsContainerLayout.setHorizontalGroup(
            resultsContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(resultsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
        );
        resultsContainerLayout.setVerticalGroup(
            resultsContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(resultsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jSplitPane2.setLeftComponent(resultsContainer);

        confidencePanel.setMinimumSize(null);
        confidencePanel.setPreferredSize(null);

        javax.swing.GroupLayout confidenceContainerLayout = new javax.swing.GroupLayout(confidenceContainer);
        confidenceContainer.setLayout(confidenceContainerLayout);
        confidenceContainerLayout.setHorizontalGroup(
            confidenceContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(confidencePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 412, Short.MAX_VALUE)
        );
        confidenceContainerLayout.setVerticalGroup(
            confidenceContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(confidencePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(confidenceContainer);

        jSplitPane4.setRightComponent(jSplitPane2);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane4, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane4)
        );

        jSplitPane1.setRightComponent(jPanel2);

        availableComponents.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Components"));
        availableComponents.setLayout(new java.awt.GridBagLayout());

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Model Components");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Preset Model Components");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("model");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("User Model");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("model");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        availableTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        availableTree.setName("availableTree"); // NOI18N
        availableTree.setPreferredSize(null);
        jScrollPane3.setViewportView(availableTree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(15, 6, 0, 6);
        availableComponents.add(jScrollPane3, gridBagConstraints);

        descriptionArea.setEditable(false);
        descriptionArea.setColumns(20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setRows(3);
        descriptionArea.setText(DEFAULT_DESCRIPTION);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEnabled(false);
        descriptionArea.setName("descriptionArea"); // NOI18N
        descriptionArea.setPreferredSize(null);
        jScrollPane2.setViewportView(descriptionArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        availableComponents.add(jScrollPane2, gridBagConstraints);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        searchField.setName("searchField"); // NOI18N
        searchField.setPreferredSize(null);
        jPanel3.add(searchField);

        searchButton.setText("Search");
        searchButton.setName("searchButton"); // NOI18N
        searchButton.setPreferredSize(null);
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        jPanel3.add(searchButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.1;
        availableComponents.add(jPanel3, gridBagConstraints);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(availableComponents, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(availableComponents, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel1.add(jSplitPane1, gridBagConstraints);

        currentSedField.setEditable(false);
        currentSedField.setEnabled(false);
        currentSedField.setName("currentSedField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sedModel.sed.id}"), currentSedField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 2);
        jPanel1.add(currentSedField, gridBagConstraints);

        currentSedLabel.setText("Current Sed: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(17, 12, 0, 0);
        jPanel1.add(currentSedLabel, gridBagConstraints);

        getContentPane().add(jPanel1);

        fileMenu.setText("File");

        loadJsonMenuItem.setText("loadJson");
        fileMenu.add(loadJsonMenuItem);

        saveTextMenuItem.setText("saveText");
        fileMenu.add(saveTextMenuItem);

        saveJsonMenuItem.setText("saveJson");
        fileMenu.add(saveJsonMenuItem);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        controller.filterModels(searchField.getText());
    }//GEN-LAST:event_searchButtonActionPerformed

    private void doFit(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doFit

        SwingWorker worker = new SwingWorker<FitResults, Void>() {

            @Override
            protected FitResults doInBackground() throws Exception {
                return controller.fit();
            }

            @Override
            protected void done() {
                try {
                    get();
                    resultsPanel.setFit(sedModel.getFit());
                    modelViewerPanel.fitResult(true);
                    modelViewerPanel.updateUI();
                    modelViewerPanel.fitResult(true);
                    // Asynchronously evaluate the model
                    VisualizerComponentPreferences prefs = IrisVisualizer.getInstance().getActivePreferences();
                    if (prefs != null) {
                        prefs.evaluateModel(sedModel, controller);
                    }
                } catch (InterruptedException ex) {
                    // noop
                } catch (ExecutionException ex) {
                    Logger.getLogger(FittingMainView.class.getName()).log(Level.SEVERE, null, ex);
                    NarrowOptionPane.showMessageDialog(FittingMainView.this,
                        ex.getMessage(),
                        ex.getClass().getSimpleName(),
                        NarrowOptionPane.ERROR_MESSAGE);
                        modelViewerPanel.fitResult(false);
                } finally {
                    busyFit.setBusy(false);
                    fitButton.setEnabled(true);
                    stopFitButton.setEnabled(false);
                    clearButton.setEnabled(true);
                }
            }
        };
            
        busyFit.setBusy(true);
        fitButton.setEnabled(false);
        stopFitButton.setEnabled(true);
        clearButton.setEnabled(false);
        worker.execute();
            
        
    }//GEN-LAST:event_doFit

    private void openFittingRangesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFittingRangesButtonActionPerformed
        if (fittingRangesFrame == null) {
            fittingRangesFrame = new FittingRangesFrame(preferences, controller);
            this.getDesktopPane().add(fittingRangesFrame);
            this.getDesktopPane().setLayer(fittingRangesFrame, 1);
        }
        GUIUtils.moveToFront(fittingRangesFrame);
    }//GEN-LAST:event_openFittingRangesButtonActionPerformed

    private void stopFitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopFitButtonActionPerformed
        stopFitButton.setEnabled(false);
        try {
            controller.stopFit();
        } catch (SampException e) {
            NarrowOptionPane.showMessageDialog(this, e.getMessage(), "Unexpected Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            stopFitButton.setEnabled(true);
        }
    }//GEN-LAST:event_stopFitButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        controller.clearAll();
        preferences.getDataModel().refresh();
        confidencePanel.reset();
    }//GEN-LAST:event_clearButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel availableComponents;
    private javax.swing.JTree availableTree;
    private org.jdesktop.swingx.JXBusyLabel busyFit;
    private javax.swing.JButton clearButton;
    private javax.swing.JPanel confidenceContainer;
    private cfa.vo.iris.fitting.ConfidencePanel confidencePanel;
    private javax.swing.JTextField currentSedField;
    private javax.swing.JLabel currentSedLabel;
    private javax.swing.JTextArea descriptionArea;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JButton fitButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JMenuItem loadJsonMenuItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel modelPanel;
    private cfa.vo.iris.gui.widgets.ModelViewerPanel modelViewerPanel;
    private javax.swing.JButton openFittingRangesButton;
    private javax.swing.JComboBox optimizationCombo;
    private javax.swing.JPanel resultsContainer;
    private cfa.vo.iris.fitting.FitResultsPanel resultsPanel;
    private javax.swing.JMenuItem saveJsonMenuItem;
    private javax.swing.JMenuItem saveTextMenuItem;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JComboBox statisticCombo;
    private javax.swing.JButton stopFitButton;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private class CustomJTree extends JTree implements ModelsListener {
        public CustomJTree() {
        }

        public void register() {
            controller.addListener(this);
        }

        @Override
        public void setModel(TreeModel model) {
            super.setModel(model);
            for (int i = 0; i < this.getRowCount(); i++) {
                this.expandRow(i);
            }
        }
    }

    private class AvailableModelsMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            TreePath selPath = availableTree.getPathForLocation(e.getX(), e.getY());
            if (selPath != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                if (node.isLeaf()) {
                    Object leaf = node.getUserObject();
                    if (Model.class.isInstance(leaf)) {
                        Model m = (Model) leaf;
                        descriptionArea.setText(m.getDescription());
                        if (e.getClickCount() == 2) {
                            controller.addModel(m);
                        }
                    } else {
                        DefaultCustomModel m = (DefaultCustomModel) leaf;
                        descriptionArea.setText(CUSTOM_DESCRIPTION);
                        if (e.getClickCount() == 2) {
                            controller.addModel(m);
                        }
                    }
                } else {
                    descriptionArea.setText(DEFAULT_DESCRIPTION);
                }
            }
        }
    }

    private class SaveTextAction extends AbstractAction {
        public SaveTextAction() {
            super("Save Text...");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            checkFittingConfigurationVersion();
            int result = chooser.showSaveDialog(FittingMainView.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    boolean overwrite = true;
                    int resp = NarrowOptionPane.YES_OPTION;
                    
                    String filePath = chooser.getSelectedFile().getAbsolutePath();
                    
                    if (chooser.getSelectedFile().exists()) {
                        resp = NarrowOptionPane.showConfirmDialog(FittingMainView.this,
                            filePath + " exists, do you want to overwrite it?", 
                            "File exists", NarrowOptionPane.YES_NO_OPTION);
                    }
                    
                    // overwrite and response are always true UNLESS the user 
                    // specifies otherwise in the OptionPane above.
                    overwrite = (resp == NarrowOptionPane.YES_OPTION);
                    if (overwrite) {
                        controller.save(new FileOutputStream(chooser.getSelectedFile()));
                        NarrowOptionPane.showMessageDialog(FittingMainView.this, 
                                "Saved file " + filePath, "Saved File", 
                                NarrowOptionPane.INFORMATION_MESSAGE);
                    }
                } catch(FileNotFoundException ex) {
                    NarrowOptionPane.showMessageDialog(FittingMainView.this, ex.getMessage(), "Error", NarrowOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class SaveJsonAction extends AbstractAction {
        public SaveJsonAction() {
            super("Save Json...");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            checkFittingConfigurationVersion();
            int result = chooser.showSaveDialog(FittingMainView.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    boolean overwrite = true;
                    int resp = NarrowOptionPane.YES_OPTION;

                    String filePath = chooser.getSelectedFile().getAbsolutePath();

                    if (chooser.getSelectedFile().exists()) {
                        resp = NarrowOptionPane.showConfirmDialog(FittingMainView.this,
                            filePath + " exists, do you want to overwrite it?", 
                            "File exists", NarrowOptionPane.YES_NO_OPTION);
                    }

                    // overwrite and response are always true UNLESS the user 
                    // specifies otherwise in the OptionPane above.
                    overwrite = (resp == NarrowOptionPane.YES_OPTION);
                    if (overwrite) {
                        controller.saveJson(new FileOutputStream(chooser.getSelectedFile()));
                        NarrowOptionPane.showMessageDialog(FittingMainView.this, 
                                "Saved file " + filePath, "Saved File", 
                                NarrowOptionPane.INFORMATION_MESSAGE);
                    }
                } catch(IOException ex) {
                    NarrowOptionPane.showMessageDialog(FittingMainView.this, ex.getMessage(), "Error", NarrowOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class LoadJsonAction extends AbstractAction {
        public LoadJsonAction() {
            super("Load Json...");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int result = chooser.showOpenDialog(FittingMainView.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    controller.loadJson(new FileInputStream(chooser.getSelectedFile()));
                } catch(IOException ex) {
                    NarrowOptionPane.showMessageDialog(FittingMainView.this, ex.getMessage(), "Error", NarrowOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void checkFittingConfigurationVersion() {
        SedModel model = controller.getSedModel();
        
        // If no version avaiable ignore it
        if (model.getFitConfigurationVersion() == 0) {
            return;
        }
        
        // Check the version against the current version
        if (model.getFitConfigurationVersion() != model.getFit().hashCode()) {
            NarrowOptionPane.showMessageDialog(FittingMainView.this,
                    "The fitting configuration has changed since the last fit was performed, you may want to refit your data before saving.",
                    "Warning", NarrowOptionPane.WARNING_MESSAGE);
        }
    }
}
