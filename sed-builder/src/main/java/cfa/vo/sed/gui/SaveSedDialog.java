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
 * SaveConfigurationDialog.java
 *
 * Created on May 25, 2011, 12:03:17 AM
 */
package cfa.vo.sed.gui;

import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.utils.SpaceTrimmer;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sed.filters.NativeFileFormat;
import cfa.vo.sed.quantities.IUnit;
import cfa.vo.sed.quantities.SPVYUnit;
import cfa.vo.sed.quantities.XUnit;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.io.SedFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;

/**
 *
 * @author olaurino
 */
public final class SaveSedDialog extends javax.swing.JDialog {

    private String xunit;
    public static final String PROP_XUNIT = "xunit";

    /**
     * Get the value of xunit
     *
     * @return the value of xunit
     */
    public String getXunit() {
        return xunit;
    }

    /**
     * Set the value of xunit
     *
     * @param xunit new value of xunit
     */
    public void setXunit(String xunit) {
        String oldXunit = this.xunit;
        this.xunit = xunit;
        firePropertyChange(PROP_XUNIT, oldXunit, xunit);
    }
    private String yunit;
    public static final String PROP_YUNIT = "yunit";

    /**
     * Get the value of yunit
     *
     * @return the value of yunit
     */
    public String getYunit() {
        return yunit;
    }

    /**
     * Set the value of yunit
     *
     * @param yunit new value of yunit
     */
    public void setYunit(String yunit) {
        String oldYunit = this.yunit;
        this.yunit = yunit;
        firePropertyChange(PROP_YUNIT, oldYunit, yunit);
    }
    private boolean single = false;
    public static final String PROP_SINGLE = "single";

    /**
     * Get the value of single
     *
     * @return the value of single
     */
    public boolean isSingle() {
        return single;
    }

    /**
     * Set the value of single
     *
     * @param single new value of single
     */
    public void setSingle(boolean single) {
        boolean oldSingle = this.single;
        this.single = single;
        firePropertyChange(PROP_SINGLE, oldSingle, single);
        jComboBox2.setVisible(single);
        jComboBox3.setVisible(single);
        jLabel2.setVisible(single);
        jLabel3.setVisible(single);
        
        warning.setVisible(!single);
        
        if(!ascii_flag && !single)
            if(sed.getNumberOfSegments()>20)
                warning.setVisible(true);
            else
                warning.setVisible(false);
    }
    
    private boolean ascii_flag = false;
    
    private boolean savable = false;
    public static final String PROP_SAVABLE = "savable";

    /**
     * Get the value of savable
     *
     * @return the value of savable
     */
    public boolean isSavable() {
        return savable;
    }

    /**
     * Set the value of savable
     *
     * @param savable new value of savable
     */
    public void setSavable(boolean savable) {
        boolean oldSavable = this.savable;
        this.savable = savable;
        firePropertyChange(PROP_SAVABLE, oldSavable, savable);
    }
    private ExtSed sed;
    private String sedName;

    /**
     * Get the value of sedName
     *
     * @return the value of sedName
     */
    public String getSedName() {
        return sedName;
    }

    /**
     * Set the value of sedName
     *
     * @param sedName new value of sedName
     */
    public void setSedName(String sedName) {
        this.sedName = sedName;
    }
    private List<NativeFileFormat> sedFormats = new ArrayList();
    public static final String PROP_SEDFORMATS = "sedFormats";

    /**
     * Get the value of sedFormats
     *
     * @return the value of sedFormats
     */
    public List<NativeFileFormat> getSedFormats() {
        return sedFormats;
    }

    /**
     * Set the value of sedFormats
     *
     * @param sedFormats new value of sedFormats
     */
    public void setSedFormats(List<NativeFileFormat> sedFormats) {
        List<NativeFileFormat> oldSedFormats = this.sedFormats;
        this.sedFormats = sedFormats;
        firePropertyChange(PROP_SEDFORMATS, oldSedFormats, sedFormats);
    }
    private NativeFileFormat format;
    public static final String PROP_FORMAT = "format";

    /**
     * Get the value of format
     *
     * @return the value of format
     */
    public NativeFileFormat getFormat() {
        return format;
    }

    /**
     * Set the value of format
     *
     * @param format new value of format
     */
    public void setFormat(NativeFileFormat format) {
        NativeFileFormat oldFormat = this.format;
        this.format = format;
        firePropertyChange(PROP_FORMAT, oldFormat, format);
	if(format == NativeFileFormat.ASCIITABLE) {
	    setSingle(true);
	    jCheckBox1.setEnabled(false);
	} else {
	    jCheckBox1.setEnabled(true);
	    setSingle(false);
	}
//        if(!filePath.isEmpty()) {
//            setFilePath(filePath.replaceAll(getExtension(new File(filePath)), format.exten()));
//        }
    }
    private String filePath = "";
    public static final String PROP_FILEPATH = "filePath";

