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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.science.stacker;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sedlib.Segment;
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
public class SedStack implements Cloneable {
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
    
//    private ExtSed sedBuilderStack = new ExtSed(this.name);
//
//    public static final String PROP_SEDBUILDERSTACK = "sedBuilderStack";
//
//    /**
//     * Get the value of sedBuilderStack
//     *
//     * @return the value of sedBuilderStack
//     */
//    public ExtSed getSedBuilderStack() {
//	return sedBuilderStack;
//    }
//
//    /**
//     * Set the value of sedBuilderStack
//     *
//     * @param sedBuilderStack new value of sedBuilderStack
//     */
//    public void setSedBuilderStack(ExtSed sedBuilderStack) {
//	ExtSed oldSedBuilderStack = this.sedBuilderStack;
//	this.sedBuilderStack = sedBuilderStack;
//	propertyChangeSupport.firePropertyChange(PROP_SEDBUILDERSTACK, oldSedBuilderStack, sedBuilderStack);
//    }

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
	ExtSed nsed = ExtSed.flatten(sed, sed.getSegment(0).getSpectralAxisUnits(), sed.getSegment(0).getFluxAxisUnits());
	nsed.setId(sed.getId());
	nsed.addAttachment(SedStackerAttachments.ORIG_REDSHIFT, sed.getAttachment(SedStackerAttachments.ORIG_REDSHIFT));
	nsed.addAttachment(SedStackerAttachments.REDSHIFT, sed.getAttachment(SedStackerAttachments.REDSHIFT));
	nsed.addAttachment(SedStackerAttachments.NORM_CONSTANT, sed.getAttachment(SedStackerAttachments.NORM_CONSTANT));
	
	seds.add(nsed);
	origSeds.add(sed);
	//Segment seg = ExtSed.flatten(sed, sed.getSegment(0).getSpectralAxisUnits(), sed.getSegment(0).getFluxAxisUnits()).getSegment(0);
	//sedBuilderStack.addSegment(seg);
    }
    
    public void add(List<ExtSed> sedList) throws SedNoDataException, SedException, UnitsException {
	for (ExtSed sed : sedList) {
	    this.add(sed);
	}
    }
    
    public void remove(int i) {
	seds.remove(i);
	origSeds.remove(i);
	//sedBuilderStack.removeSegment(i);
    }
    
    public void remove(ExtSed sed) {
	seds.remove(sed);
	origSeds.remove(sed);
//	ExtSed nsed = new ExtSed(this.name);
//	for (ExtSed sed0 : seds) {
//	    Segment seg;
//	    try {
//		seg = ExtSed.flatten(sed0, sed0.getSegment(0).getSpectralAxisUnits(), sed0.getSegment(0).getFluxAxisUnits()).getSegment(0);
//		nsed.addSegment(seg);
//	    } catch (SedException ex) {
//		Logger.getLogger(SedStack.class.getName()).log(Level.SEVERE, null, ex);
//	    } catch (UnitsException ex) {
//		Logger.getLogger(SedStack.class.getName()).log(Level.SEVERE, null, ex);
//	    }
//	}
//	setSedBuilderStack(nsed);
	//sedBuilderStack.remove(null);
    }
    
//    public void remove(Segment seg) {
//	sedBuilderStack.remove(seg);
//	
//	seds.remove(sed);
//	origSeds.remove(sed);
//    }
    
//    public void updateSedBuilderStack() {
//	for (int i=0; i<sedBuilderStack.getNumberOfSegments(); i++) {
//	    Segment seg = sedBuilderStack.getSegment(i);
//	    seg.setSpectralAxisValues(values);
//	    seg.setFluxAxisValues(values);
//	    seg.setDataValues((double[]) nsed.getSegment(0).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE), 
//		    SEDMultiSegmentSpectrum.E_UTYPE););
//	}
//    }
    
    public SedStack copy() throws CloneNotSupportedException {
	return (SedStack) this.clone();
	
//	ObjectOutputStream oos = null;
//        ObjectInputStream ois = null;
//
//        try {
//
//            SedStack copy = null;
//
//            // deep copy
//            ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
//            oos = new ObjectOutputStream(bos); 
//            // serialize and pass the object
//            oos.writeObject(this);   
//            oos.flush();               
//            ByteArrayInputStream bin = 
//			        new ByteArrayInputStream(bos.toByteArray()); 
//            ois = new ObjectInputStream(bin);                  
//            // return the new object
//            copy = (SedStack) ois.readObject(); 
//
//            return copy;
//        }
//        catch(Exception e)
//        {
//            System.out.println("Exception in main = " +  e);
//        }
//        finally
//        {        
//	    try {
//		oos.close();
//		ois.close();
//	    } catch (IOException ex) {
//		Logger.getLogger(SedStack.class.getName()).log(Level.SEVERE, null, ex);
//	    }
//        }
//	
//	return null;
    }
    
    public ExtSed getSed(int i) {
	return seds.get(i);
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void setSed(int i, ExtSed sed) {
	seds.set(i, sed);
    }
    
    public ExtSed createSedFromStack(SedStack stack) throws SedException, UnitsException {

	return createSedFromStack(stack, stack.getName());
    }
    
    public ExtSed createSedFromStack(SedStack stack, String name) throws SedException, UnitsException {

	ExtSed sedBuilderStack = new ExtSed(name);
	
	for (ExtSed sed : stack.getSeds()) {
	    ExtSed flattenedSed = ExtSed.flatten(sed, sed.getSegment(0).getSpectralAxisUnits(), sed.getSegment(0).getFluxAxisUnits());
	    Segment seg = flattenedSed.getSegment(0);
	    String targetName = sed.getId();
	    if (!seg.getTarget().isSetName())
		seg.createTarget().createName();
	    seg.getTarget().getName().setValue(targetName);
	    //seg.getTarget().setPos(flattenedSed.getSegment(0).getTarget().getPos());
	    sedBuilderStack.addSegment(seg);
	}
	
	return sedBuilderStack;
    }
    
    public void resetStack() {
	List<ExtSed> nseds = new ArrayList();
	for (ExtSed sed : this.getOrigSeds()) {
	    try {
		ExtSed nsed = ExtSed.flatten(sed, sed.getSegment(0).getSpectralAxisUnits(), sed.getSegment(0).getFluxAxisUnits());
		nsed.addAttachment(SedStackerAttachments.ORIG_REDSHIFT, sed.getAttachment(SedStackerAttachments.ORIG_REDSHIFT));
		nsed.addAttachment(SedStackerAttachments.REDSHIFT, sed.getAttachment(SedStackerAttachments.REDSHIFT));
		nsed.addAttachment(SedStackerAttachments.NORM_CONSTANT, sed.getAttachment(SedStackerAttachments.NORM_CONSTANT));
		nsed.setId(sed.getId());
		nseds.add(nsed);
	    } catch (SedNoDataException ex) {
		Logger.getLogger(SedStack.class.getName()).log(Level.SEVERE, null, ex);
	    } catch (SedException ex) {
		Logger.getLogger(SedStack.class.getName()).log(Level.SEVERE, null, ex);
	    } catch (UnitsException ex) {
		Logger.getLogger(SedStack.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
	this.setSeds(nseds);
    }
    
}
