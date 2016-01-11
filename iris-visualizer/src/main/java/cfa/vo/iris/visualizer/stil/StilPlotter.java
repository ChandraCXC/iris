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
import cfa.vo.iris.sed.stil.SegmentStarTableWrapper;
import cfa.vo.iris.visualizer.settings.PlotPreferences;
import cfa.vo.iris.visualizer.settings.SegmentLayer;
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class StilPlotter extends JPanel {
    
    private static final Logger logger = Logger.getLogger(StilPlotter.class.getName());
    
    private static final long serialVersionUID = 1L;
    
    private PlotDisplay display;
    
    private IrisApplication app;
    private IWorkspace ws;
    private ISedManager<ExtSed> sedManager;
    private StarTableAdapter adapter;
    
    // How can we keep this in sync with the iris application?
    private Map<ISegment, SegmentLayer> segments;
    private PlotPreferences plotPreferences;
    
    public StilPlotter(IrisApplication app, IWorkspace ws, StarTableAdapter adapter) {
        this.adapter = adapter;
        this.ws = ws;
        this.app = app;
        this.sedManager = ws.getSedManager();
        this.segments = new HashMap<>();
        this.plotPreferences = PlotPreferences.getDefaultPlotPreferences();
        
        setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        setBackground(Color.WHITE);
        setLayout(new GridLayout(1, 0, 0, 0));
        
        reset();
    }
    
    public void reset() {
        if (display != null) {
            display.removeAll();
            remove(display);
        }
        
        try {
            this.display = createPlotComponent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        add(display, BorderLayout.CENTER);
    }
    
    private PlotDisplay createPlotComponent() throws Exception {
        logger.info("Creating new plot from selected SED");
        
        MapEnvironment env = new MapEnvironment();
        env.setValue("type", "plot2plane");
        env.setValue("insets", new Insets(50, 40, 40, 40));
        
        ExtSed sed = sedManager.getSelected();
        
        // Add high level plot preferences
        for (String key : plotPreferences.getPreferences().keySet()) {
            env.setValue(key, plotPreferences.getPreferences().get(key));
        }
        
        // Add segments and segment preferences
        addSegmentLayers(sed, env);
        
        return new PlanePlot2Task().createPlotComponent(env, true);
    }
    
    private void addSegmentLayers(ExtSed sed, MapEnvironment env) {
        if (sed == null) {
            return;
        }
        
        logger.info(String.format("Plotting SED with %s segments...", sed.getNamespace()));
        
        for (int i=0; i<sed.getNumberOfSegments(); ++i) {
            ISegment segment = sed.getSegment(i);
            
            if (!segments.containsKey(segment)) {
                segments.put(segment, new SegmentLayer(new SegmentStarTableWrapper(segment)));
            }
            
            SegmentLayer layer = segments.get(segment);
            
            for (String key : layer.getPreferences().keySet()) {
                env.setValue(key, layer.getPreferences().get(key));
            }
        }
    }
}
