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

import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;

import org.apache.commons.lang.ArrayUtils;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.plotter.SegmentLayer;
import cfa.vo.iris.visualizer.preferences.SedPreferences;
import cfa.vo.iris.visualizer.preferences.VisualizerChangeEvent;
import cfa.vo.iris.visualizer.preferences.VisualizerCommand;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.iris.visualizer.preferences.VisualizerListener;
import cfa.vo.iris.visualizer.stil.tables.ColumnInfoMatcher;
import cfa.vo.iris.visualizer.stil.tables.IrisStarTable;
import cfa.vo.iris.visualizer.stil.tables.SegmentColumnInfoMatcher;
import cfa.vo.iris.visualizer.stil.tables.StackedStarTable;
import cfa.vo.iris.visualizer.stil.tables.UtypeColumnInfoMatcher;

import java.util.LinkedList;
import java.util.List;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.gui.StarJTable;

public class MetadataBrowserMainView extends javax.swing.JInternalFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger
            .getLogger(MetadataBrowserMainView.class.getName());
    
    public static final String MB_WINDOW_NAME = "Metadata Browser (%s)";

    final VisualizerComponentPreferences preferences;
    final IWorkspace ws;
    
    ExtSed selectedSed; // Selected ws sed
    List<IrisStarTable> selectedTables; // list of star tables associated with selectedSed
    List<IrisStarTable> selectedStarTables; // list of selected StarTables from selectedTables
    StackedStarTable plotterDataTable; // Single star table container for all plotted data
    StackedStarTable segmentDataTable; // Single star table container for all point metadata
    
    // Used for star table stacking the plotter and segment data startables.
    ColumnInfoMatcher plotterInfoMatcher;
    ColumnInfoMatcher segmentInfoMatcher;
    
    /**
     * Creates new form MetadataBrowser
     */
    public MetadataBrowserMainView(IWorkspace ws,
            VisualizerComponentPreferences preferences) 
    {
        this.ws = ws;
        this.preferences = preferences;
        this.plotterInfoMatcher = new SegmentColumnInfoMatcher();
        this.segmentInfoMatcher = new UtypeColumnInfoMatcher();
        
        initComponents();
        setChangeListener();

        resetData();
    }

    protected void setChangeListener() {
        VisualizerChangeEvent.getInstance().add(new MetadataChangeListener());
    }

    public void resetData() {
        setSelectedSed((ExtSed) ws.getSedManager().getSelected());
        logger.info("Resetting metadata browser");
        
        updateTitle();
        updateSelectedTables();
        
        // Select 0th indexed segment if available
        starTableList.setSelectedIndex(0);
        updateSelectedStarTables(new int[] {0});
        updateDataTables();
    }
    
    public void redrawData() {
        logger.info("Re-drawing metadata browser");
        
        // Attempt to maintain segment selection
        int[] selection = starTableList.getSelectedIndices();
        
        updateTitle();
        updateSelectedTables();
        updateDataTables();

        starTableList.setSelectedIndices(selection);
        updateSelectedStarTables(selection);
    }
    
    private void updateTitle() {
        title = String.format(MB_WINDOW_NAME, 
                selectedSed == null ? "Select SED" : selectedSed.getId());
        setTitle(title);
    }
    
    private void updateSelectedTables() {
        List<IrisStarTable> newTables = new LinkedList<>();
        
        // If no SED selected then just leave an empty list
        if (selectedSed != null) {
            // Read all startables to list
            SedPreferences prefs = preferences.getSelectedSedPreferences();
            
            for (int i=0; i<selectedSed.getNumberOfSegments(); i++) {
                SegmentLayer layer = prefs.getSegmentPreferences(selectedSed.getSegment(i));
                newTables.add(layer.getInSource());
            }
        }
        
        // Update segment metadata table
        segmentJTable.setModel(new IrisMetadataTableModel((List<StarTable>)(List<?>) newTables));
        StarJTable.configureColumnWidths(segmentJTable, 200, 10);
        
        setSelectedTables(newTables);
    }
    
    private void updateSelectedStarTables(int[] indexes) {
        List<IrisStarTable> newTables = new LinkedList<>();
        for (int i : indexes) {
            if (i < selectedTables.size() && i >= 0) {
                newTables.add(selectedTables.get(i));
            }
        }
        setSelectedStarTables(newTables);
    }
    
    
    private void updateDataTables() {
        List<StarTable> plotterDataTables = new LinkedList<>();
        List<StarTable> segmentDataTables = new LinkedList<>();
        
        for (IrisStarTable table : selectedStarTables) {
            plotterDataTables.add(table.getPlotterTable());
            segmentDataTables.add(table.getSegmentDataTable());
        }
        
        plotterDataTable = new StackedStarTable(plotterDataTables, plotterInfoMatcher);
        segmentDataTable = new StackedStarTable(segmentDataTables, segmentInfoMatcher);
        
        setPlotterDataTable(plotterDataTable);
        setSegmentDataTable(segmentDataTable);
    }
    
    /**
     * Specifies a star table (by index) and a row to be added to the selected plotter/
     * data table tabs' row selections.
     * 
     * @param starTableIndex - index of the star table in the selectedTables list.
     * @param irow - row to be selected in the star table.
     */
    public void addRowToSelection(int starTableIndex, int irow) {
        
        // If the star table isn't currently selected, add it to the selection
        int[] selection = starTableList.getSelectedIndices();
        if (ArrayUtils.indexOf(selection, starTableIndex) < 0) {
            starTableList.setSelectedIndices(ArrayUtils.add(selection, starTableIndex));
        }
        
        // TODO: This will be complicated by masking and sorting, those methods should be taken
        // care of by Iris star tables, and the sorting model in our jtables.
        
        // Select the correct row
        IrisStarTable selectedTable = selectedTables.get(starTableIndex);
        int index = IrisStarTable.getTableStartIndex(selectedStarTables, selectedTable);
        plotterStarJTable.selectRowIndex(index + irow);
    }
    
    
    /*
     * getters and setters
     */
    
    
    public ExtSed getSelectedSed() {
        return selectedSed;
    }
    
    public static final String PROP_SELECTED_SED = "selectedSed";
    public void setSelectedSed(ExtSed sed) {
        ExtSed oldSed = selectedSed;
        this.selectedSed = sed;
        firePropertyChange(PROP_SELECTED_SED, oldSed, selectedSed);
    }
    
    public List<IrisStarTable> getSelectedTables() {
        return selectedTables;
    }
    
    public static final String PROP_SELECTED_TABLES = "selectedTables";
    public void setSelectedTables(List<IrisStarTable> newTables) {
        List<IrisStarTable> oldTables = selectedTables;
        this.selectedTables = newTables;
        firePropertyChange(PROP_SELECTED_TABLES, oldTables, selectedTables);
    }
    
    public List<IrisStarTable> getSelectedStarTables() {
        return selectedStarTables;
    }

    public static final String PROP_SELECTED_STARTABLES = "selectedStarTables";
    public void setSelectedStarTables(List<IrisStarTable> newStarTables) {
        List<IrisStarTable> oldStarTables = selectedStarTables;
        this.selectedStarTables = newStarTables;
        firePropertyChange(PROP_SELECTED_STARTABLES, oldStarTables, newStarTables);
    }
    
    public StackedStarTable getPlotterDataTable() {
        return plotterDataTable;
    }

    public static final String PROP_PLOTTER_TABLE = "plotterDataTable";
    public void setPlotterDataTable(StackedStarTable newTable) {
        StackedStarTable oldTable = plotterDataTable;
        this.plotterDataTable = newTable;
        
        plotterStarJTable.setStarTable(plotterDataTable);
        plotterStarJTable.configureColumnWidths(200, 30);
        
        firePropertyChange(PROP_PLOTTER_TABLE, oldTable, plotterDataTable);
    }
    
    public StackedStarTable getSegmentDataTable() {
        return segmentDataTable;
    }

    public static final String PROP_SEGMENT_TABLE = "segmentDataTable";
    public void setSegmentDataTable(StackedStarTable newTable) {
        StackedStarTable oldTable = segmentDataTable;
        this.segmentDataTable = newTable;

        pointStarJTable.setUtypeAsNames(true);
        pointStarJTable.setStarTable(segmentDataTable);
        pointStarJTable.configureColumnWidths(200, 30);
        
        firePropertyChange(PROP_SEGMENT_TABLE, oldTable, plotterDataTable);
    }
    
    /**
     * Listener class for changes in the visualizer component.
     *
     */
    private class MetadataChangeListener implements VisualizerListener {

        @Override
        public void process(ExtSed source, VisualizerCommand payload) {
            if (VisualizerCommand.RESET.equals(payload)
                || VisualizerCommand.SELECTED.equals(payload))
            {
                resetData();
            }
            else if (VisualizerCommand.REDRAW.equals(payload)) {
                redrawData();
            }
        }
    }
    
    static class StarTableCellRenderer extends JLabel implements ListCellRenderer<StarTable> {
        private static final long serialVersionUID = 1L;
        
        @Override
        public Component getListCellRendererComponent(
                JList<? extends StarTable> list, StarTable entry, int index,
                boolean isSelected, boolean cellHasFocus) 
        {
            if (entry != null) {
                setText(entry.getName());
            }
            
            setOpaque(true);
            
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            return this;
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        filterExpressionField = new javax.swing.JTextField();
        selectPointsButton = new javax.swing.JButton();
        applyMaskButton = new javax.swing.JButton();
        selectAllButton = new javax.swing.JButton();
        clearSelectionButton = new javax.swing.JButton();
        invertSelectionButton = new javax.swing.JButton();
        clearMaskButton = new javax.swing.JButton();
        clearAllButton = new javax.swing.JButton();
        dataPane = new javax.swing.JSplitPane();
        starTableScrollPane = new javax.swing.JScrollPane();
        starTableList = new javax.swing.JList<IrisStarTable>();
        dataTabsPane = new javax.swing.JTabbedPane();
        plotterMetadataPanel = new javax.swing.JPanel();
        plotterMetadataScrollPane = new javax.swing.JScrollPane();
        plotterStarJTable = new cfa.vo.iris.visualizer.stil.IrisStarJTable();
        pointMetadataPanel = new javax.swing.JPanel();
        pointMetadataScrollPane = new javax.swing.JScrollPane();
        pointStarJTable = new cfa.vo.iris.visualizer.stil.IrisStarJTable();
        segmentMetadataPanel = new javax.swing.JPanel();
        segmentMetadataScrollPane = new javax.swing.JScrollPane();
        segmentJTable = new javax.swing.JTable();
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
        setTitle("Metadata Browser");
        setName(""); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        filterExpressionField.setText("Filter Expression");
        filterExpressionField.setToolTipText("Enter a column selection expression");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(filterExpressionField, gridBagConstraints);

        selectPointsButton.setFont(new java.awt.Font("DejaVu Sans", 0, 12)); // NOI18N
        selectPointsButton.setText("Select Points");
        selectPointsButton.setToolTipText("Select points matching the filter expression");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(selectPointsButton, gridBagConstraints);

        applyMaskButton.setFont(new java.awt.Font("DejaVu Sans", 0, 12)); // NOI18N
        applyMaskButton.setText("Apply Masks");
        applyMaskButton.setToolTipText("Mask the selected points");
        applyMaskButton.setName(""); // NOI18N
        applyMaskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyMaskButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
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
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(selectAllButton, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(invertSelectionButton, gridBagConstraints);

        clearMaskButton.setFont(new java.awt.Font("DejaVu Sans", 0, 12)); // NOI18N
        clearMaskButton.setText("Remove Masks");
        clearMaskButton.setToolTipText("Remove the mask from the selected points");
        clearMaskButton.setName(""); // NOI18N
        clearMaskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearMaskButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
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
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(clearAllButton, gridBagConstraints);

        dataPane.setToolTipText("");
        dataPane.setLastDividerLocation(25);
        dataPane.setMinimumSize(new java.awt.Dimension(50, 50));
        dataPane.setName("dataPane"); // NOI18N

        starTableScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Segments", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP));
        starTableScrollPane.setName("starTableScrollPane"); // NOI18N
        starTableScrollPane.setOpaque(false);
        starTableScrollPane.setPreferredSize(new java.awt.Dimension(270, 100));

        starTableList.setCellRenderer(new StarTableCellRenderer());

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${selectedTables}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, starTableList);
        bindingGroup.addBinding(jListBinding);

        starTableList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                handleStarTableSelection(evt);
            }
        });
        starTableScrollPane.setViewportView(starTableList);

        dataPane.setLeftComponent(starTableScrollPane);

        dataTabsPane.setName("dataTabsPane"); // NOI18N

        plotterMetadataScrollPane.setToolTipText("");
        plotterMetadataScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        plotterMetadataScrollPane.setViewportView(plotterStarJTable);

        javax.swing.GroupLayout plotterMetadataPanelLayout = new javax.swing.GroupLayout(plotterMetadataPanel);
        plotterMetadataPanel.setLayout(plotterMetadataPanelLayout);
        plotterMetadataPanelLayout.setHorizontalGroup(
            plotterMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(plotterMetadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)
        );
        plotterMetadataPanelLayout.setVerticalGroup(
            plotterMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(plotterMetadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );

        dataTabsPane.addTab("Data", plotterMetadataPanel);

        pointMetadataScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, plotterStarJTable, org.jdesktop.beansbinding.ELProperty.create("${selectionModel}"), pointStarJTable, org.jdesktop.beansbinding.BeanProperty.create("selectionModel"));
        bindingGroup.addBinding(binding);

        pointMetadataScrollPane.setViewportView(pointStarJTable);

        javax.swing.GroupLayout pointMetadataPanelLayout = new javax.swing.GroupLayout(pointMetadataPanel);
        pointMetadataPanel.setLayout(pointMetadataPanelLayout);
        pointMetadataPanelLayout.setHorizontalGroup(
            pointMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pointMetadataScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)
        );
        pointMetadataPanelLayout.setVerticalGroup(
            pointMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pointMetadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );

        dataTabsPane.addTab("Point Metadata", pointMetadataPanel);

        segmentMetadataScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        segmentJTable.setModel(new IrisMetadataTableModel());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, starTableList, org.jdesktop.beansbinding.ELProperty.create("${selectionModel}"), segmentJTable, org.jdesktop.beansbinding.BeanProperty.create("selectionModel"));
        bindingGroup.addBinding(binding);

        segmentMetadataScrollPane.setViewportView(segmentJTable);

        javax.swing.GroupLayout segmentMetadataPanelLayout = new javax.swing.GroupLayout(segmentMetadataPanel);
        segmentMetadataPanel.setLayout(segmentMetadataPanelLayout);
        segmentMetadataPanelLayout.setHorizontalGroup(
            segmentMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(segmentMetadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)
        );
        segmentMetadataPanelLayout.setVerticalGroup(
            segmentMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(segmentMetadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );

        dataTabsPane.addTab("Segment Metadata", segmentMetadataPanel);

        dataPane.setRightComponent(dataTabsPane);
        dataTabsPane.getAccessibleContext().setAccessibleName("");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(dataPane, gridBagConstraints);

        fileMenu.setText("File");
        fileMenu.setName("Extract"); // NOI18N

        extractToSedMenuItem.setText("Extract to New SED");
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

    private void handleStarTableSelection(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_handleStarTableSelection
        updateSelectedStarTables(starTableList.getSelectedIndices());
        updateDataTables();
    }//GEN-LAST:event_handleStarTableSelection

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
        // Applying a mask always acts on the selections from the plotter and 
        // segment data tables.
        int[] selected = this.plotterStarJTable.getSelectedRows();
        
        logger.info(String.format("Applying mask of %s points to %s tables", selected.length, selectedStarTables.size()));
        IrisStarTable.applyFilters(selectedStarTables, selected);
        VisualizerChangeEvent.getInstance().fire(selectedSed, VisualizerCommand.REDRAW);
    }//GEN-LAST:event_applyMaskButtonActionPerformed

    private void clearMaskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearMaskButtonActionPerformed
        int[] selected = this.plotterStarJTable.getSelectedRows();
        
        logger.info(String.format("Removing masks of %s points from %s tables", selected.length, selectedStarTables.size()));
        IrisStarTable.clearFilters(selectedStarTables, selected);
        VisualizerChangeEvent.getInstance().fire(selectedSed, VisualizerCommand.REDRAW);
    }//GEN-LAST:event_clearMaskButtonActionPerformed

    private void clearAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearAllButtonActionPerformed
        IrisStarTable.clearAllFilters(selectedTables);
        VisualizerChangeEvent.getInstance().fire(selectedSed, VisualizerCommand.REDRAW);
    }//GEN-LAST:event_clearAllButtonActionPerformed

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
    private javax.swing.JMenuItem extractToSedMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JTextField filterExpressionField;
    private javax.swing.JButton invertSelectionButton;
    private javax.swing.JMenuItem invertSelectionMenuItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel plotterMetadataPanel;
    private javax.swing.JScrollPane plotterMetadataScrollPane;
    private cfa.vo.iris.visualizer.stil.IrisStarJTable plotterStarJTable;
    private javax.swing.JPanel pointMetadataPanel;
    private javax.swing.JScrollPane pointMetadataScrollPane;
    private cfa.vo.iris.visualizer.stil.IrisStarJTable pointStarJTable;
    private javax.swing.JMenuItem removeMasksMenuItem;
    private javax.swing.JMenuItem restoreSetMenuItem;
    private javax.swing.JTable segmentJTable;
    private javax.swing.JPanel segmentMetadataPanel;
    private javax.swing.JScrollPane segmentMetadataScrollPane;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JMenuItem selectAllMenuItem;
    private javax.swing.JMenu selectMenu;
    private javax.swing.JButton selectPointsButton;
    private javax.swing.JList<IrisStarTable> starTableList;
    private javax.swing.JScrollPane starTableScrollPane;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
