/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.builder.dm;

import cfa.vo.sedlib.Segment;

/**
 *
 * @author olaurino
 */
public final class ExtendedTarget extends Target {

    public ExtendedTarget(Target target, String publisher) {
        setName(target.getName());
        setRa(target.getRa());
        setDec(target.getDec());
        setPublisher(publisher);
    }

    public ExtendedTarget() {
        
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

    @Override
    public void addTo(Segment segment) {
        super.addTo(segment);
        segment.createCuration().createPublisher().setValue(publisher);
    }

}
