/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris;

import cfa.vo.iris.sed.ISedManager;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

/**
 *
 * A workspace is implemented by the enclosing application to provide components with
 * hooks to the shared context in which they are executed or operations that are to be performed
 * by the enclosing desktop.
 *
 * @author olaurino
 */
public interface IWorkspace {
    /**
     * Add a frame to the workspace so that it can be managed by the underlying Desktop.
     * @param frame The JInternalFrame that has to be managed by the Desktop.
     */
    void addFrame(JInternalFrame frame);
    /**
     * Remove an internal frame from the Desktop.
     * @param frame The frame that must be dropped from the Desktop.
     */
    void removeFrame(JInternalFrame frame);
    /**
     * Get a singleton instance of a file chooser whose state is saved between different accesses.
     * This allows the last selected file system location to be shared among different components.
     *
     * @return a JFileChooser instance.
     */
    JFileChooser getFileChooser();
    /**
     * SED objects are maintained by a centralized SED manager. Components must refer to this manager
     * to get an updated view of the SEDs opened by the user or to perform particular operations.
     * 
     * @return an ISedManager instance.
     */
    ISedManager getSedManager();
    /**
     * When spawning new windows and frame the components usually need a reference to the application root frame.
     * @return A reference to the main application frame.
     */
    JFrame getRootFrame();
    /**
     * This method returns a reference to the actual desktop in which all the components live.
     *
     * @return A reference to the JDesktopPane included in this application.
     */
    JDesktopPane getDesktop();
}
