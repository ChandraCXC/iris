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
package cfa.vo.iris.visualizer.plotter;

import javax.swing.JInternalFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;

import javax.swing.JButton;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.visualizer.metadata.MetadataBrowserView;
import cfa.vo.iris.visualizer.preferences.VisualizerChangeEvent;
import cfa.vo.iris.visualizer.preferences.VisualizerCommand;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import cfa.vo.iris.visualizer.preferences.VisualizerListener;
import cfa.vo.iris.visualizer.stil.StilPlotter;
import cfa.vo.sedlib.Segment;
import cfa.vo.iris.IWorkspace;
import javax.swing.JFrame;

public class PlotterView extends JInternalFrame {
    
    private static final long serialVersionUID = 1L;
    
    private IWorkspace ws;
    private IrisApplication app;
    private final VisualizerComponentPreferences preferences;

    // Plotting Components
    private StilPlotter plotter;
    private JInternalFrame residuals;
    private MetadataBrowserView metadataBrowser;
    
    // Buttons, etc.
    private JButton btnReset;
    private JToggleButton tglbtnShowhideResiduals;
    private JSpinner secondaryPlotOptions;
    private JButton zoomIn;
    private JButton zoomOut;
    private BasicArrowButton left;
    private BasicArrowButton right;
    private BasicArrowButton up;
    private BasicArrowButton down;
    private JCheckBox chckbxAbsolute;
    private JButton btnUnits;
    private JTextField txtXposistion;
    private JTextField txtYposition;
    private JSpinner fluxOrDensity;
    private JButton metadataButton;
    
    // Menu items
    private JMenuBar menuBar;
    private JMenu mnF;
    private JMenuItem mntmExport;
    private JMenuItem mntmProperties;
    private JMenuItem mntmOpen;
    private JMenuItem mntmSave;
    private JMenuItem mntmPrint;
    private JMenu mnEdit;
    private JMenuItem mntmSomething;
    private JMenu mnView;
    private JMenu mnPlotType;
    private JMenu mnLog;
    private JMenuItem mntmRegularLog;
    private JMenuItem mntmExcendedLog;
    private JMenuItem mntmLinear;
    private JMenuItem mntmXlog;
    private JMenuItem mntmYlog;
    private JMenuItem mntmErrorBars;
    private JMenuItem mntmAutofixed;
    private JMenuItem mntmGridOnoff;
    private JMenuItem mntmCoplot;
    

