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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.sdk;

import cfa.vo.iris.ICommandLineInterface;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.NullCommandLineInterface;
import java.util.ArrayList;
import java.util.List;
import org.astrogrid.samp.client.MessageHandler;

/**
 *
 * @author olaurino
 */
public abstract class AbstractIrisComponent implements IrisComponent {
    
    protected IrisApplication app;
    protected IWorkspace workspace;
    
    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
        this.app = app;
        this.workspace = workspace;
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface(getName().replaceAll(" ", "").toLowerCase());
    }

    @Override
    public void initCli(IrisApplication app) {
        this.app = app;
    }
    
    @Override
    public List<MessageHandler> getSampHandlers() {
        return new ArrayList();
    }

    @Override
    public void shutdown() {
        
    }
    
}
