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
package cfa.vo.iris.visualizer.plotter;

import javax.swing.JPanel;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.sed.SedlibSedManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.BevelBorder;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StoragePolicy;
import uk.ac.starlink.ttools.plot2.task.PlanePlot2Task;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.task.MapEnvironment;
import uk.ac.starlink.util.DataSource;
import uk.ac.starlink.util.FileDataSource;
import uk.ac.starlink.votable.VOTableBuilder;

public class StilPlotter extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private IrisApplication app;
    private IWorkspace ws;
    private SedlibSedManager sedManager;
    
    private StarTablePreferences tablePreferences;
    private List<StarSegment> tables; // TODO: update to new class when #222 is done
    private PlotDisplay display;

    public StilPlotter(String title, IrisApplication app, IWorkspace ws) {
        this.ws = ws;
        this.app = app;
        this.sedManager = (SedlibSedManager) ws.getSedManager();
        setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        setBackground(Color.WHITE);
        setLayout(new GridLayout(1, 0, 0, 0));
        
        // Default settings for the table
        // TODO: should come from a user preferences configuration IF one exists
        tablePreferences = new StarTablePreferences()                
                .setColor("blue")
                .setXlog(true)
                .setYlog(true)
                .setGrid(true)
                .setxCol("DataSpectralValue")
                .setyCol("DataFluxValue")
                .setYerrhi("DataFluxStatErr")
                .setErrBar("capped_lines")
                .setXlabel("Spectral Value")
                .setYlabel("Flux");
        
        // display selected SED
        reset();
    }
    
    private void reset() {
        
        this.tables = new ArrayList<>();
        
        try {
            this.display = createPlotComponent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        this.add(display, BorderLayout.CENTER);
    }
    
    private PlotDisplay createPlotComponent() throws Exception {
        
        MapEnvironment env = new MapEnvironment();
        env.setValue("type", "plot2plane");
        env.setValue("insets", new Insets(50, 40, 40, 40));

        // TODO: change to something like this when #222 is done
//        ExtSed sed = sedManager.getSelected();
        
        // For now, just use a StarTable
        URL url = this.getClass().getResource("/3c273.vot");
        DataSource dataSource = new FileDataSource(url.getFile());
        VOTableBuilder builder = new VOTableBuilder();
        // StoragePolicy.ADAPTIVE may write StarTables to a temp file 
        // OR in memory, depending on the size of the file.
        StarSegment table = new StarSegment(builder.makeStarTable(dataSource, false, StoragePolicy.ADAPTIVE));
        
        tables.add(table);
        
        // Table and layer preferences
        for (String key : tablePreferences.preferences.keySet()) {
            env.setValue(key, tablePreferences.preferences.get(key));
        }
        for (StarSegment layer : tables) {            
            for (String key : layer.getPreferences().keySet()) {
                env.setValue(key, layer.getPreferences().get(key));
            }
        }
        
        return new PlanePlot2Task().createPlotComponent(env, true);
    }
    
    public void addTable(StarTable table) {
        tables.add(new StarSegment(table));
    }
    
    public void resetPlotView() {
        throw new UnsupportedOperationException("This class has not been implemented yet.");
    }
    
    /**
     * Get the spectral axis values from the segment
     * @return ArrayList
     */
    public ArrayList getXAxis() {
        return getColumn(0);
    }

    /**
     * Get the spectral axis lower errors from the segment
     * @return ArrayList
     */
    public ArrayList getYAxis() {
        return getColumn(1);
    }
    
    /**
     * Get the symmetrical flux axis errors from the segment
     * @return ArrayList
     */
    public ArrayList getYErrors() {
        return getColumn(2);
    }
    
    /**
     * Get the spectral axis upper errors from the segment
     * @return ArrayList
     */
    public ArrayList getXAxisUpperErrors() {
        return getColumn(3);
    }
    
    /**
     * Get the spectral axis lower errors from the segment
     * @return ArrayList
     */
    public ArrayList getXAxisLowerErrors() {
        return getColumn(4);
    }
    
    /**
     * Get the spectral axis lower errors from the segment
     * @return ArrayList
     */
    public ArrayList getYAxisUpperErrors() {
        return getColumn(5);
    }
    
    /**
     * Get the spectral axis lower errors from the segment
     * @return ArrayList
     */
    public ArrayList getYAxisLowerErrors() {
        return getColumn(6);
    }
    
    /**
     * Get the specified axis from the segment.
     * 
     * @param index - The Column to extract. 
     * 0 = x value
     * 1 = y value
     * 2 = y error
     * 3 = x upper error
     * 4 = x lower error
     * 5 = y upper error
     * 6 = y lower error
     * 
     * @return ArrayList
     */
    public ArrayList getColumn(int index) {
        
        ArrayList<Double> col = new ArrayList<>();
        
        for (StarSegment table : tables) {
            for (int j = 0; j < table.getRowCount(); j++) {
                try {
                    // get the specified column to extract
                    Double point = (Double) table.getRow(j)[index];
                    col.add(point);
                } catch (IOException ex) {
                    Logger.getLogger(StilPlotter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return col;
    }
    
    // Add methods for setting colors, etc...
    // As well as listener methods
    
}
