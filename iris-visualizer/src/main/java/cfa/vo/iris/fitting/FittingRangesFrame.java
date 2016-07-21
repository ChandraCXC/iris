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
package cfa.vo.iris.fitting;

import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.SedException;
import cfa.vo.iris.sed.quantities.XUnit;
import cfa.vo.iris.visualizer.plotter.MouseXRangesClickedListener;
import cfa.vo.iris.visualizer.preferences.VisualizerComponentPreferences;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

/**
 *
 * A JInternalFrame for managing fitting ranges. Users can add ranges by 
 * clicking on the plot, or by specifying specific start and end points. Fit 
 * ranged can be removed from here as well.
 */
public class FittingRangesFrame extends javax.swing.JInternalFrame {

    private FitController controller;
    private VisualizerComponentPreferences preferences;
    private MouseXRangesClickedListener listener;
    
    /**
     * Creates new form FittingRangesFrame
     * @param preferences - the VisualizerComponentPreferences
     * @param controller - the FitController
     */
    public FittingRangesFrame(VisualizerComponentPreferences preferences, FitController controller) {
        initComponents();
        
        this.preferences = preferences;
        this.controller = controller;
        
        jTable1.setModel(new FittingRangeTableModel(controller.getFit().getFittingRanges()));
        
        // listener for fitting range changes
        listener = (MouseXRangesClickedListener) preferences.getMouseListenerManager().getListener(MouseXRangesClickedListener.class);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        addRangeFromPlotButton = new javax.swing.JButton();
        addRangeButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        clearAllButton = new javax.swing.JButton();
        OKButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Fitting Ranges Manager");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Fitting Ranges"));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Start Point", "End Point", "Unit"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
        );

        addRangeFromPlotButton.setText("Add from plot");
        addRangeFromPlotButton.setToolTipText("Add a fitting range by clicking on the start and end points on the plot");
        addRangeFromPlotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRangeFromPlotButtonActionPerformed(evt);
            }
        });

        addRangeButton.setText("Add range");
        addRangeButton.setToolTipText("Add a fitting range");
        addRangeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRangeButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.setToolTipText("Remove selected fitting ranges");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        clearAllButton.setText("Clear all");
        clearAllButton.setToolTipText("Remove all fitting ranges");
        clearAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearAllButtonActionPerformed(evt);
            }
        });

        OKButton.setText("OK");
        OKButton.setMaximumSize(new java.awt.Dimension(63, 27));
        OKButton.setPreferredSize(new java.awt.Dimension(63, 27));
        OKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKButtonActionPerformed(evt);
            }
        });

        jButton1.setText("Refresh");
        jButton1.setToolTipText("Refresh the table");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(addRangeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(addRangeFromPlotButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(removeButton, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                            .addComponent(clearAllButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(82, 82, 82)
                        .addComponent(OKButton, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 133, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addRangeButton)
                    .addComponent(removeButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clearAllButton)
                    .addComponent(addRangeFromPlotButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OKButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_OKButtonActionPerformed

    private void addRangeFromPlotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRangeFromPlotButtonActionPerformed
         // if there are no SEDs, don't set the fitting rangesss
        if (this.controller.getSedModel() == null) {
            return;
        }
        
        // if the PlotterView isn't up, ask the user to open it
        if (listener == null || listener.getPlotterView() == null || !listener.getPlotterView().isVisible()) {
            String message = "The Visualizer must be open before selecting a fitting range.";
            NarrowOptionPane.showMessageDialog(this, message, "Fitting Tool", NarrowOptionPane.WARNING_MESSAGE);
            return;
        }
        listener.setPickingRanges(true);
    }//GEN-LAST:event_addRangeFromPlotButtonActionPerformed

    private void addRangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRangeButtonActionPerformed

        //
        // Make the panel for setting start, end, and units for fitting range
        //
        JPanel panel = new JPanel(new GridLayout(0, 1));
        
        // start point
        JLabel startLabel = new JLabel("Start point", JLabel.TRAILING);
        panel.add(startLabel);
        JTextField startPoint = new JTextField();
        panel.add(startPoint);
        
        // end point
        JLabel endLabel = new JLabel("End point", JLabel.TRAILING);
        panel.add(endLabel);
        JTextField endPoint = new JTextField();
        panel.add(endPoint);

        // xunits combo box
        JLabel unitsLabel = new JLabel("Unit", JLabel.TRAILING);
        panel.add(unitsLabel);
        JComboBox units = new JComboBox();
        units.setModel(new DefaultComboBoxModel(loadEnum(XUnit.class)));
        units.setSelectedIndex(0);
        panel.add(units);
        //
        // end making panel
        //
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Set Fitting Range", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE
        );
        
        // add fitting range to FitConfiguration
        if (result == JOptionPane.OK_OPTION) {
            
            String[] range = {startPoint.getText(), endPoint.getText()};
            
            // check for valid range values
            if (!checkValidRanges(range)) {
                NarrowOptionPane.showMessageDialog(null, "Invalid fitting range values", "ERROR", NarrowOptionPane.ERROR_MESSAGE);
                return;
            }
            
            FittingRange frange;
            try {
            String unit = units.getSelectedItem().toString();
            frange = new FittingRange(
                    Double.parseDouble(range[0]),
                    Double.parseDouble(range[1]),
                    XUnit.getFromUnitString(XUnit.valueOf(unit).getString())
            );
                
                controller.getFit().addFittingRange(frange);
            } catch (SedException ex) {
                // this shouldn't happen...
                Logger.getLogger(FittingRangesFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // update table
            updateTable();
        }
    }//GEN-LAST:event_addRangeButtonActionPerformed

    private void clearAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearAllButtonActionPerformed
        controller.getFit().clearFittingRanges();
        updateTable();
    }//GEN-LAST:event_clearAllButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // remove selected fitting rangesss
        for (int i : this.jTable1.getSelectedRows()) {
            controller.getFit().removeFittingRange(i);
        }
        updateTable();
    }//GEN-LAST:event_removeButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // refresh the table
        updateTable();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton OKButton;
    private javax.swing.JButton addRangeButton;
    private javax.swing.JButton addRangeFromPlotButton;
    private javax.swing.JButton clearAllButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables

    public void updateTable() {
        jTable1.setModel(new FittingRangeTableModel(controller.getFit().getFittingRanges()));
    }
    
    /**
     * Table model for the fitting ranges
     */
    public class FittingRangeTableModel extends AbstractTableModel {
        String[][] data = new String[][]{};
        String[] columnNames = new String[]{"Start Point", "End Point", "Unit"};

        public FittingRangeTableModel(List<FittingRange> ranges) {

            data = new String[ranges.size()][3];
            
            // populate table with current rangesss
            for (int i=0; i<ranges.size(); i++) {
                data[i][0] = String.valueOf(ranges.get(i).getStartPoint());
                data[i][1] = String.valueOf(ranges.get(i).getEndPoint());
                data[i][2] = ranges.get(i).getXUnit().getString();
            }
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            return data[row][column];
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false; // table is not editable
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            this.data[row][col] = (String) value;
        }

    }
    
    private static boolean isNumeric(String str) {
        try {
            new BigDecimal(str);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    /**
     * Checks that the input array of strings are valid numeric, positive values.
     * @param range - array of strings to check
     */
    private boolean checkValidRanges(String[] range) {
        boolean aok = true;
        for (String range1 : range) {
            if (range1 == null) {
                aok = false;
            } else if (range1.isEmpty()) {
                aok = false;
            } else if (!isNumeric(range1)) {
                aok = false;
            } else if (Double.parseDouble(range1) < 0) {
                aok = false;
            } else {
            }
        }
        return aok;
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
            Logger.getLogger(FittingRangesFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
