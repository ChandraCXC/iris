/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.sedstacker;

import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.logging.LogEntry;
import cfa.vo.iris.logging.LogEvent;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sedlib.Field;
import cfa.vo.sedlib.Param;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sedlib.common.SedNullException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import spv.util.UnitsException;

/**
 *
 * @author jbudynk
 */
public class Stack extends ExtSed implements IStack {

    private String id = "Stack";
    private Map<String, ExtSed> sedMap = new HashMap();
    private Map<String, ExtSed> origSedMap = new HashMap();

    private Map<String, Object> attachments = new HashMap();
    private boolean managed = true; // do i need this??

    public StackedSed stackedSed; //make private. include getters/setters

    public ExtSed getStackedSed() {
	return 
    }
    
    public Stack(String id) {
	super(id);
    }

    public Stack(String id, boolean managed) {
	super(id, managed);
	this.id = id;
	this.managed = managed;

    }

//    public Stack(List<ExtSed> seds, boolean managed) throws SedInconsistentException, SedNoDataException {
//	Stack stack = new Stack("Stack", managed);
//	for(ExtSed sed : seds) {
//	    stack.add(sed);
//	}
//    }
    public void add(ExtSed sed) {
	try {
	    // Add the SED, then Redo UndoableEdits in UndoManager??
	    // TODO: add a listener to 'newSed' so that changes made to 'sed' in the Builder are reflected in 'newSed' 
	    ExtSed newSed = SedBuilder.flatten(sed, sed.getSegment(0).getSpectralAxisUnits(), sed.getSegment(0).getFluxAxisUnits());
	    newSed.getSegment(0).getTarget().getName().setName(sed.getId()); // sets the TargetName to the SedID.
	    sedMap.put(sed.getId(), newSed);
	    origSedMap.put(sed.getId(), newSed);
	    newSed.getSegment(0).addCustomParam(new Param("", "iris:original redshift", "", "iris:original redshift"));
	    newSed.getSegment(0).addCustomParam(new Param("1.0", "iris:normalization constant", "", "iris:normalization constant"));
	    newSed.getSegment(0).addCustomData(new Field("iris:counts", "", "", "", "iris:counts"), new double[newSed.getSegment(0).getLength()]);
	    this.addSegment(newSed.getSegment(0));
	    if (managed) {
		SegmentEvent.getInstance().fire(newSed.getSegment(0), new SegmentEvent.SegmentPayload(this, SedCommand.ADDED));
		SedEvent.getInstance().fire(this, SedCommand.CHANGED);
		LogEvent.getInstance().fire(this, new LogEntry("Segment added to SED: " + id, this));
	    }
	} catch (SedException ex) {
	    Logger.getLogger(Stack.class.getName()).log(Level.SEVERE, null, ex);
	} catch (UnitsException ex) {
	    Logger.getLogger(Stack.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void add(List<ExtSed> seds) {

	for (ExtSed sed : seds) {
	    this.add(sed);
	}
    }

    public void remove(ExtSed sed, int segment) {
	// Remove the SED, then Redo UndoableEdits in UndoManager??
	sedMap.remove(sed.getId());
	origSedMap.remove(sed.getId());
	//boolean resp = super.segmentList.remove(s);
	//if (managed) {
	//    SegmentEvent.getInstance().fire(s, new SegmentEvent.SegmentPayload(this, SedCommand.REMOVED));
	//    LogEvent.getInstance().fire(this, new LogEntry("Segments removed from SED: " + id, this));
	// }
	// return resp;
	this.removeSegment(segment);
    }

    public void remove(List<ExtSed> seds, List<Integer> segments) {
	// Remove the SED, then Redo UndoableEdits in UndoManager??
	int sed = 0;
	for (Integer i : segments) {
	    this.remove(seds.get(sed), i);
	    i++;
	}
    }

    public Map<String, ExtSed> getSedMap() {
	return sedMap;
    }

    public Map<String, ExtSed> getOrigSedMap() {
	return origSedMap;
    }

    public String getId() {
	return id;
    }

    public void setId(String newId) {
	this.id = newId;
    }

    public List<double[]> getFluxValues() {

	try {

	    List<double[]> matrix = new ArrayList();

	    for (Segment seg : this.segmentList) {
		matrix.add(seg.getFluxAxisValues());
	    }
	    return matrix;

	} catch (SedNoDataException ex) {
	    Logger.getLogger(Stack.class.getName()).log(Level.SEVERE, null, ex);
	}
	return null;
    }

    public void setFluxValues(List<double[]> values) {

	for (int i = 0; i < this.getNumberOfSegments(); i++) {
	    this.getSegment(i).setFluxAxisValues(values.get(i));
	}
    }

    public List<String> getFluxUnits() {
	try {
	    List<String> units = new ArrayList();
	    for (Segment seg : this.segmentList) {
		units.add(seg.getFluxAxisUnits());
	    }
	    return units;
	} catch (SedNoDataException ex) {
	    Logger.getLogger(Stack.class.getName()).log(Level.SEVERE, null, ex);
	}
	return null;
    }

    public void setFluxUnits(List<String> values) {

	try {
	    for (int i = 0; i < this.getNumberOfSegments(); i++) {
		this.getSegment(i).setFluxAxisUnits(values.get(i));
	    }
	} catch (SedNoDataException ex) {
	    Logger.getLogger(Stack.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public List<double[]> getSpectralValues() {

	try {

	    List<double[]> matrix = new ArrayList();

	    for (Segment seg : this.segmentList) {
		matrix.add(seg.getSpectralAxisValues());
	    }
	    return matrix;

	} catch (SedNoDataException ex) {
	    Logger.getLogger(Stack.class.getName()).log(Level.SEVERE, null, ex);
	}
	return null;
    }

    public void setSpectralValues(List<double[]> values) {

	for (int i = 0; i < this.getNumberOfSegments(); i++) {
	    this.getSegment(i).setSpectralAxisValues(values.get(i));
	}
    }

    public List<String> getSpectralUnits() {
	try {
	    List<String> units = new ArrayList();
	    for (Segment seg : this.segmentList) {
		units.add(seg.getSpectralAxisUnits());
	    }
	    return units;
	} catch (SedNoDataException ex) {
	    Logger.getLogger(Stack.class.getName()).log(Level.SEVERE, null, ex);
	}
	return null;
    }

    public void setSpectralUnits(List<String> values) {

	try {
	    for (int i = 0; i < this.getNumberOfSegments(); i++) {
		this.getSegment(i).setSpectralAxisUnits(values.get(i));
	    }
	} catch (SedNoDataException ex) {
	    Logger.getLogger(Stack.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public List<double[]> getFluxErrorValues() throws SedInconsistentException {

	try {

	    List<double[]> matrix = new ArrayList();

	    for (Segment seg : this.segmentList) {
		matrix.add((double[]) seg.getDataValues("Spectrum.Data.FluxAxis.Accuracy.StatError"));
	    }
	    return matrix;

	} catch (SedNoDataException ex) {
	    Logger.getLogger(Stack.class.getName()).log(Level.SEVERE, null, ex);
	}
	return null;
    }

    public void setFluxErrorValues(List<double[]> values) throws SedInconsistentException {

	for (int i = 0; i < this.getNumberOfSegments(); i++) {
	    this.getSegment(i).setDataValues(values.get(i), "Spectrum.Data.FluxAxis.Accuracy.StatError");
	}
    }

    public void setRedshift(Segment seg, double z) throws SedNoDataException, SedInconsistentException, SedNullException {
	seg.findCustomParam("iris:original redshift").setValue(Double.toString(z));
    }

    public void setRedshifts(double[] zs) throws SedNoDataException, SedInconsistentException, SedNullException, SedStackerException {
	if (zs.length != this.getNumberOfSegments()) {
	    throw new SedStackerException("Input array length must equal Stack length.");
	} else {
	    for (int i = 0; i < this.getNumberOfSegments(); i++) {
		setRedshift(this.getSegment(i), zs[i]);
	    }
	}
    }

    public double getRedshift(Segment seg) throws SedNoDataException, SedNullException {
	return Double.parseDouble(seg.findCustomParam("iris:original redshift").getValue());
    }

    public double[] getRedshifts() throws SedNoDataException, SedNullException {
	double[] redshifts = new double[this.getNumberOfSegments()];
	for (int i = 0; i < this.getNumberOfSegments(); i++) {
	    redshifts[i] = this.getRedshift(this.getSegment(i));
	}
	return redshifts;
    }

    public void setNormConstant(Segment seg, double norm) throws SedNoDataException, SedInconsistentException, SedNullException {
	seg.findCustomParam("iris:normalization constant").setValue(Double.toString(norm));
    }

    public void setNormConstants(double[] norms) throws SedNoDataException, SedInconsistentException, SedNullException, SedStackerException {
	if (norms.length != this.getNumberOfSegments()) {
	    throw new SedStackerException("Input array length must equal Stack length.");
	} else {
	    for (int i = 0; i < this.getNumberOfSegments(); i++) {
		setRedshift(this.getSegment(i), norms[i]);
	    }
	}
    }

    public double getNormConstant(Segment seg) throws SedNoDataException, SedNullException {
	return Double.parseDouble(seg.findCustomParam("iris:normalization constant").getValue());
    }

    public double[] getNormConstants() throws SedNoDataException, SedNullException {
	double[] norms = new double[this.getNumberOfSegments()];

	for (int i = 0; i < this.getNumberOfSegments(); i++) {
	    norms[i] = this.getNormConstant(this.getSegment(i));
	}
	return norms;
    }

    public Stack copy() {

	Stack stack = (Stack) this.clone();

	return stack;
    }

    public void addAttachment(String attachmentId, Object object) {
	attachments.put(id, object);
    }

    public void removeAttachment(String attachmentId) {
	attachments.remove(attachmentId);
    }

    public Object getAttachment(String attachmentId) {
	if (attachments.containsKey(attachmentId)) {
	    return attachments.get(attachmentId);
	} else {
	    return null;
	}
    }

    public Segment getSegment(String id) {
	return sedMap.get(id).getSegment(0);
    }

}
