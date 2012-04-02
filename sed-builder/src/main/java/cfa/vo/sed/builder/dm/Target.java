/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.builder.dm;

import cfa.vo.sedlib.DoubleParam;
import cfa.vo.sedlib.Segment;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author olaurino
 */
public final class Target implements SegmentComponent {

    private String name;
    public static final String PROP_NAME = "name";

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        propertyChangeSupport.firePropertyChange(PROP_NAME, oldName, name);
    }

    private String publisher = "UNKNOWN";
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

    private Double ra;
    public static final String PROP_RA = "ra";

    /**
     * Get the value of ra
     *
     * @return the value of ra
     */
    public Double getRa() {
        return ra;
    }

    /**
     * Set the value of ra
     *
     * @param ra new value of ra
     */
    public void setRa(Double ra) {
        Double oldRa = this.ra;
        this.ra = ra;
        propertyChangeSupport.firePropertyChange(PROP_RA, oldRa, ra);
    }

    private Double dec;
    public static final String PROP_DEC = "dec";

    /**
     * Get the value of dec
     *
     * @return the value of dec
     */
    public Double getDec() {
        return dec;
    }

    /**
     * Set the value of dec
     *
     * @param dec new value of dec
     */
    public void setDec(Double dec) {
        Double oldDec = this.dec;
        this.dec = dec;
        propertyChangeSupport.firePropertyChange(PROP_DEC, oldDec, dec);
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

    @Override
    public Validation validate() {
        return new Validation();
    }

    @Override
    public void addTo(Segment segment) {
        segment.createTarget().createName().setValue(name);

        DoubleParam raD = new DoubleParam(Double.NaN);
        DoubleParam decD = new DoubleParam(Double.NaN);
        if(ra!=null) {
            raD = new DoubleParam(ra);
        }
        if(dec!=null) {
            decD = new DoubleParam(dec);
        }

        DoubleParam[] pos = new DoubleParam[]{raD, decD};

        segment.createTarget().createPos().setValue(pos);
        
        segment.createCuration().createPublisher().setValue(publisher);
    }

    @Override
    public boolean isValid() {
        return validate().isValid();
    }

    
}
