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
 * NewJInternalFrame.java
 *
 * Created on May 19, 2011, 7:54:58 AM
 */
package cfa.vo.sed.gui;

import cfa.vo.iris.gui.ConfirmJInternalFrame;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.ISedManager;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.utils.NameResolver;
import cfa.vo.iris.utils.NameResolver.Position;
import cfa.vo.iris.utils.SkyCoordinates;
import cfa.vo.sed.setup.SetupBean;
import cfa.vo.sed.setup.validation.AxesValidator;
import cfa.vo.sed.setup.validation.ErrorValidator;
import cfa.vo.sed.builder.ISegmentColumn;
import cfa.vo.sed.builder.ISegmentMetadata;
import cfa.vo.sed.builder.ISegmentParameter;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sed.builder.SegmentImporter;
import cfa.vo.sed.filters.FileFormatManager;
import cfa.vo.sed.filters.FilterException;
import cfa.vo.sed.filters.IFileFormat;
import cfa.vo.sed.quantities.IUnit;
import cfa.vo.sed.quantities.SPVYQuantity;
import cfa.vo.sed.quantities.XQuantity;
import cfa.vo.sed.setup.validation.IValidator;
import cfa.vo.sedlib.Segment;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JInternalFrame;
import jsky.catalog.Catalog;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;

/**
 *
 * @author olaurino
 */
public final class SetupFrame extends ConfirmJInternalFrame implements SegmentFrame {

    private NameResolver resolver = NameResolver.getInstance();
    private boolean configurationValid;
    public static final String PROP_CONFIGURATIONVALID = "configurationValid";

    /**
     * Get the value of configurationValid
     *
     * @return the value of configurationValid
     */
    public boolean isConfigurationValid() {
        return configurationValid;
    }

    /**
     * Set the value of configurationValid
     *
     * @param configurationValid new value of configurationValid
     */
    public void setConfigurationValid(boolean configurationValid) {
        boolean oldConfigurationValid = this.configurationValid;
        this.configurationValid = configurationValid;
        firePropertyChange(PROP_CONFIGURATIONVALID, oldConfigurationValid, configurationValid);
    }
    private String message;
    public static final String PROP_MESSAGE = "message";

