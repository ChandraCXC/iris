/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.setup.validation;

import java.beans.PropertyChangeListener;

/**
 *
 * @author olaurino
 */
public interface Validable {
    Validator getValidator();
    Validation validate();
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
}
