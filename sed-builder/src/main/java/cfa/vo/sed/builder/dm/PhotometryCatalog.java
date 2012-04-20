/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.builder.dm;

import cfa.vo.sed.setup.validation.Validator;
import cfa.vo.sed.setup.validation.Validation;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author olaurino
 */
public class PhotometryCatalog extends ArrayList<PhotometryCatalogEntry> implements SedBlock {

    public PhotometryCatalog() {
        validator = new Validator();
        validator.add(this);
    }

    List<Segment> generated;

    @Override
    public List<Segment> addTo(ExtSed sed) throws SedException {
        if(generated!=null)
            for(Segment s: generated)
                sed.remove(s);

        generated = new ArrayList();
        for(int i=0; i<size(); i++) {
            generated.addAll(get(i).addTo(sed));
        }
        return generated;
    }
    
    private String publisher;
    public static final String PROP_PUBLISHER = "publisher";

    /**
     * Get the value of publisher
     *
     * @return the value of publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Set the value of publisher
     *
     * @param publisher new value of publisher
     */
    public void setPublisher(String publisher) {
        String oldPublisher = this.publisher;
        this.publisher = publisher;
        propertyChangeSupport.firePropertyChange(PROP_PUBLISHER, oldPublisher, publisher);
    }
    
    private Validator validator;
    public static final String PROP_VALIDATOR = "validator";

    /**
     * Get the value of validator
     *
     * @return the value of validator
     */
    @Override
    public Validator getValidator() {
        return validator;
    }

    /**
     * Set the value of validator
     *
     * @param validator new value of validator
     */
    public void setValidator(Validator validator) {
        Validator oldValidator = this.validator;
        this.validator = validator;
        propertyChangeSupport.firePropertyChange(PROP_VALIDATOR, oldValidator, validator);
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
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public Validation validate() {
        Validation v = new Validation();

        for(int i=0; i<size(); i++) {
            Validation pv = get(i).validate();
            v.getErrors().addAll(pv.getErrors());
            v.getWarnings().addAll(pv.getWarnings());
        }

        return v;
    }

}
