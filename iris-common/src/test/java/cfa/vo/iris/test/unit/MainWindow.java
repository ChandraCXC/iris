package cfa.vo.iris.test.unit;

import javax.swing.*;

public class MainWindow extends JFrame implements IMainWindow {
    private JDesktopPane desktop;

    public MainWindow() {
        super("Test App");
        desktop = new JDesktopPane();
    }

    public JDesktopPane getDesktop() {
        return desktop;
    }

    public JFrame getMainFrame() {
        return this;
    }
}
