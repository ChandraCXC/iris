/**
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
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

package cfa.vo.iris.sed.stil;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.PrimitiveArrayColumn;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.units.UnitsManager;
import cfa.vo.iris.units.XUnit;
import cfa.vo.iris.units.YUnit;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sedlib.common.Utypes;
import cfa.vo.utils.Default;

public class SegmentStarTable extends ColumnStarTable {
    
    private static final Logger logger = Logger.getLogger(SegmentStarTable.class.getName());
    
    private Segment segment;
    private XUnit specUnits;
    private YUnit fluxUnits;

    private double[] specValues;
    private double[] fluxValues;

    private double[] specErrValues;
    private double[] specErrValuesLo;
    private double[] fluxErrValues;
    private double[] fluxErrValuesLo;
    
    private static final UnitsManager units = Default.getInstance().getUnitsManager();
    
    public enum Column {
        SPECTRAL_COL("X axis values", "iris.spec.value"),
        SPECTRAL_ERR_HI("X axis error values", "iris.spec.value"),
        SPECTRAL_ERR_LO("X axis low error values", "iris.spec.value"),
        FLUX_COL("Y axis values", "iris.flux.value"),
        FLUX_ERR_HI("Y axis error values", "iris.flux.value"),
        FLUX_ERR_LO("Y axis low error values", "iris.flux.value");
        
        public String description;
        public String utype;
        private ColumnInfo columnInfo;
        
        private Column(String description, String utype) {
            this.description = description;
            this.utype = utype;
            this.columnInfo = new ColumnInfo(name(), Double.class, description);
            columnInfo.setUtype(utype);
        }
        
        public ColumnInfo getColumnInfo() {
            return new ColumnInfo(columnInfo);
        }
    }
    
    public SegmentStarTable(Segment segment) 
            throws SedNoDataException, UnitsException, SedInconsistentException {
        this(segment, UUID.randomUUID().toString(), 
                units.newXUnits(segment.getSpectralAxisUnits()),
                units.newYUnits(segment.getFluxAxisUnits()));
    }
    
    public SegmentStarTable(Segment segment, String id, XUnit xunit, YUnit yunit) 
            throws UnitsException, SedNoDataException, SedInconsistentException 
    {
        this.segment = segment;
        this.specUnits = xunit;
        this.fluxUnits = yunit;
        this.setName(id);
        
        // Add spectral data points
        this.specValues = units.convertX(
                segment.getSpectralAxisValues(), 
                units.newXUnits(segment.getSpectralAxisUnits()), 
                xunit);
        addColumn(PrimitiveArrayColumn.makePrimitiveColumn(
                Column.SPECTRAL_COL.getColumnInfo(), (Object) specValues));
        
        // Add flux data points
        this.fluxValues = units.convertY(
                segment.getFluxAxisValues(), 
                specValues,
                units.newYUnits(segment.getFluxAxisUnits()), 
                specUnits,
                yunit);
        addColumn(PrimitiveArrayColumn.makePrimitiveColumn(
                Column.FLUX_COL.getColumnInfo(), (Object) fluxValues));
        
        // Try to add spectral error columns
        this.specErrValues = (double[]) getDataFromSegment(Utypes.SEG_DATA_SPECTRALAXIS_ACC_STATERR);
        if (isEmpty(specErrValues)) {
            specErrValues = (double[]) getDataFromSegment(Utypes.SEG_DATA_SPECTRALAXIS_ACC_STATERRHIGH);
            specErrValuesLo = (double[]) getDataFromSegment(Utypes.SEG_DATA_SPECTRALAXIS_ACC_STATERRLOW);
        }
        if (!isEmpty(specErrValues)) {
            addColumn(PrimitiveArrayColumn.makePrimitiveColumn(
                    Column.SPECTRAL_ERR_HI.getColumnInfo(), (Object) specErrValues));
        }
        if (!isEmpty(specErrValuesLo)) {
            addColumn(PrimitiveArrayColumn.makePrimitiveColumn(
                    Column.SPECTRAL_ERR_LO.getColumnInfo(), (Object) specErrValuesLo));
        }

        // Try to add flux error values
        this.fluxErrValues = (double[]) getDataFromSegment(Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR);
        if (isEmpty(fluxErrValues)) {
            fluxErrValues = (double[]) getDataFromSegment(Utypes.SEG_DATA_FLUXAXIS_ACC_STATERRHIGH);
            fluxErrValuesLo = (double[]) getDataFromSegment(Utypes.SEG_DATA_FLUXAXIS_ACC_STATERRLOW);
        }
        if (!isEmpty(fluxErrValues)) {
            addColumn(PrimitiveArrayColumn.makePrimitiveColumn(
                    Column.FLUX_ERR_HI.getColumnInfo(), (Object) fluxErrValues));
        }
        if (!isEmpty(fluxErrValuesLo)) {
            addColumn(PrimitiveArrayColumn.makePrimitiveColumn(
                    Column.FLUX_ERR_LO.getColumnInfo(), (Object) fluxErrValuesLo));
        }
    }
    
    private double[] getDataFromSegment(int utype) {
        double[] ret = null;
        try {
            ret = (double[]) segment.getData().getDataValues(utype);
        } catch (SedNoDataException | SedInconsistentException e) {
            logger.warning("Cannot read data for segment for utype: " + e.getLocalizedMessage());
        }
        return ret;
    }
    
    private static final boolean isEmpty(double[] data) {
        boolean ret = ArrayUtils.isEmpty(data);
        
        if (ret) return ret;
        
        for (int i=0; i<data.length; i++) {
            if (!Double.isNaN(data[i])) return false;
        }
        
        return true;
    }

    public void setXUnits(XUnit newUnit) throws UnitsException {
        specValues = units.convertX(specValues, specUnits, newUnit);
        specUnits = newUnit;
    }
    
    public void setYUnits(YUnit newUnit) throws UnitsException {
        fluxValues = units.convertY(fluxValues, specValues, fluxUnits, specUnits, newUnit);
        fluxUnits = newUnit;
    }
    
    protected Object[] getPointRow(int irow) {
        return new Object[] {specValues[(int) irow], fluxValues[(int) irow]};
    }

    @Override
    public long getRowCount() {
        return segment.getData().getLength();
    }
}
