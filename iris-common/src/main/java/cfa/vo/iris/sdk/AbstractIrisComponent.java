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
