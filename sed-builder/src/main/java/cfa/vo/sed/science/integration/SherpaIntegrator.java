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
import cfa.vo.sherpa.CompositeModel;
import cfa.vo.sherpa.Data;
import cfa.vo.sherpa.FitConfiguration;
import cfa.vo.sherpa.SherpaClient;
import spv.util.UnitsException;
import spv.util.XUnits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public synchronized Response integrateComponents(List<PassBand> bands, CompositeModel model) throws Exception {
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

        // Compute the model over the passbands
        FitConfiguration conf = (FitConfiguration) SAMPFactory.get(FitConfiguration.class);
        conf.addModel(model);

        Data dataset = client.createData("integrate");
        conf.addDataset(dataset);

        double[] x = new double[0];

        for (PassBand pb : pbs) {
            if (pb instanceof PhotometryFilter) {
                PhotometryFilter f = (PhotometryFilter) pb;
                double[] drange = range(f.getWlmin(), f.getWlmax(), 10000);
                x = concat(x, drange);
            }

            if (pb instanceof EnergyBin) {
                EnergyBin b = (EnergyBin) pb;
                Double min = convertValues(new double[]{b.getMin()}, b.getUnits(), "Angstrom")[0];
                Double max = convertValues(new double[]{b.getMax()}, b.getUnits(), "Angstrom")[0];
                double[] drange = range(min, max, 10000);
                x = concat(x, drange);
            }
        }

        dataset.setX(x);

        SAMPMessage modelMessage = SAMPFactory.createMessage("spectrum.fit.calc.model.values", conf, FitConfiguration.class);
        org.astrogrid.samp.Response response = controller.callAndWait(sherpaId, modelMessage.get(), 20);

        if (client.isException(response)) {
            throw client.getException(response);
        }

        Map result = response.getResult();

        double[] xx = cfa.vo.interop.EncodeDoubleArray.decodeBase64(((List<String>)result.get("results")).get(0), false);
        double[] yy = cfa.vo.interop.EncodeDoubleArray.decodeBase64(((List<String>)result.get("results")).get(1), false);

        // Integrate
        payload.setX(xx);
        payload.setY(yy);
        SAMPMessage message = SAMPFactory.createMessage("spectrum.integrate", payload, IntegrationPayload.class);
        return (Response) SAMPFactory.get(controller.callAndWait(sherpaId, message.get(), 20).getResult(), Response.class);
    }
    
    private double[] convertValues(double[] values, String fromUnits, String toUnits) throws UnitsException {
        return XUnits.convert(values, new XUnits(fromUnits), new XUnits(toUnits));
    }

    private double[] range(double start, double stop, int n)
    {
        double[] result = new double[n];
        double step = (stop-start)/n;
        double currentStart = start;
        for(int i=0; i<n; i++) {
            result[i] = currentStart + step;
            currentStart += step;
        }
        return result;
    }

    private double[] concat(double[] A, double[] B) {
        int aLen = A.length;
        int bLen = B.length;
        double[] C= new double[aLen+bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }

}
