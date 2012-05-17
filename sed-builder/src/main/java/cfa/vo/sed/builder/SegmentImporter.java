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

package cfa.vo.sed.builder;

import cfa.vo.sed.filters.FileFormatManager;
import cfa.vo.sed.setup.SetupManager;
import cfa.vo.sed.setup.ErrorType;
import cfa.vo.sed.setup.ISetup;
import cfa.vo.sed.filters.FilterException;
import cfa.vo.sed.filters.IFileFormat;
import cfa.vo.sed.filters.IFilter;
import cfa.vo.sed.quantities.IAxisMetadata;
import cfa.vo.sed.quantities.XUnit;
import cfa.vo.sed.quantities.AxisMetadata;
import cfa.vo.sed.quantities.SPVYQuantity;
import cfa.vo.sed.quantities.SPVYUnit;
import cfa.vo.sed.quantities.XQuantity;
import cfa.vo.sedlib.DoubleParam;
import cfa.vo.sedlib.Field;
import cfa.vo.sedlib.Interval;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sedlib.common.Utypes;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author omarlaurino
 */
public class SegmentImporter {

    public static List<Segment> getSegments(URL configURL) throws IOException, SedInconsistentException, MalformedURLException, FilterException {
        return getSegments(SetupManager.read(configURL));
    }

    public static List<Segment> getSegments(List<ISetup> confList) throws SedInconsistentException, MalformedURLException, IOException, FilterException {

        List<Segment> segList = new ArrayList();

        for(ISetup conf : confList) {
            segList.addAll(getSegments(conf));
        }

        return segList;
    }

