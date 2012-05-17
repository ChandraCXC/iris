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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.logger;

import cfa.vo.iris.AbstractMenuItem;
import cfa.vo.iris.ICommandLineInterface;
import cfa.vo.iris.IMenuItem;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.NullCommandLineInterface;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.logging.LogEntry;
import cfa.vo.iris.logging.LogEvent;
import cfa.vo.iris.logging.LogListener;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.astrogrid.samp.client.MessageHandler;

/**
 *
 * @author olaurino
 */
public class IrisLogger implements IrisComponent, LogListener {

    private LoggerViewer view;
    private File logFile;
    private IWorkspace ws;

    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
        ws = workspace;

        logFile = new File(app.getConfigurationDir().getAbsolutePath() + "/log.txt");

        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException ex) {
                NarrowOptionPane.showMessageDialog(workspace.getRootFrame(), "Cannot create file: " + logFile.getAbsolutePath(), "Logger", NarrowOptionPane.ERROR_MESSAGE);
            }
        }

        if (logFile.canWrite()) {
            LogEvent.getInstance().add(this);
        } else {
            NarrowOptionPane.showMessageDialog(workspace.getRootFrame(), "Cannot write to file: " + logFile.getAbsolutePath(), "Logger", NarrowOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public String getName() {
        return "Iris Logger";
    }

    @Override
    public String getDescription() {
        return "Prints log messages to the Iris log file";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("logger");
    }

    @Override
    public List<IMenuItem> getMenus() {
        return new Menus();
    }

    @Override
    public List<MessageHandler> getSampHandlers() {
        return new ArrayList();
    }

    @Override
    public void process(Object source, LogEntry payload) {
        try {
            Files.append(payload.getFormatted() + "\n", logFile, Charsets.UTF_8);
        } catch (IOException ex) {
            NarrowOptionPane.showMessageDialog(ws.getRootFrame(), "Cannot write to file: " + logFile.getAbsolutePath(), "Logger", NarrowOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void initCli(IrisApplication app) {
    }

    private class Menus extends ArrayList<IMenuItem> {

        public Menus() {
            super();

            add(new AbstractMenuItem("View Log", "View Iris Log File", false, "/tool.png", "/tool_tiny.png") {
                private Point location;
                private Dimension size;

                @Override
                public void onClick() {
                    try {
                        if(view!=null) {
                            location = view.getLocation();
                            size = view.getSize();
                            view.dispose();
                        }
                        
                        view = new LoggerViewer(logFile, ws);
                        ws.addFrame(view);

                        if(location!=null)
                            view.setLocation(location);

                        if(size!=null)
                            view.setSize(size);

                        view.show();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(IrisLogger.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
    }
}
