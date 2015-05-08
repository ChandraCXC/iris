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

package spv.fit;

/**
 * Created by IntelliJ IDEA.
 * User: busko
 * Date: 5/12/11
 * Time: 11:18 AM
 */

import spv.util.ExceptionHandler;

import java.io.*;
import java.util.Map;

public class SherpaSamp extends AbstractFittingEngine {
    private ProcessBuilder builder;
    private Process process;
    private Map<String, String> env;
    private boolean destroyed = false;

    public SherpaSamp() {
        builder = new ProcessBuilder("sherpa-samp");
        env = builder.environment();
        env.put("PYTHONPATH", "");
        builder.redirectErrorStream(true);
    }

    @Override
    public void run() {
        process = null;

        try {
            process = builder.start();
        } catch (SecurityException se) {
            ExceptionHandler.handleException(se);
        } catch (IOException ie) {
            ExceptionHandler.handleException(ie);
        } catch (NullPointerException ne) {
            ExceptionHandler.handleException(ne);
        } catch (IndexOutOfBoundsException iobe) {
            ExceptionHandler.handleException(iobe);
        }
            
    }

    @Override
    public void shutdown() {
        if (process != null && !destroyed) {
            destroyed = true;
            process.destroy();
        }
        
    }
}
