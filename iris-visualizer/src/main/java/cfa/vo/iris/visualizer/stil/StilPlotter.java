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
package cfa.vo.iris.visualizer.stil;

import javax.swing.JPanel;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.ISedManager;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.sed.stil.StarTableAdapter;
import cfa.vo.iris.visualizer.stil.preferences.PlotPreferences;
import cfa.vo.iris.visualizer.stil.preferences.SegmentLayer;
import cfa.vo.sedlib.ISegment;
import uk.ac.starlink.ttools.plot2.task.PlanePlot2Task;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.task.MapEnvironment;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.border.BevelBorder;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StilPlotter extends JPanel {
    
    private static final Logger logger = Logger.getLogger(StilPlotter.class.getName());
    
    private static final long serialVersionUID = 1L;
    
    private PlotDisplay display;
    
    private IrisApplication app;
    private IWorkspace ws;
    private SedlibSedManager sedManager;
    private ExtSed currentSed;
    private StarTableAdapter<ISegment> adapter;
    
    // TODO: How can we keep this in sync with the iris application?
    private Map<ISegment, SegmentLayer> segments;
    private PlotPreferences plotPreferences;
    
    public StilPlotter(IrisApplication app, IWorkspace ws, StarTableAdapter<ISegment> adapter) {
        this.adapter = adapter;
        this.ws = ws;
        this.app = app;
        this.sedManager = (SedlibSedManager) ws.getSedManager();
        
        // Use weak key references so that unused segments will be caught by the gc.
        this.segments = new WeakHashMap<>();
        this.plotPreferences = PlotPreferences.getDefaultPlotPreferences();
        
        setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        setBackground(Color.WHITE);
        setLayout(new GridLayout(1, 0, 0, 0));
        
        reset(null);
    }
    
    public void reset(ExtSed sed) {
        if (display != null) {
            display.removeAll();
            remove(display);
            display = null; // just to be safe
        }
        
        try {
            display = createPlotComponent(sed);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        add(display, BorderLayout.CENTER);
        display.revalidate();
        display.repaint();
    }

    public ExtSed getSed() {
        return currentSed;
    }

    public Map<ISegment, SegmentLayer> getSegmentsMap() {
        return Collections.unmodifiableMap(segments);
    }
    
    private PlotDisplay createPlotComponent(ExtSed sed) throws Exception {
        logger.info("Creating new plot from selected SED");

        if (sed == null) {
            sed = sedManager.getSelected();
        }
        this.currentSed = sed;
        
        MapEnvironment env = new MapEnvironment();
        env.setValue("type", "plot2plane");
        env.setValue("insets", new Insets(50, 80, 50, 50)); 
        // TODO: force numbers on Y axis to only be 3-5 digits long. Keeps
        // Y-label from falling off the jpanel. Conversely, don't set "insets"
        // and let the plotter dynamically change size to keep axes labels
        // on the plot.
        
        // Add high level plot preferences
        for (String key : plotPreferences.getPreferences().keySet()) {
            env.setValue(key, plotPreferences.getPreferences().get(key));
        }
        
        if (sed != null) {
            // set title of plot if available
            env.setValue("title", sed.getId());
            
            // Add segments and segment preferences
            addSegmentLayers(sed, env);
        }
        
        logger.log(Level.FINE, "plot environment:");
        logger.log(Level.FINE, ReflectionToStringBuilder.toString(env));
        
        return new PlanePlot2Task().createPlotComponent(env, true);
    }
    
    // TODO: Preferences, etc....
    private void addSegmentLayers(ExtSed sed, MapEnvironment env) throws IOException {
        
        logger.info(String.format("Plotting SED with %s segments...", sed.getNamespace()));
        
        for (int i=0; i<sed.getNumberOfSegments(); ++i) {
            ISegment segment = sed.getSegment(i);
            
            if (!segments.containsKey(segment)) {
                segments.put(segment, new SegmentLayer(adapter.convertStarTable(segment)));
            }
            
            SegmentLayer layer = segments.get(segment);
            
            for (String key : layer.getPreferences().keySet()) {
                env.setValue(key, layer.getPreferences().get(key));
            }
        }
    }
}
