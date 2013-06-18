/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package cfa.vo.iris.gui;

import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

/**
 * Common GUI utilities accessed via static methods.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class GUIUtils {

    public static void moveToFront(final JInternalFrame fr) {
        if (fr != null) {
            processOnSwingEventThread(new Runnable() {
                @Override
                public void run() {
                    fr.moveToFront();
                    fr.setVisible(true);
                    try {
                        fr.setSelected(true);
                        if (fr.isIcon()) {
                            fr.setIcon(false);
                        }
                        fr.setSelected(true);
                    } catch (PropertyVetoException ex) {
                    }
                    fr.requestFocus();
                }
            });
        }

    }

    public static void processOnSwingEventThread(Runnable todo) {
        processOnSwingEventThread(todo, false);
    }

    public static void processOnSwingEventThread(Runnable todo, boolean wait) {
        if (todo == null) {
            throw new IllegalArgumentException("Runnable == null");
        }

        if (wait) {
            if (SwingUtilities.isEventDispatchThread()) {
                todo.run();
            } else {
                try {
                    SwingUtilities.invokeAndWait(todo);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else {
            if (SwingUtilities.isEventDispatchThread()) {
                todo.run();
            } else {
                SwingUtilities.invokeLater(todo);
            }
        }
    }
}
