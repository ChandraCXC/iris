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
package cfa.vo.sed.science.interpolation;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.units.UnitsManager;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sherpa.SherpaClient;
import org.astrogrid.samp.Response;

public class SherpaInterpolator {

    private SherpaClient client;
    private SedlibSedManager manager;
    private static String INTERPOLATE_MTYPE = "spectrum.interpolate";
    private UnitsManager um;

    public SherpaInterpolator(SherpaClient client, SedlibSedManager manager, UnitsManager unitsManager) {
        this.client = client;
        this.um = unitsManager;
    }

    public ExtSed interpolate(ExtSed sed, InterpolationConfig interpConf) throws Exception {

        if (sed.getNumberOfSegments() == 0) {
            throw new SedNoDataException();
        }
        
        ExtSed newsed = ExtSed.flatten(sed, "Angstrom", "Jy");
        
        String intervUnits = interpConf.getUnits();
        Double xmin = interpConf.getXMin();
        Double xmax = interpConf.getXMax();
        
        if(xmin>Double.NEGATIVE_INFINITY)
            xmin = um.convertX(new double[]{xmin}, intervUnits, "Angstrom")[0];
        
        if(xmax<Double.POSITIVE_INFINITY)
            xmax = um.convertX(new double[]{xmax}, intervUnits, "Angstrom")[0];
        
        interpConf.setXMin(Math.min(xmin, xmax));
        interpConf.setXMax(Math.max(xmin, xmax));

        interpConf.setX(newsed.getSegment(0).getSpectralAxisValues());
        interpConf.setY(newsed.getSegment(0).getFluxAxisValues());
        SAMPMessage message = SAMPFactory.createMessage(INTERPOLATE_MTYPE, interpConf, InterpolationPayload.class);
        Response rspns = client.sendMessage(message);

        InterpolationPayload response = (InterpolationPayload) SAMPFactory.get(rspns.getResult(), InterpolationPayload.class);
                
        if(xmin>Double.NEGATIVE_INFINITY)
            xmin = um.convertX(new double[]{xmin}, "Angstrom", intervUnits)[0];
        
        if(xmax<Double.POSITIVE_INFINITY)
            xmax = um.convertX(new double[]{xmax}, "Angstrom", intervUnits)[0];

        interpConf.setXMin(Math.min(xmin, xmax));
        interpConf.setXMax(Math.max(xmin, xmax));
        
        double[] x = um.convertX(response.getX(), "Angstrom", intervUnits);
        
        Segment segment = new Segment();
        segment.setSpectralAxisValues(x);
        segment.setFluxAxisValues(response.getY());
        segment.setTarget(sed.getSegment(0).getTarget());
        segment.setSpectralAxisUnits(intervUnits);
        segment.setFluxAxisUnits("Jy");
        String ucd="em.wl";
        if(intervUnits.equals("Hz"))
            ucd = "em.freq";
        else if(intervUnits.equals("keV"))
            ucd = "em.energy";
        segment.createChar().createSpectralAxis().setUcd(ucd);
        segment.createChar().createFluxAxis().setUcd("phot.flux.density;"+ucd);
        
        ExtSed newSed = manager.newSed(sed.getId() + "_" + interpConf.getMethod().replaceAll(" ", ""));
        newSed.addSegment(segment);
        newSed.checkChar();

        return newSed;

    }
}
