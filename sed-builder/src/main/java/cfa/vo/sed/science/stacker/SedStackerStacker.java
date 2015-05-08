/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
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

import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.ExtSed;
import static cfa.vo.sed.science.stacker.SedStackerAttachments.COUNTS;
import cfa.vo.sedlib.Segment;

import cfa.vo.sedlib.common.SedException;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sherpa.SherpaClient;
import org.astrogrid.samp.Response;
import spv.spectrum.SEDMultiSegmentSpectrum;
import spv.util.UnitsException;

import java.util.List;
import javax.swing.JOptionPane;
import org.astrogrid.samp.client.SampException;
import spv.util.XUnits;

/**
 *
 * @author jbudynk
 */
public class SedStackerStacker {
    private SherpaClient client;
    private SAMPController controller;
    private static String STACK_MTYPE = "stack.stack";

    public SedStackerStacker(SAMPController controller) {
        this.client = new SherpaClient(controller);
        this.controller = controller;
    }

    public ExtSed stack(SedStack stack) throws Exception {
	return stack(stack, stack.getConf().getStackConfiguration());
    }

    public ExtSed stack(SedStack stack, StackConfiguration stackConfig) throws Exception {

	if(stack.getSeds().isEmpty()) {
	    NarrowOptionPane.showMessageDialog(null,
                    "Stack is empty. Please add SEDs to the stack to stack.",
                    "Empty Stack",
                    NarrowOptionPane.ERROR_MESSAGE);
            throw new SedNoDataException();
	}
        
	try {
	    client.findSherpa();
	} catch (SampException ex) {
            NarrowOptionPane.showMessageDialog(null,
		    "Error stacking: "+
                    "Iris could not find the Sherpa process running in the background. Please check the Troubleshooting section in the Iris documentation.",
                    "Cannot connect to Sherpa",
                    NarrowOptionPane.ERROR_MESSAGE);
            throw new Exception("Sherpa not found");
	}
	
	// Create copy of stack and convert new stack to same units.
	convertUnits(stack, stackConfig.getBinsizeUnit(), stackConfig.getYUnits());
	
	/* calculate number of bins. If there are too many bins 
	* (i.e., so many that it will cause a Memory Exception in Python), 
	* warn the user
	*/
	int numOfBins = calculateNumberOfBins(stack, stack.getConf().getStackConfiguration().isLogbin());
	if (numOfBins > 500000) {
	    NarrowOptionPane.showMessageDialog(null, 
		    "Too many bins (number of bins calculated: "+numOfBins+"). "+
			    "Make the binsize smaller, or use logarithmic binning.", 
		    "ERROR", 
		    JOptionPane.ERROR_MESSAGE);
	    throw new StackException("Too many bins (number of bins calculated: "+numOfBins+"). Make the binsize smaller or use log binning.");
	}
	
	SedStackerStackPayload payload = (SedStackerStackPayload) SAMPFactory.get(SedStackerStackPayload.class);
	
	for (int i=0; i<stack.getSeds().size(); i++) {
	    
	    SegmentPayload segment = (SegmentPayload) SAMPFactory.get(SegmentPayload.class);
	    
	    segment.setX(stack.getSed(i).getSegment(0).getSpectralAxisValues());
	    segment.setY(stack.getSed(i).getSegment(0).getFluxAxisValues());
	    segment.setYerr((double[]) stack.getSed(i).getSegment(0).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE));
	    
	    payload.addSegment(segment);
	}	
	if (stackConfig.getStatistic().equals("Average")) {
	    payload.setStatistic("avg");
	} else if (stackConfig.getStatistic().equals("Weighted Avg")) {
	    payload.setStatistic("wavg");
	} else if (stackConfig.getStatistic().equals("Sum")) {
	    payload.setStatistic("sum");
	} else {
	    payload.setStatistic("avg");
	}
	payload.setBinsize(stackConfig.getBinsize());
	payload.setLogBin(stackConfig.isLogbin());
	payload.setSmooth(stackConfig.isSmooth());
	payload.setSmoothBinsize(stackConfig.getSmoothBinsize());
	
        SAMPMessage message = SAMPFactory.createMessage(STACK_MTYPE, payload, SedStackerStackPayload.class);

        Response rspns = controller.callAndWait(client.findSherpa(), message.get(), 10);
        if (client.isException(rspns)) {
            Exception ex = client.getException(rspns);
	    NarrowOptionPane.showMessageDialog(null, 
		    "A stacking error has occured. Please check the Iris documentation.", 
		    "ERROR", 
		    JOptionPane.ERROR_MESSAGE);
            throw ex;
        }

