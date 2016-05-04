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
package cfa.vo.iris.cli;

import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.test.TestBuilder;
import cfa.vo.iris.test.TestLogger;
import cfa.vo.iris.test.TestSSAServer;
import cfa.vo.iris.test.vizier.VizierClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class CommandLine {
    private static final File CONFIGURATION_DIR = new File(System.getProperty("user.home") + "/.vao/iris/");
    private static final boolean MAC_OS_X = System.getProperty("os.name").toLowerCase().startsWith("mac os x");

    private List<Class<? extends IrisComponent>> components = new ArrayList<>();
    private boolean isCrossLookAndFeel = false;
    private Level logLevel = Level.OFF;
    private boolean isSampEnabled = true;
    private boolean isBatch = false;
    private String cliComponentName;
    private String[] cliComponentArgs;

    public CommandLine(String[] args) {
        List<String> properties = new ArrayList<>();
        List<String> arguments = new ArrayList<>();
        for (String arg : args) {
            if (arg.startsWith("--")) {
                arg = arg.replaceFirst("--", "");
                properties.add(arg);
            } else {
                arguments.add(arg);
            }
        }

        if (arguments.size() >= 1) {
            isBatch = true;
            cliComponentName = arguments.get(0);
            cliComponentArgs = new String[arguments.size() - 1];
            for (int i = 1; i < arguments.size(); i++) {
                cliComponentArgs[i - 1] = arguments.get(i);
            }
        }

        for (String prop : properties) {
            if (prop.equals("test")) {
                components.add(TestBuilder.class);
                components.add(TestLogger.class);
                components.add(TestSSAServer.class);
            }

            if (prop.equals("lnf")) {
                isCrossLookAndFeel = true;
            }

            if (prop.equals("vizier")) {
                components.add(VizierClient.class);
            }

            if (prop.equals("debug")) {
                logLevel = Level.INFO;
            }

            if (prop.equals("nosamp")) {
                isSampEnabled = false;
            }
        }
    }

    public boolean isBatch() {
        return isBatch;
    }

    public List<Class<? extends IrisComponent>> getAdditionalComponents() {
        return components;
    }

    public boolean isCrossLookAndFeel() {
        return isCrossLookAndFeel;
    }

    public boolean isSampEnabled() {
        return isSampEnabled;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    protected String getCliComponentName() {
        return cliComponentName;
    }

    protected String[] getCliComponentArgs() {
        return cliComponentArgs;
    }

    public File getConfigurationDir() {
        return CONFIGURATION_DIR;
    }

    public boolean isMacOsX() {
        return MAC_OS_X;
    }

    public int run(Map<String, IrisComponent> components) {
        if (!isBatch()) {
            throw new IllegalStateException("This command line does not call any components.");
        }
        int status;
        String name = getCliComponentName();
        if (!components.containsKey(name)) {
            String msg = "Component " + name + " does not exist.";
            System.err.println(msg);
            return 1;
        } else {
            try {
                status = components.get(name).getCli().call(getCliComponentArgs());
            } catch (Exception ex) {
                System.err.println("An error was encountered when running command line");
                ex.printStackTrace(System.err);
                return 1;
            }
        }
        return status;
    }
}
