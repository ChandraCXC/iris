/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.sedstacker.samp;

import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.test.sedstacker.SedStackerManager;
import cfa.vo.iris.test.sedstacker.Stack;
import cfa.vo.iris.test.sedstacker.ZConfig;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sedlib.common.SedNullException;
import cfa.vo.sherpa.SherpaClient;
import java.util.Arrays;
import java.util.List;
import org.astrogrid.samp.Response;
import spv.spectrum.SEDMultiSegmentSpectrum;
import spv.util.UnitsException;

/**
 *
 * @author jbudynk
 */
public class SedStackerRedshifter {

    private SherpaClient client;
    private SedStackerManager manager;
    private SAMPController controller;
    private static String REDSHIFT_MTYPE = "stack.redshift";

    public SedStackerRedshifter(SAMPController controller, SedStackerManager manager) {
        this.client = new SherpaClient(controller);
        this.manager = manager;
        this.controller = controller;
    }

    

    public void shift(Stack stack, double[] fromRedshift, ZConfig zconfig) throws SedNullException, SedInconsistentException, UnitsException, SedNoDataException, Exception {

        client.findSherpa();
        
        if(stack.getNumberOfSegments()==0)
            throw new SedNoDataException();
        
        String sherpaId = client.getSherpaId();
        
        if (sherpaId == null) {
            NarrowOptionPane.showMessageDialog(null,
                    "Iris could not find the Sherpa process running in the background. Please check the Troubleshooting section in the Iris documentation.",
                    "Cannot connect to Sherpa",
                    NarrowOptionPane.ERROR_MESSAGE);
            throw new Exception("Sherpa not found");
	}
	
	// Stack nstack = stack.copy();
	
	// convert Stack to same units. First save the original units
	List<String> xunits = stack.getSpectralUnits();
	List<String> yunits = stack.getFluxUnits();
	convertUnits(stack, "Angstrom", "Jy");
	
	SedStackerRedshiftPayload payload = (SedStackerRedshiftPayload) SAMPFactory.get(SedStackerRedshiftPayload.class);
	
	for (int i=0; i<stack.getNumberOfSegments(); i++) {
	    SegmentPayload segment = (SegmentPayload) SAMPFactory.get(SegmentPayload.class);
	    segment.setX(stack.getSegment(i).getSpectralAxisValues());
	    System.out.println(Arrays.toString(segment.getX()));
	    segment.setY(stack.getSegment(i).getFluxAxisValues());
	    segment.setYerr((double[]) stack.getSegment(i).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE));
	    segment.setRedshift(stack.getRedshift(stack.getSegment(i)));
	    System.out.println(segment.getRedshift());
	    // 'counts' and 'normConstant' aren't used for redshift analysis
	    segment.setCounts(null);
	    segment.setNormConstant(stack.getNormConstant(stack.getSegment(i)));
	    payload.addSegment(segment);
	}
        payload.setToRedshift(zconfig.getNewz());
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
	    System.out.println(Arrays.toString(segment.getX())); // REMOVE!!!
	    System.out.println(segment.getRedshift());
	    stack.getSegment(i).setSpectralAxisValues(segment.getX());
	    stack.getSegment(i).setSpectralAxisValues(segment.getY());
	    stack.getSegment(i).setDataValues(segment.getYerr(), SEDMultiSegmentSpectrum.E_UTYPE);
	    i++;
	}
	convertUnits(stack, xunits, yunits);
        
        //stack.checkChar();
        
        //manager.add(inputSed);
        
        //return stack;
    }
    
    private void convertUnits(Stack stack, String xUnits, String yUnits) throws SedException, UnitsException {
	for(int i=0; i<stack.getNumberOfSegments(); i++) {
	    ExtSed seg = new ExtSed("seg");
	    seg.addSegment(stack.getSegment(i));
	    
	    ExtSed nseg = SedBuilder.flatten(seg, xUnits, yUnits);
	    
	    stack.getSegment(i).setFluxAxisUnits(yUnits);
	    stack.getSegment(i).setSpectralAxisUnits(xUnits);
	    stack.getSegment(i).setFluxAxisValues(nseg.getSegment(0).getFluxAxisValues());
	    stack.getSegment(i).setSpectralAxisValues(nseg.getSegment(0).getSpectralAxisValues());
	    stack.getSegment(i).setDataValues((double[]) nseg.getSegment(0).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE), SEDMultiSegmentSpectrum.E_UTYPE);
	}
    }
    
    private void convertUnits(Stack stack, List<String> xUnits, List<String> yUnits) throws SedException, UnitsException {
	for(int i=0; i<stack.getNumberOfSegments(); i++) {
	    ExtSed seg = new ExtSed("seg");
	    seg.addSegment(stack.getSegment(i));
	    
	    ExtSed nseg = SedBuilder.flatten(seg, xUnits.get(i), yUnits.get(i));
	    
	    stack.getSegment(i).setFluxAxisUnits(yUnits.get(i));
	    stack.getSegment(i).setSpectralAxisUnits(yUnits.get(i));
	    stack.getSegment(i).setFluxAxisValues(nseg.getSegment(0).getFluxAxisValues());
	    stack.getSegment(i).setSpectralAxisValues(nseg.getSegment(0).getSpectralAxisValues());
	    stack.getSegment(i).setDataValues((double[]) nseg.getSegment(0).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE), SEDMultiSegmentSpectrum.E_UTYPE);
	}
    }

//    private List<double[]> convertSpectralValues(Stack stack, String toUnits) throws UnitsException, SedNoDataException {
//	List<double[]> conversions = new ArrayList<double[]>();
//	for (int i=0; i<stack.getNumberOfSegments(); i++) {
//	    
//	    double[] values = stack.getSegment(i).getSpectralAxisValues();
//	    String fromUnits = stack.getSegment(i).getSpectralAxisUnits();
//	    double[] convert = XUnits.convert(values, new XUnits(fromUnits), new XUnits(toUnits));
//	    conversions.set(i, convert);
//	}
//	return conversions;
//    }
    
//    private Map<String, List<double[]>> convertFluxValues(Stack stack, String toUnits) throws UnitsException, SedNoDataException, SedInconsistentException {
//	Map<String, List<double[]>> fluxMap = new HashMap();
//	List<double[]> yconversions = new ArrayList<double[]>();
//	List<double[]> errconversions = new ArrayList<double[]>();
//	for (int i=0; i<stack.getNumberOfSegments(); i++) {
//	    double[] y = stack.getSegment(i).getFluxAxisValues();
//	    double[] x = stack.getSegment(i).getSpectralAxisValues();
//	    double[] err = (double[]) stack.getSegment(i).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE);
//	    String fromUnits = stack.getSegment(i).getFluxAxisUnits();
//	    String xUnits = stack.getSegment(i).getSpectralAxisUnits();
//	    yconversions.set(i, YUnits.convert(y, x, new YUnits(fromUnits), new XUnits(xUnits), new YUnits(toUnits), true));
//	    errconversions.set(i, YUnits.convertErrors(err, y, x, new YUnits(fromUnits), new XUnits(xUnits), new YUnits(toUnits), true));
//	}
//	fluxMap.put("y", yconversions);
//	fluxMap.put("err", errconversions);
//	return fluxMap;
//    }

    
}
