/**
 * Copyright (C) 2013, 2015 Smithsonian Astrophysical Observatory
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.integration;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.units.UnitsManager;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.sed.builder.photfilters.EnergyBin;
import cfa.vo.sed.builder.photfilters.PassBand;
import cfa.vo.sed.builder.photfilters.PhotometryFilter;
import cfa.vo.sherpa.*;
import cfa.vo.sherpa.models.CompositeModel;
import cfa.vo.sherpa.models.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SherpaIntegrator {

    private SherpaClient client;
    private UnitsManager um;
    
    public SherpaIntegrator(SherpaClient client, UnitsManager unitsManager) {
        this.client = client;
        this.um = unitsManager;
    }

    public synchronized Response integrate(ExtSed sed, List<PassBand> bands) throws Exception {
        
        List<PassBand> pbs = new ArrayList<>(bands);
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
        
        ExtSed flatten = ExtSed.flatten(sed, "Angstrom", "erg/s/cm2/Angstrom");
        double[] x = flatten.getSegment(0).getSpectralAxisValues();
        double[] y = flatten.getSegment(0).getFluxAxisValues();
        
        payload.setX(x);
        payload.setY(y);
        
        SAMPMessage message = SAMPFactory.createMessage("spectrum.integrate", payload, IntegrationPayload.class);
        return (Response) SAMPFactory.get(client.sendMessage(message).getResult(), Response.class);
        
    }


    public synchronized Response integrateComponents(List<PassBand> bands, CompositeModel model, List<UserModel> userModels, Integer nBins) throws Exception {
        
        SherpaFitConfiguration conf = (SherpaFitConfiguration) SAMPFactory.get(SherpaFitConfiguration.class);
        conf.addModel(model);

        if (userModels != null) {
            for (UserModel um : userModels) {
                conf.addUsermodel(um);
            }
        }

        Data dataset = client.createData("integrate");
        conf.addDataset(dataset);

        double[] x = new double[0];

        List<PassBand> pbs = new ArrayList<>(bands);
        IntegrationPayload payload = (IntegrationPayload) SAMPFactory.get(IntegrationPayload.class);
        for (PassBand pb : pbs) {
            if (pb instanceof PhotometryFilter) {
                PhotometryFilter f = (PhotometryFilter) pb;
                TransmissionCurve curve = (TransmissionCurve) SAMPFactory.get(TransmissionCurve.class);
                curve.setFileName(f.getLocalFile());
                curve.setId(f.getId());
                curve.setEffWave(f.getWleff());
                payload.addCurve(curve);

                double[] drange = range(f.getWlmin(), f.getWlmax(), nBins);
                x = concat(x, drange);
            }

            if (pb instanceof EnergyBin) {
                EnergyBin b = (EnergyBin) pb;
                Window w = (Window) SAMPFactory.get(Window.class);
                Double nmin = convertValues(new double[]{b.getMin()}, b.getUnits(), "Angstrom")[0];
                Double nmax = convertValues(new double[]{b.getMax()}, b.getUnits(), "Angstrom")[0];
                Double min = Math.min(nmin, nmax);
                Double max = Math.max(nmin, nmax);
                w.setMin(min);
                w.setMax(max);
                w.setId(pb.toString());
                payload.addWindow(w);
                double[] drange = range(min, max, nBins);
                x = concat(x, drange);
            }
        }

        dataset.setX(x);

        SAMPMessage modelMessage = SAMPFactory.createMessage("spectrum.fit.calc.model.values", conf, SherpaFitConfiguration.class);
        org.astrogrid.samp.Response response = client.sendMessage(modelMessage);

        Data result = SAMPFactory.get(response.getResult(), Data.class);

        double[] xx = result.getX();
        double[] yy = result.getY();

        yy = um.convertY(yy, xx, "photon/s/cm2/Angstrom", "Angstrom", "erg/s/cm2/Angstrom");

        // Integrate
        payload.setX(xx);
        payload.setY(yy);
        SAMPMessage message = SAMPFactory.createMessage("spectrum.integrate", payload, IntegrationPayload.class);

        Response res = (Response) SAMPFactory.get(client.sendMessage(message).getResult(), Response.class);
        for (SimplePhotometryPoint p : res.getPoints()) {
            double erg = p.getFlux();
//            double erg = YUnits.convert(new double[]{photon}, new double[]{p.getWavelength()}, new YUnits("photon/s/cm2/Angstrom"), new XUnits("Angstrom"), new YUnits("erg/s/cm2"), true)[0];
            p.setFlux(erg);
        }
        return res;
    }

    
    private double[] convertValues(double[] values, String fromUnits, String toUnits) throws UnitsException {
        return um.convertX(values, um.newXUnits(fromUnits), um.newXUnits(toUnits));
    }


    private double[] range(double start, double stop, int n)
    {
        double[] result = new double[n];
        double step = (stop-start)/n;
        double currentStart = start;

        for(int i=0; i<n-1; i++) {
            result[i] = currentStart + step;
            currentStart += step;
        }
        result[n-1] = stop;

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
