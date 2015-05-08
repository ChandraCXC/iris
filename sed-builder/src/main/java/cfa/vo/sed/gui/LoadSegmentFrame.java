/**
 * Copyright (C) 2012, 2015 Smithsonian Astrophysical Observatory
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
 * LoadSegmentFrame.java
 *
 * Created on May 14, 2011, 5:23:17 AM
 */
package cfa.vo.sed.gui;

import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.ISedManager;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.utils.SpaceTrimmer;
import cfa.vo.sed.builder.AsciiConf;
import cfa.vo.sed.builder.NEDImporter;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sed.builder.SegmentImporter;
import cfa.vo.sed.builder.SegmentImporterException;
import cfa.vo.sed.filters.AbstractSingleStarTableFilter;
import cfa.vo.sed.filters.FileFormatManager;
import cfa.vo.sed.filters.FilterException;
import cfa.vo.sed.filters.IFileFormat;
import cfa.vo.sed.filters.IFilter;
import cfa.vo.sed.filters.NativeFileFormat;
import cfa.vo.sed.setup.PhotometryCatalogBuilder;
import cfa.vo.sed.setup.SetupBean;
import cfa.vo.sedlib.DoubleParam;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sedlib.common.ValidationError;
import cfa.vo.sedlib.common.ValidationErrorEnum;
import cfa.vo.sedlib.io.SedFormat;
import java.awt.Component;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;

/**
 *
 * @author olaurino
 */
public final class LoadSegmentFrame extends JInternalFrame {

    private ISedManager<ExtSed> manager;
    private ExtSed sed;

    public void setSed(ExtSed sed) {
        this.sed = sed;
        if (sed != null) {
            sedId.setText(sed.getId());
        }
    }
    private String nedEndpoint = NEDImporter.NED_DATA_DEFAULT_ENDPOINT;
    public static final String PROP_NEDENDPOINT = "nedEndpoint";

    /**
     * Get the value of nedEndpoint
     *
     * @return the value of nedEndpoint
     */
    public String getNedEndpoint() {
        return nedEndpoint;
    }

    /**
     * Set the value of nedEndpoint
     *
     * @param nedEndpoint new value of nedEndpoint
     */
    public void setNedEndpoint(String nedEndpoint) {
        String oldNedEndpoint = this.nedEndpoint;
        this.nedEndpoint = nedEndpoint;
        firePropertyChange(PROP_NEDENDPOINT, oldNedEndpoint, nedEndpoint);
    }
    private String urlText = "";
    public static final String PROP_URLTEXT = "urlText";

    /**
     * Get the value of urlText
     *
     * @return the value of urlText
     */
    public String getUrlText() {
        return urlText;
    }

    /**
     * Set the value of urlText
     *
     * @param urlText new value of urlText
     */
    public void setUrlText(String urlText) {
        urlText = SpaceTrimmer.sideTrim(urlText);
        String oldUrlText = this.urlText;
        this.urlText = urlText;
        firePropertyChange(PROP_URLTEXT, oldUrlText, urlText);
        setUrlS(urlText);
    }
    private boolean urlSelected = false;
    public static final String PROP_URLSELECTED = "urlSelected";

    /**
     * Get the value of urlSelected
     *
     * @return the value of urlSelected
     */
    public boolean isUrlSelected() {
        return urlSelected;
    }

    /**
     * Set the value of urlSelected
     *
     * @param urlSelected new value of urlSelected
     */
    public void setUrlSelected(boolean urlSelected) {
        boolean oldUrlSelected = this.urlSelected;
        this.urlSelected = urlSelected;
        firePropertyChange(PROP_URLSELECTED, oldUrlSelected, urlSelected);
        setUrlS(urlText);
    }
    private boolean localSelected = true;
    public static final String PROP_LOCALSELECTED = "localSelected";

    /**
     * Get the value of localSelected
     *
     * @return the value of localSelected
     */
    public boolean isLocalSelected() {
        return localSelected;
    }

    /**
     * Set the value of localSelected
     *
     * @param localSelected new value of localSelected
     */
    public void setLocalSelected(boolean localSelected) {
        boolean oldLocalSelected = this.localSelected;
        this.localSelected = localSelected;
        firePropertyChange(PROP_LOCALSELECTED, oldLocalSelected, localSelected);
        if (localSelected) {
            setUrlS("file://" + new File(diskLocation).getAbsolutePath());
        }
    }
    private boolean nedVisible = false;
    public static final String PROP_NEDVISIBLE = "nedVisible";

