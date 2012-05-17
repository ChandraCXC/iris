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

package cfa.vo.iris.gui;

import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author olaurino
 */
public class NarrowOptionPane extends JOptionPane {
    
    @Override
    public int getMaxCharactersPerLineCount() {
        return 80;
    }

    public static void showMessageDialog(Component parent, Object message, String title, int type) {
        NarrowOptionPane pane = new NarrowOptionPane();
        pane.setMessage(message);
        pane.setMessageType(type);
        JDialog dialog = pane.createDialog(parent, title);
        dialog.setVisible(true);
    }

    public static int showConfirmDialog(Component parent, Object message) {
        return JOptionPane.showOptionDialog(parent,

                message,

                "Confirm Action",

                JOptionPane.YES_NO_OPTION,

                JOptionPane.QUESTION_MESSAGE,

                null, null, null);
    }
}
