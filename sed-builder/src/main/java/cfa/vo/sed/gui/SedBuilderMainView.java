/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
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
 * SedBuilderMainView.java
 *
 * Created on Dec 23, 2011, 3:05:23 PM
 */

package cfa.vo.sed.gui;

import cfa.vo.iris.events.MultipleSegmentEvent;
import cfa.vo.iris.events.MultipleSegmentListener;
import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.iris.events.SegmentListener;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.gui.widgets.SedList;
import cfa.vo.iris.interop.SedSAMPController;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.utils.NameResolver;
import cfa.vo.iris.utils.NameResolver.Position;
import cfa.vo.iris.utils.SkyCoordinates;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sedlib.DoubleParam;
import cfa.vo.sedlib.PositionParam;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.TextParam;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import java.awt.Component;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import jsky.catalog.Catalog;
import org.astrogrid.samp.client.SampException;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;

/**
 *
 * @author olaurino
 */
public class SedBuilderMainView extends JInternalFrame {

    private SedlibSedManager manager;

    private JFrame rootFrame;

    private LoadSegmentFrame loadFrame;

    private ExtSed sed;
    public static final String PROP_SED = "sed";

    /** Creates new form SedBuilderMainView */
    public SedBuilderMainView(final SedlibSedManager manager, JFrame rootFrame) {
        initComponents();

        this.manager = manager;
      
        loadFrame = new LoadSegmentFrame(manager);
        SedBuilder.getWorkspace().addFrame(loadFrame);

        this.rootFrame = rootFrame;

        sedPanel.setViewportView(new SedList(manager));
        setSed(manager.getSelected());

        SedEvent.getInstance().add(new SedListener() {

            @Override
            public void process(ExtSed source, SedCommand payload) {
                if(payload!=SedCommand.REMOVED) {
                    setSed(null);
                    setSed(source);
                }
                else
                    setSed(null);
            }
        });

        SegmentEvent.getInstance().add(new SegmentListener() {

            @Override
            public void process(Segment source, SegmentPayload payload) {
                ExtSed s = payload.getSed();
                setSed(s);
            }
        });

        MultipleSegmentEvent.getInstance().add(new MultipleSegmentListener() {

            @Override
            public void process(List<Segment> source, SegmentPayload payload) {
                ExtSed s = payload.getSed();
                setSed(s);
            }
        });

        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if(!lse.getValueIsAdjusting()) {
                    int[] selected = jTable1.getSelectedRows();
                    List<Segment> selSegs = new ArrayList();
                    for(int i=0; i<selected.length; i++) {
                        selSegs.add(sed.getSegment(selected[i]));
                    }
                    setSelectedSegments(selSegs);
                    setSegmentSelected(!selSegs.isEmpty());
                }
            }
        });
        
    }

    public LoadSegmentFrame getLoadSegmentFrame() {
        return loadFrame;
    }

    private void setSed(ExtSed sed) {
        this.sed = sed;
        boolean n = sed==null;
        sedName.setText(n? "" : sed.getId());
        setIsSed(!n);

        loadFrame.setSed(sed);

        List<Segment> list = new ArrayList();

        if(sed!=null)
            for(int i=0; i<sed.getNumberOfSegments(); i++)
                list.add(sed.getSegment(i));

        setSegments(list);
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jToolBar1 = new javax.swing.JToolBar();
        jLabel3 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        sedPanel = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        sedName = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel4 = new javax.swing.JLabel();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jToolBar3 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jButton13 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButton17 = new javax.swing.JButton();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("SED Builder");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        jLabel3.setText("SED: ");
        jLabel3.setName("jLabel3"); // NOI18N
        jToolBar1.add(jLabel3);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(SedBuilderMainView.class, this);
        jButton8.setAction(actionMap.get("newSed")); // NOI18N
        jButton8.setIcon(new ImageIcon(getClass().getResource("/list_add.png")));
        jButton8.setBorderPainted(false);
        jButton8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setMargin(new java.awt.Insets(2, 2, 0, 2));
        jButton8.setMaximumSize(new java.awt.Dimension(100, 50));
        jButton8.setMinimumSize(new java.awt.Dimension(65, 50));
        jButton8.setName("jButton8"); // NOI18N
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton8);

        jButton2.setAction(actionMap.get("removeSed")); // NOI18N
        jButton2.setIcon(new ImageIcon(getClass().getResource("/edit_clear_list.png")));
        jButton2.setBorderPainted(false);
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setMargin(new java.awt.Insets(2, 2, 0, 2));
        jButton2.setMaximumSize(new java.awt.Dimension(100, 50));
        jButton2.setMinimumSize(new java.awt.Dimension(65, 50));
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${isSed}"), jButton2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar1.add(jButton2);

        jButton9.setAction(actionMap.get("saveSed")); // NOI18N
        jButton9.setIcon(new ImageIcon(getClass().getResource("/document_save_all.png")));
        jButton9.setBorderPainted(false);
        jButton9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setMargin(new java.awt.Insets(2, 2, 0, 2));
        jButton9.setMaximumSize(new java.awt.Dimension(100, 50));
        jButton9.setMinimumSize(new java.awt.Dimension(65, 50));
        jButton9.setName("jButton9"); // NOI18N
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${isSed}"), jButton9, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar1.add(jButton9);

        jButton11.setAction(actionMap.get("duplicateSed")); // NOI18N
        jButton11.setIcon(new ImageIcon(getClass().getResource("/editcopy.png")));
        jButton11.setBorderPainted(false);
        jButton11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton11.setFocusable(false);
        jButton11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton11.setMargin(new java.awt.Insets(2, 2, 0, 2));
        jButton11.setMaximumSize(new java.awt.Dimension(100, 50));
        jButton11.setMinimumSize(new java.awt.Dimension(65, 50));
        jButton11.setName("jButton11"); // NOI18N
        jButton11.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${isSed}"), jButton11, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar1.add(jButton11);

        jButton10.setAction(actionMap.get("broadcast")); // NOI18N
        jButton10.setIcon(new ImageIcon(getClass().getResource("/file_export.png")));
        jButton10.setBorderPainted(false);
        jButton10.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton10.setFocusable(false);
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton10.setMargin(new java.awt.Insets(2, 2, 0, 2));
        jButton10.setMaximumSize(new java.awt.Dimension(100, 50));
        jButton10.setMinimumSize(new java.awt.Dimension(65, 50));
        jButton10.setName("jButton10"); // NOI18N
        jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${isSed}"), jButton10, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar1.add(jButton10);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar1.add(jSeparator1);

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(150);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Open SEDs"));
        jPanel3.setName("jPanel3"); // NOI18N

        sedPanel.setBorder(null);
        sedPanel.setMinimumSize(new java.awt.Dimension(100, 20));
        sedPanel.setName("sedPanel"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, sedPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sedPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel3);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Selected SED"));
        jPanel1.setName("jPanel1"); // NOI18N

        jLabel2.setText("ID:");
        jLabel2.setName("jLabel2"); // NOI18N

        sedName.setAction(actionMap.get("changeName")); // NOI18N
        sedName.setName("sedName"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sed.id}"), sedName, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${isSed}"), sedName, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton1.setAction(actionMap.get("changeName")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        jLabel4.setText("Segments: ");
        jLabel4.setName("jLabel4"); // NOI18N
        jToolBar2.add(jLabel4);

        jButton15.setAction(actionMap.get("newSegment")); // NOI18N
        jButton15.setIcon(new ImageIcon(getClass().getResource("/list_add.png")));
        jButton15.setBorderPainted(false);
        jButton15.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton15.setFocusable(false);
        jButton15.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton15.setMargin(new java.awt.Insets(2, 2, 0, 2));
        jButton15.setMaximumSize(new java.awt.Dimension(100, 50));
        jButton15.setMinimumSize(new java.awt.Dimension(65, 50));
        jButton15.setName("jButton15"); // NOI18N
        jButton15.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jButton15);

        jButton16.setAction(actionMap.get("newPoint")); // NOI18N
        jButton16.setIcon(new ImageIcon(getClass().getResource("/list_add.png")));
        jButton16.setBorderPainted(false);
        jButton16.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton16.setFocusable(false);
        jButton16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton16.setMargin(new java.awt.Insets(2, 2, 0, 2));
        jButton16.setMaximumSize(new java.awt.Dimension(100, 50));
        jButton16.setMinimumSize(new java.awt.Dimension(65, 50));
        jButton16.setName("jButton16"); // NOI18N
        jButton16.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jButton16);

        jButton5.setAction(actionMap.get("removeSegment")); // NOI18N
        jButton5.setIcon(new ImageIcon(getClass().getResource("/list_remove.png")));
        jButton5.setBorderPainted(false);
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setMargin(new java.awt.Insets(2, 2, 0, 2));
        jButton5.setMaximumSize(new java.awt.Dimension(100, 50));
        jButton5.setMinimumSize(new java.awt.Dimension(65, 50));
        jButton5.setName("jButton5"); // NOI18N
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${segmentSelected}"), jButton5, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(jButton5);

        jButton7.setAction(actionMap.get("saveSegments")); // NOI18N
        jButton7.setIcon(new ImageIcon(getClass().getResource("/document_save.png")));
        jButton7.setBorderPainted(false);
        jButton7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setMargin(new java.awt.Insets(2, 2, 0, 2));
        jButton7.setMaximumSize(new java.awt.Dimension(100, 50));
        jButton7.setMinimumSize(new java.awt.Dimension(65, 50));
        jButton7.setName("jButton7"); // NOI18N
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${segmentSelected}"), jButton7, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(jButton7);

        jButton4.setAction(actionMap.get("editSegment")); // NOI18N
        jButton4.setIcon(new ImageIcon(getClass().getResource("/edit.png")));
        jButton4.setBorderPainted(false);
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setMargin(new java.awt.Insets(2, 2, 0, 2));
        jButton4.setMaximumSize(new java.awt.Dimension(100, 50));
        jButton4.setMinimumSize(new java.awt.Dimension(65, 50));
        jButton4.setName("jButton4"); // NOI18N
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${segmentSelected}"), jButton4, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(jButton4);

        jButton6.setAction(actionMap.get("broadcastSegments")); // NOI18N
        jButton6.setIcon(new ImageIcon(getClass().getResource("/file_export.png")));
        jButton6.setBorderPainted(false);
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setMargin(new java.awt.Insets(2, 2, 0, 2));
        jButton6.setMaximumSize(new java.awt.Dimension(100, 50));
        jButton6.setMinimumSize(new java.awt.Dimension(65, 50));
        jButton6.setName("jButton6"); // NOI18N
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${segmentSelected}"), jButton6, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(jButton6);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setName("jTable1"); // NOI18N
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${segments}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jTable1);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${target.name.value}"));
        columnBinding.setColumnName("Target");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${target.pos.value}"));
        columnBinding.setColumnName("Coordinates");
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${curation.publisher.value}"));
        columnBinding.setColumnName("Publisher");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${data.length}"));
        columnBinding.setColumnName("#Points");
        columnBinding.setColumnClass(Integer.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(1).setCellRenderer(new PosRenderer());
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(255);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(50);

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setName("jToolBar3"); // NOI18N

        jLabel1.setText("Target Name:");
        jLabel1.setName("jLabel1"); // NOI18N
        jToolBar3.add(jLabel1);

        jTextField1.setColumns(8);
        jTextField1.setName("jTextField1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${targetName}"), jTextField1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jToolBar3.add(jTextField1);

        jComboBox1.setModel(new DefaultComboBoxModel(resolver.getCatalogs().toArray(new Catalog[resolver.getCatalogs().size()])));
        jComboBox1.setName("jComboBox1"); // NOI18N
        jToolBar3.add(jComboBox1);

        jButton13.setAction(actionMap.get("resolve")); // NOI18N
        jButton13.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jButton13.setIcon(new ImageIcon(getClass().getResource("/search.png")));
        jButton13.setBorderPainted(false);
        jButton13.setFocusable(false);
        jButton13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton13.setName("jButton13"); // NOI18N
        jButton13.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar3.add(jButton13);

        jSeparator2.setName("jSeparator2"); // NOI18N
        jToolBar3.add(jSeparator2);

        jLabel5.setText("RA:");
        jLabel5.setName("jLabel5"); // NOI18N
        jToolBar3.add(jLabel5);

        jTextField2.setColumns(4);
        jTextField2.setName("jTextField2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${ra}"), jTextField2, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jToolBar3.add(jTextField2);

        jLabel6.setText("DEC:");
        jLabel6.setName("jLabel6"); // NOI18N
        jToolBar3.add(jLabel6);

        jTextField3.setColumns(4);
        jTextField3.setName("jTextField3"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${dec}"), jTextField3, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jToolBar3.add(jTextField3);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jToolBar3.add(jSeparator3);

        jButton17.setAction(actionMap.get("apply")); // NOI18N
        jButton17.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jButton17.setIcon(new ImageIcon(getClass().getResource("/apply.png")));
        jButton17.setBorderPainted(false);
        jButton17.setFocusable(false);
        jButton17.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton17.setName("jButton17"); // NOI18N
        jButton17.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar3.add(jButton17);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sedName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1))
            .add(jToolBar2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
            .add(jToolBar3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(sedName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jToolBar3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jToolBar2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
            .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 51, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void changeName() {
        if(!sedName.getText().isEmpty())
            if(manager.existsSed(sedName.getText()))
                NarrowOptionPane.showMessageDialog(rootFrame, "This ID already exists. Please use a unique ID.", "Rename error", NarrowOptionPane.ERROR_MESSAGE);
            else
                manager.rename(sed, sedName.getText());
    }

    @Action
    public void newSed() {
        int c = 0;
        while (manager.existsSed("Sed" + c)) {
            c++;
        }

        ExtSed newSed = manager.newSed("Sed" + c);
    }

    @Action
    public void removeSed() {
        if(sed.getNumberOfSegments()==0) {
            manager.remove(sed.getId());
            setSed(null);
        } else {
            int ans = NarrowOptionPane.showConfirmDialog(rootFrame,
                "Are you sure you want to delete the selected SED?");
            if(ans==NarrowOptionPane.YES_OPTION) {
                manager.remove(sed.getId());
                setSed(null);
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JTextField sedName;
    private javax.swing.JScrollPane sedPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private boolean isSed = false;
    public static final String PROP_ISSED = "isSed";

    /**
     * Get the value of isSed
     *
     * @return the value of isSed
     */
    public boolean isIsSed() {
        return isSed;
    }

    /**
     * Set the value of isSed
     *
     * @param isSed new value of isSed
     */
    public void setIsSed(boolean isSed) {
        boolean oldIsSed = this.isSed;
        this.isSed = isSed;
        firePropertyChange(PROP_ISSED, oldIsSed, isSed);
    }

    private List<Segment> segments = new ArrayList();
    public static final String PROP_SEGMENTS = "segments";

    /**
     * Get the value of segments
     *
     * @return the value of segments
     */
    public List<Segment> getSegments() {
        return segments;
    }

    /**
     * Set the value of segments
     *
     * @param segments new value of segments
     */
    public void setSegments(List<Segment> segments) {
        List<Segment> oldSegments = this.segments;
        this.segments = segments;
        firePropertyChange(PROP_SEGMENTS, oldSegments, segments);
    }


    @Action
    public void saveSed() {
        SaveSedDialog ssd = new SaveSedDialog(rootFrame, sed);
        ssd.setVisible(true);
    }

    @Action
    public void broadcast() {
        try {
            ((SedSAMPController)SedBuilder.getApplication().getSAMPController()).sendSedMessage(sed);
        } catch (SampException ex) {
            NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                    ex.getMessage(), "Error broadcasting file", NarrowOptionPane.ERROR_MESSAGE);
        }
    }

    public void update() {
        ExtSed s = sed;
        setSed(null);
        setSed(s);

        if(!this.isIcon() && !this.isVisible()) {
            this.setVisible(true);
        }

    }

    private class PosRenderer extends JLabel implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object o, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = new DefaultTableCellRenderer()
                    .getTableCellRendererComponent(jtable, o, isSelected, hasFocus, row, col);

            DoubleParam[] radec = ((DoubleParam[]) o);
            if(radec==null) radec = new DoubleParam[]{null, null};

            String raS, decS;
            if(radec[0]==null || radec[0].getValue().isEmpty())
                raS = "-";
            else
                raS = Double.valueOf(radec[0].getValue()).isNaN() ? "-" : roundToSignificantFigures(Double.valueOf(radec[0].getValue()), 5).toString();
            if(radec[1]==null || radec[1].getValue().isEmpty())
                decS = "-";
            else
                decS = Double.valueOf(radec[1].getValue()).isNaN() ? "-" : roundToSignificantFigures(Double.valueOf(radec[1].getValue()), 5).toString();
            String content = raS+", "+decS;
            setText(content);

            this.setBackground(c.getBackground());
            this.setForeground(c.getForeground());

            this.setOpaque(true);
            
            return this;
        }

        private Double roundToSignificantFigures(double num, int n) {
            if(num == 0) {
                return 0d;
            }

            final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
            final int power = n - (int) d;

            final double magnitude = Math.pow(10, power);
            final long shifted = Math.round(num*magnitude);
            return shifted/magnitude;
        }



    }

    @Action
    public void duplicateSed() {
        ExtSed s = (ExtSed) sed.clone();
        int c = 0;
        String base = s.getId();
        while (manager.existsSed(base + "Copy" + c)) {
            c++;
        }

        s.setId(base+"Copy"+c);

        manager.add(s);
    }

    @Action
    public void newSegment() throws PropertyVetoException {
        loadFrame.setTargetName(targetName);
        loadFrame.setRa(ra);
        loadFrame.setDec(dec);
        loadFrame.show();
        if(loadFrame.isIcon())
            loadFrame.setIcon(false);
        loadFrame.setSelected(true);
        loadFrame.moveToFront();
    }

    private List<Segment> selectedSegments;
    public static final String PROP_SELECTEDSEGMENTS = "selectedSegments";

    /**
     * Get the value of selectedSegments
     *
     * @return the value of selectedSegments
     */
    public List<Segment> getSelectedSegments() {
        return selectedSegments;
    }

    /**
     * Set the value of selectedSegments
     *
     * @param selectedSegments new value of selectedSegments
     */
    public void setSelectedSegments(List<Segment> selectedSegments) {
        List oldSelectedSegments = this.selectedSegments;
        this.selectedSegments = selectedSegments;
        firePropertyChange(PROP_SELECTEDSEGMENTS, oldSelectedSegments, selectedSegments);
    }

    private boolean segmentSelected;
    public static final String PROP_SEGMENTSELECTED = "segmentSelected";

    /**
     * Get the value of segmentSelected
     *
     * @return the value of segmentSelected
     */
    public boolean isSegmentSelected() {
        return segmentSelected;
    }

    /**
     * Set the value of segmentSelected
     *
     * @param segmentSelected new value of segmentSelected
     */
    public void setSegmentSelected(boolean segmentSelected) {
        boolean oldSegmentSelected = this.segmentSelected;
        this.segmentSelected = segmentSelected;
        firePropertyChange(PROP_SEGMENTSELECTED, oldSegmentSelected, segmentSelected);
    }

    @Action
    public void editSegment() throws Exception {
        boolean warning = false;
        for(Segment s : selectedSegments) {
            Map<Segment, SegmentFrame> map = (Map<Segment, SegmentFrame>) sed.getAttachment("builder:configuration");
            if(map!=null) {
                if(map.containsKey(s)) {
                    SegmentFrame sf = map.get(s);
                    sf.update(s);
                    sf.setVisible(true);
                } else
                    warning = true;
            } else
                warning = true;

            if(warning) {
                EditTargetFrame f = new EditTargetFrame(s);
                SedBuilder.getWorkspace().addFrame(f);
                f.setVisible(true);
            }
        }
//        if(warning) {
//            NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
//                        "The segment was imported 'as is', for example from NED or from file, so it can't be edited.",
//                        "Editing not available for this segment",
//                        NarrowOptionPane.WARNING_MESSAGE);
//        }
    }

    @Action
    public void removeSegment() {
        int ans = NarrowOptionPane.showConfirmDialog(SedBuilder.getWorkspace().getRootFrame(),
                "Are you sure you want to remove the selected segments from the SED?",
                "Confirm removal",
                NarrowOptionPane.YES_NO_OPTION);
        if(ans==NarrowOptionPane.YES_OPTION) {
            sed.remove(selectedSegments);
        }
    }

    @Action
    public void broadcastSegments() {
        try {
            ExtSed s = new ExtSed(sed.getId()+"Selection", false);
            s.addSegment(selectedSegments);
            ((SedSAMPController)SedBuilder.getApplication().getSAMPController()).sendSedMessage(s);
        } catch (SedInconsistentException ex) {//If the segment is already in the SED this exception can't be thrown.
            Logger.getLogger(SedBuilderMainView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SedNoDataException ex) {//If the segment is alreadt in the SED this exception can't be thrown.
            Logger.getLogger(SedBuilderMainView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SampException ex) {
            NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                    ex.getMessage(), "Error broadcasting file", NarrowOptionPane.ERROR_MESSAGE);
        }
        

    }

    @Action
    public void saveSegments() {
        try {
            ExtSed s = new ExtSed("");
            s.addSegment(selectedSegments);
            SaveSedDialog ssd = new SaveSedDialog(rootFrame, s);
            ssd.setVisible(true);
        } catch (SedInconsistentException ex) {
            Logger.getLogger(SedBuilderMainView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SedNoDataException ex) {
            Logger.getLogger(SedBuilderMainView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Action
    public void newPoint() {
        PhotometryPointFrame f = new PhotometryPointFrame(sed, manager);
        f.setRa(ra);
        f.setDec(dec);
        f.setTargetName(targetName);
        SedBuilder.getWorkspace().addFrame(f);
        f.show();
    }

    private NameResolver resolver = NameResolver.getInstance();

    private String ra;
    public static final String PROP_RA = "ra";

    /**
     * Get the value of ra
     *
     * @return the value of ra
     */
    public String getRa() {
        return ra;
    }

    /**
     * Set the value of ra
     *
     * @param ra new value of ra
     */
    public void setRa(String ra) {
        if (ra != null) {
            if (!ra.isEmpty()) {
                ra = SkyCoordinates.getRaDegString(ra);
            }
        }
        String oldRa = this.ra;
        this.ra = ra;
        firePropertyChange(PROP_RA, oldRa, ra);
    }

    private String dec;
    public static final String PROP_DEC = "dec";

    /**
     * Get the value of dec
     *
     * @return the value of dec
     */
    public String getDec() {
        return dec;
    }

    /**
     * Set the value of dec
     *
     * @param dec new value of dec
     */
    public void setDec(String dec) {
        if (dec != null) {
            if (!dec.isEmpty()) {
                dec = SkyCoordinates.getDecDegString(dec);
            }
        }
        String oldDec = this.dec;
        this.dec = dec;
        firePropertyChange(PROP_DEC, oldDec, dec);
    }

    private String targetName;
    public static final String PROP_TARGETNAME = "targetName";

    /**
     * Get the value of targetName
     *
     * @return the value of targetName
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Set the value of targetName
     *
     * @param targetName new value of targetName
     */
    public void setTargetName(String targetName) {
        String oldTargetName = this.targetName;
        this.targetName = targetName;
        firePropertyChange(PROP_TARGETNAME, oldTargetName, targetName);
    }

    @Action
    public Task resolve() {
        return new ResolveTask(org.jdesktop.application.Application.getInstance());
    }

    private class ResolveTask extends org.jdesktop.application.Task<Object, Void> {
        private Catalog cat;

        ResolveTask(org.jdesktop.application.Application app) {
            super(app);
            cat = (Catalog) jComboBox1.getSelectedItem();
        }

        @Override protected Object doInBackground() {
            Object pos = null;
                try {
                    pos = resolver.resolve(cat, targetName);
                } catch (RuntimeException ex) {
                    return ex.getMessage();
                } catch (IOException ex) {
                    return ex.getMessage();
                }
            return pos;
        }
        @Override protected void succeeded(Object result) {
            if(result instanceof String)
                NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(), result, "Error trying to resolve name", NarrowOptionPane.ERROR_MESSAGE);
            else {
                Position pos = (Position) result;
                setRa(pos.getRa().toString());
                setDec(pos.getDec().toString());
            }
        }
    }

    @Action
    public void apply() {
        for(int i=0; i<sed.getNumberOfSegments(); i++) {
            Segment segment = sed.getSegment(i);
            segment.getTarget().setName(new TextParam(targetName));

            DoubleParam raD = new DoubleParam(Double.valueOf(ra));
            DoubleParam decD = new DoubleParam(Double.valueOf(dec));

            PositionParam pos = new PositionParam();

            DoubleParam[] p = new DoubleParam[]{raD, decD};

            pos.setValue(p);

            segment.getTarget().setPos(pos);

            SedBuilder.update();
        }
    }



}