    public static List<Segment> getSegments(ISetup conf) throws SedInconsistentException, MalformedURLException, IOException, FilterException {
        
        List<Segment> segments = new ArrayList();
        ErrorType errorType = ErrorType.valueOf(conf.getErrorType());

        Segment segment;

        URL fileLoc = new URL(conf.getFileLocation());
        IFileFormat format = FileFormatManager.getInstance().getFormatByName(conf.getFormatName());
        Integer pos = conf.getPositionInFile();
        Integer segmentNumber = pos==null ? 0 : pos;

        switch(errorType) {
            case Unknown:
                segment = new Segment();

                fillSegment(segment, conf);

                segments.add(segment);
                break;
                
            case ConstantValue:
                segment = new Segment();

                int ndata = fillSegment(segment, conf);

                double[] errors = new double[ndata];

                for(int i=0; i<ndata; i++) {
                    errors[i] = Double.valueOf(conf.getConstantErrorValue());
                }

                segment.setDataValues(errors, Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR);

                SPVYQuantity yQuantity = SPVYQuantity.valueOf(conf.getYAxisQuantity());
                SPVYUnit yUnit = SPVYUnit.valueOf(conf.getYAxisUnit());
                IAxisMetadata ymd = new AxisMetadata(yQuantity, yUnit);

                try {
                    Field di = segment.getDataInfo(Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR);
                    di.setUcd("stat.error;"+ymd.getUCD());
                    di.setUnit(ymd.getUnitString());
                    di.setName("StatError");
                    di.setId("StatError");
                    segment.setDataInfo(di, Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR);
                } catch (SedNoDataException ex) {
                    Logger.getLogger(SegmentImporter.class.getName()).log(Level.SEVERE, null, ex);
                }

                segments.add(segment);
                break;

            case SymmetricParameter:
                segment = new Segment();

                List<ISegmentMetadata> meta = format.getFilter(fileLoc).getMetadata();

                List<ISegmentParameter> params = meta.get(segmentNumber).getParameters();

                Double error = 0.;

                String name = "StatError";

                for(ISegmentParameter param : params) {
                    if(conf.getSymmetricErrorParameter().equals(param.getName())) {
                        if(param.getValue()!=null) {
                            error = ((Number) param.getValue()).doubleValue();
                            name = param.getName();
                        }
                    }
                }

                ndata = fillSegment(segment, conf);

                errors = new double[ndata];

                for(int i=0; i<ndata; i++) {
                    errors[i] = error;
                }

                segment.setDataValues(errors, Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR);

                yQuantity = SPVYQuantity.valueOf(conf.getYAxisQuantity());
                yUnit = SPVYUnit.valueOf(conf.getYAxisUnit());
                ymd = new AxisMetadata(yQuantity, yUnit);

                try {
                    Field di = segment.getDataInfo(Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR);
                    di.setUcd("stat.error;"+ymd.getUCD());
                    di.setUnit(ymd.getUnitString());
                    di.setName(name);
                    di.setId(name);
                    segment.setDataInfo(di, Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR);
                } catch (SedNoDataException ex) {
                    Logger.getLogger(SegmentImporter.class.getName()).log(Level.SEVERE, null, ex);
                }

                segments.add(segment);
                break;
            case AsymmetricParameter:
                segment = new Segment();

                meta = format.getFilter(fileLoc).getMetadata();
                params = meta.get(segmentNumber).getParameters();

                Double upperError = 0.;
                Double lowerError = 0.;

                for(ISegmentParameter param : params) {
                    if(conf.getUpperErrorParameter().equals(param.getName())) {
                        if(param.getValue()!=null)
                            upperError = ((Number) param.getValue()).doubleValue();
                    }
                }

                for(ISegmentParameter param : params) {
                    if(conf.getLowerErrorParameter().equals(param.getName())) {
                        if(param.getValue()!=null)
                            lowerError = ((Number) param.getValue()).doubleValue();
                    }
                }

                segment.createChar().createFluxAxis().createAccuracy().createStatErrHigh().setValue(upperError);
                segment.createChar().createFluxAxis().createAccuracy().createStatErrLow().setValue(lowerError);

                yQuantity = SPVYQuantity.valueOf(conf.getYAxisQuantity());
                yUnit = SPVYUnit.valueOf(conf.getYAxisUnit());
                ymd = new AxisMetadata(yQuantity, yUnit);

                segment.createChar().createFluxAxis().createAccuracy().createStatErrHigh().setUnit(ymd.getUnitString());
                segment.createChar().createFluxAxis().createAccuracy().createStatErrHigh().setUcd("stat.error;"+ymd.getUCD());
                segment.createChar().createFluxAxis().createAccuracy().createStatErrLow().setUnit(ymd.getUnitString());
                segment.createChar().createFluxAxis().createAccuracy().createStatErrLow().setUcd("stat.error;"+ymd.getUCD());

                fillSegment(segment, conf);

                segments.add(segment);

                break;
            case SymmetricColumn:
                segment = new Segment();

                Number[] errNumbers = (Number[]) format.getFilter(fileLoc).getData(segmentNumber, conf.getSymmetricErrorColumnNumber());

                errors = new double[errNumbers.length];

                fillSegment(segment, conf);

                for(int i=0; i<errNumbers.length; i++) {
                    if(errNumbers[i]!=null)
                        errors[i] = errNumbers[i].doubleValue();
                    else
                        errors[i] = Double.NaN;
                }

                segment.setDataValues(errors, Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR);

                yQuantity = SPVYQuantity.valueOf(conf.getYAxisQuantity());
                yUnit = SPVYUnit.valueOf(conf.getYAxisUnit());
                ymd = new AxisMetadata(yQuantity, yUnit);

                segment.createChar().createFluxAxis().createAccuracy().createStatError().setUnit(ymd.getUnitString());
                segment.createChar().createFluxAxis().createAccuracy().createStatError().setUcd("stat.error;"+ymd.getUCD());

                ISegmentMetadata metadata = format.getFilter(fileLoc).getMetadata().get(segmentNumber);

                try {
                    Field di = segment.getDataInfo(Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR);
                    di.setUcd("stat.error;"+ymd.getUCD());
                    di.setUnit(ymd.getUnitString());
                    di.setName(metadata.getColumns().get(conf.getSymmetricErrorColumnNumber()).getName());
                    di.setId(metadata.getColumns().get(conf.getSymmetricErrorColumnNumber()).getName());
                    segment.setDataInfo(di, Utypes.SEG_DATA_FLUXAXIS_ACC_STATERR);
                } catch (SedNoDataException ex) {
                    Logger.getLogger(SegmentImporter.class.getName()).log(Level.SEVERE, null, ex);
                }

                segments.add(segment);
                break;
                
            case AsymmetricColumn:
                segment = new Segment();

                Number[] lowErrNumbers = (Number[]) format.getFilter(fileLoc).getData(segmentNumber, conf.getLowerErrorColumnNumber());
                Number[] hiErrNumbers = (Number[]) format.getFilter(fileLoc).getData(segmentNumber, conf.getUpperErrorColumnNumber());

                double[] lowerrors = new double[lowErrNumbers.length];
                double[] hierrors = new double[hiErrNumbers.length];

                for(int i=0; i<lowErrNumbers.length; i++) {
                    lowerrors[i] = lowErrNumbers[i].doubleValue();
                    hierrors[i] = hiErrNumbers[i].doubleValue();
                }

                segment.setDataValues(lowerrors, Utypes.SEG_DATA_FLUXAXIS_ACC_STATERRLOW);
                segment.setDataValues(hierrors, Utypes.SEG_DATA_FLUXAXIS_ACC_STATERRHIGH);

                yQuantity = SPVYQuantity.valueOf(conf.getYAxisQuantity());
                yUnit = SPVYUnit.valueOf(conf.getYAxisUnit());
                ymd = new AxisMetadata(yQuantity, yUnit);

                segment.createChar().createFluxAxis().createAccuracy().createStatErrHigh().setUnit(ymd.getUnitString());
                segment.createChar().createFluxAxis().createAccuracy().createStatErrHigh().setUcd("stat.error;"+ymd.getUCD());
                segment.createChar().createFluxAxis().createAccuracy().createStatErrLow().setUnit(ymd.getUnitString());
                segment.createChar().createFluxAxis().createAccuracy().createStatErrLow().setUcd("stat.error;"+ymd.getUCD());

                try {
                    Field di = segment.getDataInfo(Utypes.SEG_DATA_FLUXAXIS_ACC_STATERRLOW);
                    di.setUcd(ymd.getUCD());
                    di.setUnit(ymd.getUnitString());
                    di = segment.getDataInfo(Utypes.SEG_DATA_FLUXAXIS_ACC_STATERRHIGH);
                    di.setUcd(ymd.getUCD());
                    di.setUnit(ymd.getUnitString());
                } catch (SedNoDataException ex) {
                    Logger.getLogger(SegmentImporter.class.getName()).log(Level.SEVERE, null, ex);
                }

                fillSegment(segment, conf);

                segments.add(segment);
                break;
            
        }



        return segments;
    }

