/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.science.stacker;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sedlib.common.SedNoDataException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.observablecollections.ObservableCollections;
import spv.util.UnitsException;


/**
 *
 * @author olaurino
 */
public class SedStack implements Cloneable{
    private String name;
   
    public SedStack(String name) {
	this.name = name;
        this.setConf(new Configuration());
    }
    
    public SedStack(String name, List<ExtSed> seds) {
	this.name = name;
	this.setSeds(seds);
	this.setConf(new Configuration());
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    private Configuration conf;

    public static final String PROP_CONF = "conf";

    /**
     * Get the value of conf
     *
     * @return the value of conf
     */
    public Configuration getConf() {
        return conf;
    }

    /**
     * Set the value of conf
     *
     * @param conf new value of conf
     */
    public final void setConf(Configuration conf) {
        Configuration oldConf = this.conf;
        this.conf = conf;
        propertyChangeSupport.firePropertyChange(PROP_CONF, oldConf, conf);
    }
    
    private List<ExtSed> seds = ObservableCollections.observableList(new ArrayList());

    public static final String PROP_SEDS = "seds";

    /**
     * Get the value of seds
     *
     * @return the value of seds
     */
    public List<ExtSed> getSeds() {
	return seds;
    }

    /**
     * Set the value of seds
     *
     * @param seds new value of seds
     */
    public void setSeds(List<ExtSed> seds) {
	List<ExtSed> oldSeds = this.seds;
	this.seds = seds;
	propertyChangeSupport.firePropertyChange(PROP_SEDS, oldSeds, seds);
    }
    
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
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
    
    public PropertyChangeSupport getPropertyChangeSupport() {
	return this.propertyChangeSupport;
    }

//    void removeSeds(List<Integer> selectedSeds) {
//	for (int i=0; i<selectedSeds.size(); i++) {
//	    seds.remove(selectedSeds.get(i));
//	}
//    }
    
    private List<ExtSed> origSeds = ObservableCollections.observableList(new ArrayList());

    public static final String PROP_ORIGSEDS = "origSeds";

    /**
     * Get the value of origSeds
     *
     * @return the value of origSeds
     */
    public List<ExtSed> getOrigSeds() {
	return origSeds;
    }

    /**
     * Set the value of origSeds
     *
     * @param origSeds new value of origSeds
     */
    public void setOrigSeds(List<ExtSed> origSeds) {
	List<ExtSed> oldOrigSeds = this.origSeds;
	this.origSeds = origSeds;
	propertyChangeSupport.firePropertyChange(PROP_ORIGSEDS, oldOrigSeds, origSeds);
    }
    
    private ExtSed sedBuilderStack; //= new ExtSed("Stack", true);

    public static final String PROP_SEDBUILDERSTACK = "sedBuilderStack";

    /**
     * Get the value of sedBuilderStack
     *
     * @return the value of sedBuilderStack
     */
    public ExtSed getSedBuilderStack() {
	return sedBuilderStack;
    }

    /**
     * Set the value of sedBuilderStack
     *
     * @param sedBuilderStack new value of sedBuilderStack
     */
    public void setSedBuilderStack(ExtSed sedBuilderStack) {
	ExtSed oldSedBuilderStack = this.sedBuilderStack;
	this.sedBuilderStack = sedBuilderStack;
	propertyChangeSupport.firePropertyChange(PROP_SEDBUILDERSTACK, oldSedBuilderStack, sedBuilderStack);
    }

    public List<String> getSpectralUnits() {
	try {
	    List<String> units = new ArrayList();
	    for (ExtSed sed : this.seds) {
		for (int i=0; i<sed.getNumberOfSegments(); i++) {
		    units.add(sed.getSegment(i).getSpectralAxisUnits());
		}
	    }
	    return units;
	} catch (SedNoDataException ex) {
	    Logger.getLogger(SedStack.class.getName()).log(Level.SEVERE, null, ex);
	}
	return null;
    }

    public List<String> getFluxUnits() {
	try {
	    List<String> units = new ArrayList();
	    for (ExtSed sed : this.seds) {
		for (int i=0; i<sed.getNumberOfSegments(); i++) {
		    units.add(sed.getSegment(i).getFluxAxisUnits());
		}
	    }
	    return units;
	} catch (SedNoDataException ex) {
	    Logger.getLogger(SedStack.class.getName()).log(Level.SEVERE, null, ex);
	}
	return null;
    }
    
    public void add(ExtSed sed) throws SedNoDataException, SedException, UnitsException {
	//ExtSed nsed = ExtSed.flatten(sed, sed.getSegment(0).getSpectralAxisUnits(), sed.getSegment(0).getFluxAxisUnits());
	//nsed.addAttachment("sedstacker:redshift", sed.getAttachment("sedstacker:redshift"));
	//nsed.addAttachment("sedstacker:normConst", sed.getAttachment("sedstacker:redshift"));
	seds.add(sed);
	origSeds.add(sed);
    }
    
    public void add(List<ExtSed> sedList) throws SedNoDataException, SedException, UnitsException {
	for (ExtSed sed : sedList) {
	    //ExtSed nsed = ExtSed.flatten(sed, sed.getSegment(0).getSpectralAxisUnits(), sed.getSegment(0).getFluxAxisUnits());
	    //nsed.
	    //nsed.addAttachment("sedstacker:redshift", sed.getAttachment("sedstacker:redshift"));
	    //nsed.addAttachment("sedstacker:normConst", sed.getAttachment("sedstacker:redshift"));
	    seds.add(sed);
	    origSeds.add(sed);
	}
    }
    
    public SedStack copy() throws CloneNotSupportedException {
	return (SedStack) this.clone();
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void setSed(int i, ExtSed sed) {
	seds.set(i, sed);
    }
}
