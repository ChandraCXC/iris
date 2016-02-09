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
package cfa.vo.iris.visualizer.stil.preferences;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SpinnerListModel;

/**
 *
 * @author jbudynk
 */
public class SegmentPreferencesPanel extends JPanel {

    private Color color;
    private List<ImageIcon> symbols;
    private List<ImageIcon> sizes;
    private List<ImageIcon> errorbars;
    
    /**
     * Creates new form SegmentPreferencesPanel2
     */
    public SegmentPreferencesPanel() {
        color = Color.RED;
        
        // create imageicon lists for jspinner models
        symbols = new ArrayList<>();
        for (MarkType symbol : MarkType.values()) {
//            symbols.add(new ImageIcon(this.getClass().getResource(symbol.toString()+".gif")));
            symbols.add(new ImageIcon(this.getClass().getResource("/tool_tiny.png").getFile()));
        }
        sizes = new ArrayList<>();
        for (MarkSize size : MarkSize.values()) {
//            sizes.add(new ImageIcon(this.getClass().getResource("mark-size"+size.toString()+".gif")));
            sizes.add(new ImageIcon(this.getClass().getResource("/tool_tiny.png").getFile()));
        }
        errorbars = new ArrayList<>();
        for (ErrorBarType errorbar : ErrorBarType.values()) {
//            errorbars.add(new ImageIcon(this.getClass().getResource(errorbar.toString()+".gif")));
            errorbars.add(new ImageIcon(this.getClass().getResource("/tool_tiny.png").getFile()));
        }
        
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new ColorChooserButton(color);
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();

        jLabel1.setText("Color:");

        jLabel2.setText("Symbol:");

        jLabel3.setText("Size:");

        jLabel4.setText("Error bar:");

        jButton1.setToolTipText("Plot symbol colors");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(symbols.toArray()));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(sizes.toArray()));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(errorbars.toArray()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addGap(70, 70, 70))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(49, 49, 49)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1))
                        .addGap(41, 41, 41)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(119, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    private String[] loadEnum(Class<? extends Enum> clazz) {
        try {
            Enum[] l;
            l = (Enum[]) clazz.getMethod("values").invoke(null);
            String[] s = new String[l.length];
            for (int i = 0; i < l.length; i++) {
                MarkType u = (MarkType) l[i];
                s[i] = u.toString();
            }
            return s;
        } catch (Exception ex) {
            Logger.getLogger(SegmentPreferencesPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private List<ImageIcon> createIconList(Enum en) {
        List<ImageIcon> errorbars = new ArrayList<>();
        for (ErrorBarType errorbar : ErrorBarType.values()) {
            try {
//                errorbars.add(new ImageIcon(this.getClass().getResource(errorbar.toString()+".gif")));
                errorbars.add(new ImageIcon(this.getClass().getResource("/iris_button_tiny.png")));
            } catch (Exception ex) {
                Logger.getLogger(SegmentPreferencesPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return errorbars;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables
}
