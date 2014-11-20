/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.sedstacker.samp;

import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.test.sedstacker.SedStackerManager;
import cfa.vo.iris.test.sedstacker.Stack;
import cfa.vo.iris.test.sedstacker.StackConfig;
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
public class SedStackerStacker {
    private SherpaClient client;
    private SedStackerManager manager;
    private SAMPController controller;
    private static String STACK_MTYPE = "stack.stack";

    public SedStackerStacker(SAMPController controller, SedStackerManager manager) {
        this.client = new SherpaClient(controller);
        this.manager = manager;
        this.controller = controller;
    }

    

    public void stack(Stack stack, StackConfig stackConfig) throws Exception {

//        client.findSherpa();
        
        if(stack.getNumberOfSegments()==0)
            throw new SedNoDataException();
        
//        String sherpaId = client.getSherpaId();
//
//        if (sherpaId == null) {
//            NarrowOptionPane.showMessageDialog(null,
//                    "Iris could not find the Sherpa process running in the background. Please check the Troubleshooting section in the Iris documentation.",
//                    "Cannot connect to Sherpa",
//                    NarrowOptionPane.ERROR_MESSAGE);
//            throw new Exception("Sherpa not found");
//	    }
	
	// convert Stack to same units
	//Stack nstack = stack.copy();
	
	List<String> xunits = stack.getSpectralUnits();
	List<String> yunits = stack.getFluxUnits();
	convertUnits(stack, "Angstrom", "Jy");
	
	SedStackerStackPayload payload = (SedStackerStackPayload) SAMPFactory.get(SedStackerStackPayload.class);
	
	for (int i=0; i<stack.getNumberOfSegments(); i++) {
	    SegmentPayload segment = (SegmentPayload) SAMPFactory.get(SegmentPayload.class);
	    segment.setX(stack.getSegment(i).getSpectralAxisValues());
	    segment.setY(stack.getSegment(i).getFluxAxisValues());
	    segment.setYerr((double[]) stack.getSegment(i).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE));
	    segment.setRedshift(stack.getRedshift(stack.getSegment(i)));
	    // 'normConst' and 'redshift' aren't used for stacking
	    segment.setCounts(null);
	    segment.setNormConstant(1.0);
	    payload.addSegment(segment);
	}
	payload.setStatistic(stackConfig.getStatistic());
	payload.setBinSize(stackConfig.getBinSize());
	payload.setBinUnits(stackConfig.getBinUnits());
	payload.setLogBinning(stackConfig.isLogBinning());
	payload.setSmooth(stackConfig.isSmooth());
	payload.setSmoothBoxSize(stackConfig.getSmoothBoxSize());
	
        SAMPMessage message = SAMPFactory.createMessage(STACK_MTYPE, payload, SedStackerStackPayload.class);

        Response rspns = controller.callAndWait(client.findSherpa(), message.get(), 10);
        if (client.isException(rspns)) {
            Exception ex = client.getException(rspns);
            throw ex;
        }

        SedStackerRedshiftPayload response = (SedStackerRedshiftPayload) SAMPFactory.get(rspns.getResult(), SedStackerRedshiftPayload.class);

	int i=0;
	for (SegmentPayload segment : response.getSegments()) {
	    stack.getSegment(i).setSpectralAxisValues(segment.getX());
	    stack.getSegment(i).setSpectralAxisValues(segment.getY());
	    stack.getSegment(i).setDataValues(segment.getYerr(), SEDMultiSegmentSpectrum.E_UTYPE);
	    i++;
	}
	convertUnits(stack, xunits, yunits);
    }
	
	
	private void convertUnits(Stack stack, String xUnits, String yUnits) throws SedException, UnitsException {
	for(int i=0; i<stack.getNumberOfSegments(); i++) {
	    ExtSed seg = new ExtSed("seg");
	    seg.addSegment(stack.getSegment(i));
	    
	    ExtSed nseg = ExtSed.flatten(seg, xUnits, yUnits);
	    
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
	    
	    ExtSed nseg = ExtSed.flatten(seg, xUnits.get(i), yUnits.get(i));
	    
	    stack.getSegment(i).setFluxAxisUnits(yUnits.get(i));
	    stack.getSegment(i).setSpectralAxisUnits(yUnits.get(i));
	    stack.getSegment(i).setFluxAxisValues(nseg.getSegment(0).getFluxAxisValues());
	    stack.getSegment(i).setSpectralAxisValues(nseg.getSegment(0).getSpectralAxisValues());
	    stack.getSegment(i).setDataValues((double[]) nseg.getSegment(0).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE), SEDMultiSegmentSpectrum.E_UTYPE);
	}
    }
	
	
	
