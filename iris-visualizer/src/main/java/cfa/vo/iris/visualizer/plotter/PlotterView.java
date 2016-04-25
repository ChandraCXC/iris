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

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.metadata.MetadataBrowserMainView;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.visualizer.plotter.PlotPreferences.PlotType;
import cfa.vo.iris.visualizer.preferences.VisualizerChangeEvent;
import cfa.vo.iris.visualizer.preferences.VisualizerCommand;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.iris.visualizer.preferences.VisualizerListener;
import cfa.vo.iris.visualizer.stil.StilPlotter;
import cfa.vo.sedlib.Segment;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.plaf.basic.BasicArrowButton;

public class PlotterView extends JInternalFrame {
    
    private static final Logger logger = Logger.getLogger(StilPlotter.class.getName());
    
    private static final long serialVersionUID = 1L;
    
    private IWorkspace ws;
    private IrisApplication app;
    // Plotting Components
    // StilPlotter plotter initialized in initComponents()
    private JInternalFrame residuals;
    private MetadataBrowserMainView metadataBrowser;
    private UnitsManagerFrame unitsManagerFrame;
    private JInternalFrame plotterNavHelpFrame;
    
    // Plot mouse coordinate locations
    private String xcoord = "0E0";
    private String ycoord = "0E0";
    
    public static double ZOOM_SCALE = 0.5;
    
