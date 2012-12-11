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

package cfa.vo.iris.desktop;

import java.awt.Dimension;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 *
 * @author olaurino
 */
// A DesktopManager that keeps its frames inside the desktop
public class BoundDesktopManager implements DesktopManager {    
    JDesktopPane desk;
    DesktopManager dm;

    public BoundDesktopManager(JDesktopPane desktop){

        desk = desktop;
        dm = desktop.getDesktopManager();

    }

    // This is called whenever a frame is moved. This implementation keeps the frame

    // from leaving the desktop.
    @Override
    public void dragFrame(JComponent f, int x, int y) {
        if (f instanceof JInternalFrame ||
            f instanceof JInternalFrame.JDesktopIcon) { // Deal only with internal frames.

            Dimension d = desk.getSize( );
            // Nothing all that fancy below, just figuring out how to adjust
            // to keep the frame on the desktop
            if (x < 0) { // Too far left?
                x = 0;// Flush against the left side.
                }
            else {
                if (x + f.getWidth( ) > d.width) {// Too far right?
                x = d.width - f.getWidth( ); // Flush against right side.
            }
            }
            if (y < 0) { // Too high?
                y=0;// Flush against the top.
                }
            else {
            if (y + f.getHeight( ) > d.height) {// Too low?
                y = d.height - f.getHeight( );// Flush against the bottom.
            }
        }
    }
        // Pass along the (possibly cropped) values to the normal drag handler.
        dm.dragFrame(f, x, y);
    }

    @Override
    public void openFrame(JInternalFrame jif) {
        dm.openFrame(jif);
    }

    @Override
    public void closeFrame(JInternalFrame jif) {
        dm.closeFrame(jif);
    }

    @Override
    public void maximizeFrame(JInternalFrame jif) {
        dm.maximizeFrame(jif);
    }

    @Override
    public void minimizeFrame(JInternalFrame jif) {
        dm.minimizeFrame(jif);
    }

    @Override
    public void iconifyFrame(JInternalFrame jif) {
        dm.iconifyFrame(jif);
    }

    @Override
    public void deiconifyFrame(JInternalFrame jif) {
        dm.deiconifyFrame(jif);
    }

    @Override
    public void activateFrame(JInternalFrame jif) {
        if(outOfBounds(jif))
            dragFrame(jif, jif.getX(), jif.getY());
        dm.activateFrame(jif);
    }

    @Override
    public void deactivateFrame(JInternalFrame jif) {
        dm.deactivateFrame(jif);
    }

    @Override
    public void beginDraggingFrame(JComponent jc) {
        dm.beginDraggingFrame(jc);
    }

    @Override
    public void endDraggingFrame(JComponent jc) {
        dm.endDraggingFrame(jc);
    }

    @Override
    public void beginResizingFrame(JComponent jc, int i) {
        dm.beginResizingFrame(jc, i);
    }

    @Override
    public void resizeFrame(JComponent jc, int i, int i1, int i2, int i3) {
        dm.resizeFrame(jc, i, i1, i2, i3);
    }

    @Override
    public void endResizingFrame(JComponent jc) {
        dm.endResizingFrame(jc);
    }

    @Override
    public void setBoundsForFrame(JComponent jc, int i, int i1, int i2, int i3) {
        dm.setBoundsForFrame(jc, i, i1, i2, i3);
    }

    private boolean outOfBounds(JInternalFrame jif) {
        int x = jif.getX();
        int y = jif.getY();
        
        Dimension d = desk.getSize();
        if (x < 0) { // Too far left?
            return true;
        } else {
            if (x + jif.getWidth() > d.width) {// Too far right?
                return true;
            }
        }
        if (y < 0) { // Too high?
            return true;
        } else {
            if (y + jif.getHeight() > d.height) {// Too low?
                return true;
            }
        }
        
        return false;

    }    
}
