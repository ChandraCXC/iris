/**
 * Copyright (C) Smithsonian Astrophysical Observatory
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

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 *
 * @author olaurino
 */
public class ConfirmJInternalFrame extends JInternalFrame {

    public ConfirmJInternalFrame(String title) {
        super(title);

        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                int confirm = JOptionPane.showOptionDialog(ConfirmJInternalFrame.this,

                "Do you really want to close " + getTitle() + "?",

                "Close Confirmation",

                JOptionPane.YES_NO_OPTION,

                JOptionPane.QUESTION_MESSAGE,

                null, null, null);

              if (confirm == 0) {

                ConfirmJInternalFrame.this.setVisible(false);

              }
            }
        });
    }
}
//class ConfirmJInternalFrame extends JInternalFrame
//
//   implements VetoableChangeListener {
//
//    public ConfirmJInternalFrame(String title, boolean resizable,
//
//     boolean closable, boolean maximizable, boolean iconifiable) {
//
//      super(title, resizable, closable, maximizable, iconifiable);
//
//      addVetoableChangeListener(this);
//
//    }
//
//    public ConfirmJInternalFrame(String title) {
//        this(title, false, true, false, true);
//    }
//
//    public void vetoableChange(PropertyChangeEvent pce)
//
//     throws PropertyVetoException {
//
//      if (pce.getPropertyName().equals(IS_CLOSED_PROPERTY)) {
//
//        boolean changed = ((Boolean) pce.getNewValue()).booleanValue();
//
//        if (changed) {
//
//          int confirm = JOptionPane.showOptionDialog(this,
//
//            "Do you really want to close " + getTitle() + "?",
//
//            "Close Confirmation",
//
//            JOptionPane.YES_NO_OPTION,
//
//            JOptionPane.QUESTION_MESSAGE,
//
//            null, null, null);
//
//          if (confirm == 0) {
//
//            this.dispose();
//
//          }
//
//          else throw new PropertyVetoException("Cancelled",null);
//
//        }
//
//      }
//
//    }
//
//  }