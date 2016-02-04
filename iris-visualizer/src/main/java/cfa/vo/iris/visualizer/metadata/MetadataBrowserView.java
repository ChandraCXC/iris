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
package cfa.vo.iris.visualizer.metadata;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.stil.StarTableAdapter;
import cfa.vo.iris.visualizer.plotter.SegmentLayer;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.sedlib.ISegment;
import cfa.vo.sedlib.Segment;
import uk.ac.starlink.table.EmptyStarTable;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.gui.StarJTable;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

public class MetadataBrowserView extends JInternalFrame {
    
    private static final long serialVersionUID = 1L;
    
    public static final String PROP_SELECTED_TABLE = "selectedTables";
    public static final String PROP_SELECTED_SED = "selectedSed";
    protected static final String MB_WINDOW_TITLE = "Metadata Browser";
    
    private static final StarTable EMPTY_STARTABLE = new EmptyStarTable();
    
    protected IWorkspace ws;
    protected VisualizerComponentPreferences preferences;
    
    protected JList<StarTable> selectedTables;
    protected StarTable selectedStarTable;
    protected ExtSed selected;
    
    // Window Objects
    protected StarJTable metadataTable;
    protected final JTextField textField;
    protected final JButton btnFilter;
    protected final JButton btnApplyMask;
    protected final JButton selectAll;
    protected final JScrollPane segmentListScrollPane = new JScrollPane();
    protected final JScrollPane segmentDataScrollPane = new JScrollPane();
    
    // Menu Items
    final JMenuBar menuBar = new JMenuBar();
    final JMenu extractMenu = new JMenu("Extract\n");
    final JMenuItem mntmExtraeextractlkj = new JMenuItem("Extract to new SED");
    final JMenuItem mntmSendToSamp = new JMenuItem("Broadcast to SAMP");
    final JMenuItem mntmCreate = new JMenuItem("Create Subset");
    final JMenu mnEdit = new JMenu("Edit");
    final JMenuItem menuItem_1 = new JMenuItem("Create New Colum");
    final JMenuItem menuItem_2 = new JMenuItem("Restore Set");
    final JMenu mnSelect = new JMenu("Select");
    final JMenuItem menuItem = new JMenuItem("Invert Selection");
    final JMenuItem mntmApplyMask = new JMenuItem("Apply Mask");
    final JMenuItem mntmNewMenuItem = new JMenuItem("New menu item");
    final JPanel segmentListPanel = new JPanel();
    
    final StarTableCellRenderer starTableCellRenderer = new StarTableCellRenderer();
    
