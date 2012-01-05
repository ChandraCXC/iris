/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SedBuilderMainView.java
 *
 * Created on Dec 23, 2011, 3:05:23 PM
 */

package cfa.vo.sed.gui;

import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.iris.events.SegmentListener;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.gui.widgets.SedList;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.sed.SedlibSedManager.ExtSed;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sedlib.DoubleParam;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import java.awt.Component;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.astrogrid.samp.client.SampException;
import org.jdesktop.application.Action;

/**
 *
 * @author olaurino
 */
public class SedBuilderMainView extends JInternalFrame {

    private SedlibSedManager manager;

    private JFrame rootFrame;

    private LoadSegmentFrame loadFrame;

    private ExtSed sed;
    public static final String PROP_SED = "sed";

    /** Creates new form SedBuilderMainView */
    public SedBuilderMainView(final SedlibSedManager manager, JFrame rootFrame) {
        initComponents();

        this.manager = manager;

        loadFrame = new LoadSegmentFrame(manager);
        SedBuilder.getWorkspace().addFrame(loadFrame);

        this.rootFrame = rootFrame;

        sedPanel.setViewportView(new SedList(manager));
        setSed(manager.getSelected());

        SedEvent.getInstance().add(new SedListener() {

            @Override
            public void process(ExtSed source, SedCommand payload) {
                if(payload!=SedCommand.REMOVED)
                    setSed(source);
                else
                    setSed(null);
            }
        });

        SegmentEvent.getInstance().add(new SegmentListener() {

            @Override
            public void process(Segment source, SegmentPayload payload) {
                ExtSed s = payload.getSed();
                setSed(s);
            }
        });

        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if(!lse.getValueIsAdjusting()) {
                    int[] selected = jTable1.getSelectedRows();
                    List<Segment> selSegs = new ArrayList();
                    for(int i=0; i<selected.length; i++) {
                        selSegs.add(sed.getSegment(selected[i]));
                    }
                    setSelectedSegments(selSegs);
                    setSegmentSelected(!selSegs.isEmpty());
                }
            }
        });
        
    }

    private void setSed(ExtSed sed) {
        this.sed = sed;
        boolean n = sed==null;
        sedName.setText(n? "" : sed.getId());
        setIsSed(!n);

        loadFrame.setSed(sed);

        List<Segment> list = new ArrayList();

        if(sed!=null)
            for(int i=0; i<sed.getNumberOfSegments(); i++)
                list.add(sed.getSegment(i));

        setSegments(list);
        
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

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        sedName = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel3 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jLabel4 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        sedPanel = new javax.swing.JScrollPane();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("SED Builder");

        jPanel2.setName("jPanel2"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Selected SED"));
        jPanel1.setName("jPanel1"); // NOI18N

        jLabel2.setText("ID:");
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(SedBuilderMainView.class, this);
        sedName.setAction(actionMap.get("changeName")); // NOI18N
        sedName.setName("sedName"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sed.id}"), sedName, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${isSed}"), sedName, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton1.setAction(actionMap.get("changeName")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        jLabel3.setText("SED: ");
        jLabel3.setName("jLabel3"); // NOI18N
        jToolBar2.add(jLabel3);

        jButton8.setAction(actionMap.get("newSed")); // NOI18N
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setName("jButton8"); // NOI18N
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jButton8);

        jButton2.setAction(actionMap.get("removeSed")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${isSed}"), jButton2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(jButton2);

        jButton9.setAction(actionMap.get("saveSed")); // NOI18N
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setName("jButton9"); // NOI18N
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${isSed}"), jButton9, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(jButton9);

        jButton11.setAction(actionMap.get("duplicateSed")); // NOI18N
        jButton11.setFocusable(false);
        jButton11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton11.setName("jButton11"); // NOI18N
        jButton11.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${isSed}"), jButton11, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(jButton11);

        jButton10.setAction(actionMap.get("broadcast")); // NOI18N
        jButton10.setFocusable(false);
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton10.setName("jButton10"); // NOI18N
        jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${isSed}"), jButton10, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(jButton10);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar2.add(jSeparator1);

        jLabel4.setText("Segment(s): ");
        jLabel4.setName("jLabel4"); // NOI18N
        jToolBar2.add(jLabel4);

        jButton3.setAction(actionMap.get("newSegment")); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setName("jButton3"); // NOI18N
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${isSed}"), jButton3, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(jButton3);

        jButton4.setAction(actionMap.get("editSegment")); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setName("jButton4"); // NOI18N
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${segmentSelected}"), jButton4, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(jButton4);

        jButton5.setAction(actionMap.get("removeSegment")); // NOI18N
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setName("jButton5"); // NOI18N
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${segmentSelected}"), jButton5, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(jButton5);

        jButton6.setAction(actionMap.get("broadcastSegments")); // NOI18N
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setName("jButton6"); // NOI18N
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${segmentSelected}"), jButton6, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(jButton6);

        jButton7.setAction(actionMap.get("saveSegments")); // NOI18N
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setName("jButton7"); // NOI18N
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${segmentSelected}"), jButton7, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(jButton7);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setName("jTable1"); // NOI18N
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${segments}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jTable1);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${target.pos.value}"));
        columnBinding.setColumnName("Coordinates");
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${curation.publisher.value}"));
        columnBinding.setColumnName("Publisher");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${data.length}"));
        columnBinding.setColumnName("#Points");
        columnBinding.setColumnClass(Integer.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTable1.getColumnModel().getColumn(0).setCellRenderer(new PosRenderer());

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sedName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1))
            .add(jToolBar2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(sedName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jToolBar2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE))
        );

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setName("jSeparator2"); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Open SEDs"));
        jPanel3.setName("jPanel3"); // NOI18N

        sedPanel.setBorder(null);
        sedPanel.setName("sedPanel"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, sedPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sedPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(25, 25, 25)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void changeName() {
        if(!sedName.getText().isEmpty())
            manager.rename(sed, sedName.getText());
    }

    @Action
    public void newSed() {
        int c = 0;
        while (manager.existsSed("Sed" + c)) {
            c++;
        }

        ExtSed newSed = manager.newSed("Sed" + c);
    }

    @Action
    public void removeSed() {
        if(sed.getNumberOfSegments()==0) {
            manager.remove(sed.getId());
            setSed(null);
        } else {
            int ans = NarrowOptionPane.showConfirmDialog(rootFrame,
                "Are you sure you want to delete the selected SED?");
            if(ans==NarrowOptionPane.YES_OPTION) {
                manager.remove(sed.getId());
                setSed(null);
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JTextField sedName;
    private javax.swing.JScrollPane sedPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private boolean isSed = false;
    public static final String PROP_ISSED = "isSed";

    /**
     * Get the value of isSed
     *
     * @return the value of isSed
     */
    public boolean isIsSed() {
        return isSed;
    }

    /**
     * Set the value of isSed
     *
     * @param isSed new value of isSed
     */
    public void setIsSed(boolean isSed) {
        boolean oldIsSed = this.isSed;
        this.isSed = isSed;
        firePropertyChange(PROP_ISSED, oldIsSed, isSed);
    }

    private List<Segment> segments = new ArrayList();
    public static final String PROP_SEGMENTS = "segments";

    /**
     * Get the value of segments
     *
     * @return the value of segments
     */
    public List<Segment> getSegments() {
        return segments;
    }

    /**
     * Set the value of segments
     *
     * @param segments new value of segments
     */
    public void setSegments(List<Segment> segments) {
        List<Segment> oldSegments = this.segments;
        this.segments = segments;
        firePropertyChange(PROP_SEGMENTS, oldSegments, segments);
    }


    @Action
    public void saveSed() {
        SaveSedDialog ssd = new SaveSedDialog(rootFrame, sed);
        ssd.setVisible(true);
    }

    @Action
    public void broadcast() {
        try {
            SedBuilder.getApplication().sendSedMessage(sed, sed.getId());
        } catch (SampException ex) {
            NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                    ex.getMessage(), "Error broadcasting file", NarrowOptionPane.ERROR_MESSAGE);
        }
    }

    private class PosRenderer extends JLabel implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object o, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = new DefaultTableCellRenderer()
                    .getTableCellRendererComponent(jtable, o, isSelected, hasFocus, row, col);

            DoubleParam[] radec = ((DoubleParam[]) o);
            if(radec==null) radec = new DoubleParam[]{null, null};

            String raS, decS;
            if(radec[0]==null)
                raS = "-";
            else
                raS = Double.valueOf(radec[0].getValue()).isNaN() ? "-" : roundToSignificantFigures(Double.valueOf(radec[0].getValue()), 5).toString();
            if(radec[1]==null)
                decS = "-";
            else
                decS = Double.valueOf(radec[1].getValue()).isNaN() ? "-" : roundToSignificantFigures(Double.valueOf(radec[1].getValue()), 5).toString();
            String content = raS+", "+decS;
            setText(content);

            this.setBackground(c.getBackground());
            this.setForeground(c.getForeground());

            this.setOpaque(true);
            
            return this;
        }

        private Double roundToSignificantFigures(double num, int n) {
            if(num == 0) {
                return 0d;
            }

            final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
            final int power = n - (int) d;

            final double magnitude = Math.pow(10, power);
            final long shifted = Math.round(num*magnitude);
            return shifted/magnitude;
        }



    }

    @Action
    public void duplicateSed() {
        ExtSed s = (ExtSed) sed.clone();
        int c = 0;
        String base = s.getId();
        while (manager.existsSed(base + "Copy" + c)) {
            c++;
        }

        s.setId(base+"Copy"+c);

        manager.add(s);
    }

    @Action
    public void newSegment() throws PropertyVetoException {
        loadFrame.setVisible(true);
        if(loadFrame.isIcon())
            loadFrame.setIcon(false);
    }

    private List<Segment> selectedSegments;

    /**
     * Get the value of selectedSegments
     *
     * @return the value of selectedSegments
     */
    public List<Segment> getSelectedSegments() {
        return selectedSegments;
    }

    /**
     * Set the value of selectedSegments
     *
     * @param selectedSegments new value of selectedSegments
     */
    public void setSelectedSegments(List<Segment> selectedSegments) {
        this.selectedSegments = selectedSegments;
    }

    private boolean segmentSelected;
    public static final String PROP_SEGMENTSELECTED = "segmentSelected";

    /**
     * Get the value of segmentSelected
     *
     * @return the value of segmentSelected
     */
    public boolean isSegmentSelected() {
        return segmentSelected;
    }

    /**
     * Set the value of segmentSelected
     *
     * @param segmentSelected new value of segmentSelected
     */
    public void setSegmentSelected(boolean segmentSelected) {
        boolean oldSegmentSelected = this.segmentSelected;
        this.segmentSelected = segmentSelected;
        firePropertyChange(PROP_SEGMENTSELECTED, oldSegmentSelected, segmentSelected);
    }

    @Action
    public void editSegment() throws Exception {
        boolean warning = false;
        for(Segment s : selectedSegments) {
            Map<Segment, SetupFrame> map = (Map<Segment, SetupFrame>) sed.getAttachment("builder:configuration");
            if(map!=null) {
                if(map.containsKey(s)) {
                    SetupFrame sf = map.get(s);
                    sf.setVisible(true);
                } else
                    warning = true;
            } else
                warning = true;
        }
        if(warning) {
            NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                        "The segment was imported 'as is', for example from NED or from file, so it can't be edited.",
                        "Editing not available for this segment",
                        NarrowOptionPane.WARNING_MESSAGE);
        }
    }

    @Action
    public void removeSegment() {
        int ans = NarrowOptionPane.showConfirmDialog(SedBuilder.getWorkspace().getRootFrame(),
                "Are you sure you want to remove the selected segments from the SED?",
                "Confirm removal",
                NarrowOptionPane.YES_NO_OPTION);
        if(ans==NarrowOptionPane.YES_OPTION) {
            for(Segment s : selectedSegments)
                sed.remove(s);
        }
    }

    @Action
    public void broadcastSegments() {
        try {
            Sed s = new Sed();
            s.addSegment(selectedSegments);
            SedBuilder.getApplication().sendSedMessage(s, sed.getId()+"Selection");
        } catch (SampException ex) {
            Logger.getLogger(SedBuilderMainView.class.getName()).log(Level.SEVERE, null, ex);
            NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                        "A SAMP error occurred. Is a SAMP hub running? Is the SAMP connection working?",
                        "Error broadcasting segments",
                        NarrowOptionPane.ERROR_MESSAGE);
        } catch (SedInconsistentException ex) {//If the segment is already in the SED this exception can't be thrown.
            Logger.getLogger(SedBuilderMainView.class.getName()).log(Level.SEVERE, null, ex);
            NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                        "Segments are physically inconsistent and an SED can't be built for them",
                        "Error broadcasting segments",
                        NarrowOptionPane.ERROR_MESSAGE);
        } catch (SedNoDataException ex) {//If the segment is alreadt in the SED this exception can't be thrown.
            Logger.getLogger(SedBuilderMainView.class.getName()).log(Level.SEVERE, null, ex);
            NarrowOptionPane.showMessageDialog(SedBuilder.getWorkspace().getRootFrame(),
                        "A Segment has no data",
                        "Error broadcasting segments",
                        NarrowOptionPane.ERROR_MESSAGE);
        }

    }

    @Action
    public void saveSegments() {
        try {
            ExtSed s = manager.new ExtSed("");
            s.addSegment(selectedSegments);
            SaveSedDialog ssd = new SaveSedDialog(rootFrame, s);
            ssd.setVisible(true);
        } catch (SedInconsistentException ex) {
            Logger.getLogger(SedBuilderMainView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SedNoDataException ex) {
            Logger.getLogger(SedBuilderMainView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }




}
