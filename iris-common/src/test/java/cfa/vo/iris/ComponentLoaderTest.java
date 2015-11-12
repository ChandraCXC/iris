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

package cfa.vo.iris;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cfa.vo.iris.sdk.AbstractIrisComponent;

public class ComponentLoaderTest {

    private URL testUrl;

    @Before
    public void setUp() {
        testUrl = this.getClass().getResource("/test_components");
    }

    @Test
    public void testLoadComponents() {
        ComponentLoader loader = new ComponentLoader(testUrl);
        List<IrisComponent> components = loader.getComponents();

        assertEquals(1, components.size());
        assertTrue(components.get(0) instanceof TestIrisComponent);

        assertEquals(1, loader.failures.size());
        assertEquals(BrokenTestIrisComponent.class.getName(), loader.failures.get(0));
    }

    @Test
    public void testFailedIORead() throws Exception {
        ComponentLoader loader = new ComponentLoader(new ArrayList());
        List<IrisComponent> components = loader.getComponents();

        assertEquals(0, components.size());
    }

    // Simulated components for testing only.
    public static class TestIrisComponent extends AbstractIrisComponent {
        @Override
        public String getName() {
            return null;
        }
        @Override
        public String getDescription() {
            return null;
        }
        @Override
        public List<IMenuItem> getMenus() {
            return null;
        }
    }
    public static class BrokenTestIrisComponent extends AbstractIrisComponent {
        public BrokenTestIrisComponent() {
            super();
            throw new RuntimeException("I don't work");
        }
        @Override
        public String getName() {
            return null;
        }
        @Override
        public String getDescription() {
            return null;
        }
        @Override
        public List<IMenuItem> getMenus() {
            return null;
        }
    }
}
