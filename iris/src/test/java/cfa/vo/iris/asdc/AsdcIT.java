/**
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cfa.vo.iris.asdc;

import cfa.vo.iris.test.IrisAppResource;
import cfa.vo.iris.test.unit.AbstractUISpecTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.uispec4j.Desktop;
import org.uispec4j.Window;
import org.uispec4j.assertion.Assertion;

import static org.uispec4j.assertion.UISpecAssert.waitUntil;

public class AsdcIT extends AbstractUISpecTest {
    private final String WIN_NAME = "ASDC Catalog Query";
    private Desktop desktop;

    @Rule
    public IrisAppResource iris = new IrisAppResource(false, false);

    @Before
    public void setup() {
        final Window window = iris.getAdapter().getMainWindow();
        desktop = window.getDesktop();

        // Menu may not be ready right away
        waitUntil(new Assertion() {
            @Override
            public void check() {
                window.getMenuBar().getMenu("Tools").getSubMenu("ASDC SED Data").getSubMenu("ASDC Data").click();
            }
        }, 1000);
    }

    @Test
    public void basicTest() throws Exception {
        desktop.containsWindow(WIN_NAME).check();
    }

}
