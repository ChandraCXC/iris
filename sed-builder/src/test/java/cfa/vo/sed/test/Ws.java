/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.test;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.sed.ISedManager;
import cfa.vo.iris.sed.SedlibSedManager;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

/**
 *
 * @author olaurino
 */
public class Ws implements IWorkspace {

    SedlibSedManager manager = new SedlibSedManager();

//    JDesktopPane desktop = new JDesktopPane();
    List<JInternalFrame> frames = new ArrayList<JInternalFrame>();

    @Override
    public void addFrame(JInternalFrame frame) {
        frames.add(frame);
    }

    @Override
    public void removeFrame(JInternalFrame frame) {
        frames.remove(frame);
    }

    @Override
    public JFileChooser getFileChooser() {
        return null;
    }

    @Override
    public ISedManager getSedManager() {
        return manager;
    }

    @Override
    public JFrame getRootFrame() {
        return null;
    }

    @Override
    public JDesktopPane getDesktop() {
        return null;
    }
}
