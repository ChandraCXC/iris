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

package cfa.vo.iris.test;

import cfa.vo.iris.ICommandLineInterface;
import cfa.vo.iris.IMenuItem;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.NullCommandLineInterface;
import cfa.vo.iris.logging.LogEntry;
import cfa.vo.iris.logging.LogEvent;
import cfa.vo.iris.logging.LogListener;
import java.util.ArrayList;
import java.util.List;
import org.astrogrid.samp.client.MessageHandler;

/**
 *
 * @author olaurino
 */
public class TestLogger implements IrisComponent, LogListener {

    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
        LogEvent.getInstance().add(this);
    }

    @Override
    public String getName() {
        return "Test Logger";
    }

    @Override
    public String getDescription() {
        return "Prints log messages to the standard output";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("tlogger");
    }

    @Override
    public List<IMenuItem> getMenus() {
        return new ArrayList();
    }

    @Override
    public List<MessageHandler> getSampHandlers() {
        return new ArrayList();
    }

    @Override
    public void process(Object source, LogEntry payload) {
        System.out.println(payload.getFormatted());
    }

    @Override
    public void shutdown() {
        
    }

    @Override
    public void initCli(IrisApplication app) {
        
    }

}
