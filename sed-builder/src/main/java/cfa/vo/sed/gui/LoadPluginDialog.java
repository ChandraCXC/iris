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
 * LoadSegmentDialog.java
 *
 * Created on Aug 12, 2011, 1:57:45 AM
 */

package cfa.vo.sed.gui;

import cfa.vo.sed.builder.SedBuilder;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JFileChooser;

/**
 *
 * @author olaurino
 */
public final class LoadPluginDialog extends javax.swing.JDialog {


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
        urlText = urlText.trim();
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
        if(localSelected)
            setUrlS("file://"+new File(diskLocation).getAbsolutePath());
    }

    private boolean chosen = false;

    /**
     * Get the value of chosen
     *
     * @return the value of chosen
     */
    public boolean isChosen() {
        return chosen;
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
        location = location.trim();
        String oldLocation = this.diskLocation;
        this.diskLocation = location;
        firePropertyChange(PROP_DISKLOCATION, oldLocation, location);
        setUrlS("file://"+new File(location).getAbsolutePath());
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
        if(localSelected)
            return new File(diskLocation).isFile();
        if(urlSelected)
            try {
                return !getURL().getHost().isEmpty() && !getURL().getPath().isEmpty() && getURL().getProtocol().matches("http|ftp");
            } catch (MalformedURLException ex) {
                return false;
            }
        return false;

    }

    /** Creates new form LoadSegmentDialog */
    public LoadPluginDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);
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
        jRadioButton1 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jButton2 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();

        setTitle("Load an input File");
        setResizable(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${diskLocation}"), jTextField2, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jTextField2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

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

        jButton2.setText("Load");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${isLoadable}"), jButton2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                load(evt);
            }
        });

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${urlText}"), jTextField1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jTextField1, org.jdesktop.beansbinding.BeanProperty.create("editable"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jButton1)
                    .add(layout.createSequentialGroup()
                        .add(jRadioButton2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jRadioButton1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE))
                    .add(jButton2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
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
                .add(jButton2)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void browse(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browse
        JFileChooser jfc = SedBuilder.getWorkspace().getFileChooser();
        File l = new File(diskLocation);
        if(l.isDirectory())
            jfc.setCurrentDirectory(l);
        jfc.setApproveButtonText("Select");
        int returnval = jfc.showOpenDialog(SedBuilder.getWorkspace().getRootFrame());
        if(returnval == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            setDiskLocation(f.getAbsolutePath());
        }
    }//GEN-LAST:event_browse

    private void load(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_load
        chosen=true;
        this.setVisible(false);
    }//GEN-LAST:event_load


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    public void reset() {
        chosen = false;
    }

}
