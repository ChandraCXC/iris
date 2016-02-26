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
import javax.swing.ListCellRenderer;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.plotter.SegmentLayer;
import cfa.vo.iris.visualizer.preferences.SedPreferences;
import cfa.vo.iris.visualizer.preferences.VisualizerChangeEvent;
import cfa.vo.iris.visualizer.preferences.VisualizerCommand;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.iris.visualizer.preferences.VisualizerListener;
import cfa.vo.iris.visualizer.stil.IrisStarTable;
import java.util.ArrayList;
import java.util.List;
import uk.ac.starlink.table.EmptyStarTable;
import uk.ac.starlink.table.StarTable;

public class MetadataBrowserMainView extends javax.swing.JInternalFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger
            .getLogger(MetadataBrowserMainView.class.getName());
    
    private static final StarTable EMPTY_STARTABLE = new EmptyStarTable();

    public static final String MB_WINDOW_NAME = "Metadata Browser (%s)";
    
    public static final String PROP_SELECTEDTABLE = "selectedStarTable";
    public static final String PROP_SELECTEDTABLES = "selectedTables";

    protected final VisualizerComponentPreferences preferences;
    protected final IWorkspace ws;
    
    protected ExtSed selectedSed;
    protected IrisStarTable selectedStarTable;
    protected List<IrisStarTable> selectedTables = new ArrayList<>();
    
    /**
     * Creates new form MetadataBrowser
     */
    public MetadataBrowserMainView(IWorkspace ws,
            VisualizerComponentPreferences preferences) 
    {
        this.preferences = preferences;
        this.ws = ws;

        initComponents();
        setChangeListener();

        resetData();
    }

    protected void setChangeListener() {
        VisualizerChangeEvent.getInstance().add(new MetadataChangeListener());
    }

    public synchronized void resetData() {
        this.selectedSed = (ExtSed) ws.getSedManager().getSelected();
        
        setTitle();
        setDataTables();
        setSelectedStarTable(0);
    }
    
    private void setTitle() {
        title = String.format(MB_WINDOW_NAME, 
                selectedSed == null ? "Select SED" : selectedSed.getId());
        setTitle(title);
    }
    
    private void setDataTables() {
        
        List<IrisStarTable> newTables = new ArrayList<>();
        
        // If no SED selected then just leave an empty list
        if (selectedSed != null) {
            // Read all startables to list
            SedPreferences prefs = preferences.getSelectedSedPreferences();
            
            for (int i=0; i<selectedSed.getNumberOfSegments(); i++) {
                SegmentLayer layer = prefs.getSegmentPreferences(selectedSed.getSegment(i));
                newTables.add(layer.getInSource());
            }
        }
        
        setSelectedTables(newTables);
    }
    
    private void setSelectedStarTable(int index) {
        
        IrisStarTable newTable = null;
        
        // Only select a new table if the selection index is within the bounds 
        // or the list of star tables.
        if (index < selectedTables.size() && index >= 0) {
            newTable = selectedTables.get(index);
        }

        if (newTable == null) {
            plotterStarJTable.setStarTable(EMPTY_STARTABLE, false);
            pointStarJTable.setStarTable(EMPTY_STARTABLE, false);
        } else {
            plotterStarJTable.setStarTable(newTable.getPlotterTable(), true);
            pointStarJTable.setStarTable(newTable.getDataTable(), true);
        }
        
        // Autoconfigures the column width for viewing the tables.
        plotterStarJTable.configureColumnWidths(200, 20);
        pointStarJTable.configureColumnWidths(200, 20);
        
        // TODO: Implement some method for viewing StarTable metadata
        segmentStarJTable.setStarTable(EMPTY_STARTABLE, false);
        segmentStarJTable.configureColumnWidths(200, 20);
        //
        
        setSelectedStarTable(newTable);
    }
    
    /*
     * 
     * Getters and Setters 
     *  
     */
    
    public List<IrisStarTable> getSelectedTables() {
        return selectedTables;
    }    
    
    public void setSelectedTables(List<IrisStarTable> newTables) {
        List<IrisStarTable> oldTables = selectedTables;
        selectedTables = newTables;
        firePropertyChange(PROP_SELECTEDTABLES, oldTables, selectedTables);
    }
    
    public IrisStarTable getSelectedStarTable() {
        return selectedStarTable;
    }
    
    public void setSelectedStarTable(IrisStarTable table) {
        IrisStarTable oldTable = selectedStarTable;
        selectedStarTable = table;
        firePropertyChange(PROP_SELECTEDTABLE, oldTable, selectedStarTable);
    }
    
    /**
     * Listener class for changes in the visualizer component.
     *
     */
    private class MetadataChangeListener implements VisualizerListener {

        @Override
        public void process(ExtSed source, VisualizerCommand payload) {
            if (VisualizerCommand.RESET.equals(payload)
                || VisualizerCommand.SELECTED.equals(payload)
                || VisualizerCommand.REDRAW.equals(payload)) 
            {
                resetData();
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        filterExpressionField = new javax.swing.JTextField();
        selectPointsButton = new javax.swing.JButton();
        applyMaskButton = new javax.swing.JButton();
        selectAllButton = new javax.swing.JButton();
        invertSelectionButton = new javax.swing.JButton();
        dataPane = new javax.swing.JPanel();
        dataTabsPane = new javax.swing.JTabbedPane();
        plotterMetadataPanel = new javax.swing.JPanel();
        plotterMetadataScrollPane = new javax.swing.JScrollPane();
        plotterStarJTable = new cfa.vo.iris.visualizer.stil.IrisStarJTable();
        pointMetadataPanel = new javax.swing.JPanel();
        pointMetadataScrollPane = new javax.swing.JScrollPane();
        pointStarJTable = new cfa.vo.iris.visualizer.stil.IrisStarJTable();
        segmentMetadataPanel = new javax.swing.JPanel();
        segmentMetadataScrollPane = new javax.swing.JScrollPane();
        segmentStarJTable = new cfa.vo.iris.visualizer.stil.IrisStarJTable();
        starTableScrollPane = new javax.swing.JScrollPane();
        starTableList = new javax.swing.JList<IrisStarTable>();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        extractToSedMenuItem = new javax.swing.JMenuItem();
        broadcastToSampMenuItem = new javax.swing.JMenuItem();
        createSubsetMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        createNewColumnMenuItem = new javax.swing.JMenuItem();
        restoreSetMenuItem = new javax.swing.JMenuItem();
        selectMenu = new javax.swing.JMenu();
        invertSelectionMenuItem = new javax.swing.JMenuItem();
        applyMaskMenuItem = new javax.swing.JMenuItem();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Metadata Browser");
        setName(""); // NOI18N

        filterExpressionField.setText("Filter Expression");

        selectPointsButton.setText("Select Points");

        applyMaskButton.setText("Apply Mask");
        applyMaskButton.setName(""); // NOI18N

        selectAllButton.setText("Select All");
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });

        invertSelectionButton.setText("Invert Selection");
        invertSelectionButton.setActionCommand("");

        dataPane.setToolTipText("");
        dataPane.setName("dataPanel"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedSed.id}"), dataPane, org.jdesktop.beansbinding.BeanProperty.create("border"));
        bindingGroup.addBinding(binding);

        dataTabsPane.setName("dataTabsPane"); // NOI18N

        plotterMetadataScrollPane.setToolTipText("");
        plotterMetadataScrollPane.setViewportView(plotterStarJTable);

        javax.swing.GroupLayout plotterMetadataPanelLayout = new javax.swing.GroupLayout(plotterMetadataPanel);
        plotterMetadataPanel.setLayout(plotterMetadataPanelLayout);
        plotterMetadataPanelLayout.setHorizontalGroup(
            plotterMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plotterMetadataPanelLayout.createSequentialGroup()
                .addComponent(plotterMetadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 965, Short.MAX_VALUE)
                .addContainerGap())
        );
        plotterMetadataPanelLayout.setVerticalGroup(
            plotterMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(plotterMetadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
        );

        dataTabsPane.addTab("Plotter Data", plotterMetadataPanel);

        pointMetadataScrollPane.setViewportView(pointStarJTable);

        javax.swing.GroupLayout pointMetadataPanelLayout = new javax.swing.GroupLayout(pointMetadataPanel);
        pointMetadataPanel.setLayout(pointMetadataPanelLayout);
        pointMetadataPanelLayout.setHorizontalGroup(
            pointMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pointMetadataScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 977, Short.MAX_VALUE)
        );
        pointMetadataPanelLayout.setVerticalGroup(
            pointMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pointMetadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
        );

        dataTabsPane.addTab("Point Metadata", pointMetadataPanel);

        segmentMetadataScrollPane.setViewportView(segmentStarJTable);

        javax.swing.GroupLayout segmentMetadataPanelLayout = new javax.swing.GroupLayout(segmentMetadataPanel);
        segmentMetadataPanel.setLayout(segmentMetadataPanelLayout);
        segmentMetadataPanelLayout.setHorizontalGroup(
            segmentMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(segmentMetadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 977, Short.MAX_VALUE)
        );
        segmentMetadataPanelLayout.setVerticalGroup(
            segmentMetadataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(segmentMetadataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
        );

        dataTabsPane.addTab("Segment Metadata", segmentMetadataPanel);

        starTableScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Segments", 0, 1));
        starTableScrollPane.setName("starTableScrollPane"); // NOI18N
        starTableScrollPane.setOpaque(false);

        starTableList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
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

        javax.swing.GroupLayout dataPaneLayout = new javax.swing.GroupLayout(dataPane);
        dataPane.setLayout(dataPaneLayout);
        dataPaneLayout.setHorizontalGroup(
            dataPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(starTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataTabsPane)
                .addContainerGap())
        );
        dataPaneLayout.setVerticalGroup(
            dataPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dataTabsPane, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(dataPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(starTableScrollPane))
        );

        dataTabsPane.getAccessibleContext().setAccessibleName("Point Metadata");

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

        invertSelectionMenuItem.setText("Invert Selection");
        selectMenu.add(invertSelectionMenuItem);

        applyMaskMenuItem.setText("Apply Mask");
        selectMenu.add(applyMaskMenuItem);

        jMenuBar1.add(selectMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(selectAllButton, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(invertSelectionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(filterExpressionField, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectPointsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(applyMaskButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(220, 220, 220))
            .addComponent(dataPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(dataPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterExpressionField, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(selectPointsButton)
                    .addComponent(applyMaskButton)
                    .addComponent(selectAllButton)
                    .addComponent(invertSelectionButton))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void handleStarTableSelection(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_handleStarTableSelection
        setSelectedStarTable(starTableList.getSelectedIndex());
    }//GEN-LAST:event_handleStarTableSelection

    private void selectAllButtonActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_selectAllButtonActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_selectAllButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyMaskButton;
    private javax.swing.JMenuItem applyMaskMenuItem;
    private javax.swing.JMenuItem broadcastToSampMenuItem;
    private javax.swing.JMenuItem createNewColumnMenuItem;
    private javax.swing.JMenuItem createSubsetMenuItem;
    private javax.swing.JPanel dataPane;
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
    private javax.swing.JMenuItem restoreSetMenuItem;
    private javax.swing.JPanel segmentMetadataPanel;
    private javax.swing.JScrollPane segmentMetadataScrollPane;
    private cfa.vo.iris.visualizer.stil.IrisStarJTable segmentStarJTable;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JMenu selectMenu;
    private javax.swing.JButton selectPointsButton;
    private javax.swing.JList<IrisStarTable> starTableList;
    private javax.swing.JScrollPane starTableScrollPane;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
