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
import cfa.vo.iris.test.sedstacker.NormConfig;
import cfa.vo.iris.test.sedstacker.SedStackerManager;
import cfa.vo.iris.test.sedstacker.Stack;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sherpa.SherpaClient;
import org.astrogrid.samp.Response;
import spv.spectrum.SEDMultiSegmentSpectrum;
import spv.util.UnitsException;
import spv.util.XUnits;
import spv.util.YUnits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jbudynk
 */
public class SedStackerNormalizer {
    private SherpaClient client;
    private SedStackerManager manager;
    private SAMPController controller;
    private static String NORMALIZE_MTYPE = "stack.normalize";

    public SedStackerNormalizer(SAMPController controller, SedStackerManager manager) {
        this.client = new SherpaClient(controller);
        this.manager = manager;
        this.controller = controller;
    }

    

    public void normalize(Stack stack, NormConfig normConfig) throws Exception {

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
	convertUnits(stack, normConfig.getAtPointXUnits(), normConfig.getAtPointYUnits());
	
	SedStackerNormalizePayload payload = (SedStackerNormalizePayload) SAMPFactory.get(SedStackerNormalizePayload.class);
	
	for (int i=0; i<stack.getNumberOfSegments(); i++) {
	    SegmentPayload segment = (SegmentPayload) SAMPFactory.get(SegmentPayload.class);
	    segment.setX(stack.getSegment(i).getSpectralAxisValues());
	    segment.setY(stack.getSegment(i).getFluxAxisValues());
	    segment.setYerr((double[]) stack.getSegment(i).getDataValues(SEDMultiSegmentSpectrum.E_UTYPE));
	    segment.setRedshift(stack.getRedshift(stack.getSegment(i)));
	    // 'counts' and 'redshift' aren't used for normalization
	    segment.setCounts(null);
	    segment.setNormConstant(stack.getNormConstant(stack.getSegment(i)));
	    payload.addSegment(segment);
	}
	payload.setAtPointXUnits(normConfig.getAtPointXUnits());
	payload.setStats(normConfig.getAtPointYType());
	payload.setAtPointYUnits(normConfig.getAtPointYUnits());
	payload.setY0(normConfig.getAtPointYValue());
	payload.setIntegrate(normConfig.isIntegrate());
	payload.setXmax(normConfig.getIntegrateBoundsXMax());
	payload.setXmin(normConfig.getIntegrateBoundsXMin());
	payload.setIntegrateXUnits(normConfig.getIntegrateXUnits());
//	payload.setIntegrateYType(normConfig.getIntegrateYType());
//	payload.setIntegrateYValue(normConfig.getIntegrateYValue());
	payload.setIntegrateYUnits(normConfig.getIntegrateYUnits());
	
        SAMPMessage message = SAMPFactory.createMessage(NORMALIZE_MTYPE, payload, SedStackerNormalizePayload.class);

        Response rspns = controller.callAndWait(client.findSherpa(), message.get(), 10);
        if (client.isException(rspns)) {
            Exception ex = client.getException(rspns);
            throw ex;
        }

        SedStackerNormalizePayload response = (SedStackerNormalizePayload) SAMPFactory.get(rspns.getResult(), SedStackerNormalizePayload.class);

	int i=0;
	for (SegmentPayload segment : response.getSegments()) {
	    stack.getSegment(i).setSpectralAxisValues(segment.getX());
	    stack.getSegment(i).setSpectralAxisValues(segment.getY());
	    stack.getSegment(i).setDataValues(segment.getYerr(), SEDMultiSegmentSpectrum.E_UTYPE);
	    stack.setNormConstant(stack.getSegment(i), segment.getNormConstant());
	    i++;
	}
	convertUnits(stack, xunits, yunits);
	
	
	
	
	
//	stack.setFluxValues(convertFluxValues(stack, normConfig.getAtPointYUnits()).get("y"));
//	stack.setFluxErrorValues(convertFluxValues(stack, normConfig.getAtPointYUnits()).get("err"));
//	stack.setSpectralValues(convertSpectralValues(stack, normConfig.getAtPointXUnits()));
//        
//        SedStackerNormalizePayload payload = (SedStackerNormalizePayload) SAMPFactory.get(SedStackerNormalizePayload.class);
//        payload.setX(stack.getSpectralValues());
//        payload.setY(stack.getFluxValues());
//	payload.setYErr(stack.getFluxErrorValues());
//	
//	payload.setAtPointXUnits(normConfig.getAtPointXUnits());
//	payload.setAtPointYType(normConfig.getAtPointYType());
//	payload.setAtPointYUnits(normConfig.getAtPointYUnits());
//	payload.setAtPointYValue(normConfig.getAtPointYValue());
//	payload.setIntegrate(normConfig.isIntegrate());
//	payload.setIntegrateBoundsXMax(normConfig.getIntegrateBoundsXMax());
//	payload.setIntegrateBoundsXMin(normConfig.getIntegrateBoundsXMin());
//	payload.setIntegrateXUnits(normConfig.getIntegrateXUnits());
//	payload.setIntegrateYType(normConfig.getIntegrateYType());
//	payload.setIntegrateYValue(normConfig.getIntegrateYValue());
//	payload.setIntegrateYUnits(normConfig.getIntegrateYUnit());
//	
//        SAMPMessage message = SAMPFactory.createMessage(NORMALIZE_MTYPE, payload, SedStackerNormalizePayload.class);
//
//        Response rspns = controller.callAndWait(sherpaId, message.get(), 10);
//        if (client.isException(rspns)) {
//            Exception ex = client.getException(rspns);
//            throw ex;
//        }
//
//        SedStackerNormalizePayload response = (SedStackerNormalizePayload) SAMPFactory.get(rspns.getResult(), SedStackerNormalizePayload.class);
//        stack.setSpectralValues(response.getX());
//        stack.setFluxValues(response.getY());
//	stack.setFluxErrorValues(response.getYErr());
//	
//	//Set the units back to the original units
//	for (int i=0; i<stack.getNumberOfSegments(); i++) {
//	    stack.setFluxValues(convertFluxValues(stack, yunits.get(i)).get("y"));
//	    stack.setFluxErrorValues(convertFluxValues(stack, yunits.get(i)).get("err"));
//	    stack.setSpectralValues(convertSpectralValues(stack, xunits.get(i)));
//	    stack.setFluxUnits(yunits);
//	    stack.setSpectralUnits(xunits);
//	    
//	    //stack.getSegment(i).addCustomParam(new Param(oldNormConstant, "iris:old norm constant", ""));
//	    stack.getSegment(i).addCustomParam(new Param(Double.toString(response.getNormConstants()[i]), "iris:normalization constant", ""));
//	}
        
        //stack.checkChar();
        
        //manager.add(inputSed);
        
        //return stack;
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
    

    private List<double[]> convertSpectralValues(Stack stack, String toUnits) throws UnitsException, SedNoDataException {
	List<double[]> conversions = new ArrayList<double[]>();
	for (int i=0; i<stack.getNumberOfSegments(); i++) {
	    double[] value = stack.getSegment(i).getSpectralAxisValues();
	    String fromUnits = stack.getSegment(i).getSpectralAxisUnits();
	    conversions.set(i, XUnits.convert(value, new XUnits(fromUnits), new XUnits(toUnits)));
	}
	return conversions;
    }
    
    private Map<String, List<double[]>> convertFluxValues(Stack stack, String toUnits) throws UnitsException, SedNoDataException, SedInconsistentException {
	Map<String, List<double[]>> fluxMap = new HashMap();
	List<double[]> yconversions = new ArrayList<double[]>();
	List<double[]> errconversions = new ArrayList<double[]>();
	for (int i=0; i<stack.getNumberOfSegments(); i++) {
	    double[] y = stack.getSegment(i).getFluxAxisValues();
	    double[] x = stack.getSegment(i).getSpectralAxisValues();
	    double[] err = (double[]) stack.getSegment(i).getDataValues("Spectrum.Data.FluxAxis.Accuracy.StatError");
	    String fromUnits = stack.getSegment(i).getFluxAxisUnits();
	    String xUnits = stack.getSegment(i).getSpectralAxisUnits();
	    yconversions.set(i, YUnits.convert(y, x, new YUnits(fromUnits), new XUnits(xUnits), new YUnits(toUnits), true));
	    errconversions.set(i, YUnits.convertErrors(err, y, x, new YUnits(fromUnits), new XUnits(xUnits), new YUnits(toUnits), true));
	}
	fluxMap.put("y", yconversions);
	fluxMap.put("err", errconversions);
	return fluxMap;
    }
}