//	stack.setFluxValues(convertFluxValues(stack, "erg/s/cm2").get("y"));
//	stack.setFluxErrorValues(convertFluxValues(stack, "erg/s/cm2").get("err"));
//	stack.setSpectralValues(convertSpectralValues(stack, "Angstrom"));
//        
//        SedStackerStackPayload payload = (SedStackerStackPayload) SAMPFactory.get(SedStackerStackPayload.class);
//        payload.setX(stack.getSpectralValues());
//        payload.setY(stack.getFluxValues());
//	payload.setYErr(stack.getFluxErrorValues());
//	
//	payload.setStatistic(stackConfig.getStatistic());
//	payload.setBinSize(stackConfig.getBinSize());
//	payload.setBinUnits(stackConfig.getBinUnits());
//	payload.setLogBinning(stackConfig.isLogBinning());
//	payload.setSmooth(stackConfig.isSmooth());
//	payload.setSmoothBoxSize(stackConfig.getSmoothBoxSize());
//	payload.setCounts(new double[stack.getNumberOfSegments()]);
//	
//        SAMPMessage message = SAMPFactory.createMessage(STACK_MTYPE, payload, SedStackerStackPayload.class);
//
//        Response rspns = controller.callAndWait(sherpaId, message.get(), 10);
//        if (client.isException(rspns)) {
//            Exception ex = client.getException(rspns);
//            throw ex;
//        }
//
//        SedStackerStackPayload response = (SedStackerStackPayload) SAMPFactory.get(rspns.getResult(), SedStackerStackPayload.class);
//        stack.stackedSed.setSpectralValues(response.getX());
//        stack.stackedSed.setFluxValues(response.getY());
//	stack.stackedSed.setFluxErrorValues(response.getYErr());
//	stack.stackedSed.setCounts(response.getCounts());
//	
//	//Set the units back to the original units
//	for (int i=0; i<stack.getNumberOfSegments(); i++) {
//	    stack.setFluxValues(convertFluxValues(stack, yunits.get(i)).get("y"));
//	    stack.setFluxErrorValues(convertFluxValues(stack, yunits.get(i)).get("err"));
//	    stack.setSpectralValues(convertSpectralValues(stack, xunits.get(i)));
//	    stack.setFluxUnits(yunits);
//	    stack.setSpectralUnits(xunits);
//	}
//        
//        //stack.checkChar();
//        
//        //manager.add(inputSed);
//        
//        //return stack;
//    }
//
//    private List<double[]> convertSpectralValues(Stack stack, String toUnits) throws UnitsException, SedNoDataException {
//	List<double[]> conversions = new ArrayList<double[]>();
//	for (int i=0; i<stack.getNumberOfSegments(); i++) {
//	    double[] value = stack.getSegment(i).getSpectralAxisValues();
//	    String fromUnits = stack.getSegment(i).getSpectralAxisUnits();
//	    conversions.set(i, XUnits.convert(value, new XUnits(fromUnits), new XUnits(toUnits)));
//	}
//	return conversions;
//    }
//    
//    private Map<String, List<double[]>> convertFluxValues(Stack stack, String toUnits) throws UnitsException, SedNoDataException, SedInconsistentException {
//	Map<String, List<double[]>> fluxMap = new HashMap();
//	List<double[]> yconversions = new ArrayList<double[]>();
//	List<double[]> errconversions = new ArrayList<double[]>();
//	for (int i=0; i<stack.getNumberOfSegments(); i++) {
//	    double[] y = stack.getSegment(i).getFluxAxisValues();
//	    double[] x = stack.getSegment(i).getSpectralAxisValues();
//	    double[] err = (double[]) stack.getSegment(i).getDataValues("Spectrum.Data.FluxAxis.Accuracy.StatError");
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
