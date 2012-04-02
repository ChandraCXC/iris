/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.builder.dm;

import java.beans.PropertyChangeListener;

/**
 *
 * @author olaurino
 */
interface Validable {
    Validation validate();
    boolean isValid();
    void addPropertyChangeListener(PropertyChangeListener listener);
}
