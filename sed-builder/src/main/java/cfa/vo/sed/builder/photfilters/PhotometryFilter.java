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

package cfa.vo.sed.builder.photfilters;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olaurino
 */
public class PhotometryFilter implements Cloneable {
    private String id;
    private String unit;
    private String band;
    private String instrument;
    private String facility;
    private String description;
    private Float wlmean;
    private Float wleff;
    private Float wlmin;
    private Float wlmax;
    private URL curveURL;

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public URL getCurveURL() {
        return curveURL;
    }

    public void setCurveURL(URL curveURL) {
        this.curveURL = curveURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Float getWleff() {
        return wleff;
    }

    public void setWleff(Float wleff) {
        this.wleff = wleff;
    }

    public Float getWlmax() {
        return wlmax;
    }

    public void setWlmax(Float wlmax) {
        this.wlmax = wlmax;
    }

    public Float getWlmean() {
        return wlmean;
    }

    public void setWlmean(Float wlmean) {
        this.wlmean = wlmean;
    }

    public Float getWlmin() {
        return wlmin;
    }

    public void setWlmin(Float wlmin) {
        this.wlmin = wlmin;
    }

    @Override
    public String toString() {
        return getId().split("/")[1];
    }
    
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(PhotometryFilter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
