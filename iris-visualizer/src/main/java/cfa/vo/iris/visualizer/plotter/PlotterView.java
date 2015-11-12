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

import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.IWorkspace;
import javax.swing.JFrame;

public class PlotterView extends JInternalFrame {
    
    private static final long serialVersionUID = 1L;

    private StilPlotter plotter;
    private StilPlotter residuals;
    private JTextField txtXposistion;
    private JTextField txtYposition;
    private IWorkspace ws;
    private IrisApplication app;
    
    protected MetadataBrowser metadataBrowser;
    
    
    /**
     * Create the frame.
     * @param ws 
     * @param app 
     * @param title
     */
    public PlotterView(String title, IrisApplication app, IWorkspace ws) throws Exception {
        setTitle(title);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSelected(true);
        setResizable(true);
        setClosable(true);
        toFront();
        this.metadataBrowser = new MetadataBrowser();
        
        setMaximizable(true);
        setIconifiable(true);
        setBounds(100, 100, 1096, 800);
        
        this.ws = ws;
        this.app = app;
        
        plotter = new StilPlotter((String) null, app, ws);
        residuals = new StilPlotter((String) null, app, ws);
        
        JButton btnReset = new JButton("Reset");
        
        JToggleButton tglbtnShowhideResiduals = new JToggleButton("Show Residuals");
        
        JSpinner spinner = new JSpinner();
        spinner.setModel(new SpinnerListModel(new String[] {"Residuals", "Ratios", "Something"}));
        
        JButton button = new JButton("In");
        
        JButton button_1 = new JButton("Out");
        
        BasicArrowButton basicArrowButton = new BasicArrowButton(0);
        basicArrowButton.setDirection(7);
        
        BasicArrowButton basicArrowButton_1 = new BasicArrowButton(0);
        basicArrowButton_1.setDirection(3);
        
        BasicArrowButton basicArrowButton_2 = new BasicArrowButton(0);
        basicArrowButton_2.setDirection(1);
        
        BasicArrowButton basicArrowButton_3 = new BasicArrowButton(0);
        basicArrowButton_3.setDirection(5);
        
        txtXposistion = new JTextField();
        txtXposistion.setText("x-position");
        txtXposistion.setColumns(10);
        
        txtYposition = new JTextField();
        txtYposition.setText("y-position");
        txtYposition.setColumns(10);
        
        JCheckBox chckbxAbsolute = new JCheckBox("Absolute");
        
        JButton btnUnits = new JButton("Units");
        
        JSpinner spinner_1 = new JSpinner();
        spinner_1.setModel(new SpinnerListModel(new String[] {"Flux", "Flux Density"}));
        
        JButton metadataButton = new JButton("Metadata");
        metadataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    openMetadataBrowser();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        
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
                            .addComponent(button)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(button_1)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(basicArrowButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(basicArrowButton_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(basicArrowButton_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(basicArrowButton_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(82)
                            .addComponent(chckbxAbsolute)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(txtXposistion, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(txtYposition, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                            .addComponent(metadataButton)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(btnUnits))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(tglbtnShowhideResiduals)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)))
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
                            .addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                            .addComponent(metadataButton))
                        .addComponent(basicArrowButton_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(basicArrowButton_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(basicArrowButton_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_1)
                        .addComponent(button, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnReset, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(basicArrowButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(plotter, GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(residuals, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(tglbtnShowhideResiduals, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        getContentPane().setLayout(groupLayout);
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        JMenu mnF = new JMenu("File");
        menuBar.add(mnF);
        
        JMenuItem mntmExport = new JMenuItem("Export");
        mnF.add(mntmExport);
        
        JMenuItem mntmProperties = new JMenuItem("Properties");
        mnF.add(mntmProperties);
        
        JMenuItem mntmOpen = new JMenuItem("Open");
        mnF.add(mntmOpen);
        
        JMenuItem mntmSave = new JMenuItem("Save");
        mnF.add(mntmSave);
        
        JMenuItem mntmPrint = new JMenuItem("Print");
        mnF.add(mntmPrint);
        
        JMenu mnEdit = new JMenu("Edit");
        menuBar.add(mnEdit);
        
        JMenuItem mntmSomething = new JMenuItem("Something");
        mnEdit.add(mntmSomething);
        
        JMenu mnView = new JMenu("View");
        menuBar.add(mnView);
        
        JMenu mnPlotType = new JMenu("Plot Type");
        mnView.add(mnPlotType);
        
        JMenu mnLog = new JMenu("Log");
        mnPlotType.add(mnLog);
        
        JMenuItem mntmRegularLog = new JMenuItem("Regular Log");
        mnLog.add(mntmRegularLog);
        
        JMenuItem mntmExcendedLog = new JMenuItem("Extended Log");
        mnLog.add(mntmExcendedLog);
        
        JMenuItem mntmLinear = new JMenuItem("Linear");
        mnPlotType.add(mntmLinear);
        
        JMenuItem mntmXlog = new JMenuItem("xLog");
        mnPlotType.add(mntmXlog);
        
        JMenuItem mntmYlog = new JMenuItem("yLog");
        mnPlotType.add(mntmYlog);
        
        JMenuItem mntmErrorBars = new JMenuItem("Error Bars");
        mnView.add(mntmErrorBars);
        
        JMenuItem mntmAutofixed = new JMenuItem("Auto/Fixed");
        mnView.add(mntmAutofixed);
        
        JMenuItem mntmGridOnoff = new JMenuItem("Grid on/off");
        mnView.add(mntmGridOnoff);
        
        JMenuItem mntmCoplot = new JMenuItem("Coplot");
        mnView.add(mntmCoplot);

    }
    
    private void openMetadataBrowser() throws Exception {
        if (!metadataBrowser.isVisible()) {
            this.ws.getDesktop().add("Metadata Browser", metadataBrowser);
            GUIUtils.moveToFront(metadataBrowser);
        }
        else {
            GUIUtils.moveToFront(metadataBrowser);
        }
    }
    
    private static void addPopup(Component component, final JPopupMenu popup) {
    }
}