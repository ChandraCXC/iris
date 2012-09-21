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

package cfa.vo.sed.setup;

import cfa.vo.sed.builder.dm.PhotometryPoint;
import cfa.vo.sed.filters.IFilter;
import cfa.vo.sed.setup.validation.AbstractValidable;
import cfa.vo.sed.setup.validation.AbstractValidableParent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;

/**
 *
 * @author olaurino
 */
public class PhotometryPointBuilder extends AbstractValidableParent implements Builder<PhotometryPoint>, Cloneable {

    public PhotometryPointBuilder(String id) {
        this.id = id;
        children = ObservableCollections.observableList(new ArrayList());
        children.addObservableListListener(getValidator());
        spectralAxisConfiguration = new SpectralAxisBuilder();
        children.add(spectralAxisConfiguration);
        fluxAxisConfiguration = new FluxAxisBuilder();
        children.add(fluxAxisConfiguration);
    }
    
    @Override
    public Object clone() {
        try {
            PhotometryPointBuilder cloned = (PhotometryPointBuilder) super.clone();
            cloned.children = ObservableCollections.observableList(new ArrayList());
            cloned.children.addObservableListListener(cloned.getValidator());
            cloned.spectralAxisConfiguration = (SpectralAxisBuilder) cloned.spectralAxisConfiguration.clone();
            cloned.fluxAxisConfiguration = (FluxAxisBuilder) cloned.fluxAxisConfiguration.clone();
            cloned.children.add(cloned.spectralAxisConfiguration);
            cloned.children.add(cloned.fluxAxisConfiguration);
            return cloned;
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(PhotometryPointBuilder.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private FluxAxisBuilder fluxAxisConfiguration;
    public static final String PROP_FLUXAXISCONFIGURATION = "fluxAxisConfiguration";

    /**
     * Get the value of fluxAxisConfiguration
     *
     * @return the value of fluxAxisConfiguration
     */
    public FluxAxisBuilder getFluxAxisBuilder() {
        return fluxAxisConfiguration;
    }

    /**
     * Set the value of fluxAxisConfiguration
     *
     * @param fluxAxisConfiguration new value of fluxAxisConfiguration
     */
    public void setFluxAxisBuilder(FluxAxisBuilder fluxAxisConfiguration) {
        FluxAxisBuilder oldFluxAxisConfiguration = this.fluxAxisConfiguration;
        this.fluxAxisConfiguration = fluxAxisConfiguration;
        propertyChangeSupport.firePropertyChange(PROP_FLUXAXISCONFIGURATION, oldFluxAxisConfiguration, fluxAxisConfiguration);
    }

    private SpectralAxisBuilder spectralAxisConfiguration;
    public static final String PROP_SPECTRALAXISCONFIGURATION = "spectralAxisConfiguration";

    /**
     * Get the value of spectralAxisConfiguration
     *
     * @return the value of spectralAxisConfiguration
     */
    public SpectralAxisBuilder getSpectralAxisBuilder() {
        return spectralAxisConfiguration;
    }

    /**
     * Set the value of spectralAxisConfiguration
     *
     * @param spectralAxisConfiguration new value of spectralAxisConfiguration
     */
    public void setSpectralAxisBuilder(SpectralAxisBuilder spectralAxisConfiguration) {
        SpectralAxisBuilder oldSpectralAxisConfiguration = this.spectralAxisConfiguration;
        this.spectralAxisConfiguration = spectralAxisConfiguration;
        propertyChangeSupport.firePropertyChange(PROP_SPECTRALAXISCONFIGURATION, oldSpectralAxisConfiguration, spectralAxisConfiguration);
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

    private ObservableList<AbstractValidable> children;

    @Override
    protected ObservableList<AbstractValidable> getValidableChildren() {
        return children;
    }

    @Override
    public PhotometryPoint build(IFilter filter, int row) throws Exception {
        PhotometryPoint fp = new PhotometryPoint();
        spectralAxisConfiguration.setAxis(fp.getSpectralAxis());
        spectralAxisConfiguration.build(filter, row);
        fluxAxisConfiguration.setAxis(fp.getFluxAxis());
        fluxAxisConfiguration.build(filter, row);
        fp.setId(id);
        return fp;
    }

    @Override
    public String toString() {
        return id;
    }

}
