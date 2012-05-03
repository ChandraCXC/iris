/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.test;

import cfa.vo.iris.AbstractDesktopItem;
import cfa.vo.iris.ICommandLineInterface;
import cfa.vo.iris.IMenuItem;
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
public class CrossHairTestComponent implements IrisComponent {
    private IWorkspace ws;
    private CrossHairFrame frame;


    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
        ws = workspace;
    }

    @Override
    public String getName() {
        return "Test Cross Hair";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("ch");
    }

    @Override
    public void initCli(IrisApplication app) {

    }

    @Override
    public List<IMenuItem> getMenus() {
        return new TestMenus();
    }

    @Override
    public List<MessageHandler> getSampHandlers() {
        return new ArrayList();
    }

    @Override
    public void shutdown() {
        
    }

    private class TestMenus extends ArrayList<IMenuItem> {
        public TestMenus() {
            add(new AbstractDesktopItem("CrossHair", "Simple Test Builder", "/tool.png", "/tool_tiny.png") {

                @Override
                public void onClick() {
                    if(frame==null) {
                        frame = new CrossHairFrame();
                        ws.addFrame(frame);
                    }
                    frame.setVisible(true);
                }
            });
        }
    }

}
