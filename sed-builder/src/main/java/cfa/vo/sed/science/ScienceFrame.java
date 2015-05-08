/**
 * Copyright (C) 2013, 2015 Smithsonian Astrophysical Observatory
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
 * ScienceFrame.java
 *
 * Created on Feb 5, 2013, 12:07:03 PM
 */
package cfa.vo.sed.science;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.gui.widgets.SedList;
import cfa.vo.iris.logging.LogEntry;
import cfa.vo.iris.logging.LogEvent;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sed.builder.photfilters.*;
import cfa.vo.sed.gui.PhotometryPointFrame.PhotometryFilterSelector;
import cfa.vo.sed.gui.SaveSedDialog;
import cfa.vo.sed.science.integration.Response;
import cfa.vo.sed.science.integration.SherpaIntegrator;
import cfa.vo.sed.science.integration.SimplePhotometryPoint;
import cfa.vo.sed.science.interpolation.InterpolationConfig;
import cfa.vo.sed.science.interpolation.SherpaInterpolator;
import cfa.vo.sed.science.interpolation.SherpaRedshifter;
import cfa.vo.sed.science.interpolation.ZConfig;
import cfa.vo.sedlib.Param;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sherpa.CompositeModel;
import cfa.vo.sherpa.UserModel;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;
import spv.components.SherpaModelManager;
import spv.controller.SpectrumContainer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olaurino
 */
public class ScienceFrame extends javax.swing.JInternalFrame implements SedListener {

    private IrisApplication app;
    private SedlibSedManager manager;
    private IWorkspace ws;
    private static String ZCONF_ATTACH = "science:z";
    private static String ICONF_ATTACH = "science:interpolation";
    private ExtSed sed;
    public static final String PROP_SED = "sed";

    /**
     * Get the value of sed
     *
     * @return the value of sed
     */
    public ExtSed getSed() {
        return sed;
    }

    /**
     * Set the value of sed
     *
     * @param sed new value of sed
     */
    private void setSed(ExtSed sed) {
        ExtSed oldSed = this.sed;
        this.sed = sed;

        if (sed != null) {
            setIntegrateModel(isIntegrateModel()); // Hack - Force model reevaluation
            ZConfig zconf = (ZConfig) sed.getAttachment(ZCONF_ATTACH);
            if (zconf == null) {
                zconf = new ZConfig();
                sed.addAttachment(ZCONF_ATTACH, zconf);
            }

            if (sed.getNumberOfSegments() != 0) {
                List<Param> ps = (List<Param>) sed.getSegment(0).getCustomParams();
                for (Param p : ps) {
                    if (p.isSetName() && p.getName().equals("iris:final redshift")) {
                        zconf.setRedshift(Double.valueOf(p.getValue()));
                        zconf.setNewz(Double.valueOf(p.getValue()));
                    }
                }
            }

            InterpolationConfig iconf = (InterpolationConfig) sed.getAttachment(ICONF_ATTACH);
            if (iconf == null) {
                iconf = new InterpolationConfig();
                sed.addAttachment(ICONF_ATTACH, iconf);
            }
            
            setZconfig(zconf);
            setInterpConf(iconf);
        } else {
            setZconfig(null);
            setInterpConf(null);
        }

        firePropertyChange(PROP_SED, oldSed, sed);

        
    }
    private ZConfig zconfig;
    public static final String PROP_ZCONFIG = "zconfig";

    /**
     * Get the value of zconfig
     *
     * @return the value of zconfig
     */
    public ZConfig getZconfig() {
        return zconfig;
    }

    /**
     * Set the value of zconfig
     *
     * @param zconfig new value of zconfig
     */
    public void setZconfig(ZConfig zconfig) {
        ZConfig oldZconfig = this.zconfig;
        this.zconfig = zconfig;
        firePropertyChange(PROP_ZCONFIG, oldZconfig, zconfig);
    }