    /**
     * Create the frame.
     * @param ws 
     * @param app 
     * @param title
     * @param preferences
     * @throws java.lang.Exception
     */
    public PlotterView(String title, 
                       IrisApplication app, 
                       IWorkspace ws,
                       VisualizerComponentPreferences preferences) 
                               throws Exception
    {
        setTitle(title);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSelected(true);
        setResizable(true);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setBounds(100, 100, 1096, 800);
        toFront();
        
        this.ws = ws;
        this.app = app;
        this.metadataBrowser = new MetadataBrowserMainView(ws, preferences);
        this.residuals = new JInternalFrame();
        
        initComponents();
        
        // initializing the stil plotter
        plotter.setWorkSpace(ws);
        plotter.setSedManager((SedlibSedManager) ws.getSedManager());
        plotter.setVisualizerPreferences(preferences);
        plotter.reset(null, true);
        
        // units chooser frame
        this.unitsManagerFrame = new UnitsManagerFrame(plotter);
        
        // plotter navigation help frame
        this.plotterNavHelpFrame = new PlotterNavHelpFrame("Plotter Navigation Help");
                
        // Action for opening metadata browser
        metadataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    openMetadataBrowser();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        
        // Action to set linear plotting
        mntmLinear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mntmLinear.setSelected(true);
                changePlotType(PlotPreferences.PlotType.LINEAR);
            }
        });
        
        // Action to set log plotting
        mntmLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mntmLog.setSelected(true);
                changePlotType(PlotPreferences.PlotType.LOG);
            }
        });
        
        // Action to set x-axis to log
        mntmXlog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mntmXlog.setSelected(true);
                changePlotType(PlotPreferences.PlotType.X_LOG);
            }
        });
        
        // Action to set the y-axis to log
        mntmYlog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mntmYlog.setSelected(true);
                changePlotType(PlotPreferences.PlotType.Y_LOG);
            }
        });
        
        // TODO: remove this after plotPreference bindings are done properly!!
        mntmGridOnOff.setSelected(PlotPreferences.getDefaultPlotPreferences()
                .getShowGrid());
        
        // Action to toggle grid on/off
        mntmGridOnOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean on = mntmGridOnOff.isSelected();
                setGridOn(on);
            }
        });
        
        // TODO: remove this after plotPreference bindings are done properly!!
        mntmGridOnOff.setSelected(PlotPreferences.getDefaultPlotPreferences()
                .getShowGrid());
        
        // Action to fix or unfix the plot viewport
        mntmAutoFixed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean fixed = mntmAutoFixed.isSelected();
                setFixedViewPort(fixed);
            }
        });
        
        addPlotChangeListener();
        
        // Set listeners to point to this view
        preferences.getMouseListenerManager().setPlotterView(this);
    }
    
    protected void addPlotChangeListener() {
        VisualizerChangeEvent.getInstance().add(new PlotChangeListener());
    }
    
    public ExtSed getSed() {
        return plotter.getSed();
    }

    public Map<Segment, SegmentModel> getSegmentsMap() {
        return plotter.getSegmentsMap();
    }
    
    private void openMetadataBrowser() throws Exception {
        if (!metadataBrowser.isVisible()) {
            ws.addFrame(metadataBrowser);
            GUIUtils.moveToFront(metadataBrowser);
        }
        else {
            GUIUtils.moveToFront(metadataBrowser);
        }
    }    
    
    public MetadataBrowserMainView getMetadataBrowserView() {
        return this.metadataBrowser;
    }
    
    private void resetPlot(ExtSed sed) {
        // TODO: At somepoint we may want this to be a feature if we ever have static SEDs.
        this.plotter.reset(sed, true);
    }

    private void redrawPlot() {
        this.plotter.redraw(true);
    }
    
    private void updatePlot(ExtSed source) {
        this.plotter.reset(source, true);
    }
    
    public String getXcoord() {
        return xcoord;
    }
    
    private static final String XCOORD_PROPERTY = "xcoord";
    public void setXcoord(String x) {
        String old = this.xcoord;
        this.xcoord = x;
        firePropertyChange(XCOORD_PROPERTY, old, xcoord);
    }
    
    public String getYcoord() {
        return ycoord;
    }
    
    private static final String YCOORD_PROPERTY = "ycoord";
    public void setYcoord(String y) {
        String old = this.ycoord;
        this.ycoord = y;
        firePropertyChange(YCOORD_PROPERTY, old, ycoord);
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

        plotTypeButtonGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        btnReset = new javax.swing.JButton();
        txtXposition = new javax.swing.JTextField();
        zoomIn = new javax.swing.JButton();
        btnUnits = new javax.swing.JButton();
        down = new cfa.vo.iris.gui.JButtonArrow(BasicArrowButton.SOUTH);
        fluxOrDensity = new javax.swing.JSpinner();
        left = new cfa.vo.iris.gui.JButtonArrow(BasicArrowButton.WEST);
        zoomOut = new javax.swing.JButton();
        metadataButton = new javax.swing.JButton();
        txtYposition = new javax.swing.JTextField();
        up = new cfa.vo.iris.gui.JButtonArrow(BasicArrowButton.NORTH);
        left1 = new cfa.vo.iris.gui.JButtonArrow(BasicArrowButton.EAST);
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        tglbtnShowHideResiduals = new javax.swing.JToggleButton();
        secondaryPlotOptions = new javax.swing.JSpinner();
        plotter = new cfa.vo.iris.visualizer.stil.StilPlotter();
        menuBar = new javax.swing.JMenuBar();
        mnF = new javax.swing.JMenu();
        mntmExport = new javax.swing.JMenuItem();
        mntmProperties = new javax.swing.JMenuItem();
        mntmOpen = new javax.swing.JMenuItem();
        mntmSave = new javax.swing.JMenuItem();
        mnEdit = new javax.swing.JMenu();
        mntmSomething = new javax.swing.JMenuItem();
        mnView = new javax.swing.JMenu();
        mnPlotType = new javax.swing.JMenu();
        mntmLog = new javax.swing.JRadioButtonMenuItem();
        mntmLinear = new javax.swing.JRadioButtonMenuItem();
        mntmXlog = new javax.swing.JRadioButtonMenuItem();
        mntmYlog = new javax.swing.JRadioButtonMenuItem();
        mntmErrorBars = new javax.swing.JCheckBoxMenuItem();
        mntmAutoFixed = new javax.swing.JCheckBoxMenuItem();
        mntmGridOnOff = new javax.swing.JCheckBoxMenuItem();
        mntmCoplot = new javax.swing.JMenuItem();
        mnHelp = new javax.swing.JMenu();
        mntmPlotterNavigationHelp = new javax.swing.JMenuItem();

        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        txtXposition.setEditable(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${xcoord}"), txtXposition, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        zoomIn.setText("In");
        zoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInActionPerformed(evt);
            }
        });

        btnUnits.setText("Units");
        btnUnits.setName("unitsButton"); // NOI18N
        btnUnits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnitsActionPerformed(evt);
            }
        });

        down.setText("jButtonArrow4");

        fluxOrDensity.setModel(new javax.swing.SpinnerListModel(new String[] {"Flux", "Flux Density"}));

        left.setText("jButtonArrow1");

        zoomOut.setText("Out");
        zoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomOutActionPerformed(evt);
            }
        });

        metadataButton.setText("Metadata");

        txtYposition.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${ycoord}"), txtYposition, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        up.setText("jButtonArrow3");

        left1.setText("jButtonArrow1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(btnReset)
                .addGap(18, 18, 18)
                .addComponent(zoomIn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(zoomOut)
                .addGap(18, 18, 18)
                .addComponent(left, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(left1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(up, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(down, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(135, 135, 135)
                .addComponent(txtXposition, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtYposition, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(metadataButton)
                .addGap(18, 18, 18)
                .addComponent(fluxOrDensity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUnits)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReset)
                    .addComponent(zoomIn)
                    .addComponent(zoomOut)
                    .addComponent(left, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(up, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(down, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtXposition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtYposition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(metadataButton)
                    .addComponent(btnUnits)
                    .addComponent(fluxOrDensity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(left1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tglbtnShowHideResiduals.setText("Show Residuals");

        //JFormattedTextField txtBoxShowHideResiduals = ((JSpinner.ListEditor) secondaryPlotOptions.getEditor()).getTextField();
        //txtBoxShowHideResiduals.setEditable(false);
        secondaryPlotOptions.setModel(new javax.swing.SpinnerListModel(new String[] {"Residuals", "Ratios"}));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tglbtnShowHideResiduals)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(secondaryPlotOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tglbtnShowHideResiduals)
                    .addComponent(secondaryPlotOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        plotter.setName("plotter"); // NOI18N

        menuBar.setName("menuBar"); // NOI18N

        mnF.setText("File");

        mntmExport.setText("Export Plot to File");
        mntmExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mntmExportActionPerformed(evt);
            }
        });
        mnF.add(mntmExport);

        mntmProperties.setText("Properties");
        mnF.add(mntmProperties);

        mntmOpen.setText("Open");
        mnF.add(mntmOpen);

        mntmSave.setText("Save");
        mnF.add(mntmSave);

        menuBar.add(mnF);

        mnEdit.setText("Edit");

        mntmSomething.setText("Something");
        mnEdit.add(mntmSomething);

        menuBar.add(mnEdit);

        mnView.setText("View");

        mnPlotType.setText("Plot Type");

        plotTypeButtonGroup.add(mntmLog);
        mntmLog.setSelected(true);
        mntmLog.setText("Log");
        mntmLog.setName("mntmLog"); // NOI18N
        mnPlotType.add(mntmLog);

        plotTypeButtonGroup.add(mntmLinear);
        mntmLinear.setText("Linear");
        mntmLinear.setName("mntmLinear"); // NOI18N
        mnPlotType.add(mntmLinear);

        plotTypeButtonGroup.add(mntmXlog);
        mntmXlog.setText("X Log");
        mntmXlog.setName("mntmXlog"); // NOI18N
        mnPlotType.add(mntmXlog);

        plotTypeButtonGroup.add(mntmYlog);
        mntmYlog.setText("Y Log");
        mntmYlog.setName("mntmYlog"); // NOI18N
        mnPlotType.add(mntmYlog);

        mnView.add(mnPlotType);

        mntmErrorBars.setSelected(true);
        mntmErrorBars.setText("Error Bars");
        mntmErrorBars.setEnabled(false);
        mnView.add(mntmErrorBars);

        mntmAutoFixed.setText("Fixed");
        mntmAutoFixed.setToolTipText("<html>Fix the plot ranges when the SED changes. Otherwise, <br/> \nthe plot ranges automatically update when a SED changes.</html>");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, plotter, org.jdesktop.beansbinding.ELProperty.create("${visualizerPreferences.plotPreferences.fixed}"), mntmAutoFixed, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mnView.add(mntmAutoFixed);

        mntmGridOnOff.setText("Grid on/off");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, plotter, org.jdesktop.beansbinding.ELProperty.create("${visualizerPreferences.plotPreferences.showGrid}"), mntmGridOnOff, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mnView.add(mntmGridOnOff);

        mntmCoplot.setText("Coplot...");
        mnView.add(mntmCoplot);

        menuBar.add(mnView);

        mnHelp.setText("Help");

        mntmPlotterNavigationHelp.setText("Open Visualizer Navigation Help...");
        mntmPlotterNavigationHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mntmPlotterNavigationHelpActionPerformed(evt);
            }
        });
        mnHelp.add(mntmPlotterNavigationHelp);

        menuBar.add(mnHelp);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plotter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(plotter, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        plotter.getAccessibleContext().setAccessibleName("plotter");
        plotter.getAccessibleContext().setAccessibleDescription("");

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void zoomInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInActionPerformed
        // zoom in by a factor.
        this.plotter.zoom(1 + ZOOM_SCALE);
    }//GEN-LAST:event_zoomInActionPerformed

    private void zoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomOutActionPerformed
        // zoom out by a factor
        this.plotter.zoom(1 - ZOOM_SCALE*2/3);
    }//GEN-LAST:event_zoomOutActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        PlotPreferences plotPrefs = plotter.getPlotPreferences();
        
        // if fixed, temporarily unfix the plot to reset the viewport to the
        // full X, Y range
        boolean fixed = plotPrefs.getFixed();
        if (fixed)
           plotPrefs.setFixed(false);
        
        resetPlot(getSed());
        
        if (fixed)
           plotPrefs.setFixed(true);
    }//GEN-LAST:event_btnResetActionPerformed

    private void mntmExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mntmExportActionPerformed
        PlotImageWriter writer = new PlotImageWriter(plotter, this);
        writer.openSavePlotDialog();
    }//GEN-LAST:event_mntmExportActionPerformed

    private void btnUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnitsActionPerformed
        
        if (!unitsManagerFrame.isVisible()) {
            ws.addFrame(unitsManagerFrame);
        }
        GUIUtils.moveToFront(unitsManagerFrame);
    }//GEN-LAST:event_btnUnitsActionPerformed

    private void mntmPlotterNavigationHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mntmPlotterNavigationHelpActionPerformed
        GUIUtils.moveToFront(plotterNavHelpFrame);
    }//GEN-LAST:event_mntmPlotterNavigationHelpActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnUnits;
    private cfa.vo.iris.gui.JButtonArrow down;
    private javax.swing.JSpinner fluxOrDensity;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private cfa.vo.iris.gui.JButtonArrow left;
    private cfa.vo.iris.gui.JButtonArrow left1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton metadataButton;
    private javax.swing.JMenu mnEdit;
    private javax.swing.JMenu mnF;
    private javax.swing.JMenu mnHelp;
    private javax.swing.JMenu mnPlotType;
    private javax.swing.JMenu mnView;
    private javax.swing.JCheckBoxMenuItem mntmAutoFixed;
    private javax.swing.JMenuItem mntmCoplot;
    private javax.swing.JCheckBoxMenuItem mntmErrorBars;
    private javax.swing.JMenuItem mntmExport;
    private javax.swing.JCheckBoxMenuItem mntmGridOnOff;
    private javax.swing.JRadioButtonMenuItem mntmLinear;
    private javax.swing.JRadioButtonMenuItem mntmLog;
    private javax.swing.JMenuItem mntmOpen;
    private javax.swing.JMenuItem mntmPlotterNavigationHelp;
    private javax.swing.JMenuItem mntmProperties;
    private javax.swing.JMenuItem mntmSave;
    private javax.swing.JMenuItem mntmSomething;
    private javax.swing.JRadioButtonMenuItem mntmXlog;
    private javax.swing.JRadioButtonMenuItem mntmYlog;
    private javax.swing.ButtonGroup plotTypeButtonGroup;
    private cfa.vo.iris.visualizer.stil.StilPlotter plotter;
    private javax.swing.JSpinner secondaryPlotOptions;
    private javax.swing.JToggleButton tglbtnShowHideResiduals;
    private javax.swing.JTextField txtXposition;
    private javax.swing.JTextField txtYposition;
    private cfa.vo.iris.gui.JButtonArrow up;
    private javax.swing.JButton zoomIn;
    private javax.swing.JButton zoomOut;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private void changePlotType(PlotPreferences.PlotType plotType) {
        plotter.changePlotType(plotType);
    }
    
    private void setGridOn(boolean on) {
        plotter.setGridOn(on);
    }
    
    /**
     * Fix the plot viewport, or let the viewport automatically resize itself
     * when updated. Zooming and panning are enabled in both states.
     * 
     * If there is no selected SED, then the global plot preferences are set.
     * However, the plotter always updates to the preferences of the currently 
     * selected SED. Only if no SED is selected will the global plot 'fixed'
     * preference be used.
     * 
     * @param fixed set to "true" to fix the viewport when SED changes occur, 
     * and set to "false" to let the viewport resize automatically with changes.
     */
    private void setFixedViewPort(boolean fixed) {
        
        this.plotter.getPlotPreferences().setFixed(fixed);
            
        // needs to be set whenever viewport changes
        this.plotter.getPlotPreferences()
                .setAspect(this.plotter.getPlotDisplay().getAspect());
    }
    
    /**
     * Update the plot preference items in the Viewer to the selected SED's
     * preferences
     */
    public void updatePreferences() {
        // Plot Type
        this.mntmLinear.setSelected(this.plotter.getPlotPreferences()
                .getPlotType()==PlotType.LINEAR);
        this.mntmLog.setSelected(this.plotter.getPlotPreferences()
                .getPlotType()==PlotType.LOG);
        this.mntmXlog.setSelected(this.plotter.getPlotPreferences()
                .getPlotType()==PlotType.X_LOG);
        this.mntmYlog.setSelected(this.plotter.getPlotPreferences()
                .getPlotType()==PlotType.Y_LOG);
        
        // Grid on/off
        this.mntmGridOnOff.setSelected(this.plotter.getPlotPreferences().getShowGrid());
        
        // turn errorbars on/off
//        this.mntmErrorBars.setSelected(this.stilPlotter1.getVisualizerPreferences()
//                .getSedPreferences(plotter.getSed()).getPlotPreferences()
//                .getShowErrorBars());
        
        // set plot window fixed
        this.mntmAutoFixed.setSelected(this.plotter.getPlotPreferences()
                .getFixed());
    }
    
    private class PlotChangeListener implements VisualizerListener {

        @Override
        public void process(ExtSed source, VisualizerCommand payload) {
            if (VisualizerCommand.RESET.equals(payload)) 
            {
                resetPlot(source);
                updatePreferences();
            }
            else if (VisualizerCommand.REDRAW.equals(payload)) {
                redrawPlot();
            }
            else if (VisualizerCommand.SELECTED.equals(payload)) {
                updatePlot(source);
                updatePreferences();
            }
        }
    }
    
    // plotter navigation help window. Is closable, maximizable, and 
    // iconifiable.
    public class PlotterNavHelpFrame extends JInternalFrame {
        
        public PlotterNavHelpFrame (String title) {
            super(title, true, true, true, true);
            ws.addFrame(this);
            this.getContentPane().add(new NavigationHelpPanel());
            this.setPreferredSize(new Dimension(400, 500));

            this.validate();
            this.pack();
            
            this.setLocation(0, 0);
            
            this.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        }
    }
}
