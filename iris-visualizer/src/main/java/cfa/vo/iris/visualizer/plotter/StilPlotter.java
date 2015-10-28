package cfa.vo.iris.visualizer.plotter;

import javax.swing.JPanel;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import java.awt.GridLayout;

public class StilPlotter extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private IrisApplication app;
    private IWorkspace ws;

    public StilPlotter(String title, IrisApplication app, IWorkspace ws) {
        this.ws = ws;
        this.app = app;
        setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        setBackground(Color.WHITE);
        setLayout(new GridLayout(1, 0, 0, 0));
    }
}
