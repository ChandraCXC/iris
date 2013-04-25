/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.integration;

import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sed.builder.photfilters.EnergyBin;
import cfa.vo.sed.builder.photfilters.PassBand;
import cfa.vo.sed.builder.photfilters.PhotometryFilter;
import cfa.vo.sherpa.SherpaClient;
import java.util.ArrayList;
import java.util.List;
import spv.util.UnitsException;
import spv.util.XUnits;

/**
 *
 * @author olaurino
 */
public class SherpaIntegrator {

    private SAMPController controller;
    private SherpaClient client;
    
    public SherpaIntegrator(SAMPController controller) {
        this.controller = controller;
        this.client = new SherpaClient(controller);
    }

    public synchronized Response integrate(ExtSed sed, List<PassBand> bands) throws Exception {
        client.findSherpa();
        
        String sherpaId = client.getSherpaId();
        
        if (sherpaId == null) {
            NarrowOptionPane.showMessageDialog(null,
                    "Iris could not find the Sherpa process running in the background. Please check the Troubleshooting section in the Iris documentation.",
                    "Cannot connect to Sherpa",
                    NarrowOptionPane.ERROR_MESSAGE);
            throw new Exception("Sherpa not found");
        }
        
        List<PassBand> pbs = new ArrayList(bands);
        IntegrationPayload payload = (IntegrationPayload) SAMPFactory.get(IntegrationPayload.class);
        for (PassBand pb : pbs) {
            if (pb instanceof PhotometryFilter) {
                PhotometryFilter f = (PhotometryFilter) pb;
                TransmissionCurve curve = (TransmissionCurve) SAMPFactory.get(TransmissionCurve.class);
                curve.setFileName(f.getLocalFile());
                curve.setId(f.getId());
                curve.setEffWave(f.getWleff());
                payload.addCurve(curve);
            }

            if (pb instanceof EnergyBin) {
                EnergyBin b = (EnergyBin) pb;
                Window w = (Window) SAMPFactory.get(Window.class);
                Double min = convertValues(new double[]{b.getMin()}, b.getUnits(), "Angstrom")[0];
                Double max = convertValues(new double[]{b.getMax()}, b.getUnits(), "Angstrom")[0];
                w.setMin(Math.min(min, max));
                w.setMax(Math.max(min, max));
                w.setId(pb.toString());
                payload.addWindow(w);
            }
        }
        
        ExtSed flatten = SedBuilder.flatten(sed, "Angstrom", "Jy");
        double[] x = flatten.getSegment(0).getSpectralAxisValues();
        double[] y = flatten.getSegment(0).getFluxAxisValues();
        
        payload.setX(x);
        payload.setY(y);
        
        SAMPMessage message = SAMPFactory.createMessage("spectrum.integrate", payload, IntegrationPayload.class);
        return (Response) SAMPFactory.get(controller.callAndWait(sherpaId, message.get(), 20).getResult(), Response.class);
        
    }
    
    private double[] convertValues(double[] values, String fromUnits, String toUnits) throws UnitsException {
        return XUnits.convert(values, new XUnits(fromUnits), new XUnits(toUnits));
    }
    
}
