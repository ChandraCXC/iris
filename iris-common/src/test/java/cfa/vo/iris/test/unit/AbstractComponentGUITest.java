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
package cfa.vo.iris.test.unit;

import cfa.vo.iris.IrisComponent;

import java.util.Arrays;
import java.util.List;

/**
 * This abstract class makes it easier to unit-test a single component.
 *
 * Implementing classes can return a single {@link IrisComponent} instance rather
 * than a list of them.
 */
public abstract class AbstractComponentGUITest extends AbstractGUITest {

    @Override
    protected List<IrisComponent> getComponents() {
        return Arrays.asList(new IrisComponent[]{getComponent()});
    }

    protected abstract IrisComponent getComponent();
}
