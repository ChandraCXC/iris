package cfa.vo.sed.science;

import cfa.vo.iris.gui.widgets.ModelViewerPanel;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sherpa.IFitConfiguration;
import cfa.vo.sherpa.models.Model;

import javax.swing.*;

public class ModelViewerFrame extends JInternalFrame {
    public ModelViewerFrame(ExtSed sed) {
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setResizable(true);
        setTitle("Model Viewer");
        ModelViewerPanel p = new ModelViewerPanel();
        p.setSed(sed);
        setContentPane(p);
        pack();
    }
}
