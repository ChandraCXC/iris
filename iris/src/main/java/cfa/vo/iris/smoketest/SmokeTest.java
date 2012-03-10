/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.smoketest;

import cfa.vo.iris.ICommandLineInterface;
import cfa.vo.iris.IMenuItem;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.IrisComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.astrogrid.samp.client.MessageHandler;

/**
 *
 * @author olaurino
 */
public class SmokeTest implements IrisComponent {

    private SherpaSmokeTest test;

    private IrisApplication app;

    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
    }

    @Override
    public String getDescription() {
        return "Smoke Test";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new ICommandLineInterface() {

            @Override
            public String getName() {
                return "smoketest";
            }

            @Override
            public void call(String[] args) {

                String testFile = System.getProperty("IRIS_DIR")+"/examples/3c273.xml";
                if(args.length>0) {
                    try{
                        Integer timeout = Integer.parseInt(args[0]);
                        test = new SherpaSmokeTest(testFile, timeout);
                    } catch (NumberFormatException ex) {
                        System.out.println(args[0]+" is not a number.");
                        return;
                    }
                }
                else
                    test = new SherpaSmokeTest(testFile);
                try {
                    test.runTest();
                } catch (Exception ex) {
                    Logger.getLogger(SmokeTest.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    test.exit();
                }
            }
        };
    }

    @Override
    public void initCli(IrisApplication app) {
        this.app = app;
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
    public void shutdown() {
    }

    @Override
    public String getName() {
        return "Smoke Test";
    }
}
