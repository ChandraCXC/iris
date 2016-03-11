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

import org.uispec4j.UISpecAdapter;
import org.uispec4j.Window;

public class StubAdapter implements UISpecAdapter {
    private ApplicationStub stub;
    private Window mainWindow;

    public StubAdapter(ApplicationStub app) {
        stub = app;
        mainWindow = new Window(app.getWorkspace().getRootFrame());
    }

    public StubAdapter() {
        this(new ApplicationStub());
    }

    @Override
    public Window getMainWindow() {
        return mainWindow;
    }

    public ApplicationStub getIrisApplication() {
        return stub;
    }
}
