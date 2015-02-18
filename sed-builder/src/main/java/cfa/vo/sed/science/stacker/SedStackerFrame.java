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
 * SedStackerFrame2.java
 *
 * Created on Sep 22, 2014, 10:34:10 AM
 */
package cfa.vo.sed.science.stacker;

import cfa.vo.interop.SAMPController;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.gui.WiderJComboBox;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.sed.quantities.IUnit;
import cfa.vo.iris.sed.quantities.SPVYUnit;
import cfa.vo.iris.sed.quantities.XUnit;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sedlib.common.SedNoDataException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import org.apache.commons.lang.StringUtils;
import org.astrogrid.samp.client.SampException;
import org.jdesktop.observablecollections.ObservableCollections;
import spv.util.UnitsException;

/**
 *
 * @author jbudynk
 */
public class SedStackerFrame extends javax.swing.JInternalFrame {
    
    private JFrame rootFrame;
    private IrisApplication app;
    private SAMPController controller;
    private SedlibSedManager manager;
    private IWorkspace ws;

    public SedStackerFrame(IrisApplication app, IWorkspace ws) {
	initComponents();
	
	this.rootFrame = ws.getRootFrame();
	this.app = app;
	this.controller = app.getSAMPController();
	this.manager = (SedlibSedManager) ws.getSedManager();
	this.ws = ws;
	
	if (stacks.isEmpty()) {
	    SedStack stack = new SedStack("Stack");
	    updateStackList(stack, true);
	}
	
	// normalization comboBoxes. Chooses the list of units available 
	// based on the normalization type chosen (Value, Median, or Average).
	// Also disable Y value text box if using Average or Median.
	integrationNormType.addActionListener( new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		
		String normType = (String) integrationNormType.getSelectedItem();
		if (normType.equals("Value")) {
		    integrationYUnit.setModel(new DefaultComboBoxModel(new String[] {"erg/s/cm2","Jy-Hz","Watt/m2","erg/s","Watt"}));
		    integrationValueText.setEnabled(true);
		} else {
		    integrationYUnit.setModel(new DefaultComboBoxModel(loadEnum(SPVYUnit.class)));
		    integrationValueText.setEnabled(false);
		}
	    }
	});
	
	/* The following four statements add Action Listeners to the unit combo boxes
	in the normalization options. These are so that whatever units the user
	uses for normalization will be selected for stacking.
	
	THIS CODE WORKS!! Commenting it out because this might not be desired functionality. (01/20/2015)
	*/
//	integrationYUnit.addActionListener(new ActionListener() {
//	    @Override
//	    public void actionPerformed(ActionEvent e) {
//		String unitName = (String) integrationYUnit.getSelectedItem();
//		for (int i=0; i<stackYUnitComboBox.getModel().getSize(); i++) {
//		    if (stackYUnitComboBox.getModel().getElementAt(i).equals(unitName)) {
//			stackYUnitComboBox.setSelectedItem(unitName);
//		    }
//		}
//	    }
//	});
//	atPointYUnit.addActionListener(new ActionListener() {
//	    @Override
//	    public void actionPerformed(ActionEvent e) {
//		int unit = (int) atPointYUnit.getSelectedIndex();
//		stackYUnitComboBox.setSelectedIndex(unit);
//	    }
//	});
//	integrationMinMaxUnit.addActionListener(new ActionListener() {
//	    @Override
//	    public void actionPerformed(ActionEvent e) {
//		int unit = (int) integrationMinMaxUnit.getSelectedIndex();
//		stackBinSizeUnitsComboBox.setSelectedIndex(unit);
//	    }
//	});
//	atPointXUnit.addActionListener(new ActionListener() {
//	    @Override
//	    public void actionPerformed(ActionEvent e) {
//		int unit = (int) atPointXUnit.getSelectedIndex();
//		stackBinSizeUnitsComboBox.setSelectedIndex(unit);
//	    }
//	});
	
