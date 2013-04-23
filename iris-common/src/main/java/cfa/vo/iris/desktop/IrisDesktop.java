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
 * SedImporterMainView.java
 *
 * Created on May 6, 2011, 3:53:29 PM
 */
package cfa.vo.iris.desktop;

import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.interop.SAMPConnectionListener;
import cfa.vo.iris.AbstractDesktopItem;
import cfa.vo.iris.IMenuItem;
import cfa.vo.iris.AbstractIrisApplication;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.events.PluginJarEvent;
import cfa.vo.iris.events.PluginListener;
import cfa.vo.iris.sdk.IrisPlugin;
import cfa.vo.iris.sdk.PluginJar;
import cfa.vo.iris.sdk.PluginManager;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import org.astrogrid.samp.client.MessageHandler;
import org.jdesktop.application.Action;

/**
 *
 * @author olaurino
 */
public class IrisDesktop extends JFrame implements PluginListener {

    private List<DesktopButton> buttons = new ArrayList();
    private List<IrisComponent> components = new ArrayList();
    private JDialog aboutBox;
    AbstractIrisApplication app;
    IWorkspace ws;
    DesktopButton helpButton;

    public void setWorkspace(IWorkspace ws) {
        this.ws = ws;
    }

    /** Creates new form SedImporterMainView */
    public IrisDesktop(final AbstractIrisApplication app) throws Exception {

        PluginJarEvent.getInstance().add(this);

        initComponents();

        this.app = app;

        aboutBox = app.getAboutBox();

        aboutLabel.setText("About " + app.getName());

        components.addAll(app.getComponents());

        setTitle(app.getName());

        desktopPane.setDesktopManager(new BoundDesktopManager(desktopPane));
        desktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);


        if (app.isSampEnabled()) {
            for (IrisComponent component : components) {
                for (MessageHandler handler : component.getSampHandlers()) {
                    app.addMessageHandler(handler);
                }
            }

            app.addConnectionListener(new SampStatusListener());

        }

        sampIcon.setVisible(app.isSampEnabled());


        jLabel2.setIcon(new ImageIcon(app.getDesktopIcon()));

        this.setLocationRelativeTo(null);
        Toolkit tk = Toolkit.getDefaultToolkit();
        int xSize = (int) ((int) tk.getScreenSize().getWidth() * 0.8);
        int ySize = (int) ((int) tk.getScreenSize().getHeight() * 0.8);

        this.setSize(xSize, ySize);

        int[] bo = getVaoBounds();
        jLabel2.setBounds(bo[0], bo[1], bo[2], bo[3]);

        desktopPane.setLayer(jLabel2, -1);


        AbstractDesktopItem help = new AbstractDesktopItem("Help", "Help on " + app.getName(), "/help_contextual.png", "/help_contextual_tiny.png") {

            @Override
            public void onClick() {
                showLink(app.getHelpURL());
            }
        };

        IrisMenuItem helpItem = new IrisMenuItem(help);

        helpMenu.add(helpItem);
        helpButton = new DesktopButton(help);
        buttons.add(helpButton);
        desktopPane.add(helpButton);

        paintButtons();

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        if (app.MAC_OS_X) {
            try {
                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods
                OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("exit", (Class[]) null));
                OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[]) null));
//                OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[])null));
//                OSXAdapter.setFileHandler(this, getClass().getDeclaredMethod("loadImageFile", new Class[] { String.class }));
            } catch (Exception e) {
                System.err.println("Error while loading the OSXAdapter:");
                e.printStackTrace();
            }
        }


    }
