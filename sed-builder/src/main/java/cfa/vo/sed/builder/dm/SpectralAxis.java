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

package cfa.vo.sed.builder.dm;

import cfa.vo.sed.setup.validation.Validation;
import cfa.vo.sed.builder.photfilters.PhotometryFilter;
import cfa.vo.sed.quantities.XQuantity;
import cfa.vo.sedlib.DoubleParam;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sedlib.common.Utypes;

/**
 *
 * @author olaurino
 */
public class SpectralAxis extends AbstractAxis<XQuantity> {

    private String mode;
    public static final String PROP_MODE = "mode";

    /**
     * Get the value of mode
     *
     * @return the value of mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * Set the value of mode
     *
     * @param mode new value of mode
     */
    public void setMode(String mode) {
        String oldMode = this.mode;
        this.mode = mode;
        propertyChangeSupport.firePropertyChange(PROP_MODE, oldMode, mode);
    }

    private Double binmin;
    public static final String PROP_BINMIN = "binmin";

    /**
     * Get the value of binmin
     *
     * @return the value of binmin
     */
    public Double getBinmin() {
        return binmin;
    }

    /**
     * Set the value of binmin
     *
     * @param binmin new value of binmin
     */
    public void setBinmin(Double binmin) {
        Double oldBinmin = this.binmin;
        this.binmin = binmin;
        propertyChangeSupport.firePropertyChange(PROP_BINMIN, oldBinmin, binmin);
    }

    private Double binmax;
    public static final String PROP_BINMAX = "binmax";

    /**
     * Get the value of binmax
     *
     * @return the value of binmax
     */
    public Double getBinmax() {
        return binmax;
    }

    /**
     * Set the value of binmax
     *
     * @param binmax new value of binmax
     */
    public void setBinmax(Double binmax) {
        Double oldBinmax = this.binmax;
        this.binmax = binmax;
        propertyChangeSupport.firePropertyChange(PROP_BINMAX, oldBinmax, binmax);
    }

    private PhotometryFilter filter;
    public static final String PROP_FILTER = "filter";

    /**
     * Get the value of filter
     *
     * @return the value of filter
     */
    public PhotometryFilter getFilter() {
        return filter;
    }

    /**
     * Set the value of filter
     *
     * @param filter new value of filter
     */
    public void setFilter(PhotometryFilter filter) {
        PhotometryFilter oldFilter = this.filter;
        this.filter = filter;
        propertyChangeSupport.firePropertyChange(PROP_FILTER, oldFilter, filter);
    }


    @Override
    public void addTo(Segment segment) throws SedException {

        if(mode.equals("Single Value") || mode.equals("Single Value Column")) {
            segment.createChar().createSpectralAxis().setUcd(getQuantity().getUCD());
            segment.setSpectralAxisValues(new double[]{getValue()});
            segment.setSpectralAxisUnits(getUnit().getString());
            segment.createChar().createSpectralAxis().setUnit(getUnit().getString());
        }

        if(mode.equals("Passband")) {
            segment.createChar().createSpectralAxis().setUcd(getQuantity().getUCD());
            segment.createChar().createSpectralAxis().setUnit(getUnit().getString());
            segment.setSpectralAxisValues(new double[]{(binmax+binmin)/2});
            segment.createChar().createSpectralAxis().setValueByUtype(Utypes.SEG_CHAR_SPECTRALAXIS_COV_BOUNDS_MIN, new DoubleParam(binmin));
            segment.createChar().createSpectralAxis().setValueByUtype(Utypes.SEG_CHAR_SPECTRALAXIS_COV_BOUNDS_MAX, new DoubleParam(binmax));
            segment.setSpectralAxisUnits(getUnit().getString());
        }

        if(mode.equals("Photometry Filter")) {
            segment.createChar().createSpectralAxis().setUcd(XQuantity.WAVELENGTH.getUCD());
            segment.createChar().createSpectralAxis().setUnit(filter.getUnit());
            segment.setSpectralAxisValues(new double[]{filter.getWleff()});
            segment.setSpectralAxisUnits(filter.getUnit());
            //TODO Add filter information
        }

    }

    @Override
    public Validation validate() {
        Validation v = new Validation();

        if(mode == null || !mode.matches("Single Value Column|Single Value|Passband|Photometry Filter"))
            v.addError("Missing X Axis Type. Please select a Type.");

        if(mode!=null) {
            if(mode.equals("Single Value") && (getValue()==null || getValue().isNaN())) {
                v.addError("Missing/Invalid X Axis Value");
            }
            
            if(mode.equals("Single Value Column") && (getValue()==null || getValue().isNaN())) {
                v.addError("Missing/Invalid X Axis Value");
            }

            if(mode.equals("Passband") && (binmin==null || Double.isNaN(binmin))) {
                v.addError("Missing/Invalid X Axis Bin Min");
            }

            if(mode.equals("Passband") && (binmax==null || Double.isNaN(binmax))) {
                v.addError("Missing/Invalid X Axis Bin Max");
            }

            if(mode.equals("Photometry Filter") && filter==null) {
                v.addError("No Photometry Filter selected");
            }

        }

        if(getQuantity()==null && (mode!=null && !mode.equals("Photometry Filter")))
            v.addError("Missing X Axis Quantity");

        return v;
    }

    

}
