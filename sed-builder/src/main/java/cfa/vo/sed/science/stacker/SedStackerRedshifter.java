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
	
	// convert Stack to same units. First save the original units
	List<String> xunits = stack.getSpectralUnits();
	List<String> yunits = stack.getFluxUnits();
	//convertUnits(stack, "Angstrom", "Jy");  //TODO: chose which method to use here.
	convertUnits(stack, "Angstrom");
	
	SedStackerRedshiftPayload payload = (SedStackerRedshiftPayload) SAMPFactory.get(SedStackerRedshiftPayload.class);
	
	for (int i=0; i<stack.getSeds().size(); i++) {
	    SegmentPayload segment = (SegmentPayload) SAMPFactory.get(SegmentPayload.class);
	    segment.setX(stack.getSeds().get(i).getSegment(0).getSpectralAxisValues());
	    segment.setY(stack.getSeds().get(i).getSegment(0).getFluxAxisValues());
	    segment.setYerr((double[]) stack.getSeds().get(i).getSegment(0).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE));
	    if (stack.getSeds().get(i).getAttachment(REDSHIFT) != "") {
		segment.setZ((Double) stack.getSeds().get(i).getAttachment(REDSHIFT));
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

	int i=0;
	for (SegmentPayload segment : response.getSegments()) {
	    stack.getSeds().get(i).getSegment(0).setSpectralAxisValues(segment.getX());
	    stack.getSeds().get(i).getSegment(0).setFluxAxisValues(segment.getY());
	    stack.getSeds().get(i).getSegment(0).setDataValues(segment.getYerr(), SEDMultiSegmentSpectrum.E_UTYPE);
	    stack.getSeds().get(i).addAttachment(REDSHIFT, zconfig.getToRedshift());
	    i++;
	}
	convertUnits(stack, xunits, yunits);
        
        //return stack;
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

}