    /**
     * Get the value of nedVisible
     *
     * @return the value of nedVisible
     */
    public boolean isNedVisible() {
        return nedVisible;
    }

    /**
     * Set the value of nedVisible
     *
     * @param nedVisible new value of nedVisible
     */
    public void setNedVisible(boolean nedVisible) {
        boolean oldNedVisible = this.nedVisible;
        this.nedVisible = nedVisible;
        firePropertyChange(PROP_NEDVISIBLE, oldNedVisible, nedVisible);
        setIsLoadable(!nedVisible);
    }
    private List<Segment> segList = new ArrayList();

    public boolean hasSegments() {
        return !segList.isEmpty();
    }

    public List<Segment> getSegments() {
        return segList;
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
    }
    private String ra;

    public void setRa(String ra) {
        this.ra = ra;
    }
    private String dec;

    public void setDec(String dec) {
        this.dec = dec;
    }
    private boolean isLoadable = false;
    public static final String PROP_ISLOADABLE = "isLoadable";

    /**
     * Get the value of isLoadable
     *
     * @return the value of isLoadable
     */
    public boolean isIsLoadable() {
        return isLoadable;
    }

    /**
     * Set the value of isLoadable
     *
     * @param isLoadable new value of isLoadable
     */
    public void setIsLoadable(boolean isLoadable) {
        boolean oldIsLoadable = this.isLoadable;
        this.isLoadable = isLoadable;
        firePropertyChange(PROP_ISLOADABLE, oldIsLoadable, isLoadable);
    }
    public static final String PROP_FORMATS = "formats";
    private String diskLocation = "";
    public static final String PROP_DISKLOCATION = "diskLocation";

    /**
     * Get the value of location
     *
     * @return the value of location
     */
    public String getDiskLocation() {
        return diskLocation;
    }

    /**
     * Set the value of location
     *
     * @param location new value of location
     */
    public void setDiskLocation(String location) {
        location = SpaceTrimmer.sideTrim(location);
        String oldLocation = this.diskLocation;
        this.diskLocation = location;
        firePropertyChange(PROP_DISKLOCATION, oldLocation, location);
        setUrlS("file://" + new File(location).getAbsolutePath());
    }
    public static final String PROP_URLS = "urlS";
    private String urlS;

    /**
     * Get the value of urlS
     *
     * @return the value of urlS
     */
    public String getUrlS() {
        return urlS;
    }

    /**
     * Set the value of urlS
     *
     * @param urlS new value of urlS
     */
    public void setUrlS(String urlS) {
        String oldUrlS = this.urlS;
        this.urlS = urlS;
        firePropertyChange(PROP_URLS, oldUrlS, urlS);
        setIsLoadable(isSetURL());
    }

    public URL getURL() throws MalformedURLException {
        return new URL(urlS);
    }

    public boolean isSetURL() {
        if (localSelected) {
            return new File(diskLocation).isFile();
        }
        if (urlSelected) {
            try {
                return !getURL().getHost().isEmpty() && !getURL().getPath().isEmpty() && getURL().getProtocol().matches("http|ftp");
            } catch (MalformedURLException ex) {
                return false;
            }
        }
        return false;

    }
    private IFileFormat format = NativeFileFormat.VOTABLE;
    public static final String PROP_FORMAT = "format";

    /**
     * Get the value of format
     *
     * @return the value of format
     */
    public IFileFormat getFormat() {
        return format;
    }

    /**
     * Set the value of format
     *
     * @param format new value of format
     */
    public void setFormat(IFileFormat format) {
        IFileFormat oldFormat = this.format;
        this.format = format;
        firePropertyChange(PROP_FORMAT, oldFormat, format);
    }

