/*
 * Copyright 2016 Chandra X-Ray Observatory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cfa.vo.iris.visualizer.preferences;

import cfa.vo.iris.sed.stil.SegmentColumn;
import cfa.vo.iris.sed.stil.SegmentColumn.Column;
import cfa.vo.iris.visualizer.plotter.LayerType;
import cfa.vo.iris.visualizer.stil.tables.SortedStarTable;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.ttools.jel.ColumnIdentifier;

/**
 *
 * Evaluated model preferences. This describes the STIL preferences for plotting 
 * an evaluated model.
 */
public class FunctionModel {
    
    public static final String DEFAULT_FUNCTION_COLOR = "red";
    public static final String RATIOS = "Ratios";
    public static final String RESIDUALS = "Residuals";
    
    public static final String FUNCTION_SUFFIX = "_MODEL";
    
    private SortedStarTable sortedStackedStarTable;
    private StarTable baseTable;
    
    private String functionColor = "red";
    private Double functionDash = Double.NaN;
    private Integer functionThickness = 1;

    public FunctionModel(StarTable baseTable) {
        setBaseTable(baseTable);
    }
    
    /**
     * Create a LayerModel for the evaluated function model associated with the
     * SedModel.
     * @return a LayerModel of the evaluated function
     */
    public LayerModel getFunctionLayerModel() {
        
        LayerModel layer = new LayerModel(sortedStackedStarTable);
        layer.setShowErrorBars(false);
        layer.setShowMarks(false);
        layer.setShowLines(true);
        layer.setLayerType(LayerType.line.name());
        layer.setX(SegmentColumn.Column.Spectral_Value.name());
        layer.setY(SegmentColumn.Column.Model_Values.name());
        layer.setLineColor(getFunctionColor());
        layer.setLineThickness(getFunctionThickness());
        layer.setLineDash(getFunctionDash());
        return layer;
    }
    
    /**
     * Create a LayerModel for plotting the residuals of the SedModel's fit. The
     * residuals can be plotted as the "raw" residual (model - observed) or as 
     * the ratio
     * @param residualType - the type of residual to create: "Residuals" is the 
     * (model - observed), and "Ratios" is the (model - observed)/model. 
     * @return a LayerModel representing the residuals for the SedModel.
     */
    public LayerModel getResidualsLayerModel(String residualType) {
        
        LayerModel layer = new LayerModel(sortedStackedStarTable);
        if (residualType.equals(RATIOS)) {
            layer.setX(SegmentColumn.Column.Spectral_Value.name());
            layer.setY(SegmentColumn.Column.Ratios.name());
        } else if (residualType.equals(RESIDUALS)) {
            layer.setX(SegmentColumn.Column.Spectral_Value.name());
            layer.setY(SegmentColumn.Column.Residuals.name());
        } else {
            throw new IllegalArgumentException("Unrecognized residual type. "
                    + "Must be \"Ratios\" or \"Residuals\"");
        }

        layer.setShowErrorBars(false);
        layer.setShowMarks(true);
        layer.setMarkColor("black");
        
        return layer;
    }
    
    /**
     * Set the baseTable for the underlying sorted 
     * StarTable that represents the function model. 
     * @param baseTable 
     */
    public void setBaseTable(StarTable baseTable) {
        this.baseTable = baseTable;
        
        // update the sorted star table
        updateSortedStarTable();
    }
    
    public StarTable getBaseTable() {
        return this.baseTable;
    }
    
    /**
     * @return True is this function has the Residuals, Ratios, and Model_Values in it's
     * baseTable.
     */
    public boolean hasModelValues() {
        for (int i=0; i<baseTable.getColumnCount(); i++) {
            ColumnInfo c = baseTable.getColumnInfo(i);
            if (StringUtils.equals(c.getName(), Column.Model_Values.name())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Update the sortedStarTable given the current SedModel
     */
    private void updateSortedStarTable() {
        
        // Verify that the function value columns are present - and set the appropriate fields
        // in this function model.
        ColumnIdentifier colId = new ColumnIdentifier(baseTable);
        try {
            // find column containing spectral values
            int col = colId.getColumnIndex(SegmentColumn.Column.Spectral_Value.name());
            
            // create a table sorted by the spectral axis
            sortedStackedStarTable = new SortedStarTable(baseTable, col, true);
            sortedStackedStarTable.setName(baseTable.getName() + FUNCTION_SUFFIX);
            
        } catch (IOException ex) {
            // Any exceptions and this becomes an invalid layer
            throw new IllegalArgumentException("Must have spectral value column in a function model!");
        }
    }
    
    public SortedStarTable getSortedStarTable() {
        return this.sortedStackedStarTable;
    }
    
    public FunctionModel setFunctionColor(String color) {
        this.functionColor = color;
        return this;
    }
    
    public String getFunctionColor() {
        return functionColor;
    }
    
    public FunctionModel setFunctionDash(Double dash) {
        this.functionDash = dash;
        return this;
    }
    
    public double getFunctionDash() {
        return functionDash;
    }
    
    public FunctionModel setFunctionThickness(Integer thickness) {
        this.functionThickness = thickness;
        return this;
    }
    
    public int getFunctionThickness() {
        return functionThickness;
    }
}
