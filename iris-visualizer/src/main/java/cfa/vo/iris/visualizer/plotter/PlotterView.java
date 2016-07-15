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
import cfa.vo.iris.fitting.FitController;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.iris.visualizer.IrisVisualizer;
import cfa.vo.iris.visualizer.metadata.MetadataBrowserMainView;
import cfa.vo.iris.visualizer.plotter.PlotPreferences.PlotType;
import cfa.vo.iris.visualizer.preferences.CoPlotManagementWindow;
import cfa.vo.iris.visualizer.preferences.SedModel;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.iris.visualizer.preferences.VisualizerDataModel;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingConstants;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class PlotterView extends JInternalFrame {
    
    private static final long serialVersionUID = 1L;
    
    // Plotting Components
    private final VisualizerComponentPreferences preferences;
    private final MetadataBrowserMainView metadataBrowser;
    private final JInternalFrame plotterNavHelpFrame;
    private final UnitsManagerFrame unitsManagerFrame;
    private final IWorkspace ws;
    
    // Plot mouse coordinate locations
    private String xcoord = "0E0";
    private String ycoord = "0E0";
    
    // Bound to the StilPlotter preferences
    private PlotPreferences plotPreferences;
    
    // Coplotting selection window
    CoPlotManagementWindow coplotWindow;
    
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
                       IWorkspace ws,
                       VisualizerComponentPreferences preferences) 
                               throws Exception
    {
        setTitle(title);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        toFront();
        
        this.preferences = preferences;
        this.ws = ws;
        this.metadataBrowser = new MetadataBrowserMainView(preferences);
        
        initComponents();
        
        // plotter navigation help frame
        this.plotterNavHelpFrame = new PlotterNavHelpFrame("Plotter Navigation Help");
        
        // Units manager
        this.unitsManagerFrame = new UnitsManagerFrame();
        
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
        
        // Action to fix or unfix the plot viewport
        mntmAutoFixed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean fixed = mntmAutoFixed.isSelected();
                setFixedViewPort(fixed);
            }
        });
        
        // Set listeners to point to this view
        preferences.getMouseListenerManager().setPlotterView(this);
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
    
    public VisualizerDataModel getDataModel() {
        return this.preferences.getDataModel();
    }
    
    public PlotPreferences getPlotPreferences() {
        return plotter.getPlotPreferences();
    }
    
    // Tied to the stil plotter plot preferences
    public void setPlotPreferences(PlotPreferences pp) {
        this.plotPreferences = pp;
        
        // Update the view with the current settings
        // Plot Type
        this.mntmLinear.setSelected(plotPreferences.getPlotType()==PlotType.LINEAR);
        this.mntmLog.setSelected(plotPreferences.getPlotType()==PlotType.LOG);
        this.mntmXlog.setSelected(plotPreferences.getPlotType()==PlotType.X_LOG);
        this.mntmYlog.setSelected(plotPreferences.getPlotType()==PlotType.Y_LOG);
        
        // Grid on/off
        this.mntmGridOnOff.setSelected(plotPreferences.getShowGrid());
        
        // turn errorbars on/off
//        this.mntmErrorBars.setSelected(this.stilPlotter1.getVisualizerPreferences()
//                .getSedPreferences(plotter.getSed()).getPlotPreferences()
//                .getShowErrorBars());
        
        // set plot window fixed
        
        this.mntmAutoFixed.setSelected(plotPreferences.getFixed());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        plotTypeButtonGroup = new javax.swing.ButtonGroup();
        bottomButtonsPanel = new javax.swing.JPanel();
        tglbtnShowHideResiduals = new javax.swing.JToggleButton();
        secondaryPlotTypeComboBox = new javax.swing.JComboBox();
        evaluateButton = new javax.swing.JButton();
        topButtonsPanel = new javax.swing.JPanel();
        btnReset = new javax.swing.JButton();
        zoomIn = new javax.swing.JButton();
        btnUnits = new javax.swing.JButton();
        fluxOrDensity = new javax.swing.JSpinner();
        zoomOut = new javax.swing.JButton();
        metadataButton = new javax.swing.JButton();
        buttonPanel = new javax.swing.JPanel();
        up = new cfa.vo.iris.visualizer.plotter.JButtonArrow();
        down = new cfa.vo.iris.visualizer.plotter.JButtonArrow();
        left = new cfa.vo.iris.visualizer.plotter.JButtonArrow();
        right = new cfa.vo.iris.visualizer.plotter.JButtonArrow();
        mouseCoordPanel = new javax.swing.JPanel();
        txtXposition = new javax.swing.JTextField();
        txtYposition = new javax.swing.JTextField();
        plotter = new StilPlotter(preferences);
        menuBar = new javax.swing.JMenuBar();
        mnF = new javax.swing.JMenu();
        mntmExport = new javax.swing.JMenuItem();
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

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setMinimumSize(new java.awt.Dimension(300, 300));
        setPreferredSize(new java.awt.Dimension(800, 546));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, secondaryPlotTypeComboBox, org.jdesktop.beansbinding.ELProperty.create("Show ${selectedItem}"), tglbtnShowHideResiduals, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        secondaryPlotTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Residuals", "Ratios" }));

        evaluateButton.setText("Evaluate Models");
        evaluateButton.setToolTipText("Re-evaluates the models using the current fit results");
        evaluateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                evaluateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout bottomButtonsPanelLayout = new javax.swing.GroupLayout(bottomButtonsPanel);
        bottomButtonsPanel.setLayout(bottomButtonsPanelLayout);
        bottomButtonsPanelLayout.setHorizontalGroup(
            bottomButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomButtonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tglbtnShowHideResiduals)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(secondaryPlotTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(evaluateButton)
                .addContainerGap())
        );
        bottomButtonsPanelLayout.setVerticalGroup(
            bottomButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomButtonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bottomButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tglbtnShowHideResiduals)
                    .addComponent(secondaryPlotTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(evaluateButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        getContentPane().add(bottomButtonsPanel, gridBagConstraints);

        topButtonsPanel.setLayout(new java.awt.GridBagLayout());

        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 11);
        topButtonsPanel.add(btnReset, gridBagConstraints);

        zoomIn.setText("In");
        zoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        topButtonsPanel.add(zoomIn, gridBagConstraints);

        btnUnits.setText("Units");
        btnUnits.setName("unitsButton"); // NOI18N
        btnUnits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnitsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        topButtonsPanel.add(btnUnits, gridBagConstraints);

        fluxOrDensity.setModel(new javax.swing.SpinnerListModel(new String[] {"Flux", "Flux Density"}));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        topButtonsPanel.add(fluxOrDensity, gridBagConstraints);

        zoomOut.setText("Out");
        zoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomOutActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 14);
        topButtonsPanel.add(zoomOut, gridBagConstraints);

        metadataButton.setText("Metadata");
        metadataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                metadataButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        topButtonsPanel.add(metadataButton, gridBagConstraints);

        up.setText("up");
        up.setContentAreaFilled(false);
        up.setMaximumSize(null);
        up.setMinimumSize(null);
        up.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upActionPerformed(evt);
            }
        });

        down.setText("jButtonArrow2");
        down.setContentAreaFilled(false);
        down.setDirection(5);
        down.setMaximumSize(null);
        down.setMinimumSize(null);
        down.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downActionPerformed(evt);
            }
        });

        left.setText("jButtonArrow3");
        left.setContentAreaFilled(false);
        left.setDirection(3);
        left.setMaximumSize(null);
        left.setMinimumSize(null);
        left.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftActionPerformed(evt);
            }
        });

        right.setText("jButtonArrow4");
        right.setContentAreaFilled(false);
        right.setDirection(7);
        right.setMaximumSize(null);
        right.setMinimumSize(null);
        right.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(up, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(down, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(left, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(right, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(up, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(down, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(left, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(right, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        topButtonsPanel.add(buttonPanel, gridBagConstraints);

        txtXposition.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${xcoord}"), txtXposition, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtYposition.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${ycoord}"), txtYposition, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout mouseCoordPanelLayout = new javax.swing.GroupLayout(mouseCoordPanel);
        mouseCoordPanel.setLayout(mouseCoordPanelLayout);
        mouseCoordPanelLayout.setHorizontalGroup(
            mouseCoordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mouseCoordPanelLayout.createSequentialGroup()
                .addComponent(txtXposition, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtYposition, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        mouseCoordPanelLayout.setVerticalGroup(
            mouseCoordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mouseCoordPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(mouseCoordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtXposition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtYposition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        topButtonsPanel.add(mouseCoordPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(topButtonsPanel, gridBagConstraints);

        plotter.setBackground(java.awt.Color.white);
        plotter.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, plotter, org.jdesktop.beansbinding.ELProperty.create("${plotPreferences.showGrid}"), plotter, org.jdesktop.beansbinding.BeanProperty.create("gridOn"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${dataModel.layerModels}"), plotter, org.jdesktop.beansbinding.BeanProperty.create("layerModels"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${plotPreferences}"), plotter, org.jdesktop.beansbinding.BeanProperty.create("plotPreferences"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, secondaryPlotTypeComboBox, org.jdesktop.beansbinding.ELProperty.create("${selectedItem}"), plotter, org.jdesktop.beansbinding.BeanProperty.create("residualsOrRatios"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${dataModel.selectedSeds}"), plotter, org.jdesktop.beansbinding.BeanProperty.create("seds"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tglbtnShowHideResiduals, org.jdesktop.beansbinding.ELProperty.create("${selected}"), plotter, org.jdesktop.beansbinding.BeanProperty.create("showResiduals"));
        bindingGroup.addBinding(binding);

        plotter.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 680;
        gridBagConstraints.ipady = 352;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(plotter, gridBagConstraints);

        menuBar.setName("menuBar"); // NOI18N

        mnF.setText("File");

        mntmExport.setText("Export Plot to File");
        mntmExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mntmExportActionPerformed(evt);
            }
        });
        mnF.add(mntmExport);

        menuBar.add(mnF);

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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, plotter, org.jdesktop.beansbinding.ELProperty.create("${gridOn}"), mntmGridOnOff, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mnView.add(mntmGridOnOff);

        mntmCoplot.setText("Coplot...");
        mntmCoplot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mntmCoplotActionPerformed(evt);
            }
        });
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
        plotter.resetZoom();
    }//GEN-LAST:event_btnResetActionPerformed

    private void mntmExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mntmExportActionPerformed
        PlotImageWriter writer = new PlotImageWriter(plotter, this);
        writer.openSavePlotDialog();
    }//GEN-LAST:event_mntmExportActionPerformed

    private void btnUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnitsActionPerformed
        unitsManagerFrame.setDataModel(preferences.getDataModel());
        if (!unitsManagerFrame.isVisible()) {
            ws.addFrame(unitsManagerFrame);
        }
        GUIUtils.moveToFront(unitsManagerFrame);
    }//GEN-LAST:event_btnUnitsActionPerformed

    private void mntmPlotterNavigationHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mntmPlotterNavigationHelpActionPerformed
        GUIUtils.moveToFront(plotterNavHelpFrame);
    }//GEN-LAST:event_mntmPlotterNavigationHelpActionPerformed

    private void metadataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_metadataButtonActionPerformed
        try {
            openMetadataBrowser();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }//GEN-LAST:event_metadataButtonActionPerformed

    private void mntmCoplotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mntmCoplotActionPerformed
        coplotWindow = new CoPlotManagementWindow(this.preferences);
        ws.addFrame(coplotWindow);
        GUIUtils.moveToFront(coplotWindow);
    }//GEN-LAST:event_mntmCoplotActionPerformed

    private void upActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upActionPerformed
        plotter.dataPan(SwingConstants.NORTH);
    }//GEN-LAST:event_upActionPerformed

    private void downActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downActionPerformed
        plotter.dataPan(SwingConstants.SOUTH);
    }//GEN-LAST:event_downActionPerformed

    private void leftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftActionPerformed
        plotter.dataPan(SwingConstants.EAST);
    }//GEN-LAST:event_leftActionPerformed

    private void rightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightActionPerformed
        plotter.dataPan(SwingConstants.WEST);
    }//GEN-LAST:event_rightActionPerformed

    private void evaluateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_evaluateButtonActionPerformed
        // Do nothing if there is not controller available
        FitController controller = IrisVisualizer.getInstance().getController();
        if (controller == null) {
            return;
        }
        
        // Re-evaluate all models currently plotted
        for (SedModel model : preferences.getDataModel().getSedModels()) {
            preferences.evaluateModel(model, controller);
        }
    }//GEN-LAST:event_evaluateButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomButtonsPanel;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnUnits;
    private javax.swing.JPanel buttonPanel;
    private cfa.vo.iris.visualizer.plotter.JButtonArrow down;
    private javax.swing.JButton evaluateButton;
    private javax.swing.JSpinner fluxOrDensity;
    private cfa.vo.iris.visualizer.plotter.JButtonArrow left;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton metadataButton;
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
    private javax.swing.JMenuItem mntmPlotterNavigationHelp;
    private javax.swing.JRadioButtonMenuItem mntmXlog;
    private javax.swing.JRadioButtonMenuItem mntmYlog;
    private javax.swing.JPanel mouseCoordPanel;
    private javax.swing.ButtonGroup plotTypeButtonGroup;
    private cfa.vo.iris.visualizer.plotter.StilPlotter plotter;
    private cfa.vo.iris.visualizer.plotter.JButtonArrow right;
    private javax.swing.JComboBox secondaryPlotTypeComboBox;
    private javax.swing.JToggleButton tglbtnShowHideResiduals;
    private javax.swing.JPanel topButtonsPanel;
    private javax.swing.JTextField txtXposition;
    private javax.swing.JTextField txtYposition;
    private cfa.vo.iris.visualizer.plotter.JButtonArrow up;
    private javax.swing.JButton zoomIn;
    private javax.swing.JButton zoomOut;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private void changePlotType(PlotPreferences.PlotType plotType) {
        plotter.setPlotType(plotType);
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
    }
    
    // plotter navigation help window. Is closable, maximizable, and 
    // iconifiable.
    @SuppressWarnings("serial")
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