    /** Creates new form LoadSegmentFrame */
    public LoadSegmentFrame(ISedManager<ExtSed> manager) {
        initComponents();
        this.manager = manager;
        this.sed = manager.getSelected();
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
        jTextField2 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jButton2 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jTextField3 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        progressBar = new javax.swing.JProgressBar();
        cancelButton = new javax.swing.JButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jButton4 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        sedId = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jButton5 = new javax.swing.JButton();
        helpLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Load an input File");
        setName("diskTextBox"); // NOI18N

        jTextField2.setName("diskTextBox"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${diskLocation}"), jTextField2, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jTextField2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jComboBox1.setModel(new DefaultComboBoxModel(FileFormatManager.getInstance().getFormatsArray()));
        jComboBox1.setName("fileFormat"); // NOI18N
        jComboBox1.setRenderer(new FormatRenderer());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${format}"), jComboBox1, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel1.setText("File Format:");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("URL:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${urlSelected}"), jRadioButton1, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jButton1.setText("Browse...");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jButton1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browse(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Location on Disk:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${localSelected}"), jRadioButton2, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(LoadSegmentFrame.class, this);
        jButton2.setAction(actionMap.get("loadSegment")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${isLoadable}"), jButton2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jTextField1.setName("urlTextBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${urlText}"), jTextField1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jTextField1, org.jdesktop.beansbinding.BeanProperty.create("editable"));
        bindingGroup.addBinding(binding);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "NED Service"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${targetName}"), jTextField3, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${nedVisible}"), jTextField3, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel2.setText("Target Name:");

        jButton3.setAction(actionMap.get("importNed")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${nedVisible}"), jButton3, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jCheckBox1.setText("Change Endpoint");

        jLabel3.setText("Endpoint: ");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${nedEndpoint}"), jTextField4, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jTextField4, org.jdesktop.beansbinding.BeanProperty.create("editable"));
        bindingGroup.addBinding(binding);

        progressBar.setEnabled(false);
        progressBar.setString("");
        progressBar.setStringPainted(true);

        cancelButton.setAction(actionMap.get("cancel")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelButton))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton3)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jCheckBox1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 306, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .add(18, 18, 18)
                .add(jButton3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCheckBox1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jTextField4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 32, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, cancelButton)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(8, 8, 8))))
        );

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("Get an SED from the NED Service");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${nedVisible}"), jRadioButton3, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jButton4.setAction(actionMap.get("close")); // NOI18N

        jLabel4.setText("Currently selected SED: ");

        sedId.setEditable(false);

        jButton5.setAction(actionMap.get("loadCatalog")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jButton2, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jButton5, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        helpLabel.setText("Help");
        helpLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        helpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showHelp(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel5.setText("<html>While most Iris tools support data of any length, visualization tools will be slow for SEDs and spectra with more than 2500 points.</html>");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 127, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(sedId, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))
                    .add(jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton1)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jRadioButton2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(jRadioButton1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(helpLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton2))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton4)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jRadioButton3)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(sedId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButton2)
                    .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jRadioButton1))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton2)
                    .add(jButton5)
                    .add(helpLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jRadioButton3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton4)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void browse(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browse
        JFileChooser jfc = SedBuilder.getWorkspace().getFileChooser();
        File l = new File(diskLocation);
        if (l.isDirectory()) {
            jfc.setCurrentDirectory(l);
        }
        jfc.setApproveButtonText("Select");
        int returnval = jfc.showOpenDialog(SedBuilder.getWorkspace().getRootFrame());
        if (returnval == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            setDiskLocation(f.getAbsolutePath());
        }
    }//GEN-LAST:event_browse

    private void showHelp(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showHelp
        StringBuilder b = new StringBuilder();

        b.append("<html><p>A Spectrum/SED is a file in which there is, at least, a column for the spectral coordinate<br/>(energy, wavelength, frequency)");
        b.append("<br/> and a column for the flux coordinate (e.g. flux, flux density, magnitude). ");
        b.append("<br/>Also, a Spectrum/SED refers to a single astronomical source (target).</p><br/>");
        b.append("<p>A Photometry Catalog is a file in which each row refers to a different astronomical source; ");
        b.append("<br/>each row can contain information about an arbitrary number of photometry points. ");
        b.append("<br/>Spectral coordinate can be expressed by a column, by a photometry filter ");
        b.append("<br/>or by a spectral range (e.g. Passband).</p>");

        NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(), b);
    }//GEN-LAST:event_showHelp

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel helpLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField sedId;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    public void reset() {
        segList = new ArrayList();
        jComboBox1.setModel(new DefaultComboBoxModel(FileFormatManager.getInstance().getFormatsArray()));
    }
    
    public void showNed() {
        super.show();
        jRadioButton3.setSelected(true);
        GUIUtils.moveToFront(this);
    }

    private void spectralWarningCheck(Segment seg) throws SedInconsistentException, SedNoDataException {
	int numOfPoints = spectraWarning(seg);
	if(numOfPoints < 2500 && numOfPoints > 1000) {
	    sed.addSegment(seg);
	    NarrowOptionPane.showMessageDialog(this, 
		    "Over 1000 data points. Viewer may be slightly slower than usual", 
		    "", 
		    NarrowOptionPane.INFORMATION_MESSAGE);
	}
	else if(numOfPoints > 2500) {
	    int answer = NarrowOptionPane.showConfirmDialog(this, 
		    "Over 2500 data points. Viewer may be slow. Do you want to continue import?", 
		    "", 
		    NarrowOptionPane.YES_NO_OPTION);
	    if (answer == JOptionPane.NO_OPTION) {
		return;
	    } else {
		sed.addSegment(seg);
	    }
	}
	else {
	    sed.addSegment(seg);
	}
    }

    private class FormatRenderer extends BasicComboBoxRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {


            IFileFormat format = (IFileFormat) value;

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
                if (-1 < index) {
                    try {
                        list.setToolTipText(format.getFilter(null).getDescription());
                    } catch (FilterException ex) {
                        Logger.getLogger(LoadSegmentFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setFont(list.getFont());
            setText((value == null) ? "" : format.getName());

            return this;

        }
    }

    @Action
    public void close() {
        this.setVisible(false);
    }

    @Action
    public void loadSegment() throws MalformedURLException {
        segList = new ArrayList();
        List<Integer> unsuc = readCompliant();

        try {
	    
	    /* Spectra warning. If there are 1000 < points < 2500, just warn user that
	    * visualizer may be slow.
	    * If there are over 2500 points, give user decision to add to Iris or not.
	    */
	    int numOfPoints = format.getFilter(getURL()).getColumnData(0,0).length;
	    if(numOfPoints < 2500 && numOfPoints > 1000) {
		NarrowOptionPane.showMessageDialog(this, 
			"There are over 1000 data points in this file.\n"+
				"Visualization tools may be slightly slower than usual for this SED.", 
			"Large File Detected", 
			NarrowOptionPane.INFORMATION_MESSAGE);
	    }
	    else if(numOfPoints > 2500) {
		int answer = NarrowOptionPane.showOptionDialog(this, 
			"The number of data points exceeds the limit supported by Iris visualization tools (number of points detected: "+String.valueOf(numOfPoints)+").\n"+
			//"There are over 2500 points in this file (number detected: "+String.valueOf(numOfPoints)+").\n"+
				//"Iris visualization tools do not support spectra, meaning"+
				"Visualization tools will be slow for this SED.\n\n"+
				"Do you want to continue import?", 
			"Large Segment Detected", 
			NarrowOptionPane.YES_NO_OPTION, 
			NarrowOptionPane.WARNING_MESSAGE, null, new String[]{"Yes", "No"}, "No");
		if (answer == JOptionPane.NO_OPTION) {
		    return;
		}
//			    } else if (numOfPoints > 7500) {
//				int answer = NarrowOptionPane.showConfirmDialog(this, 
//					"There are over 7500 data points in this file. The Viewer and Fitting Tool WILL be slow for this SED! \nDo you want to continue import?", 
//					"Large File Detected", 
//					NarrowOptionPane.YES_NO_OPTION);
//				if (answer == JOptionPane.YES_OPTION) {
//				}
	    }
	    
            if (unsuc != null) {
                if (!segList.isEmpty()) {
//                    for (Segment seg : segList) {
                        try {
                            sed.addSegment(segList);
                        } catch (SedInconsistentException ex) {
                            NarrowOptionPane.showMessageDialog(this, "The segment was found physically inconsistent with the rest of the SED", "Segment could not be imported", NarrowOptionPane.OK_OPTION);
                        } catch (SedNoDataException ex) {
                            NarrowOptionPane.showMessageDialog(this, "The segment contains no data", "Segment could not be imported", NarrowOptionPane.OK_OPTION);
                        }
//                    }
                } else {
                    SetupBean conf = new SetupBean();
                    conf.setFileLocation(getURL().toString());
                    conf.setFormatName(format.getName());
                    SetupFrame sf = new SetupFrame(manager, conf, sed);
                    sf.setTargetName(targetName);
                    sf.setRa(ra);
                    sf.setDec(dec);
                    SedBuilder.getWorkspace().addFrame(sf);
                    sf.setVisible(true);
                }

                for (Integer i : unsuc) {
                    SetupBean conf = new SetupBean();
                    conf.setFileLocation(getURL().toString());
                    conf.setPositionInFile(i);
                    conf.setFormatName(format.getName());
                    SetupFrame sf = new SetupFrame(manager, conf, sed);
                    sf.setTargetName(targetName);
                    sf.setRa(ra);
                    sf.setDec(dec);
                    SedBuilder.getWorkspace().addFrame(sf);
                    sf.setVisible(true);
                }

            } else {
                // if the format is an ASCII-IRIS table, read it in
                try {
                    SetupBean conf = new AsciiConf().makeConf(getURL());
                    Segment seg = SegmentImporter.getSegments(conf).get(0);
		    
                    sed.addSegment(seg);
                } catch (Exception ex) {
                    SetupBean conf = new SetupBean();
                    conf.setFileLocation(getURL().toString());
                    conf.setFormatName(format.getName());
                    SetupFrame sf = new SetupFrame(manager, conf, sed);
                    sf.setTargetName(targetName);
                    sf.setRa(ra);
                    sf.setDec(dec);
                    SedBuilder.getWorkspace().addFrame(sf);
                    sf.setVisible(true);
                }
            }

            setVisible(false);

        } catch (Exception ex) {
            Logger.getLogger(LoadSegmentFrame.class.getName()).log(Level.SEVERE, "", ex);
            NarrowOptionPane.showMessageDialog(this, "An error occurred. Please check the file", "Error", NarrowOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshFormats() {
        jComboBox1.setModel(new DefaultComboBoxModel(FileFormatManager.getInstance().getFormatsArray()));
    }

    private List<Integer> readCompliant() {
        if (format.equals(NativeFileFormat.FITS) || format.equals(NativeFileFormat.VOTABLE)) {
            try {
                SedFormat f = format.equals(NativeFileFormat.FITS) ? SedFormat.FITS : SedFormat.VOT;
                Sed s = Sed.read(getURL().openStream(), f);

                List<Integer> unsuccessful = new ArrayList();

                if(s.getNumberOfSegments()==0)
                    for(int i = 0; i < format.getFilter(getURL()).getMetadata().size(); i++)
                        unsuccessful.add(i);

                for (int i = 0; i < s.getNumberOfSegments(); i++) {
                    Segment seg = s.getSegment(i);

                    List<ValidationError> errList = new ArrayList();
                    seg.validate(errList);

                    if (seg.createTarget().getPos() == null) {
                        if (seg.createChar().createSpatialAxis().createCoverage().getLocation() != null) {
                            seg.createTarget().createPos().setValue(seg.getChar().getSpatialAxis().getCoverage().getLocation().getValue());
                        } else {
                            seg.createTarget().createPos().setValue(new DoubleParam[]{new DoubleParam(Double.NaN), new DoubleParam(Double.NaN)});
                        }
                    }

                    try {
                        Double ra = (Double) seg.getTarget().getPos().getValue()[0].getCastValue();
                        Double dec = (Double) seg.getTarget().getPos().getValue()[1].getCastValue();
                        if(ra==null || dec==null)
                            throw new Exception();
                    } catch(Exception ex) {
                        seg.createTarget().createPos().setValue(new DoubleParam[]{new DoubleParam(Double.NaN), new DoubleParam(Double.NaN)});
                    }
                    
                    try {
                        Double ra = (Double) seg.getChar().getSpatialAxis().getCoverage().getLocation().getValue()[0].getCastValue();
                        Double dec = (Double) seg.getChar().getSpatialAxis().getCoverage().getLocation().getValue()[1].getCastValue();
                        if(ra==null || dec==null)
                            throw new Exception();
                    } catch(Exception ex) {
                        seg.createChar().createSpatialAxis().createCoverage().createLocation().setValue(seg.getTarget().getPos().getValue());
                    }

                    if (errList.isEmpty()) {
                        segList.add(seg);
                    } else {
                        boolean succ = true;
                        for (ValidationError err : errList) {
                            ValidationErrorEnum en = err.getError();
                            if (en.equals(ValidationErrorEnum.MISSING_DATA_FLUXAXIS_VALUE)
                                    || en.equals(ValidationErrorEnum.MISSING_DATA_SPECTRALAXIS_VALUE)) {
                                succ = false;
                            }
                        }
                        if (succ) {
                            segList.add(seg);
                        } else {
                            unsuccessful.add(i);
                        }
                    }
                }

                return unsuccessful;

            } catch (Exception ex) {
                return null;
            }
        }
        return null;
    }
    private Task task;

    @Action(block = Task.BlockingScope.ACTION)
    public Task importNed() {
        Task t = new ImportNedTask(org.jdesktop.application.Application.getInstance());
        this.task = t;
        return t;
    }

    @Action
    public void cancel() {
        if (task != null && !task.isDone()) {
            task.cancel(true);
        }
    }

    private class ImportNedTask extends org.jdesktop.application.Task<Object, Void> {

        private int resultCode = 0;
        private Sed s;
        private Exception ex;

        ImportNedTask(org.jdesktop.application.Application app) {
            super(app);
            progressBar.setEnabled(true);
            progressBar.setIndeterminate(true);
            progressBar.setString("Fetching SED from NED");
            progressBar.setStringPainted(true);
            cancelButton.setEnabled(true);
        }

        @Override
        protected void cancelled() {
            super.cancelled();
            progressBar.setEnabled(false);
            progressBar.setIndeterminate(false);
            progressBar.setString(null);
            progressBar.setStringPainted(false);
            cancelButton.setEnabled(false);
        }

        @Override
        protected Object doInBackground() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                resultCode = 2;
                return null;
            }

            if (targetName.isEmpty()) {
                resultCode = 1;

            } else {
                try {
                    s = NEDImporter.getSedFromName(targetName, nedEndpoint);
//                Sed sed = NEDImporter.getError();

                    if (s.getNumberOfSegments() == 0) {
                        resultCode = 2;
                    } else {
                        resultCode = 0;
                    }
                } catch (SegmentImporterException e) {
                    resultCode = 3;
                    this.ex = e;
                }
            }
            return null;
        }

        @Override
        protected void succeeded(Object result) {
            progressBar.setEnabled(false);
            progressBar.setIndeterminate(false);
            progressBar.setString("");
            cancelButton.setEnabled(false);
            switch (resultCode) {
                case 0:
                    for (int i = 0; i < s.getNumberOfSegments(); i++) {
                        Segment segment = s.getSegment(i);
                        try {
                            sed.addSegment(segment);
                        } catch (SedInconsistentException e) {
                            NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                                    "This segment is physically inconsistent with the rest of the SED",
                                    "Error",
                                    NarrowOptionPane.ERROR_MESSAGE);
                        } catch (SedNoDataException e) {
                            NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                                    "Empty SED",
                                    "Error",
                                    NarrowOptionPane.ERROR_MESSAGE);
                        }
                    }
                    NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                            "Segment added to SED: " + sed.getId(),
                            "Success",
                            NarrowOptionPane.INFORMATION_MESSAGE);
                    break;
                case 1:
                    NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                            "The target name is empty",
                            "Warning",
                            NarrowOptionPane.WARNING_MESSAGE);
                    break;
                case 2:
                    NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                            "No Data",
                            "Warning",
                            NarrowOptionPane.WARNING_MESSAGE);
                    break;
                case 3:
                    NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                            ex.getMessage(),
                            "Error",
                            NarrowOptionPane.ERROR_MESSAGE);
                    break;
            }

            LoadSegmentFrame.this.setVisible(false);

        }
    }

    @Action
    public void loadCatalog() {
        segList = new ArrayList();
        List<Integer> unsuc = readCompliant();
	
        try {
	    
	    /* Spectra warning. If there are 1000 < points < 2500, just warn user that
	    * visualizer may be slow.
	    * If there are over 2500 points, give user decision to add to Iris or not.
	    */
	    int numOfPoints = format.getFilter(getURL()).getColumnData(0,0).length;
	    if(numOfPoints < 2500 && numOfPoints > 1000) {
		NarrowOptionPane.showMessageDialog(this, 
			"There are over 1000 data points in this file.\n"+
				"Visualization tools may be slightly slower than usual for this SED.", 
			"Large File Detected", 
			NarrowOptionPane.INFORMATION_MESSAGE);
	    } else if(numOfPoints > 2500) {
		int answer = NarrowOptionPane.showOptionDialog(this, 
			"The number of data points exceeds the limit supported by Iris visualization tools (number of points detected: "+String.valueOf(numOfPoints)+").\n"+
			//"There are over 2500 points in this file (number detected: "+String.valueOf(numOfPoints)+").\n"+
				//"Iris visualization tools do not support spectra, meaning"+
				"Visualization tools will be slow for this SED.\n\n"+
				"Do you want to continue import?",
			"Large Segment Detected", 
			NarrowOptionPane.YES_NO_OPTION, 
			NarrowOptionPane.WARNING_MESSAGE, null, new String[]{"Yes", "No"}, "No");
		if (answer == JOptionPane.NO_OPTION) {
		    return;
		}
	    }
	    
            if (unsuc != null) {
                if (!segList.isEmpty()) {		    
                    for (Segment seg : segList) {
                        try {
                            sed.addSegment(seg);
                        } catch (SedInconsistentException ex) {
                            NarrowOptionPane.showMessageDialog(this, "The segment was found phisically inconsistent with the rest of the SED", "Segment could not be imported", NarrowOptionPane.OK_OPTION);
                        } catch (SedNoDataException ex) {
                            NarrowOptionPane.showMessageDialog(this, "The segment contains no data", "Segment could not be imported", NarrowOptionPane.OK_OPTION);
                        }
                    }
                } 

//                    else {
//                    IFilter filter = format.getFilter(getURL());
//
//                    if (!(filter instanceof AbstractSingleStarTableFilter)) {
//                        throw new Exception("Plugins are not supported yet for Photometry Catalogs. Only native file formats are supported");
//                    }
//
//                    PhotometryCatalogBuilder conf = new PhotometryCatalogBuilder((AbstractSingleStarTableFilter) filter, sed, 0);
//                    PhotometryCatalogFrame frame = new PhotometryCatalogFrame(conf);
//
//                    SedBuilder.getWorkspace().addFrame(frame);
//                    frame.setVisible(true);
//                }

                if (!unsuc.isEmpty()) {

                    for (Integer i : unsuc) {
                        IFilter filter = format.getFilter(getURL());

                        if (!(filter instanceof AbstractSingleStarTableFilter)) {
                            throw new Exception("Plugins are not supported yet for Photometry Catalogs. Only native file formats are supported");
                        }
			
                        PhotometryCatalogBuilder conf = new PhotometryCatalogBuilder((AbstractSingleStarTableFilter) filter, sed, i);
                        PhotometryCatalogFrame frame = new PhotometryCatalogFrame(conf);

                        SedBuilder.getWorkspace().addFrame(frame);
                        frame.setVisible(true);
                    }

                }

            } else {
                IFilter filter = format.getFilter(getURL());

                if (!(filter instanceof AbstractSingleStarTableFilter)) {
                    throw new Exception("Plugins are not supported yet for Photometry Catalogs. Only native file formats are supported");
                }
		
                PhotometryCatalogBuilder conf = new PhotometryCatalogBuilder((AbstractSingleStarTableFilter) filter, sed, 0);
                PhotometryCatalogFrame frame = new PhotometryCatalogFrame(conf);

                SedBuilder.getWorkspace().addFrame(frame);
                frame.setVisible(true);
            }

            setVisible(false);

        } catch (Exception ex) {
            Logger.getLogger(LoadSegmentFrame.class.getName()).log(Level.SEVERE, "", ex);
            NarrowOptionPane.showMessageDialog(this, "An error occurred. Please check the file", "Error", NarrowOptionPane.ERROR_MESSAGE);
        }
    }
    
    /* Given a list of Segments, returns the number of data points in all the
     * Segments.
     */
    private int spectraWarning(List<Segment> segList) {
        int numOfPoints = 0;
        
	for (Segment segList1 : segList) {
	    numOfPoints += segList1.getLength();
	}
        
        return numOfPoints;
    }
    private int spectraWarning(Segment seg) {
            return seg.getLength();
    }
    
}
