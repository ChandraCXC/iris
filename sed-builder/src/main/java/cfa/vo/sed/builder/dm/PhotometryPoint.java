/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.builder.dm;

import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author olaurino
 */
public class PhotometryPoint implements Validable {
    private SpectralAxis spectralAxis = new SpectralAxis();
    private FluxAxis fluxAxis = new FluxAxis();
    private Target target = new Target();

    private PointListener pl = new PointListener();

    public PhotometryPoint() {
        pl.add(spectralAxis);
        pl.add(fluxAxis);
        pl.add(target);
        pl.propertyChange(null);
    }

    public Segment get() throws SedException {
        if(!isValid())
            throw new SedException("PhotometryPoint is invalid!");

        Segment segment = new Segment();

        spectralAxis.addTo(segment);
        fluxAxis.addTo(segment);
        target.addTo(segment);

        return segment;

    }

    @Override
    public Validation validate() {
        pl.propertyChange(null);
        return validation;
    }

    @Override
    public boolean isValid() {
        return validate().isValid();
    }

    private Validation validation;
    public static final String PROP_VALIDATION = "validation";

    /**
     * Get the value of validation
     *
     * @return the value of validation
     */
    public Validation getValidation() {
        return validation;
    }

    /**
     * Set the value of validation
     *
     * @param validation new value of validation
     */
    public void setValidation(Validation validation) {
        Validation oldValidation = this.validation;
        this.validation = validation;
        propertyChangeSupport.firePropertyChange(PROP_VALIDATION, oldValidation, validation);
    }
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }


    public FluxAxis getFluxAxis() {
        return fluxAxis;
    }

    public void setFluxAxis(FluxAxis fluxAxis) {
        this.fluxAxis = fluxAxis;
    }

    public SpectralAxis getSpectralAxis() {
        return spectralAxis;
    }

    public void setSpectralAxis(SpectralAxis spectralAxis) {
        this.spectralAxis = spectralAxis;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    private class PointListener implements PropertyChangeListener {

        public List<Validable> validables = new ArrayList();

        public void add(Validable validable) {
            validables.add(validable);
            validable.addPropertyChangeListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent pce) {
            Validation v = new Validation();
            for(Validable validable : validables) {
                Validation valid = validable.validate();
                v.getWarnings().addAll(valid.getWarnings());
                v.getErrors().addAll(valid.getErrors());
            }
            setValidation(v);
        }

    }
}