    /**
     * Create the frame.
     * @return 
     * 
     * @throws Exception
     */
    public MetadataBrowserView(IWorkspace ws, VisualizerComponentPreferences preferences) throws Exception {
        this.ws = ws;
        this.preferences = preferences;
        
        setToolTipText(
                "View and browse metadata for existing SEDs in the plotting window");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSelected(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        setName(MB_WINDOW_TITLE);
        setTitle(MB_WINDOW_TITLE);
        
        getContentPane().setForeground(Color.WHITE);
        getContentPane().setName("content");
        getContentPane().setBackground(Color.LIGHT_GRAY);
        setBackground(Color.LIGHT_GRAY);
        getContentPane().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
            }
        });
        setBounds(100, 100, 1013, 599);
        
        textField = new JTextField();
        textField.setColumns(10);
        
        btnFilter = new JButton("Select Points");
        btnFilter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            }
        });
        
        btnApplyMask = new JButton("Apply Mask");
        btnApplyMask.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            }
        });

        // List of startables (segments) setup
        selectedTables = new JList<>(new DefaultListModel<StarTable>());
        selectedTables.setName("selectedTables");
        selectedTables.setCellRenderer(starTableCellRenderer);
        selectedTables.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectedTables.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (lse.getValueIsAdjusting()) {
                    return;
                }
                
                setSelectedStarTable(selectedTables.getSelectedIndex());
            }
        });
        
        segmentListScrollPane.setName("segmentListScrollPane");
        segmentListScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        segmentListScrollPane.setBorder(new TitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED, new Color(0, 0, 0), null), "Selected SED", TitledBorder.CENTER, TitledBorder.TOP, null, null));
        segmentListScrollPane.setViewportView(selectedTables);
        
        // Primary data table scroll pane setup
        segmentDataScrollPane.setName("segmentDataScrollPane");
        segmentDataScrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        segmentDataScrollPane.setViewportBorder(
                new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        segmentDataScrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        metadataTable = new StarJTable(true);
        metadataTable.setName("metadataTable");
        metadataTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        segmentDataScrollPane.setViewportView(metadataTable);
        metadataTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                // TODO: Something
            }
        });
        
        selectAll = new JButton("Select All");
        
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(12)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(segmentListPanel, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE)
                            .addGap(5)
                            .addComponent(segmentDataScrollPane, GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(selectAll)
                            .addGap(117)
                            .addComponent(textField, GroupLayout.PREFERRED_SIZE, 305, GroupLayout.PREFERRED_SIZE)
                            .addGap(6)
                            .addComponent(btnFilter, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                            .addGap(6)
                            .addComponent(btnApplyMask, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)))
                    .addGap(12))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(12)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(segmentDataScrollPane, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                            .addGap(11))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(segmentListPanel, GroupLayout.PREFERRED_SIZE, 479, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)))
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGap(2)
                            .addComponent(selectAll, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                        .addComponent(textField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnFilter, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnApplyMask, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                    .addGap(10))
        );
        GroupLayout gl_panel = new GroupLayout(segmentListPanel);
        gl_panel.setHorizontalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addComponent(segmentListScrollPane, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE)
        );
        gl_panel.setVerticalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addComponent(segmentListScrollPane, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 479, GroupLayout.PREFERRED_SIZE)
        );
        segmentListPanel.setLayout(gl_panel);
        getContentPane().setLayout(groupLayout);
        
        setJMenuBar(menuBar);
        menuBar.add(extractMenu);
        extractMenu.add(mntmExtraeextractlkj);
        extractMenu.add(mntmSendToSamp);
        extractMenu.add(mntmCreate);
        menuBar.add(mnEdit);
        mnEdit.add(menuItem_1);
        mnEdit.add(menuItem_2);
        menuBar.add(mnSelect);
        mnSelect.add(menuItem);
        mnSelect.add(mntmApplyMask);
        mnSelect.add(mntmNewMenuItem);
        
        reset();
    }

    public void reset() {
        
        // Multiple threads resetting this simultaneously can do weird things
        // to JList and JTable objects.
        synchronized(this) {
            setSelectedSed();
        }
    }
    
    private void setSelectedSed() {
        selected = (ExtSed) ws.getSedManager().getSelected();
        if (selected == null) {
            return;
        }
        
        setSedId();
        setSelectedTables();
        segmentListPanel.repaint();
    }
    
    private void setSedId() {
        TitledBorder border = (TitledBorder) segmentListScrollPane.getBorder();
        border.setTitle(selected.getId());
    }
    
    private void setSelectedTables() {
        // First clear SED list
        DefaultListModel<StarTable> model = (DefaultListModel<StarTable>) selectedTables.getModel();
        model.clear();
        
        // Read all startables to list
        for (SegmentLayer layer : preferences.getSelectedLayers()) {
            model.addElement(layer.getInSource());
        }
        
        segmentListScrollPane.setViewportView(selectedTables);
        
        // Try to set selected table to the first by default
        setSelectedStarTable(0);
    }
    
    private void setSelectedStarTable(int i) {
        // Select nothing if there is nothing, or if the selected index is greater than the 
        // number of segments (say if the list is empty).
        if (i >= selectedTables.getModel().getSize() || i < 0) {
            selectedStarTable = EMPTY_STARTABLE;
        } else {
            selectedStarTable = selectedTables.getModel().getElementAt(i);
        }
        metadataTable.setStarTable(selectedStarTable, true);
        
        segmentDataScrollPane.setViewportView(metadataTable);
        segmentDataScrollPane.repaint();
    }
    
    static class StarTableCellRenderer extends JLabel implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) 
        {
            StarTable entry = (StarTable) value;
            setText(entry.getName());
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
}
