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
