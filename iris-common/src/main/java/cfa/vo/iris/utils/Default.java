/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
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
package cfa.vo.iris.utils;

import cfa.vo.iris.units.DefaultUnitsManager;
import cfa.vo.iris.units.UnitsManager;

/**
 * Class that holds default implementations for all clients, generally IrisComponents, to share.
 */
public class Default {
    private UnitsManager unitsManager = new DefaultUnitsManager();

    private Default() {
        /* Singleton class */
    }

    private static class DefaultHolder {
        private static final Default INSTANCE = new Default();
    }

    public static Default getInstance() {
        return DefaultHolder.INSTANCE;
    }

    public UnitsManager getUnitsManager() {
        return unitsManager;
    }
}
