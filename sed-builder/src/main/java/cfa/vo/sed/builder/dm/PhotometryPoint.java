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

import cfa.vo.sed.setup.validation.AbstractValidable;
import cfa.vo.sed.setup.validation.AbstractValidableParent;
import cfa.vo.sedlib.DataID;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.TextParam;
import cfa.vo.sedlib.common.SedException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author olaurino
 */
public class PhotometryPoint extends AbstractValidableParent implements SegmentComponent {

    public PhotometryPoint() {
        super();
        spectralAxis = new SpectralAxis();
        fluxAxis = new FluxAxis();
    }

    @Override
    public void addTo(Segment segment) throws SedException {
        if (!spectralAxis.validate().isValid() || !fluxAxis.validate().isValid()) {
             throw new SedException("PhotometryPoint is invalid!");
        }

        spectralAxis.addTo(segment);
        fluxAxis.addTo(segment);
        DataID did = new DataID();
        did.setDatasetID(new TextParam(id));
        segment.setDataID(did);
    }

    private SpectralAxis spectralAxis;
    public static final String PROP_SPECTRALAXIS = "spectralAxis";

    /**
     * Get the value of spectralAxis
     *
     * @return the value of spectralAxis
     */
    public SpectralAxis getSpectralAxis() {
        return spectralAxis;
    }

    /**
     * Set the value of spectralAxis
     *
     * @param spectralAxis new value of spectralAxis
     */
    public void setSpectralAxis(SpectralAxis spectralAxis) {
        SpectralAxis oldSpectralAxis = this.spectralAxis;
        this.spectralAxis = spectralAxis;
        propertyChangeSupport.firePropertyChange(PROP_SPECTRALAXIS, oldSpectralAxis, spectralAxis);
    }

    private FluxAxis fluxAxis;
    public static final String PROP_FLUXAXIS = "fluxAxis";

    /**
     * Get the value of fluxAxis
     *
     * @return the value of fluxAxis
     */
    public FluxAxis getFluxAxis() {
        return fluxAxis;
    }

    /**
     * Set the value of fluxAxis
     *
     * @param fluxAxis new value of fluxAxis
     */
    public void setFluxAxis(FluxAxis fluxAxis) {
        FluxAxis oldFluxAxis = this.fluxAxis;
        this.fluxAxis = fluxAxis;
        propertyChangeSupport.firePropertyChange(PROP_FLUXAXIS, oldFluxAxis, fluxAxis);
    }

    private String id;
    public static final String PROP_ID = "id";

    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public String getId() {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    public void setId(String id) {
        String oldId = this.id;
        this.id = id;
        propertyChangeSupport.firePropertyChange(PROP_ID, oldId, id);
    }


    private List<AbstractValidable> children;

    @Override
    protected List<AbstractValidable> getValidableChildren() {
        if (children == null) {
            children = new ArrayList();
            children.add(spectralAxis);
            children.add(fluxAxis);
        }
        return children;
    }
}
