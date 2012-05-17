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

import cfa.vo.iris.AbstractDesktopItem;
import cfa.vo.iris.ICommandLineInterface;
import cfa.vo.iris.IMenuItem;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.iris.events.SegmentListener;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.sedlib.Segment;
import java.util.ArrayList;
import java.util.List;
import org.astrogrid.samp.client.MessageHandler;

/**
 *
 * @author olaurino
 */
public class TestBuilder implements IrisComponent {

    private static IrisApplication app;

    private IWorkspace ws;

    private TestBuilderFrame frame;

    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
        TestBuilder.app = app;
        this.ws = workspace;
//        SedEvent.getInstance().add(this);
        SegmentEvent.getInstance().add(new SegListener());
    }

    @Override
    public String getName() {
        return "Test Builder";
    }

    @Override
    public String getDescription() {
        return "Basic test builder";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new CLI();
    }

    @Override
    public List<IMenuItem> getMenus() {
        return new TestBuilderMenus();
    }

    @Override
    public List<MessageHandler> getSampHandlers() {
        return new ArrayList();
    }

    public static IrisApplication getApplication() {
        return app;
    }

    @Override
    public void shutdown() {
        
    }

    @Override
    public void initCli(IrisApplication app) {
        
    }



    private class TestBuilderMenus extends ArrayList<IMenuItem> {
        public TestBuilderMenus() {
            add(new AbstractDesktopItem("Test Builder", "Simple Test Builder", "/tool.png", "/tool_tiny.png") {

                @Override
                public void onClick() {
                    if(frame==null) {
                        frame = new TestBuilderFrame((SedlibSedManager) ws.getSedManager());
                        ws.addFrame(frame);
                    }
                    frame.setVisible(true);
                }
            });
        }
    }

    private class CLI implements ICommandLineInterface {

        @Override
        public String getName() {
            return "tbuilder";
        }

        @Override
        public void call(String[] args) {
            System.out.println(getName()+" called with arguments: "+args);
        }

    }

    private class SegListener implements SegmentListener {

        @Override
        public void process(Segment source, SegmentPayload payload) {
//            frame.setSeds(ws.getSedManager().getSeds());
        }

    }

}
