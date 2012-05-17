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
package cfa.vo.iris;

import cfa.vo.iris.events.MultipleSegmentEvent;
import cfa.vo.iris.events.MultipleSegmentListener;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.iris.events.SegmentListener;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sedlib.Segment;
import java.util.ArrayList;
import java.util.List;
import org.astrogrid.samp.client.MessageHandler;

/**
 *
 * @author olaurino
 */
public class ConsistencyChecker implements IrisComponent {

    private IrisApplication app;
    private IWorkspace workspace;

    @Override
    public void init(IrisApplication app, final IWorkspace workspace) {
        this.app = app;
        this.workspace = workspace;

        SegmentEvent.getInstance().add(new SegmentListener() {

            @Override
            public void process(Segment source, SegmentPayload payload) {
                ExtSed sed = payload.getSed();
                checkSed(sed);
            }
        });

        MultipleSegmentEvent.getInstance().add(new MultipleSegmentListener() {

            @Override
            public void process(List<Segment> source, SegmentPayload payload) {
                ExtSed sed = payload.getSed();
                checkSed(sed);
            }
        });
    }

    private void checkSed(ExtSed sed) {
        int npoints = 0;
        for (int i = 0; i < sed.getNumberOfSegments(); i++) {
            Segment segment = sed.getSegment(i);
            npoints += segment.getLength();
        }

        if (npoints == 1) {
            NarrowOptionPane.showMessageDialog(workspace.getRootFrame(),
                    "The SED: " + sed.getId() + " has only one point in it. Some functions (e.g. Visualization and Fitting) will be disabled for this SED until new points are added.",
                    "WARNING",
                    NarrowOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public String getName() {
        return "Consistency Checker";
    }

    @Override
    public String getDescription() {
        return "Checks particular conditions on open SED and shows warning when needed.";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("checker");
    }

    @Override
    public void initCli(IrisApplication app) {
        
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
}
