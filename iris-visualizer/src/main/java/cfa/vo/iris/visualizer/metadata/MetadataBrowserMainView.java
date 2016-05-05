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
package cfa.vo.iris.visualizer.metadata;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.preferences.VisualizerChangeEvent;
import cfa.vo.iris.visualizer.preferences.VisualizerCommand;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.iris.visualizer.preferences.VisualizerDataModel;
import cfa.vo.iris.visualizer.metadata.IrisStarJTable.RowSelection;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.iris.visualizer.stil.tables.SegmentColumnInfoMatcher;
import cfa.vo.iris.visualizer.stil.tables.UtypeColumnInfoMatcher;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
public class MetadataBrowserMainView extends javax.swing.JInternalFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger
            .getLogger(MetadataBrowserMainView.class.getName());
    
    public static final String MB_WINDOW_NAME = "Metadata Browser (%s)";

    final VisualizerComponentPreferences preferences;
    final VisualizerDataModel dataModel;
    
    /**
     * Creates new form MetadataBrowser
     */
    public MetadataBrowserMainView(VisualizerComponentPreferences preferences) 
    {
        this.preferences = preferences;
        this.dataModel = preferences.getDataModel();
        
        initComponents();
    }
    
    /**
     * Specifies a star table (by index) and a row to be added to the selected plotter/
     * data table tabs' row selections. Note that irow is the row in the Base Table of the
     * IrisStarTable - not of the masked star table! Any callers specifying rows based
     * on the IrisStarTable must use .getBaseTableRow in IrisStarTable to map the masked
     * row index back to the base table row index before calling this method.
     * 
     * @param starTableIndex - index of the star table in the selectedTables list.
     * @param irow - row to be selected in the star table.
     */
    public void addRowToSelection(int starTableIndex, int irow) {
        
        // Add this star table to the selection if it isn't
        IrisStarTable starTable = dataModel.getSedStarTables().get(starTableIndex);
        this.starTableTree.addToSelection(starTable);
        
        // Select the correct row
        IrisStarJTable table = getSelectedIrisJTable();
        if (table == null) return;
        
        table.selectRowIndex(starTableIndex, irow);
    }

    /**
     * Extracts the selected rows in the Metadata browser to a new SED, then adds the SED to the
     * SedManager through the Iris Workspace.
     * 
     */
    private void extractSelectionToSed() {
        
        // Do nothing if no SED is selected
        if (dataModel.getSelectedSed() == null) {
            JOptionPane.showMessageDialog(this, "No SED in browser. Please load an SED.",
                    null, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Cannot extract from segment tab
        IrisStarJTable jtable = getSelectedIrisJTable();
        if (jtable == null) {
            JOptionPane.showMessageDialog(this, "Select either Data or Point Metadata tab.",
                    null, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Do nothing if there are no rows selected
        RowSelection selectedRows = jtable.getRowSelection();
        if (selectedRows == null || selectedRows.originalRows.length == 0) {
            JOptionPane.showMessageDialog(this, "No rows selected to extract. Please select rows.",
                    null, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            
            logger.info(String.format("Extracting %s points from Sed.", selectedRows.originalRows.length));
            ExtSed newSed = preferences.createNewWorkspaceSed(selectedRows);
            JOptionPane.showMessageDialog(this, "Added new SED (" + newSed.getId() + ") to workspace.");
            
        } catch (SedInconsistentException | SedNoDataException ex) {
            String msg = "Error extracting SED: " + ex.getMessage();
            JOptionPane.showMessageDialog(this, msg, null, JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, msg, ex);
        }
    }
    
    /**
     * Forces plotter and point tables to redraw themselves by manually resetting the models.
     */
    private void resetDataTables() {
        plotterStarJTable.setSelectedStarTables(dataModel.getSelectedStarTables());
        pointStarJTable.setSelectedStarTables(dataModel.getSelectedStarTables());
    }
    
    /*
     * getters and setters
     */
    
    public VisualizerDataModel getDataModel() {
        return dataModel;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        filterExpressionField = new javax.swing.JTextField();
        selectPointsButton = new javax.swing.JButton();
        applyMaskButton = new javax.swing.JButton();
        selectAllButton = new javax.swing.JButton();
        extractButton = new javax.swing.JButton();
        clearSelectionButton = new javax.swing.JButton();
        invertSelectionButton = new javax.swing.JButton();
        clearMaskButton = new javax.swing.JButton();
        clearAllButton = new javax.swing.JButton();
        dataPane = new javax.swing.JSplitPane();
        dataTabsPane = new javax.swing.JTabbedPane();
        plotterMetadataPanel = new javax.swing.JPanel();
        plotterMetadataScrollPane = new javax.swing.JScrollPane();
        plotterStarJTable = new cfa.vo.iris.visualizer.metadata.IrisStarJTable();
        pointMetadataPanel = new javax.swing.JPanel();
        pointMetadataScrollPane = new javax.swing.JScrollPane();
        pointStarJTable = new cfa.vo.iris.visualizer.metadata.IrisStarJTable();
        segmentMetadataPanel = new javax.swing.JPanel();
        segmentMetadataScrollPane = new javax.swing.JScrollPane();
        metadataJTable1 = new cfa.vo.iris.visualizer.metadata.MetadataJTable();
        starTableScrollPane = new javax.swing.JScrollPane();
        starTableTree = new cfa.vo.iris.visualizer.metadata.StarTableJTree();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        extractToSedMenuItem = new javax.swing.JMenuItem();
        broadcastToSampMenuItem = new javax.swing.JMenuItem();
        createSubsetMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        createNewColumnMenuItem = new javax.swing.JMenuItem();
        restoreSetMenuItem = new javax.swing.JMenuItem();
        selectMenu = new javax.swing.JMenu();
        selectAllMenuItem = new javax.swing.JMenuItem();
        clearSelectionMenuItem = new javax.swing.JMenuItem();
        invertSelectionMenuItem = new javax.swing.JMenuItem();
        applyMaskMenuItem = new javax.swing.JMenuItem();
        removeMasksMenuItem = new javax.swing.JMenuItem();
        clearAllMenuItem = new javax.swing.JMenuItem();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(800, 454));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, this, org.jdesktop.beansbinding.ELProperty.create("Metadata Browser ${dataModel.dataModelTitle}"), this, org.jdesktop.beansbinding.BeanProperty.create("title"));
        binding.setSourceNullValue("Select SED");
        binding.setSourceUnreadableValue("Select SED");
        bindingGroup.addBinding(binding);

        getContentPane().setLayout(new java.awt.GridBagLayout());

        filterExpressionField.setColumns(2);
        filterExpressionField.setText("Filter Expression");
        filterExpressionField.setToolTipText("Enter a column selection expression");
        filterExpressionField.setMaximumSize(new java.awt.Dimension(75, 75));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(filterExpressionField, gridBagConstraints);

        selectPointsButton.setFont(new java.awt.Font("DejaVu Sans", 0, 12)); // NOI18N
        selectPointsButton.setText("Select Points");
        selectPointsButton.setToolTipText("Select points matching the filter expression");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(selectPointsButton, gridBagConstraints);

        applyMaskButton.setFont(new java.awt.Font("DejaVu Sans", 0, 12)); // NOI18N
        applyMaskButton.setText("Mask Points");
        applyMaskButton.setToolTipText("Mask the selected points");
        applyMaskButton.setName(""); // NOI18N
        applyMaskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyMaskButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(applyMaskButton, gridBagConstraints);

        selectAllButton.setFont(new java.awt.Font("DejaVu Sans", 0, 12)); // NOI18N
        selectAllButton.setText("Select All");
        selectAllButton.setToolTipText("Select all rows");
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(selectAllButton, gridBagConstraints);

        extractButton.setText("Extract");
        extractButton.setToolTipText("Extract selection to new SED");
        extractButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extractButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        getContentPane().add(extractButton, gridBagConstraints);

        clearSelectionButton.setFont(new java.awt.Font("DejaVu Sans", 0, 12)); // NOI18N
        clearSelectionButton.setText("Clear Selection");
        clearSelectionButton.setToolTipText("Clear selection");
        clearSelectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearSelectionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(clearSelectionButton, gridBagConstraints);

        invertSelectionButton.setFont(new java.awt.Font("DejaVu Sans", 0, 12)); // NOI18N
        invertSelectionButton.setText("Invert Selection");
        invertSelectionButton.setToolTipText("Select non-selected rows");
        invertSelectionButton.setActionCommand("");
        invertSelectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertSelectionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(invertSelectionButton, gridBagConstraints);

        clearMaskButton.setFont(new java.awt.Font("DejaVu Sans", 0, 12)); // NOI18N
        clearMaskButton.setText("Unmask Points");
        clearMaskButton.setToolTipText("Remove the mask from the selected points");
        clearMaskButton.setName(""); // NOI18N
        clearMaskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearMaskButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(clearMaskButton, gridBagConstraints);

        clearAllButton.setFont(new java.awt.Font("DejaVu Sans", 0, 12)); // NOI18N
        clearAllButton.setText("Clear All");
        clearAllButton.setToolTipText("Remove all masks from all segments");
        clearAllButton.setName(""); // NOI18N
        clearAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearAllButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(clearAllButton, gridBagConstraints);

        dataPane.setDividerLocation(150);
        dataPane.setDividerSize(4);
        dataPane.setToolTipText("");
        dataPane.setMinimumSize(new java.awt.Dimension(25, 25));
        dataPane.setName("dataPane"); // NOI18N

        dataTabsPane.setMinimumSize(new java.awt.Dimension(0, 0));
        dataTabsPane.setName("dataTabsPane"); // NOI18N

        plotterMetadataScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        plotterStarJTable.setColumnInfoMatcher(new SegmentColumnInfoMatcher());
        plotterStarJTable.setSortBySpecValues(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${dataModel.selectedStarTables}"), plotterStarJTable, org.jdesktop.beansbinding.BeanProperty.create("selectedStarTables"));
        bindingGroup.addBinding(binding);

        plotterMetadataScrollPane.setViewportView(plotterStarJTable);

        javax.swing.GroupLayout plotterMetadataPanelLayout = new javax.swing.GroupLayout(plotterMetadataPanel);
        plotterMetadataPanel.setLayout(plotterMetadataPanelLayout);
        plotterMetadataPanelLayout.setHorizontalGroup(
            plotterMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(plotterMetadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
        );
        plotterMetadataPanelLayout.setVerticalGroup(
            plotterMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(plotterMetadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
        );

        dataTabsPane.addTab("Data", plotterMetadataPanel);

        pointMetadataScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        pointStarJTable.setColumnInfoMatcher(new UtypeColumnInfoMatcher());
        pointStarJTable.setSortBySpecValues(false);
        pointStarJTable.setUsePlotterDataTables(false);
        pointStarJTable.setUtypeAsNames(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${dataModel.selectedStarTables}"), pointStarJTable, org.jdesktop.beansbinding.BeanProperty.create("selectedStarTables"));
        bindingGroup.addBinding(binding);

        pointMetadataScrollPane.setViewportView(pointStarJTable);

        javax.swing.GroupLayout pointMetadataPanelLayout = new javax.swing.GroupLayout(pointMetadataPanel);
        pointMetadataPanel.setLayout(pointMetadataPanelLayout);
        pointMetadataPanelLayout.setHorizontalGroup(
            pointMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pointMetadataScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
        );
        pointMetadataPanelLayout.setVerticalGroup(
            pointMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pointMetadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
        );

        dataTabsPane.addTab("Point Metadata", pointMetadataPanel);

        segmentMetadataScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${dataModel.sedStarTables}"), metadataJTable1, org.jdesktop.beansbinding.BeanProperty.create("selectedStarTables"));
        bindingGroup.addBinding(binding);

        segmentMetadataScrollPane.setViewportView(metadataJTable1);

        javax.swing.GroupLayout segmentMetadataPanelLayout = new javax.swing.GroupLayout(segmentMetadataPanel);
        segmentMetadataPanel.setLayout(segmentMetadataPanelLayout);
        segmentMetadataPanelLayout.setHorizontalGroup(
            segmentMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(segmentMetadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
        );
        segmentMetadataPanelLayout.setVerticalGroup(
            segmentMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(segmentMetadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
        );

        dataTabsPane.addTab("Segment Metadata", segmentMetadataPanel);

        dataPane.setRightComponent(dataTabsPane);
        dataTabsPane.getAccessibleContext().setAccessibleName("");

        starTableScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        starTableTree.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        starTableTree.setModel(null);
        starTableTree.setName("satarTableTree"); // NOI18N
        starTableTree.setRootVisible(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${dataModel}"), starTableTree, org.jdesktop.beansbinding.BeanProperty.create("dataModel"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${dataModel.selectedSeds}"), starTableTree, org.jdesktop.beansbinding.BeanProperty.create("seds"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${dataModel.sedStarTables}"), starTableTree, org.jdesktop.beansbinding.BeanProperty.create("sedStarTables"));
        bindingGroup.addBinding(binding);

        starTableScrollPane.setViewportView(starTableTree);

        dataPane.setLeftComponent(starTableScrollPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(dataPane, gridBagConstraints);

        fileMenu.setText("File");
        fileMenu.setName("Extract"); // NOI18N

        extractToSedMenuItem.setText("Extract to New SED");
        extractToSedMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extractToSedMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(extractToSedMenuItem);

        broadcastToSampMenuItem.setText("Broadcast to SAMP");
        fileMenu.add(broadcastToSampMenuItem);

        createSubsetMenuItem.setText("Create Subset");
        fileMenu.add(createSubsetMenuItem);

        jMenuBar1.add(fileMenu);

        editMenu.setText("Edit");

        createNewColumnMenuItem.setText("Create New Column");
        editMenu.add(createNewColumnMenuItem);

        restoreSetMenuItem.setText("Restore Set");
        editMenu.add(restoreSetMenuItem);

        jMenuBar1.add(editMenu);

        selectMenu.setText("Select");
        selectMenu.setName(""); // NOI18N

        selectAllMenuItem.setText("Select All");
        selectAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });
        selectMenu.add(selectAllMenuItem);

        clearSelectionMenuItem.setText("Clear Selection");
        clearSelectionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearSelectionButtonActionPerformed(evt);
            }
        });
        selectMenu.add(clearSelectionMenuItem);

        invertSelectionMenuItem.setText("Invert Selection");
        invertSelectionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertSelectionButtonActionPerformed(evt);
            }
        });
        selectMenu.add(invertSelectionMenuItem);

        applyMaskMenuItem.setText("Apply Masks");
        applyMaskMenuItem.setToolTipText("Apply mask to the selected points");
        applyMaskMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyMaskButtonActionPerformed(evt);
            }
        });
        selectMenu.add(applyMaskMenuItem);

        removeMasksMenuItem.setText("Remove Masks");
        removeMasksMenuItem.setToolTipText("Remove masks from selected poins");
        removeMasksMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearMaskButtonActionPerformed(evt);
            }
        });
        selectMenu.add(removeMasksMenuItem);

        clearAllMenuItem.setText("Clear All");
        clearAllMenuItem.setToolTipText("Remove masks from all segments");
        clearAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearAllButtonActionPerformed(evt);
            }
        });
        selectMenu.add(clearAllMenuItem);

        jMenuBar1.add(selectMenu);

        setJMenuBar(jMenuBar1);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void invertSelectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertSelectionButtonActionPerformed
        JTable table = getSelectedJTable();
        if (table == null) {
            return;
        }
        
        int[] dataSelectedIndexes = table.getSelectedRows();
        
        selectAllButtonActionPerformed(null);
        
        table.getSelectionModel().setValueIsAdjusting(true);
        for (int sel : dataSelectedIndexes) {
            table.removeRowSelectionInterval(sel, sel);
        }
        table.getSelectionModel().setValueIsAdjusting(false);
    }//GEN-LAST:event_invertSelectionButtonActionPerformed

    private void clearSelectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearSelectionButtonActionPerformed
        JTable table = getSelectedJTable();
        if (table == null) {
            return;
        }
        table.clearSelection();
    }//GEN-LAST:event_clearSelectionButtonActionPerformed

    private void applyMaskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyMaskButtonActionPerformed
        IrisStarJTable selectedTable = getSelectedIrisJTable();
        if (selectedTable == null) return;
        
        RowSelection selection = selectedTable.getRowSelection();
        logger.info(String.format("Applying mask of %s points to %s tables", selection.originalRows.length, dataModel.getSelectedStarTables().size()));
        
        for (int i=0; i<selection.selectedTables.length; i++) {
            selection.selectedTables[i].applyMasks(selection.selectedRows[i]);
        }
        
        VisualizerChangeEvent.getInstance().fire(dataModel.getSelectedSed(), VisualizerCommand.REDRAW);
        resetDataTables();
    }//GEN-LAST:event_applyMaskButtonActionPerformed

    private void clearMaskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearMaskButtonActionPerformed
        IrisStarJTable selectedTable = getSelectedIrisJTable();
        if (selectedTable == null) return;
        
        RowSelection selection = selectedTable.getRowSelection();
        logger.info(String.format("Removing masks of %s points from %s tables", selection.originalRows.length, dataModel.getSelectedStarTables().size()));
        
        for (int i=0; i<selection.selectedTables.length; i++) {
            selection.selectedTables[i].clearMasks(selection.selectedRows[i]);
        }
        
        VisualizerChangeEvent.getInstance().fire(dataModel.getSelectedSed(), VisualizerCommand.REDRAW);
        resetDataTables();
    }//GEN-LAST:event_clearMaskButtonActionPerformed

    private void clearAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearAllButtonActionPerformed
        IrisStarTable.clearAllMasks(dataModel.getSedStarTables());
        VisualizerChangeEvent.getInstance().fire(dataModel.getSelectedSed(), VisualizerCommand.REDRAW);
        resetDataTables();
    }//GEN-LAST:event_clearAllButtonActionPerformed

    private void extractToSedMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractToSedMenuItemActionPerformed
        extractSelectionToSed();
    }//GEN-LAST:event_extractToSedMenuItemActionPerformed

    private void extractButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractButtonActionPerformed
        extractSelectionToSed();
    }//GEN-LAST:event_extractButtonActionPerformed

    private void selectAllButtonActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_selectAllButtonActionPerformed
        JTable table = getSelectedJTable();
        if (table == null) {
            return;
        }
        table.selectAll();
    }// GEN-LAST:event_selectAllButtonActionPerformed

    private JTable getSelectedJTable() {
        JPanel panel = (JPanel) dataTabsPane.getSelectedComponent();
        if (panel == null) {
            return null;
        }
        return (JTable) ((JScrollPane) panel.getComponent(0)).getViewport().getComponent(0);
    }
    
    private IrisStarJTable getSelectedIrisJTable() {
        int idx = this.dataTabsPane.getSelectedIndex();
        if (idx == 0) {
            return this.plotterStarJTable;
        } else if (idx == 1) {
            return this.pointStarJTable;
        } else {
            return null;
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyMaskButton;
    private javax.swing.JMenuItem applyMaskMenuItem;
    private javax.swing.JMenuItem broadcastToSampMenuItem;
    private javax.swing.JButton clearAllButton;
    private javax.swing.JMenuItem clearAllMenuItem;
    private javax.swing.JButton clearMaskButton;
    private javax.swing.JButton clearSelectionButton;
    private javax.swing.JMenuItem clearSelectionMenuItem;
    private javax.swing.JMenuItem createNewColumnMenuItem;
    private javax.swing.JMenuItem createSubsetMenuItem;
    private javax.swing.JSplitPane dataPane;
    private javax.swing.JTabbedPane dataTabsPane;
    private javax.swing.JMenu editMenu;
    private javax.swing.JButton extractButton;
    private javax.swing.JMenuItem extractToSedMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JTextField filterExpressionField;
    private javax.swing.JButton invertSelectionButton;
    private javax.swing.JMenuItem invertSelectionMenuItem;
    private javax.swing.JMenuBar jMenuBar1;
    private cfa.vo.iris.visualizer.metadata.MetadataJTable metadataJTable1;
    private javax.swing.JPanel plotterMetadataPanel;
    private javax.swing.JScrollPane plotterMetadataScrollPane;
    private cfa.vo.iris.visualizer.metadata.IrisStarJTable plotterStarJTable;
    private javax.swing.JPanel pointMetadataPanel;
    private javax.swing.JScrollPane pointMetadataScrollPane;
    private cfa.vo.iris.visualizer.metadata.IrisStarJTable pointStarJTable;
    private javax.swing.JMenuItem removeMasksMenuItem;
    private javax.swing.JMenuItem restoreSetMenuItem;
    private javax.swing.JPanel segmentMetadataPanel;
    private javax.swing.JScrollPane segmentMetadataScrollPane;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JMenuItem selectAllMenuItem;
    private javax.swing.JMenu selectMenu;
    private javax.swing.JButton selectPointsButton;
    private javax.swing.JScrollPane starTableScrollPane;
    protected cfa.vo.iris.visualizer.metadata.StarTableJTree starTableTree;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