//	// undo management items
//	ExtendedUndoManager undoManager = new ExtendedUndoManager();
//	UndoableEditSupport undoSupport = new UndoableEditSupport();
//	undoSupport.addUndoableEditListener(new UndoAdapter());
//	refreshUndoRedo();
//	
//	Action redshift = new RedshiftAction();
//	Action normalize = new NormalizeAction();
//	Action stack = new StackAction();
	
    }
    
    private boolean createSedAfterRedshift = false;

    public static final String PROP_CREATESEDAFTERREDSHIFT = "createSedAfterRedshift";

    /**
     * Get the value of createSedAfterRedshift
     *
     * @return the value of createSedAfterRedshift
     */
    public boolean isCreateSedAfterRedshift() {
	return createSedAfterRedshift;
    }

    /**
     * Set the value of createSedAfterRedshift
     *
     * @param createSedAfterRedshift new value of createSedAfterRedshift
     */
    public void setCreateSedAfterRedshift(boolean createSedAfterRedshift) {
	boolean oldCreateSedAfterRedshift = this.createSedAfterRedshift;
	this.createSedAfterRedshift = createSedAfterRedshift;
	firePropertyChange(PROP_CREATESEDAFTERREDSHIFT, oldCreateSedAfterRedshift, createSedAfterRedshift);
    }

    private boolean createSedAfterNormalize = false;

    public static final String PROP_CREATESEDAFTERNORMALIZE = "createSedAfterNormalize";

    /**
     * Get the value of createSedAfterNormalize
     *
     * @return the value of createSedAfterNormalize
     */
    public boolean isCreateSedAfterNormalize() {
	return createSedAfterNormalize;
    }

    /**
     * Set the value of createSedAfterNormalize
     *
     * @param createSedAfterNormalize new value of createSedAfterNormalize
     */
    public void setCreateSedAfterNormalize(boolean createSedAfterNormalize) {
	boolean oldCreateSedAfterNormalize = this.createSedAfterNormalize;
	this.createSedAfterNormalize = createSedAfterNormalize;
	firePropertyChange(PROP_CREATESEDAFTERNORMALIZE, oldCreateSedAfterNormalize, createSedAfterNormalize);
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

        javax.swing.ButtonGroup buttonGroup1 = new javax.swing.ButtonGroup();
        javax.swing.ButtonGroup buttonGroup2 = new javax.swing.ButtonGroup();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem jMenuItem1 = new javax.swing.JMenuItem();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem jMenuItem2 = new javax.swing.JMenuItem();
        jButton1 = new javax.swing.JButton();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        stackPanel = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        javax.swing.JPanel jPanel5 = new javax.swing.JPanel();
        correctFlux = new javax.swing.JCheckBox();
        jTextField8 = new javax.swing.JTextField();
        javax.swing.JLabel jLabel11 = new javax.swing.JLabel();
        redshiftButton = new javax.swing.JButton();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        integrationXMaxText = new javax.swing.JTextField();
        integrationMinMaxUnit = new WiderJComboBox();
        integrationYUnit = new WiderJComboBox();
        integrationValueText = new javax.swing.JTextField();
        integrationNormType = new javax.swing.JComboBox();
        javax.swing.JLabel integrationNormToLabel = new javax.swing.JLabel();
        javax.swing.JLabel integrationXMinLabel = new javax.swing.JLabel();
        integrationXMinText = new javax.swing.JTextField();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        javax.swing.JLabel atPointXLabel = new javax.swing.JLabel();
        javax.swing.JLabel atPointYLabel = new javax.swing.JLabel();
        atPointXText = new javax.swing.JTextField();
        atPointYType = new javax.swing.JComboBox();
        atPointXUnit = new javax.swing.JComboBox();
        atPointYText = new javax.swing.JTextField();
        atPointYUnit = new javax.swing.JComboBox();
        normalizeButton = new javax.swing.JButton();
        javax.swing.JSeparator jSeparator1 = new javax.swing.JSeparator();
        javax.swing.JLabel integrationXMaxLabel = new javax.swing.JLabel();
        javax.swing.JCheckBox jCheckBox1 = new javax.swing.JCheckBox();
        javax.swing.JCheckBox jCheckBox2 = new javax.swing.JCheckBox();
        javax.swing.JPanel jPanel4 = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        sedsTable = new javax.swing.JTable();
        javax.swing.JPanel jPanel6 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        stackStatisticComboBox = new javax.swing.JComboBox();
        smoothCheckBox = new javax.swing.JCheckBox();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        logBinningCheckBox = new javax.swing.JCheckBox();
        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        binsizeTextField = new javax.swing.JTextField();
        stackBinSizeUnitsComboBox = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel10 = new javax.swing.JLabel();
        stackButton = new javax.swing.JButton();
        stackYUnitComboBox = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        resetButton = new javax.swing.JButton();
        javax.swing.JButton deleteButton = new javax.swing.JButton();
        createSedButton = new javax.swing.JButton();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        jMenuItem1.setText("Rename...");
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        jPopupMenu2.setName("jPopupMenu2"); // NOI18N

        jMenuItem2.setText("Change redshift...");
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jPopupMenu2.add(jMenuItem2);

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setResizable(true);
        setTitle("SED Stacker");

        jButton1.setText("Create New Stack");
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newStack(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Open Stacks"));
        jPanel1.setName("jPanel1"); // NOI18N

        stackPanel.setName("stackPanel"); // NOI18N

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setName("jList1"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${stacks}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jList1);
        jListBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jListBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedStack}"), jList1, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jList1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jList1MouseReleased(evt);
            }
        });
        stackPanel.setViewportView(jList1);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(stackPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, stackPanel)
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Redshift and Normalize"));
        jPanel5.setName("jPanel5"); // NOI18N

        correctFlux.setText("Correct flux");
        correctFlux.setName("correctFlux"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.redshiftConfiguration.correctFlux}"), correctFlux, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jTextField8.setName("jTextField8"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.redshiftConfiguration.toRedshift}"), jTextField8, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel11.setText("Move to redshift:");
        jLabel11.setName("jLabel11"); // NOI18N

        redshiftButton.setText("Redshift");
        redshiftButton.setName("redshiftButton"); // NOI18N
        redshiftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redshiftButtonActionPerformed(evt);
            }
        });

        jLabel6.setText("Add or multiply normalization constant:");
        jLabel6.setName("jLabel6"); // NOI18N

        buttonGroup2.add(jRadioButton3);
        jRadioButton3.setText("Add");
        jRadioButton3.setName("jRadioButton3"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.add}"), jRadioButton3, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        buttonGroup2.add(jRadioButton4);
        jRadioButton4.setText("Multiply");
        jRadioButton4.setName("jRadioButton4"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.multiply}"), jRadioButton4, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        integrationXMaxText.setName("integrationXMaxText"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.xmax}"), integrationXMaxText, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), integrationXMaxText, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        integrationMinMaxUnit.setModel(new DefaultComboBoxModel(loadEnum(XUnit.class)));
        integrationMinMaxUnit.setToolTipText("null");
        integrationMinMaxUnit.setName("integrationMinMaxUnit"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.XUnits}"), integrationMinMaxUnit, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), integrationMinMaxUnit, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        integrationYUnit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "erg/s/cm2", "Jy-Hz", "Watt/m2", "erg/s", "Watt" }));
        integrationYUnit.setName("integrationYUnit"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.integrateValueYUnits}"), integrationYUnit, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), integrationYUnit, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        integrationValueText.setName("integrationValueText"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.YValue}"), integrationValueText, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), integrationValueText, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        integrationNormType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Value", "Average", "Median" }));
        integrationNormType.setName("integrationNormType"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.stats}"), integrationNormType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), integrationNormType, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        integrationNormToLabel.setText("Normalize to");
        integrationNormToLabel.setName("integrationNormToLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), integrationNormToLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        integrationXMinLabel.setText("X Min:");
        integrationXMinLabel.setName("integrationXMinLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), integrationXMinLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        integrationXMinText.setName("integrationXMinText"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.xmin}"), integrationXMinText, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), integrationXMinText, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Integration");
        jRadioButton1.setName("jRadioButton1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.integrate}"), jRadioButton1, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("At point");
        jRadioButton2.setName("jRadioButton2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.atPoint}"), jRadioButton2, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        atPointXLabel.setText("X:");
        atPointXLabel.setName("atPointXLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), atPointXLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        atPointYLabel.setText("Y:");
        atPointYLabel.setName("atPointYLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), atPointYLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        atPointXText.setName("atPointXText"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.atPointXValue}"), atPointXText, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), atPointXText, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        atPointYType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Value", "Average", "Median" }));
        atPointYType.setName("atPointYType"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.atPointStats}"), atPointYType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), atPointYType, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        atPointXUnit.setModel(new DefaultComboBoxModel(loadEnum(XUnit.class)));
        atPointXUnit.setName("atPointXUnit"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.atPointXUnits}"), atPointXUnit, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), atPointXUnit, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        atPointYText.setName("atPointYText"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.atPointYValue}"), atPointYText, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.atPointYTextEnabled}"), atPointYText, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        atPointYUnit.setModel(new DefaultComboBoxModel(loadEnum(SPVYUnit.class)));
        atPointYUnit.setName("atPointYUnit"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.normConfiguration.atPointYUnits}"), atPointYUnit, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), atPointYUnit, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        normalizeButton.setText("Normalize");
        normalizeButton.setName("normalizeButton"); // NOI18N
        normalizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normalizeButtonActionPerformed(evt);
            }
        });

        jSeparator1.setName("jSeparator1"); // NOI18N

        integrationXMaxLabel.setText("X Max:");
        integrationXMaxLabel.setName("integrationXMaxLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), integrationXMaxLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jCheckBox1.setText("Create SED");
        jCheckBox1.setToolTipText("Create and view SED after redshifting");
        jCheckBox1.setName("jCheckBox1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${createSedAfterRedshift}"), jCheckBox1, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jCheckBox2.setText("Create SED");
        jCheckBox2.setToolTipText("Create and view SED after normalizing");
        jCheckBox2.setName("jCheckBox2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${createSedAfterNormalize}"), jCheckBox2, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator1)
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel5Layout.createSequentialGroup()
                                .add(190, 190, 190)
                                .add(correctFlux))
                            .add(jPanel5Layout.createSequentialGroup()
                                .add(jLabel11)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jTextField8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jCheckBox1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(redshiftButton))
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel5Layout.createSequentialGroup()
                                .add(jRadioButton2)
                                .add(26, 26, 26)
                                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jPanel5Layout.createSequentialGroup()
                                        .add(atPointXLabel)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(atPointXText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(3, 3, 3)
                                        .add(atPointXUnit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(jPanel5Layout.createSequentialGroup()
                                        .add(atPointYLabel)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(atPointYType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(atPointYText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 78, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(7, 7, 7)
                                        .add(atPointYUnit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                            .add(jPanel5Layout.createSequentialGroup()
                                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jPanel5Layout.createSequentialGroup()
                                        .add(integrationNormToLabel)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(integrationNormType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                                    .add(jPanel5Layout.createSequentialGroup()
                                        .add(jRadioButton1)
                                        .add(8, 8, 8)
                                        .add(integrationXMinLabel)
                                        .add(3, 3, 3)
                                        .add(integrationXMinText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(integrationXMaxLabel)
                                        .add(3, 3, 3)))
                                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(jPanel5Layout.createSequentialGroup()
                                        .add(integrationXMaxText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(integrationMinMaxUnit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(jPanel5Layout.createSequentialGroup()
                                        .add(integrationValueText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(integrationYUnit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                            .add(jPanel5Layout.createSequentialGroup()
                                .add(jLabel6)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jRadioButton3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jRadioButton4)))
                        .add(0, 0, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(jCheckBox2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(normalizeButton)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(correctFlux)
                        .add(jTextField8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel11))
                    .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(redshiftButton)
                        .add(jCheckBox1)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(jRadioButton3)
                    .add(jRadioButton4))
                .add(18, 18, 18)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButton1)
                    .add(integrationMinMaxUnit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(integrationXMaxText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(integrationXMinText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(integrationXMinLabel)
                    .add(integrationXMaxLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(integrationYUnit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(integrationValueText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(integrationNormType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(integrationNormToLabel))
                .add(11, 11, 11)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jRadioButton2)
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(atPointXText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(atPointXLabel))
                            .add(atPointXUnit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(atPointYLabel)
                            .add(atPointYType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(atPointYText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(atPointYUnit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(normalizeButton)
                    .add(jCheckBox2))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Added SEDs"));
        jPanel4.setName("jPanel4"); // NOI18N

        addButton.setText("Add...");
        addButton.setName("addButton"); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.setName("removeButton"); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        sedsTable.setModel(new StackTableModel());
        sedsTable.setName("sedsTable"); // NOI18N
        sedsTable.getTableHeader().setReorderingAllowed(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedSeds}"), sedsTable, org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        sedsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                sedsTableMousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(sedsTable);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(addButton)
                    .add(removeButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(addButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(removeButton))
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Stacking Options"));
        jPanel6.setName("jPanel6"); // NOI18N

        jLabel7.setText("Statistic:");
        jLabel7.setName("jLabel7"); // NOI18N

        stackStatisticComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Average", "Weighted Avg", "Sum" }));
        stackStatisticComboBox.setName("stackStatisticComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.stackConfiguration.statistic}"), stackStatisticComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        smoothCheckBox.setText("Smooth");
        smoothCheckBox.setName("smoothCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.stackConfiguration.smooth}"), smoothCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jLabel8.setText("Box Size:");
        jLabel8.setName("jLabel8"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, smoothCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel8, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jTextField6.setName("jTextField6"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.stackConfiguration.smoothBinsize}"), jTextField6, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, smoothCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jTextField6, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        logBinningCheckBox.setText("Logarithmic Binning");
        logBinningCheckBox.setToolTipText("java.lang.String \"Note: If logarithmic binning is on, the Bin Size is also logarithmic (e.g., a bin size of 1.0 with logarithmic binning spans 1 decade).\""); // NOI18N
        logBinningCheckBox.setName("logBinningCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.stackConfiguration.logbin}"), logBinningCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jLabel9.setText("Bin Size:");
        jLabel9.setName("jLabel9"); // NOI18N

        binsizeTextField.setName("binsizeTextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.stackConfiguration.binsize}"), binsizeTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        stackBinSizeUnitsComboBox.setModel(new DefaultComboBoxModel(loadEnum(XUnit.class)));
        stackBinSizeUnitsComboBox.setName("stackBinSizeUnitsComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.stackConfiguration.binsizeUnit}"), stackBinSizeUnitsComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel10.setText("Bin Size Units:");
        jLabel10.setName("jLabel10"); // NOI18N

        stackButton.setText("Stack!");
        stackButton.setName("stackButton"); // NOI18N
        stackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stackButtonActionPerformed(evt);
            }
        });

        stackYUnitComboBox.setModel(new DefaultComboBoxModel(loadEnum(SPVYUnit.class)));
        stackYUnitComboBox.setName("stackYUnitComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedConfig.stackConfiguration.YUnits}"), stackYUnitComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel1.setText("Y Axis:");
        jLabel1.setName("jLabel1"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel6Layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(stackButton)
                        .addContainerGap())
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(29, 29, 29)
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField6)
                        .add(17, 17, 17))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel6Layout.createSequentialGroup()
                                .add(jLabel10)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(stackBinSizeUnitsComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel6Layout.createSequentialGroup()
                                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel7)
                                    .add(jLabel1))
                                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jPanel6Layout.createSequentialGroup()
                                        .add(12, 12, 12)
                                        .add(stackYUnitComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 143, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(jPanel6Layout.createSequentialGroup()
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(stackStatisticComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 143, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                            .add(smoothCheckBox)
                            .add(logBinningCheckBox)
                            .add(jPanel6Layout.createSequentialGroup()
                                .add(jLabel9)
                                .add(43, 43, 43)
                                .add(binsizeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(stackStatisticComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(stackYUnitComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(binsizeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10)
                    .add(stackBinSizeUnitsComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(9, 9, 9)
                .add(logBinningCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(smoothCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(jTextField6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(stackButton)
                .add(18, 18, 18))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Managment"));
        jPanel2.setName("jPanel2"); // NOI18N

        resetButton.setText("Reset");
        resetButton.setToolTipText("Reset SEDs to their original values");
        resetButton.setName("resetButton"); // NOI18N
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete");
        deleteButton.setToolTipText("Delete the currently selected Stack");
        deleteButton.setName("deleteButton"); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        createSedButton.setText("Create SED");
        createSedButton.setToolTipText("Create new SED of the current Stack.");
        createSedButton.setName("createSedButton"); // NOI18N
        createSedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createSedButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(resetButton)
                    .add(createSedButton)
                    .add(deleteButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(resetButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(deleteButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(createSedButton)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jButton1)
                            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(0, 10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jButton1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 301, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newStack(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newStack
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SedStack stack = new SedStack("Stack");
		rename(stack, "Stack");
                updateStackList(stack, true);
            }
        });
    }//GEN-LAST:event_newStack
    
    private AddSedsFrame addSedsFrame;
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
	
	// TODO: remove this when I add "Load From File" option in the AddSedsFrame.
	if (manager.getSeds().isEmpty()) {
	    NarrowOptionPane.showMessageDialog(this, 
		    "There are no open SEDs. Create SEDs using the SED Builder first.",
		    "Error: No Open SEDs", JOptionPane.OK_OPTION);
	    return;
	}
	
	if (addSedsFrame == null) {
	    addSedsFrame = new AddSedsFrame(manager, selectedStack, sedsTable, this);
	    ws.addFrame(addSedsFrame);
	}
	addSedsFrame.updateSeds(selectedStack);
	GUIUtils.moveToFront(addSedsFrame);
	
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed

	List<Integer> selectedRows = new ArrayList();
	for (int i : sedsTable.getSelectedRows()) {
	    selectedRows.add(i);
	}
	Collections.reverse(selectedRows);
	final List sedList = selectedStack.getSeds();
	final List origSedList = selectedStack.getOrigSeds();
	for (int i : selectedRows) {
	    sedList.remove(i);
	    origSedList.remove(i);
	}
	sedsTable.setModel(new StackTableModel(selectedStack));
	
    }//GEN-LAST:event_removeButtonActionPerformed

    private SedStackerRedshifter redshifter;
    private void redshiftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redshiftButtonActionPerformed
	
	RedshiftConfiguration redshiftConf = getSelectedStack().getConf().getRedshiftConfiguration();
	
	// Check for invalid redshift values
	if (redshiftConf.getToRedshift() == null ||  redshiftConf.getToRedshift() < 0 || !isNumeric(redshiftConf.getToRedshift().toString())) {
            NarrowOptionPane.showMessageDialog(null, "Invalid redshift values", "WARNING", NarrowOptionPane.WARNING_MESSAGE);
	    try {
		this.setSelected(true);
	    } catch (PropertyVetoException ex) {
		Logger.getLogger(SedStackerFrame.class.getName()).log(Level.SEVERE, null, ex);
	    }
            return;
	}
	
	if (redshifter == null) {
	    redshifter = new SedStackerRedshifter(controller);
	}
	try {
	    redshifter.shift(selectedStack, redshiftConf);
	    NarrowOptionPane.showMessageDialog(this, "Successfully redshifted stack.", "SED Stacker Message", JOptionPane.INFORMATION_MESSAGE);
	    if (isCreateSedAfterRedshift() && redshifter.redshiftConfigChanged()) {
		ExtSed sed = SedStack.createSedFromStack(selectedStack, selectedStack.getName()+"_z="+redshiftConf.getToRedshift().toString()+"_");
		manager.add(sed);
	    }
	} catch (StackException ex) {
	    NarrowOptionPane.showMessageDialog(this, ex, "Redshift Error", JOptionPane.ERROR_MESSAGE);
	    Logger.getLogger(SedStackerFrame.class.getName()).log(Level.SEVERE, null, ex);
	} catch (SampException ex) {
	    NarrowOptionPane.showMessageDialog(this, ex, "Redshift Error", JOptionPane.ERROR_MESSAGE);
	    Logger.getLogger(SedStackerFrame.class.getName()).log(Level.SEVERE, null, ex);
	} catch (Exception ex) {
	    Logger.getLogger(SedStackerFrame.class.getName()).log(Level.SEVERE, null, ex);
	}
    }//GEN-LAST:event_redshiftButtonActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
	String newName = JOptionPane.showInputDialog(stackPanel, "New Stack ID:");
	List<String> names = new ArrayList();
	for (SedStack stack : stacks) {
	    names.add(stack.getName());
	}
	if (names.contains(newName)) {
	    NarrowOptionPane.showMessageDialog(rootFrame, "This Stack ID already exists. Please use a unique ID.", "Rename error", NarrowOptionPane.ERROR_MESSAGE);
	} else {
	    SedStack stack = getSelectedStack();
	    stack.setName(newName);
	    setSelectedStack(stack);
	    jList1.setSelectedValue(stack, true);
	}
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jList1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MousePressed
        if (evt.isPopupTrigger()) {
	    jList1.setSelectedIndex(jList1.locationToIndex(evt.getPoint()));
	    jPopupMenu1.show(jList1, evt.getX(), evt.getY());
	}
    }//GEN-LAST:event_jList1MousePressed

    private void jList1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseReleased
        if (evt.isPopupTrigger()) {
	    jList1.setSelectedIndex(jList1.locationToIndex(evt.getPoint()));
	    jPopupMenu1.show(jList1, evt.getX(), evt.getY());
	}
    }//GEN-LAST:event_jList1MouseReleased

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        
	int answer = NarrowOptionPane.showConfirmDialog(this, "Are you sure you want to delete "+selectedStack.getName()+"?", 
		"Delete Stack", 
		JOptionPane.YES_NO_OPTION);
	if (answer == JOptionPane.NO_OPTION)
	    return;
	
	try {
	    SwingUtilities.invokeLater(new Runnable() {
		@Override
		public void run() {
		    SedStack stack = selectedStack;
		    updateStackList(stack, false);
		}
	    });
	    sedsTable.setModel(new StackTableModel(selectedStack));
	} catch (ArrayIndexOutOfBoundsException ex) {
	    sedsTable.setModel(new StackTableModel());
	}
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void createSedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createSedButtonActionPerformed
	try {
	    
	    if(selectedStack.getSeds().isEmpty()) {
	    NarrowOptionPane.showMessageDialog(null,
                    "Stack is empty. Please add SEDs to the stack first.",
                    "Empty Stack",
                    NarrowOptionPane.ERROR_MESSAGE);
            throw new SedNoDataException();
	}
	    
	    ExtSed sed = SedStack.createSedFromStack(selectedStack);
	    
	    manager.add(sed);
	    
	} catch (SedException ex) {
	    Logger.getLogger(SedStackerFrame.class.getName()).log(Level.SEVERE, null, ex);
	} catch (UnitsException ex) {
	    Logger.getLogger(SedStackerFrame.class.getName()).log(Level.SEVERE, null, ex);
	}
    }//GEN-LAST:event_createSedButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        selectedStack.resetStack();
	sedsTable.setModel(new StackTableModel(selectedStack));
    }//GEN-LAST:event_resetButtonActionPerformed

    private SedStackerNormalizer normalizer;
    private void normalizeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normalizeButtonActionPerformed
	NormalizationConfiguration normConfig = selectedStack.getConf().getNormConfiguration();
	
	// Check for invalid values
	if (!checkNormParameters(normConfig)) {
	    try {
		this.setSelected(true);
	    } catch (PropertyVetoException ex) {
		Logger.getLogger(SedStackerFrame.class.getName()).log(Level.SEVERE, null, ex);
	    }
	    return;
	}
	
	if (normalizer == null) {
	    normalizer = new SedStackerNormalizer(controller);
	}
	try {
	    normalizer.normalize(selectedStack, normConfig);
	    NarrowOptionPane.showMessageDialog(this, "Successfully normalized stack.", "SED Stacker Message", JOptionPane.INFORMATION_MESSAGE);
	    if (isCreateSedAfterNormalize() && normalizer.normConfigChanged()) {
		ExtSed sed = SedStack.createSedFromStack(selectedStack, selectedStack.getName()+"_normalized");
		manager.add(sed);
	    }
	} catch (Exception ex) {
	    Logger.getLogger(SedStackerFrame.class.getName()).log(Level.SEVERE, null, ex);
	    //NarrowOptionPane.showMessageDialog(this, ex, "ERROR", JOptionPane.WARNING_MESSAGE);
	}
	sedsTable.setModel(new StackTableModel(selectedStack));
    }//GEN-LAST:event_normalizeButtonActionPerformed

    private SedStackerStacker stacker;
    private void stackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stackButtonActionPerformed
        StackConfiguration stackConfig = selectedStack.getConf().getStackConfiguration();
	
	// Check for invalid values
	
	
	
	if (stacker == null) {
	    stacker = new SedStackerStacker(controller);
	}
	try {
	    ExtSed stackedStack = stacker.stack(selectedStack, stackConfig);
	    NarrowOptionPane.showMessageDialog(this, "Successfully stacked.", "SED Stacker Message", JOptionPane.INFORMATION_MESSAGE);
	    manager.add(stackedStack);
	} catch (Exception ex) {
	    Logger.getLogger(SedStackerFrame.class.getName()).log(Level.SEVERE, null, ex);
	    //NarrowOptionPane.showMessageDialog(this, ex, "ERROR", JOptionPane.WARNING_MESSAGE);
	}
	
    }//GEN-LAST:event_stackButtonActionPerformed

    private void sedsTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sedsTableMousePressed
        if(evt.isPopupTrigger()) {
	    JTable source = (JTable) evt.getSource();
	    int row = source.rowAtPoint( evt.getPoint() );
	    int column = source.columnAtPoint( evt.getPoint() );

	    if (! source.isRowSelected(row))
		source.changeSelection(row, column, false, false);
	    
	    sedsTable.changeSelection(row, column, false, false);

	    jPopupMenu2.show(evt.getComponent(), evt.getX(), evt.getY());
	}
    }//GEN-LAST:event_sedsTableMousePressed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        String newRedshift = JOptionPane.showInputDialog(sedsTable, "New observed redshift: ");
	
	//TODO: check for bad z values.
	// Check for invalid redshift values
	if (newRedshift != null && newRedshift.length() > 0) {
	    if (!isNumeric(newRedshift) || Double.parseDouble(newRedshift) < 0) {
		NarrowOptionPane.showMessageDialog(null, "Invalid redshift values", "WARNING", NarrowOptionPane.WARNING_MESSAGE);
		try {
		    this.setSelected(true);
		} catch (PropertyVetoException ex) {
		    Logger.getLogger(SedStackerFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
		return;
	    }
	    
	    int i = sedsTable.getSelectedRow();
	
	    selectedStack.getSeds().get(i).addAttachment(SedStackerAttachments.REDSHIFT, newRedshift);
	    selectedStack.getSeds().get(i).addAttachment(SedStackerAttachments.ORIG_REDSHIFT, newRedshift);
	    selectedStack.getOrigSeds().get(i).addAttachment(SedStackerAttachments.REDSHIFT, newRedshift);
	    selectedStack.getOrigSeds().get(i).addAttachment(SedStackerAttachments.ORIG_REDSHIFT, newRedshift);


	    sedsTable.setModel(new StackTableModel(selectedStack));
	    
	}
	
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton addButton;
    javax.swing.JTextField atPointXText;
    javax.swing.JComboBox atPointXUnit;
    javax.swing.JTextField atPointYText;
    javax.swing.JComboBox atPointYType;
    javax.swing.JComboBox atPointYUnit;
    javax.swing.JTextField binsizeTextField;
    javax.swing.JCheckBox correctFlux;
    javax.swing.JButton createSedButton;
    javax.swing.JComboBox integrationMinMaxUnit;
    javax.swing.JComboBox integrationNormType;
    javax.swing.JTextField integrationValueText;
    javax.swing.JTextField integrationXMaxText;
    javax.swing.JTextField integrationXMinText;
    javax.swing.JComboBox integrationYUnit;
    javax.swing.JButton jButton1;
    javax.swing.JList jList1;
    javax.swing.JPopupMenu jPopupMenu1;
    javax.swing.JPopupMenu jPopupMenu2;
    javax.swing.JRadioButton jRadioButton1;
    javax.swing.JRadioButton jRadioButton2;
    javax.swing.JRadioButton jRadioButton3;
    javax.swing.JRadioButton jRadioButton4;
    javax.swing.JTextField jTextField6;
    javax.swing.JTextField jTextField8;
    javax.swing.JCheckBox logBinningCheckBox;
    javax.swing.JButton normalizeButton;
    javax.swing.JButton redshiftButton;
    javax.swing.JButton removeButton;
    javax.swing.JButton resetButton;
    public javax.swing.JTable sedsTable;
    javax.swing.JCheckBox smoothCheckBox;
    javax.swing.JComboBox stackBinSizeUnitsComboBox;
    javax.swing.JButton stackButton;
    javax.swing.JScrollPane stackPanel;
    javax.swing.JComboBox stackStatisticComboBox;
    javax.swing.JComboBox stackYUnitComboBox;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //private List<SedStack> stacks = new ArrayList();
    private List<SedStack> stacks = ObservableCollections.observableList(new ArrayList());
    
    public static final String PROP_STACKS = "stacks";

    /**
     * Get the value of stacks
     *
     * @return the value of stacks
     */
    public List<SedStack> getStacks() {
        return stacks;
    }

    /**
     * Set the value of stacks
     *
     * @param stacks new value of stacks
     */
    public void setStacks(List stacks) {
        List oldStacks = this.stacks;
        this.stacks = stacks;
        firePropertyChange(PROP_STACKS, oldStacks, stacks);
    }
    
    private SedStack selectedStack;

    public static final String PROP_SELECTEDSTACK = "selectedStack";

    /**
     * Get the value of selectedStack
     *
     * @return the value of selectedStack
     */
    public SedStack getSelectedStack() {
        return selectedStack;
    }

    /**
     * Set the value of selectedStack
     *
     * @param selectedStack new value of selectedStack
     */
    public void setSelectedStack(SedStack selectedStack) {
        SedStack oldSelectedStack = this.selectedStack;
        this.selectedStack = selectedStack;
        firePropertyChange(PROP_SELECTEDSTACK, oldSelectedStack, selectedStack);
	if (selectedStack != null) {
            setSelectedConfig(selectedStack.getConf());
	    sedsTable.setModel(new StackTableModel(selectedStack));
        }
    }
    
    private Configuration selectedConfig;

    public static final String PROP_SELECTEDCONFIG = "selectedConfig";

    /**
     * Get the value of selectedConfig
     *
     * @return the value of selectedConfig
     */
    public Configuration getSelectedConfig() {
        return selectedConfig;
    }

    /**
     * Set the value of selectedConfig
     *
     * @param selectedConfig new value of selectedConfig
     */
    public void setSelectedConfig(Configuration selectedConfig) {
        Configuration oldConfig = this.selectedConfig;
        this.selectedConfig = selectedConfig;
        firePropertyChange(PROP_SELECTEDCONFIG, oldConfig, selectedConfig);
    }

    private List<HashMap> selectedSeds;

    public static final String PROP_SELECTEDSEDS = "selectedSeds";

    /**
     * Get the value of selectedSeds
     *
     * @return the value of selectedSeds
     */
    public List<HashMap> getSelectedSeds() {
	return selectedSeds;
    }

    /**
     * Set the value of selectedSeds
     *
     * @param selectedSeds new value of selectedSeds
     */
    public void setSelectedSeds(List<HashMap> selectedSeds) {
	List<HashMap> oldSelectedSeds = this.selectedSeds;
	this.selectedSeds = selectedSeds;
	firePropertyChange(PROP_SELECTEDSEDS, oldSelectedSeds, selectedSeds);
    }
    
    public void rename(SedStack stack, String newName) {
	List<String> names = new ArrayList();
	for (int i=0; i < this.getStacks().size(); i++) {
	    names.add(this.getStacks().get(i).getName());
	}
	
        char c = '@';
	int i = 1;
	int j = 1;
        while (names.contains(newName + (c == '@' ? "" : "." + StringUtils.repeat(String.valueOf(c), j)))) {
	    int val = j*26;
	    if (i % val == 0) {
		c = '@';
		j++;
	    }
	    c++;
	    i++;
        }
        stack.setName(newName + (c == '@' ? "" : "." + StringUtils.repeat(String.valueOf(c), j)));
    }
    
    private void updateStackList(SedStack stack, Boolean add) {
	List<SedStack> newStacks = new ArrayList(stacks);
	if (add) {
	    newStacks.add(stack);
	    setStacks(newStacks);
	    setSelectedStack(stack);
	    jList1.setSelectedValue(stack, true);
	} else {
	    newStacks.remove(stack);
	    setStacks(newStacks);
	    SedStack newStack;
	    if (!getStacks().isEmpty()) {
		newStack = getStacks().get(newStacks.size()-1);
		setSelectedStack(newStack);
		jList1.setSelectedValue(newStack, true);
	    } else {
		newStack = new SedStack("Stack");
		updateStackList(newStack, true);
	    }
	}
    }

    private String[] loadEnum(Class<? extends Enum> clazz) {
        try {
            Enum[] l;
            l = (Enum[]) clazz.getMethod("values").invoke(null);
            String[] s = new String[l.length];
            for (int i = 0; i < l.length; i++) {
                IUnit u = (IUnit) l[i];
                s[i] = u.getString();
            }
            return s;
        } catch (Exception ex) {
            Logger.getLogger(SedStackerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

//    public void updateSedBuilderStack() throws SedInconsistentException, SedNoDataException, SedException, UnitsException {
//	// get the representative Sed in the SedBuilder
//	ExtSed sed = null;
//	if (manager.existsSed(selectedStack.getName())) {
//	    sed = manager.getSelected(); // FIXME
//	} else {
//	    manager.newSed(selectedStack.getName());
//	    sed = manager.getSelected();
//	}
//	// update the Sed with the latest version of the currently selected Stack
//	for (int i=0; i<sed.getNumberOfSegments(); i++) {
//	    sed.removeSegment(i);
//	}
//	for (int i=0; i<selectedStack.getSeds().size(); i++) {
//	    Segment seg = selectedStack.getSedBuilderStack().getSegment(i);
//	    sed.addSegment(seg);
//	}
    
    
    /*
     * This method is called after each undoable operation
     * in order to refresh the presentation state of the
     * undo/redo GUI
     */

//    public void refreshUndoRedo() {
//
//	// refresh undo
//	undoButton.setToolTipText(undoManager.getUndoPresentationName());
//	undoButton.setEnabled(undoManager.canUndo());
//
//	// refresh redo
//	redoButton.setToolTipText(undoManager.getRedoPresentationName());
//	redoButton.setEnabled(undoManager.canRedo());
//    }
//  
//    private SedStackerRedshifter redshifter;
//    private class RedshiftAction extends AbstractAction {
//	@Override
//	public void actionPerformed(ActionEvent evt) {
//	    if (redshifter == null) {
//		redshifter = new SedStackerRedshifter(app.getSAMPController(), manager);
//	    }
//	    // record the effect
//	    UndoableEdit edit = new RedshiftEdit(stack, zconf, undoManager);
//	    try {
//		// perform the operation
//		redshifter.shift(stack, stack.getRedshifts(), zconf);
//	    } catch (Exception ex) {
//		Logger.getLogger(SedStackerFrame.class.getName()).log(Level.SEVERE, null, ex);
//	    }
//	    // notify the listeners
//	    undoSupport.postEdit(edit);
//	}
//    }
//    
//    private SedStackerNormalizer normalizer;
//    private class NormalizeAction extends AbstractAction {
//	@Override
//	public void actionPerformed(ActionEvent evt) {
//	  if (normalizer == null) {
//		normalizer = new SedStackerNormalizer(app.getSAMPController());
//	    }
//	    // record the effect
//	    UndoableEdit edit = new NormalizeEdit(stack, normconf, undoManager);
//	    try {
//		// perform the operation
//		normalizer.normalize(stack, normconf);
//	    } catch (Exception ex) {
//		Logger.getLogger(SedStackerFrame.class.getName()).log(Level.SEVERE, null, ex);
//	    }
//	    // notify the listeners
//	    undoSupport.postEdit(edit);
//	}
//    }
//    
//    private SedStackerStacker stacker;
//    private class StackAction extends AbstractAction {
//
//	@Override
//	public void actionPerformed(ActionEvent evt) {
//
//          if (stacker == null) {
//		stacker = new SedStackerStacker(app.getSAMPController(), manager);
//	    }
//	    // record the effect
//	    UndoableEdit edit = new StackEdit(stack, stackconf, undoManager);
//	    try {
//		// perform the operation
//		stacker.stack(stack, stackconf);
//	    } catch (Exception ex) {
//		Logger.getLogger(SedStackerFrame.class.getName()).log(Level.SEVERE, null, ex);
//	    }
//	    // notify the listeners
//	    undoSupport.postEdit(edit);
//	}
//    }
//    
//    // Check if a button from a ButtonGroup is selected.
//    private boolean isButtonSelected(ButtonGroup buttonGroup, String actionCommand) {
//	
//	boolean checkIfSelected = false;
//	if(buttonGroup.getSelection().getActionCommand().equals(actionCommand)) {
//	    checkIfSelected = true;
//	}
//	return checkIfSelected;
//    }
//    
//    
//    
//    /*
//     *  undo action
//     */
//
//    private class UndoAction extends AbstractAction {
//
//	@Override
//	public void actionPerformed( ActionEvent evt ) {
//	    undoManager.undo();
//	    refreshUndoRedo();
//	}
//    }
//
//    /**
//     * inner class that defines the redo action
//     *
//     */
//
//    private class RedoAction extends AbstractAction {
//
//	@Override
//	public void actionPerformed(ActionEvent evt ) {
//	    undoManager.redo();
//	    refreshUndoRedo();
//	}
//    }
//
//    /**
//     * An undo/redo adapter. The adapter is notified when
//     * an undo edit occur(e.g. add or remove from the list)
//     * The adaptor extract the edit from the event, add it
//     * to the UndoManager, and refresh the GUI
//     */
//
//    private class UndoAdapter implements UndoableEditListener {
//	public void undoableEditHappened (UndoableEditEvent evt) {
//	    UndoableEdit edit = evt.getEdit();
//	    undoManager.addEdit(edit);
//	    refreshUndoRedo();
//	}
//    }
//
//    /**
//     * The list selection adapter change the remove button state
//     * according to the selection of the list
//     */
//    private class ListSelectionAdapter implements ListSelectionListener {
//	public void valueChanged(ListSelectionEvent evt) {
//	    if ( evt.getLastIndex() >= evt.getFirstIndex()) {
//		jButton3.setEnabled(true);
//	    }
//	}
//    }
    
    private static boolean isNumeric(String str) {
	  NumberFormat formatter = NumberFormat.getInstance();
	  ParsePosition pos = new ParsePosition(0);
	  formatter.parse(str, pos);
	  return str.length() == pos.getIndex();
	}
    
    private Boolean checkNormParameters(NormalizationConfiguration normConfig) {
	if (!normConfig.isIntegrate()) {
	    if (
		    normConfig.getAtPointXValue() == null ||
		    normConfig.getAtPointYValue() == null ||
		    !isNumeric(normConfig.getAtPointXValue().toString()) ||
		    !isNumeric(normConfig.getAtPointYValue().toString()) ||
		    normConfig.getAtPointXValue() <= 0 ||
		    normConfig.getAtPointYValue() <= 0
		) {
		NarrowOptionPane.showMessageDialog(null, "Invalid (X, Y) values.", "ERROR", NarrowOptionPane.ERROR_MESSAGE);
		return false;
	    }
	    
	} else {
	    if (
		    normConfig.getYValue() == null ||
		    !isNumeric(normConfig.getYValue().toString()) ||
		    normConfig.getYValue() <= 0
		) {
		NarrowOptionPane.showMessageDialog(this, "Invalid Y value.", "ERROR", NarrowOptionPane.ERROR_MESSAGE);
		return false;
	    }
	    
	    if (
		    normConfig.getXmax() == null ||
		    normConfig.getXmin() == null ||
		    normConfig.getXmax() <= 0 ||
		    normConfig.getXmax() <= normConfig.getXmin()
	    ) {
		NarrowOptionPane.showMessageDialog(this, "Invalid range values.", "ERROR", NarrowOptionPane.ERROR_MESSAGE);
		return false;
	    }
	    if (!normConfig.getXmin().equals(Double.NEGATIVE_INFINITY) && normConfig.getXmin() <= 0) {
		NarrowOptionPane.showMessageDialog(this, "Invalid range values.", "ERROR", NarrowOptionPane.ERROR_MESSAGE);
		return false;
	    }
	}
	
	return true;
    }
    
}