        SedStackerStackPayload response = (SedStackerStackPayload) SAMPFactory.get(rspns.getResult(), SedStackerStackPayload.class);

	SegmentPayload segment = response.getSegments().get(0);
	
	// create a Sed from the response
	Segment seg = new Segment();
	seg.setFluxAxisValues(segment.getY());
	seg.setSpectralAxisValues(segment.getX());
	seg.setDataValues(segment.getYerr(), SEDMultiSegmentSpectrum.E_UTYPE);
	seg.setFluxAxisUnits(stackConfig.getYUnits());
	seg.setSpectralAxisUnits(stackConfig.getBinsizeUnit());
	
	ExtSed stackedSed = new ExtSed(stack.getName()+"_stacked_"+stackConfig.getStatistic());
	stackedSed.addSegment(seg);
	stackedSed.addAttachment(COUNTS, segment.getCounts());
	
	return stackedSed;
	
    }
	
	
	private void convertUnits(SedStack stack, String xUnits, String yUnits) throws SedException, UnitsException {
	
	for(int i=0; i<stack.getSeds().size(); i++) {
	    
	    ExtSed sed = stack.getSeds().get(i);
	    ExtSed nsed = ExtSed.flatten(sed, xUnits, yUnits);

	    stack.getSeds().get(i).getSegment(0).setFluxAxisUnits(yUnits);
	    stack.getSeds().get(i).getSegment(0).setSpectralAxisUnits(xUnits);
	    stack.getSeds().get(i).getSegment(0).setFluxAxisValues(nsed.getSegment(0).getFluxAxisValues());
	    stack.getSeds().get(i).getSegment(0).setSpectralAxisValues(nsed.getSegment(0).getSpectralAxisValues());
	    stack.getSeds().get(i).getSegment(0).setDataValues((double[]) nsed.getSegment(0).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE), 
		    SEDMultiSegmentSpectrum.E_UTYPE);
	    
	}
    }
    
    private void convertUnits(SedStack stack, List<String> xUnits, List<String> yUnits) throws SedException, UnitsException {
	for(int i=0; i<stack.getSeds().size(); i++) {
	    
	    // convert the units with ExtSed.flatten()
	    ExtSed sed = stack.getSeds().get(i);
	    ExtSed nsed = ExtSed.flatten(sed, xUnits.get(i), yUnits.get(i)); // PROBLEM HERE!!!
	    
	    // set the converted spectral and flux values of each SED
	    stack.getSeds().get(i).getSegment(0).setFluxAxisUnits(yUnits.get(i));
	    stack.getSeds().get(i).getSegment(0).setSpectralAxisUnits(xUnits.get(i));
	    stack.getSeds().get(i).getSegment(0).setFluxAxisValues(nsed.getSegment(0).getFluxAxisValues());
	    stack.getSeds().get(i).getSegment(0).setSpectralAxisValues(nsed.getSegment(0).getSpectralAxisValues());
	    stack.getSeds().get(i).getSegment(0).setDataValues((double[]) nsed.getSegment(0).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE), 
		    SEDMultiSegmentSpectrum.E_UTYPE);
	    
	}
    }
    
    private static double[] convertXValues(double[] values, String fromUnits, String toUnits) throws UnitsException {
        return XUnits.convert(values, new XUnits(fromUnits), new XUnits(toUnits));
    }

    private int calculateNumberOfBins(SedStack stack, Boolean log) throws SedNoDataException {
	
	double max = stack.getSed(0).getSegment(0).getSpectralAxisValues()[0];
	double min = stack.getSed(0).getSegment(0).getSpectralAxisValues()[0];
	double binsize = stack.getConf().getStackConfiguration().getBinsize();
	
	if (!log) {
	
	    for (int i=0; i<stack.getSeds().size(); i++) {
		double[] x = stack.getSed(i).getSegment(0).getSpectralAxisValues();
		for (int j=0; j<x.length; j++) {
		    if (x[j] > max)
			max = x[j];
		    if (x[j] < min)
			min = x[j];
		}
	    }
	
	} else {
	    max = Math.log10(max);
	    min = Math.log10(max);
	    for (int i=0; i<stack.getSeds().size(); i++) {
		double[] x = stack.getSed(i).getSegment(0).getSpectralAxisValues();
		for (int j=0; j<x.length; j++) {
		    if (Math.log10(x[j]) > max)
			max = Math.log10(x[j]);
		    if (Math.log10(x[j]) < min)
			min = Math.log10(x[j]);
		}
	    }
	}
	
	Double num = (max-min)/binsize;
	return num.intValue();
    }
    
}