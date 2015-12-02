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
import cfa.vo.iris.utils.UTYPE;
import cfa.vo.sedlib.Param;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sherpa.SherpaClient;
import org.astrogrid.samp.Response;

public class SherpaRedshifter {

    private SherpaClient client;
    private SedlibSedManager manager;
    private static String REDSHIFT_MTYPE = "spectrum.redshift.calc";

    public SherpaRedshifter(SherpaClient client, SedlibSedManager manager) {
        this.client = client;
        this.manager = manager;
    }

    

    public ExtSed shift(ExtSed sed, Double fromRedshift, Double toRedshift) throws Exception {
        
        if(sed.getNumberOfSegments()==0)
            throw new SedNoDataException();

        ExtSed inputSed = ExtSed.flatten(sed, "Angstrom", "Jy");
        
        inputSed.setId(sed.getId() + "_" + toRedshift);
        
        RedshiftPayload payload = (RedshiftPayload) SAMPFactory.get(RedshiftPayload.class);
        payload.setX(inputSed.getSegment(0).getSpectralAxisValues());
        payload.setY(inputSed.getSegment(0).getFluxAxisValues());
        payload.setYerr((double[]) inputSed.getSegment(0).getDataValues(UTYPE.FLUX_STAT_ERROR));
        payload.setFromRedshift(fromRedshift);
        payload.setToRedshift(toRedshift);
        SAMPMessage message = SAMPFactory.createMessage(REDSHIFT_MTYPE, payload, RedshiftPayload.class);

        Response rspns = client.sendMessage(message);

        RedshiftPayload response = (RedshiftPayload) SAMPFactory.get(rspns.getResult(), RedshiftPayload.class);
        inputSed.getSegment(0).setSpectralAxisValues(response.getX());
        inputSed.getSegment(0).setFluxAxisValues(response.getY());
        inputSed.getSegment(0).setDataValues(response.getYerr(), UTYPE.FLUX_STAT_ERROR);
        
        inputSed.checkChar();
        
        inputSed.getSegment(0).addCustomParam(new Param(fromRedshift.toString(), "iris:original redshift", ""));
        inputSed.getSegment(0).addCustomParam(new Param(toRedshift.toString(), "iris:final redshift", ""));
        
        manager.add(inputSed);
        
        return inputSed;
    }

}
