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

package cfa.vo.sherpa;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olaurino
 */
public enum Models {
    PowerLaw1D(PowerLaw1D.class),
    Gaussian1D(Gaussian1D.class);

    private Class<? extends AbstractModel> modelClass;

    private Models(Class<? extends AbstractModel> modelClass) {
        this.modelClass = modelClass;
    }

    public AbstractModel getModel(String id) {
        try {
            Constructor c = modelClass.getConstructor(String.class);
            return (AbstractModel) c.newInstance(id);
        } catch (Exception ex) {
            Logger.getLogger(Models.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
