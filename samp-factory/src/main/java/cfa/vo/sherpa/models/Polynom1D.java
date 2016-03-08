/**
 * Copyright (C) 2012, 2015 Smithsonian Astrophysical Observatory
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

package cfa.vo.sherpa.models;

import java.util.Arrays;

/**
 *
 * @author olaurino
 */
public class Polynom1D extends AbstractModel implements Model {

    public Polynom1D(String id) {
        super("polynom1d", id);
        String[] parNames = new String[]{"c0", "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "offset"};
        addParams(Arrays.asList(parNames));
    }

}
