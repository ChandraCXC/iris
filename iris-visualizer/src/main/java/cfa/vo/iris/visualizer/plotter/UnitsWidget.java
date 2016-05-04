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
package cfa.vo.iris.visualizer.plotter;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.quantities.SPVYUnit;
import cfa.vo.iris.sed.quantities.XUnit;
import cfa.vo.iris.units.UnitsManager;
import cfa.vo.iris.visualizer.preferences.VisualizerChangeEvent;
import cfa.vo.iris.visualizer.preferences.VisualizerCommand;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.iris.visualizer.stil.StilPlotter;
import cfa.vo.utils.Default;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * Unit selection widget. X-units are on the left split pane, Y-units are
 * on the right side.
 */
public class UnitsWidget extends javax.swing.JPanel {

    private ExtSed currentSed;
    private VisualizerComponentPreferences prefs;
    private UnitsManager unitsManager;
    private StilPlotter plotter;
    
    /**
     * Creates new form UnitsWidget
     * @param plotter 
     */
    public UnitsWidget(StilPlotter plotter) {
        this.currentSed = plotter.getSed();
        this.plotter = plotter;
        this.prefs = plotter.getVisualizerPreferences();
        this.unitsManager = Default.getInstance().getUnitsManager();
        initComponents();
        
        // if current sed is null, set X and Y to Iris default units
        if (currentSed != null) {
            setXunit(prefs.getSedModel(currentSed).getXunits());
            setYunit(prefs.getSedModel(currentSed).getYunits());
        } else {
            // TODO: use default Iris units here; should be a static
            setXunit(unitsManager.newXUnits("Hz").toString());
            setYunit(unitsManager.newYUnits("Jy").toString());
        }
        
        xunits.setSelectedValue(getXunit(), false);
        yunits.setSelectedValue(getYunit(), false);
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

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        xunits = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        yunits = new javax.swing.JList();

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setResizeWeight(0.5);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(550, 200));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("X Units"));

        xunits.setModel(new XUnitsListModel());
        xunits.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        xunits.setName("xunitsList"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${xunit}"), xunits, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(xunits);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Y Units"));

        yunits.setModel(new YUnitsListModel());
        yunits.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        yunits.setName("yunitsList"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${yunit}"), yunits, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(yunits);

        jSplitPane1.setRightComponent(jScrollPane2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JList xunits;
    private javax.swing.JList yunits;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    /**
     * Update the units of the currently selected SED.
     * 
     * Note that if the plotter SED is empty, this will still change the 
     * preferred plotting units for this SED; all segments added to the SED will
     * be converted to the selected units.
     */
    public void updateUnits() {
        // if SED is null, don't do anything.
        if (plotter.getSed() == null) {
            return;
        }
        
        // fire Visualizer event to update plot and MB
        plotter.getVisualizerPreferences().getSedModel(plotter.getSed()).setUnits(xunit, yunit);
        fire(plotter.getSed(), VisualizerCommand.RESET);
    }
    
    /**
     * Update the units of the given SED
     * @param sed 
     * 
     * Note that if the plotter SED is empty, this will still change the 
     * preferred plotting units for this SED; all segments added to the SED will
     * be converted to the selected units.
     */
    public void updateUnits(ExtSed sed) {
        
        // if SED is null, don't do anything.
        if (plotter.getSed() == null) {
            return;
        }
        
        // fire Visualizer event to update plot and MB
        plotter.getVisualizerPreferences().getSedModel(sed).setUnits(xunit, yunit);
        fire(sed, VisualizerCommand.RESET);
    }
    
    /*
     * getters and setters
     *
     */
    
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
       //firePropertyChange(PROP_XUNIT, oldXunit, xunit);
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
        //firePropertyChange(PROP_YUNIT, oldYunit, yunit);
    }

    public ExtSed getSed() {
        return this.currentSed;
    }
    
    public void setSed(ExtSed sed) {
        this.currentSed = sed;
    }
    
    /*
     * end of getters and setters
     *
     */
    
    protected void fire(ExtSed source, VisualizerCommand command) {
        VisualizerChangeEvent.getInstance().fire(source, command);
    }
    
    /*
     * List Models for Y and X units
     *
     */
    
    private class XUnitsListModel extends AbstractListModel {

        List<String> xunits = new ArrayList<>();
        
        public XUnitsListModel() {
            for (XUnit unit : XUnit.values()) {
                xunits.add(unit.getString());
            }
        }
        @Override
        public int getSize() {
            return xunits.size();
        }

        @Override
        public Object getElementAt(int i) {
            return xunits.get(i);
        }
    }
    
    private class YUnitsListModel extends AbstractListModel {

        List<String> yunits = new ArrayList<>();
        
        public YUnitsListModel() {
            for (SPVYUnit unit : SPVYUnit.values()) {
                yunits.add(unit.getString());
            }
        }
        @Override
        public int getSize() {
            return yunits.size();
        }

        @Override
        public Object getElementAt(int i) {
            return yunits.get(i);
        }
    }
}
