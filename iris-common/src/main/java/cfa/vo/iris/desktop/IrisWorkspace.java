/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.desktop;

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
public class IrisWorkspace implements IWorkspace {

    private IrisDesktop mainview;

    private ISedManager sedManager;

    public IrisWorkspace() {
        sedManager = new SedlibSedManager();
    }

    public void setDesktop(IrisDesktop mainView) {
        this.mainview = mainView;
    }

    @Override
    public ISedManager getSedManager() {
        return sedManager;
    }

    @Override
    public JFrame getRootFrame() {
        return mainview;
    }

    @Override
    public JDesktopPane getDesktop() {
        return mainview.getDesktopPane();
    }

    private static final JFileChooser fileChooser = new JFileChooser();

    /**
     * Get the value of fileChooser
     *
     * @return the value of fileChooser
     */
    @Override
    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    @Override
    public void addFrame(JInternalFrame frame) {
        this.getDesktop().add(frame);
        this.getDesktop().setLayer(frame, 1);
        JDesktopPane d = getDesktop();
        frame.setLocation((d.getWidth()-frame.getWidth())/2, (d.getHeight()-frame.getHeight())/2);
    }

    @Override
    public void removeFrame(JInternalFrame frame) {
        this.getDesktop().remove(frame);
    }

}
