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
import cfa.vo.iris.fitting.custom.CustomModelsManager;
import cfa.vo.iris.fitting.custom.DefaultCustomModel;
import cfa.vo.iris.fitting.custom.ModelsListener;
import cfa.vo.iris.gui.widgets.ModelViewerPanel;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.utils.IPredicate;
import cfa.vo.sherpa.IFitConfiguration;
import cfa.vo.sherpa.SherpaClient;
import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.ModelFactory;
import cfa.vo.sherpa.models.ModelImpl;
import cfa.vo.sherpa.models.Parameter;
import cfa.vo.sherpa.optimization.OptimizationMethod;
import cfa.vo.sherpa.stats.Stats;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FittingMainView extends javax.swing.JInternalFrame implements SedListener {
    private ModelViewerPanel modelViewerPanel;
    private ExtSed sed;
    private String sedId;
    private ModelsTreeModel model;
    private CustomModelsManager modelsManager;
    private SherpaClient sherpaClient;
    private ModelFactory factory = new ModelFactory();
    private StringPredicate predicate = new StringPredicate("");
    private FitConfigurationBean fit = new FitConfigurationBean();
    private Logger logger = Logger.getLogger(FittingMainView.class.getName());
    public static final String PROP_FIT = "fit";
    public static final String PROP_SEDID = "sedId";
    public final String DEFAULT_DESCRIPTION = "Double click on a Component to add it to the list of selected Components.";
    public final String CUSTOM_DESCRIPTION = "User Model";
    
    /**
     * Creates new form FittingMainView
     * @param sed
     * @param sherpaClient
     */
    public FittingMainView(ExtSed sed, CustomModelsManager modelsManager, SherpaClient sherpaClient) {
        this.modelsManager = modelsManager;
        this.sherpaClient = sherpaClient;
        initComponents();
        setUpModelViewerPanel(sed);
        setUpAvailableModelsTree();
        SedEvent.getInstance().add(this);
        revalidate();
        repaint();
        pack();
    }

    public FitConfigurationBean getFit() {
        return fit;
    }

    public void setFit(FitConfigurationBean fit) {
        IFitConfiguration oldFit = this.fit;
        this.fit = fit;
        firePropertyChange(PROP_FIT, oldFit, fit);
    }

    public String getSedId() {
        return sedId;
    }

    /**
     * Set the value of sedId
     *
     * @param sedId new value of sedId
     */
    private void setSedId(String sedId) {
        String oldSedId = this.sedId;
        this.sedId = sedId;
        firePropertyChange(PROP_SEDID, oldSedId, sedId);
    }


    public ModelViewerPanel getModelViewerPanel() {
        return modelViewerPanel;
    }
    
    @Override
    public void process(ExtSed source, SedCommand payload) {
        if (SedCommand.SELECTED.equals(payload) || SedCommand.CHANGED.equals(payload) && source.equals(sed)) {
            setSed(source);
        }
    }

    private void setUpModelViewerPanel(ExtSed sed) {
        setSed(sed);
        modelViewerPanel = new ModelViewerPanel(sed);
        modelViewerPanel.setVisible(true);
        modelPanel.setLayout(new GridLayout(1, 1));
        modelPanel.add(modelViewerPanel);
        modelPanel.setPreferredSize(modelViewerPanel.getPreferredSize());
    }

    private void setUpAvailableModelsTree() {
        updateModels();
        availableTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        availableTree.addMouseListener(new MouseAdapter() {
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
                                logger.info("Added model " + m);
                                String id = sherpaClient.createId();
                                Model toAdd = new ModelImpl(m, id);
                                getFit().addModel(toAdd, id);
                                modelViewerPanel.setFitConfiguration(getFit());
                            }
                        } else {
                            DefaultCustomModel m = (DefaultCustomModel) leaf;
                            if (e.getClickCount() == 2) {
                                logger.info("Added user model " + m);
                                getFit().addUserModel(m, sherpaClient.createId());
                                modelViewerPanel.setFitConfiguration(getFit());
                            }
                            descriptionArea.setText(CUSTOM_DESCRIPTION);
                        }
                    } else {
                        descriptionArea.setText(DEFAULT_DESCRIPTION);
                    }
                }
            }
        });
        rootPane.setDefaultButton(searchButton);
    }

    private void filterModels(String searchString) {
        predicate.setString(searchString);
        List<Model> sub = predicate.apply(model.getList());
        availableTree.setModel(new ModelsTreeModel(sub, modelsManager));
    }

    /**
     * Set the value of sed
     *
     * @param sed new value of sed
     */
    private void setSed(ExtSed sed) {
        this.sed = sed;
        setSedId(sed.getId());
        FitConfigurationBean fitAttachment = (FitConfigurationBean) sed.getAttachment("fit.model");
        if (fitAttachment == null) {
            fitAttachment = new FitConfigurationBean();
            sed.addAttachment("fit.model", fitAttachment);
        }
        setFit(fitAttachment);
    }

    private void updateModels() {
        List<Model> models = new ArrayList<>(factory.getModels());
        model = new ModelsTreeModel(models, modelsManager);
        availableTree.setModel(model);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        modelPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        optimizationCombo = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        statisticCombo = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        availableComponents = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionArea = new javax.swing.JTextArea();
        searchButton = new javax.swing.JButton();
        searchField = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        availableTree = new CustomJTree();
        currentSedLabel = new javax.swing.JLabel();
        currentSedField = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuBar = new javax.swing.JMenu();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Fitting Tool");
        setAutoscrolls(true);
        setPreferredSize(new java.awt.Dimension(850, 652));
        getContentPane().setLayout(new java.awt.GridLayout(1, 1));

        jPanel1.setPreferredSize(new java.awt.Dimension(850, 418));

        jSplitPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Fit Configuration"));
        jSplitPane1.setDividerLocation(200);

        modelPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout modelPanelLayout = new javax.swing.GroupLayout(modelPanel);
        modelPanel.setLayout(modelPanelLayout);
        modelPanelLayout.setHorizontalGroup(
            modelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 701, Short.MAX_VALUE)
        );
        modelPanelLayout.setVerticalGroup(
            modelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 205, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Optimization Method:");

        optimizationCombo.setModel(new DefaultComboBoxModel<>(OptimizationMethod.values()));
        optimizationCombo.setName("optimizationCombo"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${fit.method}"), optimizationCombo, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel2.setText("Statistic:");

        statisticCombo.setModel(new DefaultComboBoxModel<>(Stats.values()));
        statisticCombo.setName("statisticCombo"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${fit.stat}"), statisticCombo, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(optimizationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(statisticCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(optimizationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statisticCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(modelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(modelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(81, 81, 81))
        );

        jSplitPane1.setRightComponent(jPanel2);

        availableComponents.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Components"));

        descriptionArea.setEditable(false);
        descriptionArea.setColumns(20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setRows(3);
        descriptionArea.setText(DEFAULT_DESCRIPTION);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEnabled(false);
        descriptionArea.setName("descriptionArea"); // NOI18N
        jScrollPane2.setViewportView(descriptionArea);

        searchButton.setText("Search");
        searchButton.setName("searchButton"); // NOI18N
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        searchField.setName("searchField"); // NOI18N

        availableTree.setName("availableTree"); // NOI18N
        jScrollPane3.setViewportView(availableTree);

        javax.swing.GroupLayout availableComponentsLayout = new javax.swing.GroupLayout(availableComponents);
        availableComponents.setLayout(availableComponentsLayout);
        availableComponentsLayout.setHorizontalGroup(
            availableComponentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(availableComponentsLayout.createSequentialGroup()
                .addComponent(searchField, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jScrollPane3)
        );
        availableComponentsLayout.setVerticalGroup(
            availableComponentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(availableComponentsLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(availableComponentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchButton)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(availableComponents, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(availableComponents, javax.swing.GroupLayout.PREFERRED_SIZE, 340, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel4);

        currentSedLabel.setText("Current Sed: ");

        currentSedField.setEditable(false);
        currentSedField.setEnabled(false);
        currentSedField.setName("currentSedField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sedId}"), currentSedField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(currentSedLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(currentSedField))
                    .addComponent(jSplitPane1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(currentSedLabel)
                    .addComponent(currentSedField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(390, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        menuBar.setText("Edit");
        jMenuBar1.add(menuBar);

        setJMenuBar(jMenuBar1);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        filterModels(searchField.getText());
    }//GEN-LAST:event_searchButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel availableComponents;
    private javax.swing.JTree availableTree;
    private javax.swing.JTextField currentSedField;
    private javax.swing.JLabel currentSedLabel;
    private javax.swing.JTextArea descriptionArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JMenu menuBar;
    private javax.swing.JPanel modelPanel;
    private javax.swing.JComboBox optimizationCombo;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JComboBox statisticCombo;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private class CustomJTree extends JTree implements ModelsListener {
        public CustomJTree() {
            modelsManager.addListener(this);
        }

        @Override
        public void setModel(TreeModel model) {
            super.setModel(model);
            for (int i = 0; i < this.getRowCount(); i++) {
                this.expandRow(i);
            }
        }

        @Override
        public void update(MutableTreeNode newTree) {
            updateModels();
        }
    }

    private class StringPredicate implements IPredicate<Model> {

        private String string;

        public StringPredicate(String string) {
            this.string = string.toLowerCase();
        }

        public void setString(String string) {
            this.string = string.toLowerCase();
        }

        @Override
        public boolean apply(Model object) {
            boolean resp = false;
            if (object.getName() != null) {
                resp = resp || object.getName().toLowerCase().contains(string);
            }
            if (object.getDescription() != null) {
                resp = resp || object.getDescription().toLowerCase().contains(string);
            }
            if (object.getPars() != null) {
                for (Parameter p : object.getPars()) {
                    resp = resp || p.getName().toLowerCase().contains(string);
                }
            }

            return resp;
        }

        // TODO Refactoring: This can be added as a concrete method in an abstract class
        public List<Model> apply(List<Model> all) {
            List<Model> sub = new ArrayList<>();

            for (Model m : all) {
                if (predicate.apply(m)) {
                    sub.add(m);
                }
            }

            return sub;
        }
    }
}
