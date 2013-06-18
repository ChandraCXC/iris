/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PhotometryFilterBrowsePanel.java
 *
 * Created on Mar 29, 2012, 5:13:27 PM
 */
package cfa.vo.sed.gui;

import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.utils.IPredicate;
import cfa.vo.sed.builder.photfilters.FilterSelectionListener;
import cfa.vo.sed.builder.photfilters.PhotometryFilter;
import cfa.vo.sed.builder.photfilters.PhotometryFilterTreeModel;
import cfa.vo.sed.builder.photfilters.PhotometryFiltersList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;

/**
 *
 * @author olaurino
 */
public class PhotometryFilterBrowsePanel extends javax.swing.JPanel {

    private PhotometryFilterTreeModel model = new PhotometryFilterTreeModel(new PhotometryFiltersList());
    private List<PhotometryFilter> filterList = new ArrayList();
    private boolean multipleSelection = false;

    /**
     * Creates new form PhotometryFilterBrowsePanel
     */
    public PhotometryFilterBrowsePanel(boolean multipleSelection) throws Exception {
        initComponents();

        this.multipleSelection = multipleSelection;

        if (multipleSelection) {
            jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        } else {
            jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        }

        jTree1.setPreferredSize(null);
        jTree1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreePath selPath = jTree1.getPathForLocation(e.getX(), e.getY());
                if (selPath != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                    if (node.isLeaf()) {
                        PhotometryFilter f = (PhotometryFilter) node.getUserObject();
                        descriptionText.setText(f.getDescription());
                        bandText.setText(f.getBand());
                        instrText.setText(f.getInstrument());
                        facilityText.setText(f.getFacility());
                        unit.setText(f.getUnit());
                        minText.setText(f.getWlmin().toString());
                        maxText.setText(f.getWlmax().toString());
                        meanText.setText(f.getWlmean().toString());
                        effText.setText(f.getWleff().toString());
                    }
                }

            }
        });
    }

    public void fireEvents() {
        filterList = new ArrayList();
        TreePath[] paths = jTree1.getSelectionModel().getSelectionPaths();

        if (paths != null) {
            for (TreePath path : paths) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (n.isLeaf()) {
                    PhotometryFilter f2 = (PhotometryFilter) n.getUserObject();
                    filterList.add(f2);
                }
            }
        }
        for (FilterSelectionListener listener : listeners) {
            for (PhotometryFilter filter : filterList) {
                listener.process(filter, SedCommand.SELECTED);
            }
        }
    }
    private List<FilterSelectionListener> listeners = new ArrayList();

    public void addFilterSelectionListener(FilterSelectionListener listener) {
        listeners.add(listener);
    }

    public void removeFilterSelectionListener(FilterSelectionListener listener) {
        listeners.remove(listener);
    }
    private StringPredicate predicate = new StringPredicate("");

    public List<PhotometryFilter> filter(String string) {
        predicate.setString(string);
        return filter(predicate);
    }

    public List<PhotometryFilter> filter(IPredicate<PhotometryFilter> predicate) {
        return filter(model.getList(), predicate);
    }

    public List<PhotometryFilter> filter(List<PhotometryFilter> list, IPredicate<PhotometryFilter> predicate) {
        List<PhotometryFilter> sub = new ArrayList();

        for (PhotometryFilter element : list) {
            if (predicate.apply(element)) {
                sub.add(element);
            }
        }

        return sub;
    }

    private class StringPredicate implements IPredicate<PhotometryFilter> {

        private String string;

        public StringPredicate(String string) {
            this.string = string;
        }

        public void setString(String string) {
            this.string = string;
        }

        @Override
        public boolean apply(PhotometryFilter object) {
            boolean resp = false;
            if (object.getBand() != null) {
                resp = resp || object.getBand().toLowerCase().contains(string.toLowerCase());
            }
            if (object.getDescription() != null) {
                resp = resp || object.getDescription().toLowerCase().contains(string.toLowerCase());
            }
            if (object.getFacility() != null) {
                resp = resp || object.getFacility().toLowerCase().contains(string.toLowerCase());
            }
            if (object.getInstrument() != null) {
                resp = resp || object.getInstrument().toLowerCase().contains(string.toLowerCase());
            }

            return resp;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        descriptionText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        bandText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        instrText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        facilityText = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        minText = new javax.swing.JTextField();
        unit = new javax.swing.JLabel();
        maxText = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        meanText = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        effText = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        searchString = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(210);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setViewportView(jTree1);

        jTree1.setModel(model);
        jTree1.setAutoscrolls(true);
        jTree1.setMaximumSize(new java.awt.Dimension(32767, 32767));
        jTree1.setMinimumSize(new java.awt.Dimension(220, 100));
        jTree1.setName("jTree1"); // NOI18N
        jTree1.setPreferredSize(new java.awt.Dimension(160, 100));
        jTree1.setSize(new java.awt.Dimension(180, 291));
        jScrollPane1.setViewportView(jTree1);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(278, 438));

        descriptionText.setEditable(false);
        descriptionText.setName("descriptionText"); // NOI18N

        jLabel1.setText("Band:");
        jLabel1.setName("jLabel1"); // NOI18N

        bandText.setEditable(false);
        bandText.setName("bandText"); // NOI18N

        jLabel2.setText("Instrument:");
        jLabel2.setName("jLabel2"); // NOI18N

        instrText.setEditable(false);
        instrText.setName("instrText"); // NOI18N

        jLabel3.setText("Facility:");
        jLabel3.setName("jLabel3"); // NOI18N

        facilityText.setEditable(false);
        facilityText.setName("facilityText"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Wavelength"));
        jPanel2.setName("jPanel2"); // NOI18N

        jLabel4.setText("Min:");
        jLabel4.setName("jLabel4"); // NOI18N

        minText.setEditable(false);
        minText.setName("minText"); // NOI18N

        unit.setName("unit"); // NOI18N

        maxText.setEditable(false);
        maxText.setName("maxText"); // NOI18N

        jLabel7.setText("Max:");
        jLabel7.setName("jLabel7"); // NOI18N

        meanText.setEditable(false);
        meanText.setName("meanText"); // NOI18N

        jLabel9.setText("Mean:");
        jLabel9.setName("jLabel9"); // NOI18N

        effText.setEditable(false);
        effText.setName("effText"); // NOI18N

        jLabel11.setText("Eff:");
        jLabel11.setName("jLabel11"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel4)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel7)
                            .add(jLabel9)
                            .add(jLabel11))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(effText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(meanText)
                                    .add(maxText)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, minText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(unit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel4)
                            .add(minText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel7)
                            .add(maxText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel9)
                            .add(meanText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel11)
                            .add(effText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(55, 55, 55)
                        .add(unit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel5.setText("Description:");
        jLabel5.setName("jLabel5"); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Search"));
        jPanel3.setName("jPanel3"); // NOI18N

        jLabel6.setText("By String:");
        jLabel6.setName("jLabel6"); // NOI18N

        searchString.setName("searchString"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(PhotometryFilterBrowsePanel.class, this);
        jButton1.setAction(actionMap.get("clear")); // NOI18N
        jButton1.setText("Clear");
        jButton1.setName("jButton1"); // NOI18N

        jButton2.setAction(actionMap.get("search")); // NOI18N
        jButton2.setText("Search");
        jButton2.setName("jButton2"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                        .add(jLabel6)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(searchString, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jButton1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jButton2)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(searchString, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton2)
                    .add(jButton1)))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(instrText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(facilityText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel5))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, descriptionText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                            .add(bandText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(descriptionText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(bandText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(instrText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(facilityText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane1.setRightComponent(jPanel1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    @Action(block = Task.BlockingScope.ACTION)
    public Task search() {
        return new SearchTask(org.jdesktop.application.Application.getInstance());
    }

    private class SearchTask extends org.jdesktop.application.Task<Object, Void> {

        private String string;

        SearchTask(org.jdesktop.application.Application app) {
            super(app);
            this.string = searchString.getText();
        }

        @Override
        protected Object doInBackground() {
            return filter(string);
        }

        @Override
        protected void succeeded(Object result) {

            jTree1.setModel(new PhotometryFilterTreeModel((List<PhotometryFilter>) result));
            if (multipleSelection) {
                jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            } else {
                jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            }
            jTree1.updateUI();
        }
    }

    @Action
    public void clear() {
        jTree1.setModel(model);
        searchString.setText("");
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField bandText;
    private javax.swing.JTextField descriptionText;
    private javax.swing.JTextField effText;
    private javax.swing.JTextField facilityText;
    private javax.swing.JTextField instrText;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JTextField maxText;
    private javax.swing.JTextField meanText;
    private javax.swing.JTextField minText;
    private javax.swing.JTextField searchString;
    private javax.swing.JLabel unit;
    // End of variables declaration//GEN-END:variables
}