    public static List<ISegmentMetadata> getSegmentsMetadata(URL file, IFileFormat format) throws IOException, FilterException {
        
        IFilter filter = format.getFilter(file);

        return filter.getMetadata();

    }

    private static int fillSegment(Segment segment, ISetup conf) throws MalformedURLException, IOException, FilterException {

        URL fileLoc = new URL(conf.getFileLocation());
        IFileFormat format = FileFormatManager.getInstance().getFormatByName(conf.getFormatName().toUpperCase());
        Integer pos = conf.getPositionInFile();
        Integer segmentNumber = pos==null ? 0 : pos;
        
        ISegmentMetadata metadata = format.getFilter(fileLoc).getMetadata().get(segmentNumber);

        SPVYQuantity yQuantity = SPVYQuantity.valueOf(conf.getYAxisQuantity());
        XQuantity xQuantity = XQuantity.valueOf(conf.getXAxisQuantity());
        SPVYUnit yUnit = SPVYUnit.valueOf(conf.getYAxisUnit());
        XUnit xUnit = XUnit.valueOf(conf.getXAxisUnit());

        IAxisMetadata ymd = new AxisMetadata(yQuantity, yUnit);
        IAxisMetadata xmd = new AxisMetadata(xQuantity, xUnit);

        Number[] xnumbers = (Number[]) format.getFilter(fileLoc).getData(segmentNumber, conf.getXAxisColumnNumber());
        Number[] ynumbers = (Number[]) format.getFilter(fileLoc).getData(segmentNumber, conf.getYAxisColumnNumber());

        double[] xdata = new double[xnumbers.length];
        double[] ydata = new double[ynumbers.length];

        for(int i=0; i<xnumbers.length; i++) {
            if(xnumbers[i]!=null)
                xdata[i] = xnumbers[i].doubleValue();
            else
                xdata[i] = Double.NaN;
            if(ynumbers[i]!=null)
                ydata[i] = ynumbers[i].doubleValue();
            else
                ydata[i] = Double.NaN;
        }

        segment.createCuration().createPublisher().setValue(conf.getPublisher());

        if(conf.getTargetName()==null)
            segment.createTarget().createName().setValue("UNKNOWN");
        else
            segment.createTarget().createName().setValue(
                    conf.getTargetName().isEmpty()? "UNKNOWN" : conf.getTargetName());
        
        DoubleParam ra = conf.getTargetRa()!=null? new DoubleParam(conf.getTargetRa()) : new DoubleParam(Double.NaN);
        DoubleParam dec = conf.getTargetDec()!=null? new DoubleParam(conf.getTargetDec()) : new DoubleParam(Double.NaN);

        segment.getTarget().createPos().setValue(new DoubleParam[]{ra, dec});        

        segment.createChar().createFluxAxis().setUnit(ymd.getUnitString());
        segment.getChar().getFluxAxis().setUcd(ymd.getUCD());

        segment.setFluxAxisValues(ydata);
        segment.setSpectralAxisValues(xdata);
        segment.getChar().createSpectralAxis().setUnit(xUnit.getString());
        segment.getChar().getSpectralAxis().setName(metadata.getColumns().get(conf.getXAxisColumnNumber()).getName());
        segment.getChar().getFluxAxis().setName(metadata.getColumns().get(conf.getYAxisColumnNumber()).getName());
        
        try {//TODO Necessary to check errors?
            Field xDI = segment.getDataInfo("Spectrum.Data.SpectralAxis.Value");
            xDI.setId(metadata.getColumns().get(conf.getXAxisColumnNumber()).getName());
            xDI.setName(metadata.getColumns().get(conf.getXAxisColumnNumber()).getName());
            segment.setDataInfo(xDI, "Spectrum.Data.SpectralAxis.Value");

            Field yDI = segment.getDataInfo("Spectrum.Data.FluxAxis.Value");
            yDI.setId(metadata.getColumns().get(conf.getYAxisColumnNumber()).getName());
            yDI.setName(metadata.getColumns().get(conf.getYAxisColumnNumber()).getName());
            segment.setDataInfo(yDI, "Spectrum.Data.FluxAxis.Value");

            segment.setSpectralAxisUnits(xUnit.getString());
            segment.setFluxAxisUnits(yUnit.getString());
        } catch (SedInconsistentException ex) {
            Logger.getLogger(SegmentImporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SedNoDataException ex) {
            Logger.getLogger(SegmentImporter.class.getName()).log(Level.SEVERE, null, ex);
        } 

        segment.createChar().createSpatialAxis().createCoverage().createLocation().setValue(new DoubleParam[]{ra, dec});
        segment.createChar().createSpatialAxis().createCoverage().createBounds().setExtent(new DoubleParam(Double.NaN));
        segment.createChar().createTimeAxis().createCoverage().createLocation().setValue(new DoubleParam[]{new DoubleParam(Double.NaN), new DoubleParam(Double.NaN)});
        segment.createChar().createTimeAxis().createCoverage().createBounds().setExtent(new DoubleParam(Double.NaN));
        segment.createChar().createSpectralAxis().createCoverage().createLocation().setValue(new DoubleParam[]{new DoubleParam(Double.NaN), new DoubleParam(Double.NaN)});
        segment.createChar().createSpectralAxis().createCoverage().createBounds().setExtent(new DoubleParam(Double.NaN));
        Interval interval = new Interval();
        interval.setMin(new DoubleParam(Double.NaN));
        interval.setMax(new DoubleParam(Double.NaN));
        segment.createChar().createSpectralAxis().createCoverage().createBounds().setRange(interval);

        segment.createChar().createSpectralAxis().setUcd(xmd.getUCD());
        segment.createChar().createFluxAxis().setUcd(ymd.getUCD());

        segment.createCoordSys();
        segment.createDataID();

        return xdata.length;

    }

}