//    private List<JMenu> menus = new ArrayList();
    private List<IrisMenuItem> fileMenus = new ArrayList();

    public void reset(List<IrisComponent> components) {
        
        if(!components.contains(manager))
            components.add(manager);

        for (DesktopButton b : buttons) {
            desktopPane.remove(b);
        }

        toolsMenu.removeAll();

        for (IrisMenuItem i : fileMenus) {
            fileMenu.remove(i);
        }

        fileMenus = new ArrayList();

        buttons = new ArrayList();

        int c = 0;
        for (IrisComponent component : components) {
            int cf = 0;
            JMenu cMenu = null;
            for (IMenuItem item : component.getMenus()) {
                IrisMenuItem i = new IrisMenuItem(item);


                if (i.getMenu().equals("File")) {
                    fileMenu.add(i, c + cf++);
                    fileMenus.add(i);
                } else {
                    if (cMenu == null) {
                        cMenu = new JMenu(component.getName());
                    }
                    cMenu.add(i);
                }

                if (item.isOnDesktop()) {
                    DesktopButton b = new DesktopButton(item);
                    buttons.add(b);
                    desktopPane.add(b, javax.swing.JLayeredPane.DEFAULT_LAYER);
                }

            }
            if (cf > 0) {
                fileMenu.add(new JSeparator(), (c++) + (cf++));
            }
            if (cMenu != null) {
                toolsMenu.add(cMenu);
            }
            paintButtons();

        }
        
        buttons.add(helpButton);
        desktopPane.add(helpButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        paintButtons();

        repaint();
    }

    @Override
    public void repaint(int i1, int i2, int i3, int i4) {
        super.repaint(i1, i2, i3, i4);
        int[] b = getVaoBounds();
        jLabel2.setBounds(b[0], b[1], b[2], b[3]);
        paintButtons();
        desktopPane.setLayer(jLabel2, -1);
    }

    private int[] getVaoBounds() {
        int xb = (this.getWidth() - jLabel2.getWidth()) / 2;
        int yb = (this.getHeight() - jLabel2.getHeight()) / 2;
        int xf = jLabel2.getWidth();
        int yf = jLabel2.getHeight();
        return new int[]{xb, yb, xf, yf};
    }

    private void paintButtons() {
        int width = this.getWidth();
        int xl = 175;
        int yl = 175;
        int baseX = 20 - xl;
        int baseY = 20;
        for (DesktopButton b : buttons) {
            baseX = baseX + xl;
            if (width > xl + 20) {
                if (baseX + xl > width) {
                    baseY = baseY + yl;
                    baseX = 20;
                }
            } else {
                baseY = baseY + 195;
            }


            b.setBounds(baseX, baseY, xl, yl);

        }


        sampIcon.setBounds(20, this.getHeight() - sampIcon.getHeight() - 90, sampIcon.getWidth(), sampIcon.getHeight());
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

        desktopPane = new javax.swing.JDesktopPane();
        sampIcon = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutLabel = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        desktopPane.setBackground(new java.awt.Color(0, 102, 102));
        desktopPane.setForeground(new java.awt.Color(255, 255, 255));
        desktopPane.setAutoscrolls(true);
        desktopPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        desktopPane.setName("sedDesktop"); // NOI18N
        desktopPane.setPreferredSize(new java.awt.Dimension(1024, 768));
        desktopPane.setSize(new java.awt.Dimension(1036, 693));

        sampIcon.setForeground(new java.awt.Color(255, 255, 255));
        sampIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sampIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/connect_no.png")));
        sampIcon.setText("SAMP status: disconnected");
        sampIcon.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        sampIcon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sampIcon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        sampIcon.setBounds(50, 490, 210, 120);
        desktopPane.add(sampIcon, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setBounds(350, 250, 330, 240);
        desktopPane.add(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jToolBar1.setRollover(true);

        fileMenu.setText("File");

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(IrisDesktop.class, this);
        exitMenuItem.setAction(actionMap.get("exit")); // NOI18N
        exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/process_stop.png")));
        fileMenu.add(exitMenuItem);

        jMenuBar1.add(fileMenu);

        toolsMenu.setText("Tools");
        jMenuBar1.add(toolsMenu);

        jMenu1.setText("Interop");

        jCheckBoxMenuItem1.setText("Run Hub Automatically");
        jCheckBoxMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/redo.png")));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sampAutoHub}"), jCheckBoxMenuItem1, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        jMenu1.add(jCheckBoxMenuItem1);

        jMenuBar1.add(jMenu1);

        helpMenu.setText("Help");
        helpMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAbout(evt);
            }
        });

        aboutLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iris_button_tiny.png")));
        aboutLabel.setText("About SedImporter");
        aboutLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAbout(evt);
            }
        });
        helpMenu.add(aboutLabel);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(desktopPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1036, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, desktopPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void showLink(URL url) {
        try {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            desktop.browse(url.toURI());
        } catch (Exception ex) {
            try {
                HelpBrowser browser = new HelpBrowser(url);
                desktopPane.add(browser);
                browser.show();
            } catch (Exception ex2) {
                JEditorPane jta = new JEditorPane();
                jta.setBackground(NarrowOptionPane.getRootFrame().getBackground());
                jta.setContentType("text/html");
                jta.setText("<html><body>SedImporter couldn't open your default browser. You can use this link directly: <br/>"
                        + url + "</body></html>");
                jta.setEditable(false);

                NarrowOptionPane.showMessageDialog(null,
                        jta,
                        "Desktop communication error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAbout(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAbout
        aboutBox.setLocation((int) desktopPane.getWidth() / 2 - 125, (int) desktopPane.getHeight() / 2 - 110);
        aboutBox.setVisible(true);
    }//GEN-LAST:event_showAbout

    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }
//    /**
//    * @param args the command line arguments
//    */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                IrisDesktop.this.setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutLabel;
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel sampIcon;
    private javax.swing.JMenu toolsMenu;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    void setComponents(List<IrisComponent> components) {
        this.components = components;
    }
    // End of variables declaration

//    private static class MainViewHolder {
//        private static final IrisDesktop INSTANCE = new IrisDesktop();
//    }
    public boolean quit() {
        int confirm = NarrowOptionPane.showOptionDialog(this,
                "Do you really want to close " + getTitle() + "?",
                "Close Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, null, null);
        return (confirm == NarrowOptionPane.YES_OPTION);
    }

    public void about() {
        showAbout(null);
    }
    private boolean sampConnected = false;

    /**
     * Get the value of sampConnected
     *
     * @return the value of sampConnected
     */
    public boolean isSampConnected() {
        return sampConnected;
    }
    private static final Icon sampNo = new ImageIcon(IrisDesktop.class.getResource("/connect_no.png"));
    private static final Icon sampYes = new ImageIcon(IrisDesktop.class.getResource("/connect_established.png"));

    /**
     * Set the value of sampConnected
     *
     * @param sampConnected new value of sampConnected
     */
    public void setSampConnected(boolean sampConnected) {
        if (sampConnected != this.sampConnected) {
            this.sampConnected = sampConnected;
            sampIcon.setIcon(sampConnected ? sampYes : sampNo);
            sampIcon.setText(sampConnected ? "SAMP status: connected" : "SAMP status: disconnected");
            sampIcon.setForeground(sampConnected ? new Color(0, 200, 0) : Color.WHITE);
        }
    }
    private boolean sampAutoHub = true;
    public static final String PROP_SAMPAUTOHUB = "sampAutoHub";

    /**
     * Get the value of sampAutoHub
     *
     * @return the value of sampAutoHub
     */
    public boolean isSampAutoHub() {
        return sampAutoHub;
    }

    /**
     * Set the value of sampAutoHub
     *
     * @param sampAutoHub new value of sampAutoHub
     */
    public void setSampAutoHub(boolean sampAutoHub) {
        boolean oldSampAutoHub = this.sampAutoHub;
        this.sampAutoHub = sampAutoHub;
        AbstractIrisApplication.setAutoRunHub(sampAutoHub);
        firePropertyChange(PROP_SAMPAUTOHUB, oldSampAutoHub, sampAutoHub);
    }

    @Override
    public void process(PluginJar source, SedCommand payload) {

        List<IrisPlugin> plugins = source.getPlugins();

        for (IrisPlugin plugin : plugins) {
            List<IrisComponent> comps = plugin.getComponents();
            if (payload.equals(SedCommand.ADDED)) {
                components.addAll(comps);
                for (IrisComponent c : comps) {
                    c.init(app, ws);
                }
            }
            if (payload.equals(SedCommand.REMOVED)) {
                components.removeAll(comps);
                for (IrisComponent c : comps) {
                    c.shutdown();
                }
            }
            reset(components);
        }

    }
    
    private PluginManager manager;

    public void setPluginManager(PluginManager manager) {
        this.manager = manager;
    }

    private class SampStatusListener implements SAMPConnectionListener {

        @Override
        public void run(boolean status) {
            setSampConnected(status);
        }
    }

    @Action
    public boolean exit() {
        if (quit()) {
            app.exitApp();
            return true;
        }
        return false;
    }
}
