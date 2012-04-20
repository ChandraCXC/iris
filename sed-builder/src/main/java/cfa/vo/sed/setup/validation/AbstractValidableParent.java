/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.setup.validation;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author olaurino
 */
public abstract class AbstractValidableParent extends AbstractValidable {

    protected abstract List<AbstractValidable> getValidableChildren();

    private Validation validation = new Validation();

    @Override
    public Validation validate() {
        validation.reset();
        updateValidables();

        return validation;
    }

    protected void updateValidables() {
        for (AbstractValidable validable : getValidableChildren()) {
            PropertyChangeListener[] listeners = getPropertyChangeListeners();
            for (PropertyChangeListener l : listeners) {
                if (l instanceof Validator) {
                    if (!Arrays.asList(validable.getPropertyChangeListeners()).contains(l)) {
                        validable.addPropertyChangeListener(l);
                    }
                }
            }

            Validation valid = validable.validate();
            validation.getWarnings().addAll(valid.getWarnings());
            validation.getErrors().addAll(valid.getErrors());
        }

    }
}
