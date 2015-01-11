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

import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.ExtSed;
import static cfa.vo.sed.science.stacker.SedStackerAttachments.REDSHIFT;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sherpa.SherpaClient;
import org.astrogrid.samp.Response;
import spv.spectrum.SEDMultiSegmentSpectrum;
import spv.util.UnitsException;

import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jbudynk
 */
public class SedStackerRedshifter {

    
    private SherpaClient client;
    private SAMPController controller;
    private static String REDSHIFT_MTYPE = "stack.redshift";

    public SedStackerRedshifter(SAMPController controller) {
        this.client = new SherpaClient(controller);
        this.controller = controller;
    }

    public void shift(SedStack stack) throws Exception {
	shift(stack, stack.getConf().getRedshiftConfiguration());
    }

    public void shift(SedStack stack, RedshiftConfiguration zconfig) throws Exception {
        
        if(stack.getSeds().isEmpty())
            throw new SedNoDataException();
	
	String sherpaId = client.findSherpa();

        if (sherpaId == null) {
            NarrowOptionPane.showMessageDialog(null,
                    "Iris could not find the Sherpa process running in the background. Please check the Troubleshooting section in the Iris documentation.",
                    "Cannot connect to Sherpa",
                    NarrowOptionPane.ERROR_MESSAGE);
            throw new Exception("Sherpa not found");
	}
	
	// Create copy of stack and convert new stack to same units. First save the original units for later.
	List<String> xunits = stack.getSpectralUnits();
	List<String> yunits = stack.getFluxUnits();
	SedStack nstack = stack.copy();
	//convertUnits(stack, "Angstrom", "Jy");  //TODO: chose which method to use here.
	convertUnits(nstack, "Angstrom");
	
	SedStackerRedshiftPayload payload = (SedStackerRedshiftPayload) SAMPFactory.get(SedStackerRedshiftPayload.class);
	
	for (int i=0; i<nstack.getSeds().size(); i++) {
	    
	    SegmentPayload segment = (SegmentPayload) SAMPFactory.get(SegmentPayload.class);
	    
	    segment.setX(nstack.getSeds().get(i).getSegment(0).getSpectralAxisValues());
	    segment.setY(nstack.getSeds().get(i).getSegment(0).getFluxAxisValues());
	    segment.setYerr((double[]) nstack.getSeds().get(i).getSegment(0).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE));
	    
	    if (nstack.getSeds().get(i).getAttachment(REDSHIFT) != "") {
		
		segment.setZ(Double.valueOf(nstack.getSeds().get(i).getAttachment(REDSHIFT).toString()));
		
	    } else {
		
		segment.setZ(Double.NaN);
		
	    }
	    
	    payload.addSegment(segment);
	}
	
        payload.setZ0(zconfig.getToRedshift());
	payload.setCorrectFlux(zconfig.isCorrectFlux());
	
        SAMPMessage message = SAMPFactory.createMessage(REDSHIFT_MTYPE, payload, SedStackerRedshiftPayload.class);

        Response rspns = controller.callAndWait(sherpaId, message.get(), 10);
        if (client.isException(rspns)) {
            Exception ex = client.getException(rspns);
            throw ex;
        }

        SedStackerRedshiftPayload response = (SedStackerRedshiftPayload) SAMPFactory.get(rspns.getResult(), SedStackerRedshiftPayload.class);

	int c=0;
	for (SegmentPayload segment : response.getSegments()) {
	    
	    nstack.getSeds().get(c).getSegment(0).setSpectralAxisValues(segment.getX());
	    nstack.getSeds().get(c).getSegment(0).setFluxAxisValues(segment.getY());
	    nstack.getSeds().get(c).getSegment(0).setDataValues(segment.getYerr(), SEDMultiSegmentSpectrum.E_UTYPE);
	    c++;
	    
	}
	
	// convert back to the original units of the Stack
	convertUnits(nstack, xunits, yunits);
	
	// store the new values in the original stack
	for (int i=0; i<nstack.getSeds().size(); i++) {
	    stack.getSeds().get(i).getSegment(0).setSpectralAxisValues(nstack.getSeds().get(i).getSegment(0).getSpectralAxisValues());
	    stack.getSeds().get(i).getSegment(0).setFluxAxisValues(nstack.getSeds().get(i).getSegment(0).getFluxAxisValues());
	    stack.getSeds().get(i).getSegment(0).setDataValues(nstack.getSeds().get(i).getSegment(0).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE),
		    SEDMultiSegmentSpectrum.E_UTYPE);
	    stack.getSeds().get(i).addAttachment(REDSHIFT, zconfig.getToRedshift());
	}
	
	NarrowOptionPane.showMessageDialog(null, "Successfully redshifted stack.", "SED Stacker Message", JOptionPane.INFORMATION_MESSAGE);
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
    
    private void convertUnits(SedStack stack, String xUnits) throws SedException, UnitsException {
	
	for(int i=0; i<stack.getSeds().size(); i++) {
	    
	    ExtSed sed = stack.getSeds().get(i);
	    String yUnits = sed.getSegment(0).getFluxAxisUnits();
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
	    ExtSed nsed = ExtSed.flatten(sed, xUnits.get(i), yUnits.get(i));
	    
	    // set the converted spectral and flux values of each SED
	    stack.getSeds().get(i).getSegment(0).setFluxAxisUnits(yUnits.get(i));
	    stack.getSeds().get(i).getSegment(0).setSpectralAxisUnits(xUnits.get(i));
	    stack.getSeds().get(i).getSegment(0).setFluxAxisValues(nsed.getSegment(0).getFluxAxisValues());
	    stack.getSeds().get(i).getSegment(0).setSpectralAxisValues(nsed.getSegment(0).getSpectralAxisValues());
	    stack.getSeds().get(i).getSegment(0).setDataValues((double[]) nsed.getSegment(0).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE), 
		    SEDMultiSegmentSpectrum.E_UTYPE);
	    
	}
    }

}