    /**
     * Get the value of filePath
     *
     * @return the value of filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Set the value of filePath
     *
     * @param filePath new value of filePath
     */
    public void setFilePath(String filePath) {
        filePath = SpaceTrimmer.sideTrim(filePath);
        String oldFilePath = this.filePath;
        this.filePath = filePath;
        File f = new File(filePath);
        setSavable(!f.isDirectory());
        firePropertyChange(PROP_FILEPATH, oldFilePath, filePath);
    }
    //private boolean ascii_flag = false;

    /** Creates new form SaveConfigurationDialog */
    // TODO: see how to turn 'ascii' switch on
    public SaveSedDialog(java.awt.Frame parent, ExtSed sed, boolean ascii) {
        super(parent, true);
        
        this.ascii_flag = ascii;
        this.sed = sed;
        setSedName(sed.getId());
        initComponents();
        
        if(!ascii)
            if(sed.getNumberOfSegments()>20)
                warning.setVisible(true);
            else
                warning.setVisible(false);
        
        if (sed.getNumberOfSegments() == 0) {
            warningText.setText("Warning: SED is empty");
        }
        List formats = new ArrayList();
        formats.add(NativeFileFormat.VOTABLE);
        formats.add(NativeFileFormat.FITS);
	formats.add(NativeFileFormat.ASCIITABLE);
        setSedFormats(formats);
        this.setLocationRelativeTo(parent);
        this.getRootPane().setDefaultButton(jButton2);

        if (ascii) {
            jCheckBox1.setSelected(true);
            jCheckBox1.setEnabled(false);
            jLabel1.setVisible(false);
            jComboBox1.setVisible(false);
        }

        jComboBox2.setVisible(single);
        jComboBox3.setVisible(single);
        jLabel2.setVisible(single);
        jLabel3.setVisible(single);

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

        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        warningText = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        busy = new org.jdesktop.swingx.JXBusyLabel();
        warning = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Save Sed File");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${filePath}"), jTextField2, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jButton1.setText("Browse...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1browse(evt);
            }
        });

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(SaveSedDialog.class, this);
        jButton2.setAction(actionMap.get("saveSed")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${savable}"), jButton2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${sedFormats}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jComboBox1);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${format}"), jComboBox1, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel1.setText("File Format");

        warningText.setForeground(new java.awt.Color(153, 0, 51));

        jCheckBox1.setText("Single Table");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${single}"), jCheckBox1, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jComboBox2.setModel(new DefaultComboBoxModel(loadEnum(XUnit.class)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${xunit}"), jComboBox2, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${single}"), jComboBox2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jComboBox3.setModel(new DefaultComboBoxModel(loadEnum(SPVYUnit.class)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${yunit}"), jComboBox3, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${single}"), jComboBox3, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel2.setText("X Units");

        jLabel3.setText("Y Units");

        warning.setText("<html>This SED contains many segments<br>and might take a while to save.");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jTextField2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(118, 118, 118)
                                .add(warningText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButton1))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(jCheckBox1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 41, Short.MAX_VALUE)
                                .add(jLabel1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 106, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jComboBox2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 109, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jComboBox3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(busy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButton2)))
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(warning, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                        .add(12, 12, 12))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(warningText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1)
                    .add(jCheckBox1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBox3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jComboBox2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton2)
                    .add(busy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(warning, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                .add(20, 20, 20))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1browse(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1browse
        JFileChooser jfc = SedBuilder.getWorkspace().getFileChooser();
        File l = new File(filePath);
        if (l.isDirectory()) {
            jfc.setCurrentDirectory(l);
        }
        jfc.setApproveButtonText("Select");
        jfc.setSelectedFile(new File(sedName));
        int returnval = jfc.showSaveDialog(this);
        if (returnval == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
//            String ext = getExtension(f).isEmpty() ? "."+format.exten() : "";
            setFilePath(f.getAbsolutePath());
//            setFilePath(f.getAbsolutePath().replaceAll(getExtension(new File(filePath)), format.exten()));
        }
}//GEN-LAST:event_jButton1browse

    public void writeAscii(ExtSed sed, File f, boolean calcFlux) throws Exception {
        ExtSed newsed = SedBuilder.flatten(sed, xunit, yunit);
        Segment segment = newsed.getSegment(0);
        double[] x = segment.getSpectralAxisValues();
        double[] y = segment.getFluxAxisValues();
        FileOutputStream fos = new FileOutputStream(f);
        PrintWriter out = new PrintWriter(fos);
        out.write("# This file was generated by Iris, the VAO SED building and analysis tool\n");
	if (calcFlux) {
	    out.write("#\n# Iris Flux Integration output\n");
	    out.write("# Spectral values are the effective wavelengths of the passbands\n");
	}
        out.write("#\n");
        Date d = new Date();
        out.write("# File created on " + d.toString()+"\n");
        out.write("#\n#\n");
        out.write("# TARGET = "+segment.getTarget().getName().getValue()+"\n");
	out.write("# RA = "+segment.getTarget().getPos().getValue()[0].getCastValue()+"\n");
	out.write("# DEC = "+segment.getTarget().getPos().getValue()[1].getCastValue()+"\n");
	out.write("# XUNIT = "+segment.getSpectralAxisUnits()+"\n");
	out.write("# YUNIT = "+segment.getFluxAxisUnits()+"\n");
	double[] yerr = (double[]) segment.getDataValues("Spectrum.Data.FluxAxis.Accuracy.StatError");
	if (IsAllNan(yerr)) {
	    out.write("#\n# x y\n\n");
	    for (int i = 0; i < x.length; i++) {
		out.write(x[i] + " " + y[i] + "\n");
	    }
	} else {
	    out.write("#\n# x y y_err\n\n");
	    for (int i = 0; i < x.length; i++) {
		out.write(x[i] + " " + y[i] + " " + yerr[i] + "\n");
	    }
	}
        out.close();
    }    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busy;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JLabel warning;
    private javax.swing.JLabel warningText;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    /*
     * Get the extension of a file.
     */
//    private String getExtension(File f) {
//        String ext = "";
//        String s = f.getName();
//        int i = s.lastIndexOf('.');
//
//        if (i > 0 &&  i < s.length() - 1) {
//            ext = s.substring(i+1).toLowerCase();
//        }
//
//        return ext;
//    }
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
            Logger.getLogger(SaveSedDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Action
    public Task saveSed() {
        return new SaveSedTask(org.jdesktop.application.Application.getInstance());
    }

    private class SaveSedTask extends org.jdesktop.application.Task<Object, Void> {

        SaveSedTask(org.jdesktop.application.Application app) {
            super(app);
        }

        @Override
        protected Object doInBackground() {
            busy.setBusy(true);
            if ((single || ascii_flag) && (xunit == null || yunit == null)) {
                NarrowOptionPane.showMessageDialog(null,
                        "Please select spectral and flux units!",
                        "Units Error",
                        NarrowOptionPane.ERROR_MESSAGE);
                return null;
            }
            try {

                File f = new File(filePath);

                boolean overwrite = true;
                int resp = NarrowOptionPane.YES_OPTION;
                if (f.exists()) {
                    resp = NarrowOptionPane.showConfirmDialog(SedBuilder.getWorkspace().getRootFrame(),
                            filePath + " exists, do you want to overwrite it?", "File exists", NarrowOptionPane.YES_NO_OPTION);
                }
		
		//If saving in VOTable or FITS format, switch file format to SedFormat
		SedFormat fmt = format.equals(NativeFileFormat.FITS) ? SedFormat.FITS : SedFormat.VOT;
		
                overwrite = (resp == NarrowOptionPane.YES_OPTION);

                if (overwrite) {
                    if (ascii_flag) {
                        writeAscii(sed, f, true);
		    } else if (jComboBox1.getSelectedItem().equals(NativeFileFormat.ASCIITABLE)) {
			writeAscii(sed, f, false);
                    } else {
                        if ((single) && (!jComboBox1.getSelectedItem().equals(NativeFileFormat.ASCIITABLE))) {
                            ExtSed newsed = SedBuilder.flatten(sed, xunit, yunit);
                            newsed.write(filePath, fmt);
                        } else {
                            sed.write(filePath, fmt);
                        }
                    }

                    NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                            "Saved file " + filePath, "Saved File", NarrowOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                        ex.getMessage(), "Error saving file", NarrowOptionPane.ERROR_MESSAGE);
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            return null;
            }
            return null;
        }

        @Override
        protected void succeeded(Object result) {
            busy.setBusy(false);
            SaveSedDialog.this.dispose();
        }
    }
    
    public boolean IsAllNan(double[] array) {
	int ct = 0;
	for (double element : array) {
	    if (Double.isNaN(element)) {
		ct++;
	    }
	}
	return ct == array.length;
    }
}