    /**
     * Creates new form ScienceFrame
     */
    public ScienceFrame(IrisApplication app, IWorkspace ws) {
        this.app = app;
        this.ws = ws;
        this.manager = (SedlibSedManager) ws.getSedManager();
        initComponents();
        sedPanel.setViewportView(new SedList(ws.getSedManager()));
        ExtSed _sed = (ExtSed) ws.getSedManager().getSelected();
        if (_sed == null) {
            NarrowOptionPane.showMessageDialog(null, "No SEDs open. Please start building SEDs using the SED builder", "Error", NarrowOptionPane.ERROR_MESSAGE);
            return;
        }
        setSed(_sed);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jSplitPane1 = new javax.swing.JSplitPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        zField = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        busy = new org.jdesktop.swingx.JXBusyLabel();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jTextField3 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jRadioButton8 = new javax.swing.JRadioButton();
        jRadioButton9 = new javax.swing.JRadioButton();
        jTextField10 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jTextField12 = new javax.swing.JTextField();
        xAxisCombo4 = new javax.swing.JComboBox();
        jButton9 = new javax.swing.JButton();
        integrateModelButton = new javax.swing.JRadioButton();
        jPanel9 = new javax.swing.JPanel();
        modelExpressionField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        fullModelButton = new javax.swing.JRadioButton();
        jTextField4 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        busy2 = new org.jdesktop.swingx.JXBusyLabel();
        jPanel4 = new javax.swing.JPanel();
        sedPanel = new javax.swing.JScrollPane();

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable2.setName("jTable2"); // NOI18N
        jScrollPane2.setViewportView(jTable2);

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setResizable(true);
        setTitle("Science");

        jSplitPane1.setDividerLocation(180);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel1.setEnabled(false);
        jPanel1.setName("jPanel1"); // NOI18N

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Redshift"));
        jPanel6.setName("jPanel6"); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        zField.setName("zField"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${zconfig.redshift}"), zField, org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setSourceNullValue(0.0);
        binding.setSourceUnreadableValue(0.0);
        bindingGroup.addBinding(binding);

        jLabel1.setText("Initial redshift:");
        jLabel1.setName("jLabel1"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(3, 3, 3)
                        .add(zField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel1))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(zField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel5.setName("jPanel5"); // NOI18N

        jFormattedTextField1.setName("jFormattedTextField1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${zconfig.newz}"), jFormattedTextField1, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        jLabel2.setText("Move to redshift:");
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(ScienceFrame.class, this);
        jButton1.setAction(actionMap.get("shiftSed")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(jFormattedTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jButton1)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jFormattedTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(74, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Interpolation"));
        jPanel3.setName("jPanel3"); // NOI18N

        jLabel3.setText("Method:");
        jLabel3.setName("jLabel3"); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Linear Spline", "Linear", "Nearest Neighbor" }));
        jComboBox1.setName("jComboBox1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${interpConf.method}"), jComboBox1, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel5.setText("X Min:");
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setText("X Max:");
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText("Units:");
        jLabel7.setName("jLabel7"); // NOI18N

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Angstrom", "Hz", "keV" }));
        jComboBox2.setName("jComboBox2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${interpConf.units}"), jComboBox2, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jButton2.setAction(actionMap.get("interpolateSed")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N

        jLabel4.setText("Number of Bins:");
        jLabel4.setName("jLabel4"); // NOI18N

        jFormattedTextField2.setName("jFormattedTextField2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${interpConf.NBins}"), jFormattedTextField2, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        jCheckBox1.setText("Normalize after interpolation");
        jCheckBox1.setName("jCheckBox1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${interpConf.normalize}"), jCheckBox1, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jTextField1.setName("jTextField1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${interpConf.XMin}"), jTextField1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new DoubleConverter());
        bindingGroup.addBinding(binding);

        jTextField2.setName("jTextField2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${interpConf.XMax}"), jTextField2, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        busy.setName("busy"); // NOI18N

        jCheckBox2.setText("Logarithmic binning");
        jCheckBox2.setName("jCheckBox2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${interpConf.log}"), jCheckBox2, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jCheckBox3.setText("Smooth");
        jCheckBox3.setName("jCheckBox3"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${interpConf.smooth}"), jCheckBox3, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jTextField3.setName("jTextField3"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${interpConf.boxSize}"), jTextField3, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new IntConverter());
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox3, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jTextField3, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel8.setText("Box Size:");
        jLabel8.setName("jLabel8"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                        .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 207, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                                .add(2, 2, 2)
                                .add(jLabel5)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(jLabel6)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jTextField2))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                                .add(jLabel4)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jFormattedTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 144, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                                .add(jCheckBox3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel8)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jTextField3)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jCheckBox1)
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(jLabel7)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jComboBox2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 137, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jCheckBox2)))))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(164, Short.MAX_VALUE)
                .add(jButton2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 127, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(busy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(171, 171, 171))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(4, 4, 4)
                        .add(jLabel3))
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(8, 8, 8)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(jLabel5))
                    .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel6)
                        .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel7)
                        .add(jComboBox2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jCheckBox1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jCheckBox2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 70, Short.MAX_VALUE)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jButton2)
                            .add(busy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jFormattedTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel4))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jCheckBox3)
                            .add(jTextField3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel8))
                        .addContainerGap())))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Redshift and Interpolation", jPanel1);

        jPanel7.setName("jPanel7"); // NOI18N

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Add Passband"));
        jPanel11.setName("jPanel11"); // NOI18N

        buttonGroup1.add(jRadioButton8);
        jRadioButton8.setText("Passband");
        jRadioButton8.setName("jRadioButton8"); // NOI18N
        jRadioButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeMode(evt);
            }
        });

        buttonGroup1.add(jRadioButton9);
        jRadioButton9.setText("Photometry Filter");
        jRadioButton9.setName("jRadioButton9"); // NOI18N
        jRadioButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeMode(evt);
            }
        });

        jTextField10.setName("jTextField10"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${pbconf.min}"), jTextField10, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton8, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jTextField10, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton3.setAction(actionMap.get("choosefilter")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton9, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jButton3, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jTextField12.setName("jTextField12"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${pbconf.max}"), jTextField12, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton8, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jTextField12, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        xAxisCombo4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Angstrom", "Hz", "keV" }));
        xAxisCombo4.setName("xAxisCombo4"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${pbconf.units}"), xAxisCombo4, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton8, org.jdesktop.beansbinding.ELProperty.create("${selected}"), xAxisCombo4, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton9.setAction(actionMap.get("addPassBands")); // NOI18N
        jButton9.setName("jButton9"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton8, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jButton9, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        integrateModelButton.setText("Integrate model (NO)"); // NOI18N
        integrateModelButton.setName("integrateModelButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${integrateModel}"), integrateModelButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Model Integration"));
        jPanel9.setName("jPanel9"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, integrateModelButton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jPanel9, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        modelExpressionField.setName("modelExpressionField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${modelExpression}"), modelExpressionField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, fullModelButton, org.jdesktop.beansbinding.ELProperty.create("${!selected && enabled}"), modelExpressionField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel9.setText("Model Expression:");
        jLabel9.setName("jLabel9"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, integrateModelButton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel9, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        fullModelButton.setText("Full Model");
        fullModelButton.setName("fullModelButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${fullModel}"), fullModelButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, integrateModelButton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), fullModelButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jTextField4.setColumns(5);
        jTextField4.setName("jTextField4"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${modelBins}"), jTextField4, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, integrateModelButton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jTextField4, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel10.setText("Model evaluation bins #:");
        jLabel10.setName("jLabel10"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, integrateModelButton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel10, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton7.setAction(actionMap.get("showModel")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, integrateModelButton, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jButton7, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(fullModelButton)
                .add(13, 13, 13)
                .add(jLabel10)
                .add(2, 2, 2)
                .add(jTextField4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton7))
            .add(jPanel9Layout.createSequentialGroup()
                .add(9, 9, 9)
                .add(jLabel9)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(modelExpressionField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(fullModelButton)
                        .add(jLabel10)
                        .add(jTextField4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jButton7))
                .add(10, 10, 10)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(modelExpressionField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel11Layout.createSequentialGroup()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel11Layout.createSequentialGroup()
                        .add(jRadioButton8)
                        .add(18, 18, 18)
                        .add(jTextField10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel11Layout.createSequentialGroup()
                        .add(jRadioButton9)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton3)))
                .add(7, 7, 7)
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(integrateModelButton)
                    .add(jPanel11Layout.createSequentialGroup()
                        .add(xAxisCombo4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jTextField10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jTextField12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(xAxisCombo4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jRadioButton8)
                        .add(jButton9))
                    .add(jPanel11Layout.createSequentialGroup()
                        .add(42, 42, 42)
                        .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jRadioButton9)
                            .add(jButton3)
                            .add(integrateModelButton))))
                .add(9, 9, 9)
                .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Results"));
        jPanel8.setName("jPanel8"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jTable3.setName("jTable3"); // NOI18N
        jTable3.setShowGrid(true);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${points}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jTable3);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${id}"));
        columnBinding.setColumnName("Passband");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${wavelength}"));
        columnBinding.setColumnName("Eff WL (Angstrom)");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${flux}"));
        columnBinding.setColumnName("Flux (erg/s/cm2)");
        columnBinding.setColumnClass(Double.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane3.setViewportView(jTable3);
        if (jTable3.getColumnModel().getColumnCount() > 0) {
            jTable3.getColumnModel().getColumn(1).setCellRenderer(new ScientificRenderer());
            jTable3.getColumnModel().getColumn(2).setCellRenderer(new ScientificRenderer());
        }

        jButton4.setAction(actionMap.get("reset")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N

        jButton5.setAction(actionMap.get("save")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N

        jButton6.setAction(actionMap.get("createSED")); // NOI18N
        jButton6.setName("jButton6"); // NOI18N

        jButton8.setAction(actionMap.get("calculate")); // NOI18N
        jButton8.setName("jButton8"); // NOI18N

        busy2.setName("busy2"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(jButton8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(busy2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButton4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButton6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButton5)
                .addContainerGap())
            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel8Layout.createSequentialGroup()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButton8)
                    .add(busy2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jButton4)
                        .add(jButton6)
                        .add(jButton5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
        );

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Calculate Flux", jPanel7);

        jSplitPane1.setRightComponent(jTabbedPane1);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Open SEDs"));
        jPanel4.setName("jPanel4"); // NOI18N

        sedPanel.setBorder(null);
        sedPanel.setName("sedPanel"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 166, Short.MAX_VALUE)
            .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, sedPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 387, Short.MAX_VALUE)
            .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(sedPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel4);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void changeMode(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeMode
    pbconf.setMode(evt.getActionCommand());
}//GEN-LAST:event_changeMode
    private List<PhotometryFilter> filters = new ArrayList<PhotometryFilter>();
    private PhotometryFilterSelector selector;
    private FilterSelectionListener listener = new FilterSelectionListener() {
        @Override
        public void process(final List<PhotometryFilter> source, SedCommand payload) {
            for (PhotometryFilter pf : source) {
                if (!filters.contains(pf)) {
                    filters.add(pf);
                    addPassBand(pf, false);
                }
            }
            List<PassBand> pbs = new ArrayList(bands);
            try {
                List<SimplePhotometryPoint> out = calculate(pbs);
//                List<SimplePhotometryPoint> ppoints = new ArrayList();
                setPoints(null);
//                ppoints.addAll(out);
                setPoints(out);
            } catch (Exception ex) {
                NarrowOptionPane.showMessageDialog(null, ex.getMessage(), "Error", NarrowOptionPane.ERROR_MESSAGE);
                Logger.getLogger(ScienceFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };

    @Action
    public void choosefilter() throws Exception {
        if (selector == null) {
            selector = new PhotometryFilterSelector(listener, true);
            JLabel label = new JLabel(convertToMultiline("You can select multiple filters.\nTransmission curves for the filters will be "
                    + "downloaded in a local cache,\nso you need an Internet connection only for downloading new filters."));
            selector.add(label, BorderLayout.SOUTH);
            label.setVisible(true);
            SedBuilder.getWorkspace().addFrame(selector);
            selector.pack();
        }

        selector.show();
    }

    public static String convertToMultiline(String orig) {
        return "<html><p style='margin-left:10px; margin-bottom:10px'>" + orig.replaceAll("\n", "<br>") + "</p></html>";
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busy;
    private org.jdesktop.swingx.JXBusyLabel busy2;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton fullModelButton;
    private javax.swing.JRadioButton integrateModelButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField modelExpressionField;
    private javax.swing.JScrollPane sedPanel;
    private javax.swing.JComboBox xAxisCombo4;
    private javax.swing.JFormattedTextField zField;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    @Override
    public void process(ExtSed source, SedCommand payload) {
        if (payload.equals(SedCommand.SELECTED) || payload.equals(SedCommand.ADDED) || payload.equals(SedCommand.CHANGED)) {
            setSed(source);
            if (source != null) {
                filters = (List<PhotometryFilter>) source.getAttachment(FILTERS_ATTACH);
                setBands((List<PassBand>) source.getAttachment(BANDS_ATTACH));
                setPoints((List<SimplePhotometryPoint>) source.getAttachment(PHOTOMETRY_ATTACH));
            } else {
                filters = new ArrayList();
                setBands(new ArrayList());
                setPoints(new ArrayList());
            }
            if (filters == null) {
                filters = new ArrayList();
            }
            iqconfig.setPassbands(bands == null ? new ArrayList() : bands);
        }

        if (payload.equals(SedCommand.REMOVED)) {
            setSed(null);
        }

//        addPassBands();

    }
    private SherpaRedshifter redshifter;

    @Action
    public void shiftSed() {
        if (zconfig.getNewz() == null || zconfig.getRedshift() == null || zconfig.getNewz() < 0 || zconfig.getRedshift() < 0) {
            NarrowOptionPane.showMessageDialog(null, "Invalid redshift values", "WARNING", NarrowOptionPane.WARNING_MESSAGE);
            return;
        }
        if (redshifter == null) {
            redshifter = new SherpaRedshifter(app.getSAMPController(), manager);
        }
        try {
            if (sed.getNumberOfSegments() == 0) {
                NarrowOptionPane.showMessageDialog(null, "SED is emtpy.", "WARNING", NarrowOptionPane.WARNING_MESSAGE);
                return;
            }
            redshifter.shift(sed, zconfig.getRedshift(), zconfig.getNewz());
            LogEvent.getInstance().fire(sed, new LogEntry("SED: " + sed + ", " + zconfig + " to Redshift: " + zconfig.getNewz() + " SUCCESS", this));
        } catch (Exception ex) {
            NarrowOptionPane.showMessageDialog(null, "Error while redshifting the SED: " + ex.getMessage(), "Error", NarrowOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ScienceFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private SherpaInterpolator interpolator;

    @Action
    public Task interpolateSed() {
        return new InterpolateSedTask(org.jdesktop.application.Application.getInstance());
    }

    private class InterpolateSedTask extends org.jdesktop.application.Task<Object, Void> {

        InterpolateSedTask(org.jdesktop.application.Application appl) {
            super(appl);
            busy.setBusy(true);
        }

        @Override
        protected Object doInBackground() {
            if (interpolator == null) {
                interpolator = new SherpaInterpolator(app.getSAMPController(), manager);
            }
            try {
                if (sed.getNumberOfSegments() == 0) {
                    NarrowOptionPane.showMessageDialog(null, "SED is emtpy.", "WARNING", NarrowOptionPane.WARNING_MESSAGE);
                    return null;
                }
                ExtSed newSed = interpolator.interpolate(sed, interpConf);
                Param param = new Param(interpConf.toString(), "iris:interpolation configuration", "");
                newSed.getSegment(0).addCustomParam(param);
                LogEvent.getInstance().fire(sed, new LogEntry("SED: " + sed + ", " + interpConf + " SUCCESS", this));
            } catch (Exception ex) {
                LogEvent.getInstance().fire(sed, new LogEntry("SED: " + sed + ", " + interpConf + " FAILURE: " + ex.getMessage(), this));
                NarrowOptionPane.showMessageDialog(null, "Error while interpolating the SED: " + ex.getMessage(), "Error", NarrowOptionPane.ERROR_MESSAGE);
                Logger.getLogger(ScienceFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            busy.setBusy(false);
        }
    }
    private InterpolationConfig interpConf;
    public static final String PROP_INTERPCONF = "interpConf";

    /**
     * Get the value of interpConf
     *
     * @return the value of interpConf
     */
    public InterpolationConfig getInterpConf() {
        return interpConf;
    }

    /**
     * Set the value of interpConf
     *
     * @param interpConf new value of interpConf
     */
    public void setInterpConf(InterpolationConfig interpConf) {
        InterpolationConfig oldInterpConf = this.interpConf;
        this.interpConf = interpConf;
        firePropertyChange(PROP_INTERPCONF, oldInterpConf, interpConf);
    }

    private String[] loadEnum(Class<? extends Enum> clazz) {
        try {
            Enum[] l;
            l = (Enum[]) clazz.getMethod("values").invoke(null);
            String[] s = new String[l.length];
            for (int i = 0; i < l.length; i++) {
                s[i] = l[i].name();
            }
            return s;
        } catch (Exception ex) {
            Logger.getLogger(ScienceFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private class DoubleConverter extends org.jdesktop.beansbinding.Converter {

        @Override
        public Object convertReverse(Object value) {
            String val = (String) value;
            Double ret = Double.valueOf(val);
            return ret;
        }

        @Override
        public Object convertForward(Object value) {
            Double val = (Double) value;
            return val.toString();
        }
    }

    private class IntConverter extends org.jdesktop.beansbinding.Converter {

        @Override
        public Object convertReverse(Object value) {
            String val = (String) value;
            Integer ret = Integer.valueOf(val);
            return ret;
        }

        @Override
        public Object convertForward(Object value) {
            Integer val = (Integer) value;
            return val.toString();
        }
    }
    private PassBandConf pbconf = new PassBandConf();

    public PassBandConf getPbconf() {
        return pbconf;
    }

    public void setPbconf(PassBandConf pbconf) {
        this.pbconf = pbconf;
    }
    private IQConfig iqconfig = new IQConfig();

    public IQConfig getIqconfig() {
        return iqconfig;
    }

    public void setIqconfig(IQConfig iqconfig) {
        this.iqconfig = iqconfig;
    }

    public synchronized void addPassBand(PassBand pb, boolean calculate) {
        if (!iqconfig.getPassbands().contains(pb)) {
            iqconfig.getPassbands().add(pb);
            if (pb instanceof PhotometryFilter) {
                PhotometryFilter f = (PhotometryFilter) pb;
                try {
                    boolean fetched = f.getCurve();
                    if (!fetched) {
                        throw new Exception();
                    }
                } catch (SedException ex) {
                    Logger.getLogger(ScienceFrame.class.getName()).log(Level.SEVERE, null, ex);
                    NarrowOptionPane.showMessageDialog(null, "Cannot read filter profile", "Error", NarrowOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    Logger.getLogger(ScienceFrame.class.getName()).log(Level.SEVERE, null, ex);
                    NarrowOptionPane.showMessageDialog(null, "Cannot read filter profile", "Error", NarrowOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    return;
                }
            }
        }

        setBands(iqconfig.getPassbands());
        if(calculate) {
            calculate(pb);
        }
        sed.addAttachment(FILTERS_ATTACH, filters);
    }

    @Action
    public void addPassBands() {
        EnergyBin eb = new EnergyBin();
        eb.setMax(pbconf.getMax());
        eb.setMin(pbconf.getMin());
        eb.setUnits(pbconf.getUnits());
        addPassBand(eb, true);
    }
    private List<PassBand> bands;
    public static final String PROP_BANDS = "bands";

    /**
     * Get the value of bands
     *
     * @return the value of bands
     */
    public List<PassBand> getBands() {
        return bands;
    }

    /**
     * Set the value of bands
     *
     * @param bands new value of bands
     */
    public void setBands(List<PassBand> bands) {
        List<PassBand> oldBands = this.bands;
        this.bands = bands;
        firePropertyChange(PROP_BANDS, oldBands, bands);
        if (bands != null && !bands.isEmpty()) {
//            List<SimplePhotometryPoint> ppoints = new ArrayList();
//            for (PassBand pb : bands) {
//                SimplePhotometryPoint point = (SimplePhotometryPoint) SAMPFactory.get(SimplePhotometryPoint.class);
//                point.setId(pb.getId());
//                ppoints.add(point);
//            }
//            setPoints(ppoints);
        } else {
            setPoints(new ArrayList());
        }

        sed.addAttachment(BANDS_ATTACH, bands);
    }

    @Action
    public Task calculate() {
        if (bands == null) {
            NarrowOptionPane.showMessageDialog(null,
                    "No bands selected, please select at least one passband first",
                    "Error",
                    NarrowOptionPane.ERROR_MESSAGE);
            return null;
        }
        return new CalculateTask(org.jdesktop.application.Application.getInstance());
    }
    SherpaIntegrator integrator;

    private synchronized List<SimplePhotometryPoint> calculate(List<PassBand> pbs) throws Exception{
        if (integrator == null) {
            integrator = new SherpaIntegrator(app.getSAMPController());
        }

        Response response = (Response) SAMPFactory.get(Response.class);
        if (integrateModel) {
            SpectrumContainer sc = (SpectrumContainer) sed.getAttachment("fit.model");
            SherpaModelManager smm = (SherpaModelManager) sc.getModelManager();
            if (!smm.lastFitted()) {
                throw new Exception("No Model Found. Please fit the data first and keep the fitting window open.");
            }
            CompositeModel model = smm.getModel();
            model.setName(modelExpression);

            List<UserModel> userModels = smm.getUserModels();

            for (PassBand pb : pbs) {
                List pbl = new ArrayList();
                pbl.add(pb);
                response.addPoint(integrator.integrateComponents(pbl, model, userModels, modelBins).getPoints().get(0));
            }
        } else {
            response = integrator.integrate(sed, pbs);
        }
        return response.getPoints();

    }


    private synchronized void calculate(PassBand pb) {
        List<PassBand> ps = new ArrayList();
        ps.add(pb);
        List<SimplePhotometryPoint> out = null;
        try {
            out = calculate(ps);
        } catch (Exception ex) {
            Logger.getLogger(ScienceFrame.class.getName()).log(Level.SEVERE, null, ex);
            NarrowOptionPane.showMessageDialog(null, (String) ex.getMessage(), "Error", NarrowOptionPane.ERROR_MESSAGE);
            return;
        }

        List<SimplePhotometryPoint> ppoints = new ArrayList(points);
        setPoints(null);
        ppoints.addAll(out);
        setPoints(ppoints);


    }

    private class CalculateTask extends org.jdesktop.application.Task<Object, Void> {

        List<PassBand> pbs;

        CalculateTask(org.jdesktop.application.Application application) {
            super(application);
            busy2.setBusy(true);
            if (integrator == null) {
                integrator = new SherpaIntegrator(app.getSAMPController());
            }

            pbs = new ArrayList(bands);
        }

        @Override
        protected Object doInBackground() {
            try {
                return calculate(pbs);
            } catch (Exception ex) {
                Logger.getLogger(ScienceFrame.class.getName()).log(Level.SEVERE, null, ex);
                return ex.getMessage();
            }
        }

        @Override
        protected void succeeded(Object result) {
            busy2.setBusy(false);
            if (result instanceof String) {
                NarrowOptionPane.showMessageDialog(null, (String) result, "Error", NarrowOptionPane.ERROR_MESSAGE);
                return;
            }

            List<SimplePhotometryPoint> ppoints = (List<SimplePhotometryPoint>) result;

            setPoints(ppoints);

        }
    }
    private static String PHOTOMETRY_ATTACH = "builder:science:photometry";
    private static String BANDS_ATTACH = "builder:science:bands";
    private static String FILTERS_ATTACH = "builder:science:filters";
    private List<SimplePhotometryPoint> points = new ArrayList();
    public static final String PROP_POINTS = "points";

    /**
     * Get the value of points
     *
     * @return the value of points
     */
    public List<SimplePhotometryPoint> getPoints() {
        return points;
    }

    /**
     * Set the value of points
     *
     * @param points new value of points
     */
    public synchronized void setPoints(List<SimplePhotometryPoint> points) {
        if (points != null) {
            Collections.sort(points, COMPARATOR);
        }
        List<SimplePhotometryPoint> oldPoints = this.points;
        this.points = points;
        firePropertyChange(PROP_POINTS, oldPoints, points);
        sed.addAttachment(PHOTOMETRY_ATTACH, points);
    }
    private static Comparator<SimplePhotometryPoint> COMPARATOR = new Comparator<SimplePhotometryPoint>() {
        @Override
        public int compare(SimplePhotometryPoint o1, SimplePhotometryPoint o2) {
            return o1.getWavelength().compareTo(o2.getWavelength());
        }
    };

    @Action
    public void reset() {
        iqconfig.setPassbands(new ArrayList());
        setBands(new ArrayList());
        setPoints(new ArrayList());
        filters = new ArrayList();
        sed.removeAttachment(PHOTOMETRY_ATTACH);
        sed.removeAttachment(BANDS_ATTACH);
        sed.removeAttachment(FILTERS_ATTACH);
        sed.removeAttachment(ICONF_ATTACH);
    }

    @Action
    public void createSED() {
        if (points == null || points.isEmpty() || isNull(points)) {
            NarrowOptionPane.showMessageDialog(null,
                    "No photometry points were created. Please add passbands and/or press the calculate button",
                    "Empy List",
                    NarrowOptionPane.ERROR_MESSAGE);
            return;
        }

        createSedFromPoints(true);

    }

    private boolean isNull(List<SimplePhotometryPoint> points) {
        boolean isnull = false;
        for (SimplePhotometryPoint p : points) {
            if (p.getWavelength() == null || p.getFlux() == null) {
                isnull = true;
            }
        }
        return isnull;
    }

    public ExtSed createSedFromPoints(boolean managed) {
        ExtSed newsed = new ExtSed("Integrated", managed);

        double[] x = new double[points.size()];
        double[] y = new double[points.size()];

        int i = 0;
        for (SimplePhotometryPoint p : points) {
            x[i] = p.getWavelength();
            y[i] = p.getFlux();
            i++;
        }

        try {
//            y = YUnits.convert(y, x, new YUnits("Jy"), new XUnits("Angstrom"), new YUnits("erg/s/cm2"), true);
            Segment segment = new Segment();
            segment.setSpectralAxisValues(x);
            segment.setFluxAxisValues(y);
            segment.setTarget(sed.getSegment(0).getTarget());
            segment.setSpectralAxisUnits("Angstrom");
            segment.setFluxAxisUnits("erg/s/cm2");
            segment.createChar().createSpectralAxis().setUcd("em.wl");
            segment.createChar().createFluxAxis().setUcd("phot.flux;em.wl");
            newsed.addSegment(segment);
            newsed.checkChar();
        } catch (Exception ex) {
            Logger.getLogger(ScienceFrame.class.getName()).log(Level.SEVERE, null, ex);
            NarrowOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Unexpected Error",
                    NarrowOptionPane.ERROR_MESSAGE);
        }

        if (managed) {
            manager.add(newsed);
        }

        return newsed;
    }

    @Action
    public void save() {
        if (points == null || points.isEmpty() || isNull(points)) {
            NarrowOptionPane.showMessageDialog(null,
                    "No photometry points were created. Please add passbands and/or press the calculate button",
                    "Empy List",
                    NarrowOptionPane.ERROR_MESSAGE);
            return;
        }
        ExtSed newSed = createSedFromPoints(false);
        SaveSedDialog ssd = new SaveSedDialog(null, newSed, true);
        ssd.setVisible(true);
    }

    private class ScientificRenderer extends DefaultTableCellRenderer {

        NumberFormat formatter;

        @Override
        public void setValue(Object value) {
            if (formatter == null) {
                formatter = new DecimalFormat("0.######E0");

            }
            setText((value == null) ? "" : formatter.format(value));
        }
    }
    
    private boolean integrateModel = false;
    public static final String PROP_INTEGRATEMODEL = "integrateModel";

    /**
     * Get the value of integrateModel
     *
     * @return the value of integrateModel
     */
    public boolean isIntegrateModel() {
        return integrateModel;
    }

    /**
     * Set the value of integrateModel
     *
     * @param integrateModel new value of integrateModel
     */
    public void setIntegrateModel(boolean integrateModel) {
        boolean oldIntegrateModel = this.integrateModel;
        this.integrateModel = integrateModel;
        firePropertyChange(PROP_INTEGRATEMODEL, oldIntegrateModel, integrateModel);
        if (integrateModel) {
            integrateModelButton.setText("Integrate Model (YES)");
        } else {
            integrateModelButton.setText("Integrate Model (NO)");
        }
        SpectrumContainer sc = (SpectrumContainer) sed.getAttachment("fit.model");
        if (sc != null) {
            SherpaModelManager smm = (SherpaModelManager) sc.getModelManager();
            if (smm != null && smm.lastFitted()) {
                setModelExpression(smm.getExpression());
            } else {
                setModelExpression("No Model");
            }
        } else {
            setModelExpression("No Model");
        }

    }
    
    private boolean fullModel = true;
    public static final String PROP_FULLMODEL = "fullModel";

    /**
     * Get the value of fullModel
     *
     * @return the value of fullModel
     */
    public boolean isFullModel() {
        return fullModel;
    }

    /**
     * Set the value of fullModel
     *
     * @param fullModel new value of fullModel
     */
    public void setFullModel(boolean fullModel) {
        boolean oldFullModel = this.fullModel;
        this.fullModel = fullModel;
        firePropertyChange(PROP_FULLMODEL, oldFullModel, fullModel);
        if (fullModel) {
            setIntegrateModel(isIntegrateModel());
        }
    }

    private int modelBins = 10000;
    public static final String PROP_MODELBINS = "modelBins";

    /**
     * Get the value of modelBins
     *
     * @return the value of modelBins
     */
    public int getModelBins() {
        return modelBins;
    }

    /**
     * Set the value of modelBins
     *
     * @param modelBins new value of modelBins
     */
    public void setModelBins(int modelBins) {
        int oldModelBins = this.modelBins;
        this.modelBins = modelBins;
        firePropertyChange(PROP_MODELBINS, oldModelBins, modelBins);
    }
    
    private String modelExpression;
    public static final String PROP_MODELEXPRESSION = "modelExpression";

    /**
     * Get the value of modelExpression
     *
     * @return the value of modelExpression
     */
    public String getModelExpression() {
        return modelExpression;
    }

    /**
     * Set the value of modelExpression
     *
     * @param modelExpression new value of modelExpression
     */
    public void setModelExpression(String modelExpression) {
        String oldModelExpression = this.modelExpression;
        this.modelExpression = modelExpression;
        firePropertyChange(PROP_MODELEXPRESSION, oldModelExpression, modelExpression);
    }
    
    private ModelViewerFrame mvf;

    @Action
    public void showModel() {

        if (mvf == null) {
            mvf = new ModelViewerFrame(sed);
            ws.addFrame(mvf);
        }
        GUIUtils.moveToFront(mvf);
    }


    
}
