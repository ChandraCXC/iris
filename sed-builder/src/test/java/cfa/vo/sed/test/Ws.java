/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.test;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.sed.ISedManager;
import cfa.vo.iris.sed.SedlibSedManager;
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

    JDesktopPane desktop = new JDesktopPane();

    @Override
    public void addFrame(JInternalFrame frame) {
        desktop.add(frame);
    }

    @Override
    public void removeFrame(JInternalFrame frame) {
        desktop.remove(frame);
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
        return desktop;
    }
}
