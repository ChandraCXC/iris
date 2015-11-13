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
package cfa.vo.iris.test;

import org.uispec4j.*;
import org.uispec4j.interception.WindowInterceptor;
import org.uispec4j.utils.MainClassTrigger;

public class SampUISpecAdapter implements UISpecAdapter {
    private Window mainWindow;
    private Trigger trigger;
    private Window samphub;

    public SampUISpecAdapter(Class mainClass, String... args) {
        if (trigger == null)
            trigger = new MainClassTrigger(mainClass, args);
        UISpec4J.setWindowInterceptionTimeLimit(60000);
    }

    public Window getMainWindow() {
        if (mainWindow == null) {
            mainWindow = WindowInterceptor.run(trigger);
            samphub = WindowInterceptor.run(new Trigger() {
                @Override
                public void run() throws Exception {

                }
            });
        }
        return mainWindow;
    }

    public Window getSamphub() {
        return samphub;
    }

}