    /**
     * Create the frame.
     * @param ws 
     * @param app 
     * @param title
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
        this.preferences = preferences;
        
        this.metadataBrowser = new MetadataBrowserView(ws, preferences);
        this.plotter = new StilPlotter(ws, preferences);
        this.residuals = new JInternalFrame();
        
        initializeComponents();
        initializeMenuItems();
        
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
        
        // Action for resetting plot
        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetPlot(null);
            }
        });
        
        VisualizerChangeEvent.getInstance().add(new PlotChangeListener());
    }

    public ExtSed getSed() {
        return plotter.getSed();
    }

    public Map<Segment, SegmentLayer> getSegmentsMap() {
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
    
    public MetadataBrowserView getMetadataBrowserView() {
        return this.metadataBrowser;
    }
    
    private void resetPlot(ExtSed sed) {
        this.metadataBrowser.reset();
        // TODO: setting second argument to "false" forces the plot display
        // to be cached. Do we want this behavior in the future?
        // Note (jb): tried opening 300k sed with "fals" and "true." Both
        // produce a .5 second lag in panning the viewer.
        this.plotter.reset(sed, true);
    }
    
    private static void addPopup(Component component, final JPopupMenu popup) {
    }
    
    private void initializeComponents() {
        
        // Construct buttons etc.
        btnReset = new JButton("Reset");
        tglbtnShowhideResiduals = new JToggleButton("Show Residuals");
        
        secondaryPlotOptions = new JSpinner();
        secondaryPlotOptions.setModel(new SpinnerListModel(new String[] {"Residuals", "Ratios", "Something"}));
        
        zoomIn = new JButton("In");
        zoomOut = new JButton("Out");
        
        left = new BasicArrowButton(0);
        left.setDirection(7);
        
        right = new BasicArrowButton(0);
        right.setDirection(3);
        
        up = new BasicArrowButton(0);
        up.setDirection(1);
        
        down = new BasicArrowButton(0);
        down.setDirection(5);
        
        txtXposistion = new JTextField();
        txtXposistion.setText("x-position");
        txtXposistion.setColumns(10);
        
        txtYposition = new JTextField();
        txtYposition.setText("y-position");
        txtYposition.setColumns(10);
        
        chckbxAbsolute = new JCheckBox("Absolute");
        
        btnUnits = new JButton("Units");
        
        fluxOrDensity = new JSpinner();
        fluxOrDensity.setModel(new SpinnerListModel(new String[] {"Flux", "Flux Density"}));
        
        metadataButton = new JButton("Metadata");
        
        // Set layout
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(plotter, GroupLayout.DEFAULT_SIZE, 1062, Short.MAX_VALUE)
                        .addComponent(residuals, GroupLayout.DEFAULT_SIZE, 1062, Short.MAX_VALUE)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(btnReset)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(zoomIn)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(zoomOut)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(left, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(right, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(up, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(down, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(82)
                            .addComponent(chckbxAbsolute)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(txtXposistion, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(txtYposition, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                            .addComponent(metadataButton)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(fluxOrDensity, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(btnUnits))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(tglbtnShowhideResiduals)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(secondaryPlotOptions, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(txtXposistion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(chckbxAbsolute)
                            .addComponent(txtYposition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUnits)
                            .addComponent(fluxOrDensity, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                            .addComponent(metadataButton))
                        .addComponent(down, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(up, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(right, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(zoomOut)
                        .addComponent(zoomIn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnReset, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(left, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(plotter, GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(residuals, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(secondaryPlotOptions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(tglbtnShowhideResiduals, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        getContentPane().setLayout(groupLayout);
    }
    
    private void initializeMenuItems() {

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        mnF = new JMenu("File");
        menuBar.add(mnF);
        
        mntmExport = new JMenuItem("Export");
        mnF.add(mntmExport);
        
        mntmProperties = new JMenuItem("Properties");
        mnF.add(mntmProperties);
        
        mntmOpen = new JMenuItem("Open");
        mnF.add(mntmOpen);
        
        mntmSave = new JMenuItem("Save");
        mnF.add(mntmSave);
        
        mntmPrint = new JMenuItem("Print");
        mnF.add(mntmPrint);
        
        mnEdit = new JMenu("Edit");
        menuBar.add(mnEdit);
        
        mntmSomething = new JMenuItem("Something");
        mnEdit.add(mntmSomething);
        
        mnView = new JMenu("View");
        menuBar.add(mnView);
        
        mnPlotType = new JMenu("Plot Type");
        mnView.add(mnPlotType);
        
        mnLog = new JMenu("Log");
        mnPlotType.add(mnLog);
        
        mntmRegularLog = new JMenuItem("Regular Log");
        mnLog.add(mntmRegularLog);
        
        mntmExcendedLog = new JMenuItem("Extended Log");
        mnLog.add(mntmExcendedLog);
        
        mntmLinear = new JMenuItem("Linear");
        mnPlotType.add(mntmLinear);
        
        mntmXlog = new JMenuItem("xLog");
        mnPlotType.add(mntmXlog);
        
        mntmYlog = new JMenuItem("yLog");
        mnPlotType.add(mntmYlog);
        
        mntmErrorBars = new JMenuItem("Error Bars");
        mnView.add(mntmErrorBars);
        
        mntmAutofixed = new JMenuItem("Auto/Fixed");
        mnView.add(mntmAutofixed);
        
        mntmGridOnoff = new JMenuItem("Grid on/off");
        mnView.add(mntmGridOnoff);
        
        mntmCoplot = new JMenuItem("Coplot");
        mnView.add(mntmCoplot);
    }
    
    private class PlotChangeListener implements VisualizerListener {

        @Override
        public void process(ExtSed source, VisualizerCommand payload) {
            if (VisualizerCommand.RESET.equals(payload)) {
                resetPlot(source);
            }
            else if (VisualizerCommand.REDRAW.equals(payload)) {
                
            }
        }
    }
}