    /**
     * Get the value of message
     *
     * @return the value of message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the value of message
     *
     * @param message new value of message
     */
    public void setMessage(String message) {
        String oldMessage = this.message;
        this.message = message;
        firePropertyChange(PROP_MESSAGE, oldMessage, message);
    }
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
        if (ra != null) {
            confBean.setTargetRa(ra);
        }
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
        if (dec != null) {
            confBean.setTargetDec(dec);
        }
    }
    private String targetName = "";
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
        if (targetName!=null && !targetName.equals("")) {
            confBean.setTargetName(targetName);
        }
    }
    private SetupBean confBean;
    public static final String PROP_CONFBEAN = "confBean";

    /**
     * Get the value of confBean
     *
     * @return the value of confBean
     */
    public SetupBean getConfBean() {
        return confBean;
    }

    /**
     * Set the value of confBean
     *
     * @param confBean new value of confBean
     */
    public void setConfBean(SetupBean confBean) {
        SetupBean oldConfBean = this.confBean;
        this.confBean = confBean;
        firePropertyChange(PROP_CONFBEAN, oldConfBean, confBean);
    }
    private ISegmentMetadata metadata;
    public static final String PROP_METADATA = "metadata";

    /**
     * Get the value of metadata
     *
     * @return the value of metadata
     */
    public ISegmentMetadata getMetadata() {
        return metadata;
    }

    /**
     * Set the value of metadata
     *
     * @param metadata new value of metadata
     */
    public void setMetadata(ISegmentMetadata metadata) {
        ISegmentMetadata oldMetadata = this.metadata;
        this.metadata = metadata;
        firePropertyChange(PROP_METADATA, oldMetadata, metadata);
    }
    private String sedID;
    public static final String PROP_SEDID = "sedID";

    /**
     * Get the value of sedID
     *
     * @return the value of sedID
     */
    public String getSedID() {
        return sedID;
    }

    /**
     * Set the value of sedID
     *
     * @param sedID new value of sedID
     */
    public void setSedID(String sedID) {
        String oldSedID = this.sedID;
        this.sedID = sedID;
        firePropertyChange(PROP_SEDID, oldSedID, sedID);
    }
    private String fileUrl;
    public static final String PROP_FILEURL = "fileUrl";

    /**
     * Get the value of fileUrl
     *
     * @return the value of fileUrl
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * Set the value of fileUrl
     *
     * @param fileUrl new value of fileUrl
     */
    public void setFileUrl(String fileUrl) {
        String oldFileUrl = this.fileUrl;
        this.fileUrl = fileUrl;
        firePropertyChange(PROP_FILEURL, oldFileUrl, fileUrl);
    }
    private String xQuantity;
    public static final String PROP_XQUANTITY = "xQuantity";

    /**
     * Get the value of xAxisQuantity
     *
     * @return the value of xAxisQuantity
     */
    public String getXQuantity() {
        return xQuantity;
    }

    /**
     * Set the value of xAxisQuantity
     *
     * @param xAxisQuantity new value of xAxisQuantity
     */
    public void setXQuantity(String xQuantity) {
        String oldXQuantity = this.xQuantity;
        this.xQuantity = xQuantity;
        firePropertyChange(PROP_XQUANTITY, oldXQuantity, xQuantity);
        List strings = new ArrayList();
        XQuantity q = XQuantity.valueOf(xQuantity);
        for (IUnit unit : q.getPossibleUnits()) {
            strings.add(unit.getString());
        }
        setTheXUnits(strings);
        confBean.setXAxisQuantity(q.name());
    }
    private String yQuantity;
    public static final String PROP_YQUANTITY = "yQuantity";

    /**
     * Get the value of yQuantity
     *
     * @return the value of yQuantity
     */
    public String getYQuantity() {
        return yQuantity;
    }

    /**
     * Set the value of yQuantity
     *
     * @param yQuantity new value of yQuantity
     */
    public void setYQuantity(String yQuantity) {
        String oldYQuantity = this.yQuantity;
        this.yQuantity = yQuantity;
        firePropertyChange(PROP_YQUANTITY, oldYQuantity, yQuantity);
        List strings = new ArrayList();
        SPVYQuantity q = SPVYQuantity.valueOf(yQuantity);
        for (IUnit unit : q.getPossibleUnits()) {
            strings.add(unit.getString());
        }
        setTheYUnits(strings);
        confBean.setYAxisQuantity(q.name());
    }
    private List<String> theXUnits;
    public static final String PROP_THEXUNITS = "theXUnits";

    /**
     * Get the value of theXUnits
     *
     * @return the value of theXUnits
     */
    public List<String> getTheXUnits() {
        return theXUnits;
    }

    /**
     * Set the value of theXUnits
     *
     * @param theXUnits new value of theXUnits
     */
    public void setTheXUnits(List<String> theXUnits) {
        List<String> oldTheXUnits = this.theXUnits;
        this.theXUnits = theXUnits;
        firePropertyChange(PROP_THEXUNITS, oldTheXUnits, theXUnits);
    }
    private List<String> theYUnits;
    public static final String PROP_THEYUNITS = "theYUnits";

    /**
     * Get the value of theYUnits
     *
     * @return the value of theYUnits
     */
    public List<String> getTheYUnits() {
        return theYUnits;
    }

    /**
     * Set the value of theYUnits
     *
     * @param theYUnits new value of theYUnits
     */
    public void setTheYUnits(List<String> theYUnits) {
        List<String> oldTheYUnits = this.theYUnits;
        this.theYUnits = theYUnits;
        firePropertyChange(PROP_THEYUNITS, oldTheYUnits, theYUnits);
    }

    public void setImportButtonLabel(String string) {
        jButton1.setText(string);
    }
    private boolean constructed = false;
    private ISedManager<ExtSed> manager;
    private ExtSed sed;

    public SetupFrame(ISedManager<ExtSed> manager, SetupBean c, ExtSed sed, ISegmentMetadata metadata) throws IOException {
        super("Import Segment");

        this.sed = sed;
        this.manager = manager;

        IValidator val = new AxesValidator(new ErrorValidator(), true, c);

        val.addPropertyHandle(PROP_CONFIGURATIONVALID, this, boolean.class);
        val.addPropertyHandle(PROP_MESSAGE, this, String.class);

        initComponents();

        setMetadata(metadata);

        setConfBean(c);

        setFileUrl(c.getFileLocation());

        if (metadata.getParameters().isEmpty()) {
            jRadioButton4.setEnabled(false);
        }

        constructed = true;

    }

    /** Creates new form NewJInternalFrame*/
    public SetupFrame(ISedManager<ExtSed> manager, SetupBean c, ExtSed sed) throws IOException, FilterException {
        super("Import Segment");
        initComponents();

        this.sed = sed;

        this.setSedID(sed.getId());

        this.manager = manager;

        URL url = new URL(c.getFileLocation());
        IFileFormat format = FileFormatManager.getInstance().getFormatByName(c.getFormatName());

        if (format == null) {
            throw new FilterException("Format not found: " + c.getFormatName());
        }

        List<ISegmentMetadata> md = SegmentImporter.getSegmentsMetadata(url, format);


        if (md.size() > 1 && c.getPositionInFile() == 0) {
            for (int i = 1; i < md.size(); i++) {
                SetupBean sb = new SetupBean();
                sb.setFileLocation(c.getFileLocation());
                sb.setFormatName(c.getFormatName());
                sb.setPositionInFile(i);
                spawn(sb, md.get(i));
            }
        }

        IValidator val = new AxesValidator(new ErrorValidator(), true, c);

        val.addPropertyHandle(PROP_CONFIGURATIONVALID, this, boolean.class);
        val.addPropertyHandle(PROP_MESSAGE, this, String.class);
        setConfBean(c);
        setMetadata(md.get(c.getPositionInFile()));

        setFileUrl(c.getFileLocation());

        if (metadata.getParameters().isEmpty()) {
            jRadioButton4.setEnabled(false);
        }

        jRadioButton3.setVisible(false);
        jRadioButton5.setVisible(false);
        jComboBox2.setVisible(false);
        jComboBox3.setVisible(false);
        jComboBox5.setVisible(false);
        jComboBox6.setVisible(false);

        constructed = true;
    }

    private void spawn(SetupBean c, ISegmentMetadata metadata) throws IOException {
        SetupFrame sf = new SetupFrame(manager, c, sed, metadata);
        SedBuilder.getWorkspace().addFrame(sf);
        sf.setVisible(true);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jTextField1 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox4 = new javax.swing.JComboBox();
        jRadioButton4 = new javax.swing.JRadioButton();
        jComboBox3 = new javax.swing.JComboBox();
        jComboBox6 = new javax.swing.JComboBox();
        jComboBox5 = new javax.swing.JComboBox();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        xAxisCombo6 = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        xAxisCombo7 = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        xAxisCombo8 = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        xAxisCombo = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        xAxisCombo2 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        xAxisCombo1 = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jComboBox7 = new javax.swing.JComboBox();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconifiable(true);
        setResizable(true);
        setTitle("Import Setup Frame");

        jSplitPane1.setBorder(null);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Y Error"));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("ConstantValue");
        jRadioButton1.setName("constantValue"); // NOI18N
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeError(evt);
            }
        });
        jPanel5.add(jRadioButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, -1));

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("AsymmetricColumn");
        jRadioButton3.setEnabled(false);
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeError(evt);
            }
        });
        jPanel5.add(jRadioButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, -1, -1));

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("SymmetricColumn");
        jRadioButton2.setName("symmetricColumn"); // NOI18N
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeError(evt);
            }
        });
        jPanel5.add(jRadioButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, -1, -1));

        jTextField1.setName("constantValueValue"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${confBean.constantErrorValue}"), jTextField1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jTextField1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel5.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 80, 176, -1));

        jComboBox1.setName("symmetricColumnValue"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${metadata.columns}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jComboBox1);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${confBean.symmetricErrorColumnNumber}"), jComboBox1, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setConverter(new ColumnConverter());
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jComboBox1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel5.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 120, 310, -1));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${metadata.columns}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jComboBox2);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${confBean.upperErrorColumnNumber}"), jComboBox2, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setConverter(new ColumnConverter());
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton3, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jComboBox2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel5.add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 190, 310, -1));

        jComboBox4.setName("symmetricParameterValue"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${metadata.parameters}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jComboBox4);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${confBean.symmetricErrorParameter}"), jComboBox4, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setConverter(new ParameterConverter());
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton4, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jComboBox4, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel5.add(jComboBox4, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 160, 310, -1));

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setText("SymmetricParameter");
        jRadioButton4.setName("symmetricParameter"); // NOI18N
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeError(evt);
            }
        });
        jPanel5.add(jRadioButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, -1, -1));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${metadata.columns}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jComboBox3);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${confBean.lowerErrorColumnNumber}"), jComboBox3, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setConverter(new ColumnConverter());
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton3, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jComboBox3, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel5.add(jComboBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 220, 310, -1));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${metadata.parameters}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jComboBox6);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${confBean.upperErrorParameter}"), jComboBox6, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setConverter(new ParameterConverter());
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton5, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jComboBox6, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel5.add(jComboBox6, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 260, 310, -1));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${metadata.parameters}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jComboBox5);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${confBean.lowerErrorParameter}"), jComboBox5, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setConverter(new ParameterConverter());
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton5, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jComboBox5, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel5.add(jComboBox5, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 290, 310, -1));

        buttonGroup1.add(jRadioButton5);
        jRadioButton5.setText("AsymmetricParameter");
        jRadioButton5.setEnabled(false);
        jRadioButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeError(evt);
            }
        });
        jPanel5.add(jRadioButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, -1, -1));

        buttonGroup1.add(jRadioButton6);
        jRadioButton6.setText("Unknown");
        jRadioButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeError(evt);
            }
        });
        jPanel5.add(jRadioButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Y Axis"));

        jLabel8.setText("Units");

        xAxisCombo6.setName("yColumn"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${metadata.columns}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, xAxisCombo6);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${confBean.YAxisColumnNumber}"), xAxisCombo6, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setConverter(new ColumnConverter());
        bindingGroup.addBinding(binding);

        jLabel9.setText("Column");

        xAxisCombo7.setName("YUnits"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${theYUnits}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, xAxisCombo7);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${confBean.YAxisUnit}"), xAxisCombo7, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel10.setText("Quantity");

        xAxisCombo8.setModel(new DefaultComboBoxModel(loadEnum(SPVYQuantity.class)));
        xAxisCombo8.setName("yQuantity"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${YQuantity}"), xAxisCombo8, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel9)
                    .add(xAxisCombo6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 168, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(xAxisCombo8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jLabel8)
                        .add(151, 151, 151))
                    .add(xAxisCombo7, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(jLabel10)
                    .add(jLabel8))
                .add(4, 4, 4)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(xAxisCombo6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(xAxisCombo8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(xAxisCombo7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "X Axis"));
        jPanel3.setMaximumSize(new java.awt.Dimension(200, 200));

        jLabel4.setText("Units");

        xAxisCombo.setName("xColumn"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${metadata.columns}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, xAxisCombo);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${confBean.XAxisColumnNumber}"), xAxisCombo, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setConverter(new ColumnConverter());
        bindingGroup.addBinding(binding);

        jLabel3.setText("Column");

        xAxisCombo2.setName("xUnits"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${theXUnits}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, xAxisCombo2);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${confBean.XAxisUnit}"), xAxisCombo2, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel2.setText("Quantity");

        xAxisCombo1.setModel(new DefaultComboBoxModel(loadEnum(XQuantity.class)));
        xAxisCombo1.setName("xQuantity"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${XQuantity}"), xAxisCombo1, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3)
                    .add(xAxisCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 166, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(xAxisCombo1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel4)
                    .add(xAxisCombo2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 186, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(70, 70, 70))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jLabel2)
                    .add(jLabel4))
                .add(4, 4, 4)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(xAxisCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(xAxisCombo1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(xAxisCombo2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3, 0, 564, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 373, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel1);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "General"));

        jLabel5.setText("SED ID:");

        jTextField2.setEditable(false);
        jTextField2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField2.setName("sedId"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sedID}"), jTextField2, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel12.setText("File:");

        jTextField3.setEditable(false);
        jTextField3.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextField3.setName("segmentFile"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${fileUrl}"), jTextField3, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jTextField8.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${confBean.positionInFile}"), jTextField8, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel1.setText("Table Position in File:");

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel12)
                            .add(jLabel5))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jTextField3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                            .add(jTextField2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(8, 8, 8)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(jTextField3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .add(12, 12, 12))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Target"));

        jLabel13.setText("Name:");

        jLabel14.setText("RA:");

        jTextField4.setName("targetName"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${targetName}"), jTextField4, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jTextField5.setName("targetRa"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${ra}"), jTextField5, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel15.setText("DEC:");

        jTextField6.setName("targetDec"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${dec}"), jTextField6, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(SetupFrame.class, this);
        jButton4.setAction(actionMap.get("resolve")); // NOI18N

        jLabel16.setText("Publisher:");

        jTextField7.setName("publisherText"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${confBean.publisher}"), jTextField7, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jComboBox7.setModel(new DefaultComboBoxModel(resolver.getCatalogs().toArray(new Catalog[resolver.getCatalogs().size()])));

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                        .add(jLabel16)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 189, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(jLabel13)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(jLabel14)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel15)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                        .add(jComboBox7, 0, 163, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton4)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBox7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel13)
                    .add(jTextField4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel14)
                    .add(jTextField5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextField6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel15))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel16)
                    .add(jTextField7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Setup Help"));

        jScrollPane1.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setForeground(new java.awt.Color(255, 102, 0));
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setName("validation"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${message}"), jTextArea1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTextArea1);

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
        );

        jButton2.setText("Save Setup");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${configurationValid}"), jButton2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save(evt);
            }
        });

        jButton1.setText("Add Segment to SED");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${configurationValid}"), jButton1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importSegment(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jButton2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 193, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(9, 9, 9))
                    .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(20, 20, 20))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton2)
                    .add(jButton1))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel2);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void importSegment(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importSegment
        try {
            Segment segment = SegmentImporter.getSegments(confBean).get(0);
            int i = sed.indexOf(generated);
            sed.remove(generated);
            sed.addSegment(segment, i >= 0 ? i : sed.getNumberOfSegments());
            this.generated = segment;
            Map<Segment, SegmentFrame> attach = (Map<Segment, SegmentFrame>) manager.getAttachment(sed.getId(), "builder:configuration");
            if (attach == null) {
                attach = new HashMap();
                sed.addAttachment("builder:configuration", attach);
            }
            attach.put(segment, this);
            this.setVisible(false);
            SedBuilder.update();
        } catch (Exception ex) {
            NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(), "Error adding the segment: " + ex.getMessage(), "Error", NarrowOptionPane.ERROR_MESSAGE);
            Logger.getLogger(SetupFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_importSegment

    private void save(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save
        SaveSetupDialog dia = new SaveSetupDialog(SedBuilder.getWorkspace().getRootFrame(), confBean);
        dia.setVisible(true);
}//GEN-LAST:event_save

    private void changeError(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeError
        confBean.setErrorType(evt.getActionCommand());
        setConfBean(confBean);
}//GEN-LAST:event_changeError

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JComboBox jComboBox7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JComboBox xAxisCombo;
    private javax.swing.JComboBox xAxisCombo1;
    private javax.swing.JComboBox xAxisCombo2;
    private javax.swing.JComboBox xAxisCombo6;
    private javax.swing.JComboBox xAxisCombo7;
    private javax.swing.JComboBox xAxisCombo8;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

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
            Logger.getLogger(SetupFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private class ColumnConverter extends org.jdesktop.beansbinding.Converter {

        @Override
        public Object convertReverse(Object value) {
            ISegmentColumn col = (ISegmentColumn) value;
            if (Number.class.isAssignableFrom(col.getContentClass()) || col.getContentClass().isArray()) {
                return col.getNumber();
            } else {
                if (constructed) {
                    NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                            "The selected column contains strings. "
                            + "You can't import string columns in a segment. "
                            + "Please correct the problem by selecting a different, numeric column.",
                            "Non numeric column selected",
                            NarrowOptionPane.WARNING_MESSAGE);
                }
                return -1;
            }
        }

        @Override
        public Object convertForward(Object value) {
            return value;
        }
    }

    private class ParameterConverter extends org.jdesktop.beansbinding.Converter {

        @Override
        public Object convertReverse(Object value) {
            ISegmentParameter param = (ISegmentParameter) value;
            if (Number.class.isAssignableFrom(param.getContentClass())) {
                return param.getName();
            } else {
                if (constructed) {
                    NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                            "The selected parameter contains a string. "
                            + "You can't import string parameters in a segment. "
                            + "Please correct the problem by selecting a different, numeric parameter.",
                            "Non numeric parameter selected",
                            NarrowOptionPane.WARNING_MESSAGE);
                }
                return "INVALID";
            }

        }

        @Override
        public Object convertForward(Object value) {
            return value;
        }
    }

    @Action
    public Task resolve() {
        return new ResolveTask(org.jdesktop.application.Application.getInstance());
    }

    private class ResolveTask extends org.jdesktop.application.Task<Object, Void> {
        private Catalog cat;

        ResolveTask(org.jdesktop.application.Application app) {
            super(app);
            cat = (Catalog) jComboBox7.getSelectedItem();
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


    private Segment generated;

    @Override
    public void update(Segment segment) {
        if(segment.createTarget().isSetName())
            setTargetName(segment.getTarget().getName().getValue());
        if(segment.getTarget().isSetPos()) {
            setRa(segment.getTarget().getPos().getValue()[0].getValue());
            setDec(segment.getTarget().getPos().getValue()[1].getValue());
        }
    }

    
}
