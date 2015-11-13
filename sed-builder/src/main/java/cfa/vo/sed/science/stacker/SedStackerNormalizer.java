/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
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

import cfa.vo.iris.units.UnitsManager;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.utils.UTYPE;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sherpa.SherpaClient;
import org.astrogrid.samp.Response;

import java.util.List;
import javax.swing.JOptionPane;

import org.astrogrid.samp.client.SampException;

public class SedStackerNormalizer {
    private SherpaClient client;
    private SAMPController controller;
    private static String NORMALIZE_MTYPE = "stack.normalize";
    private boolean normConfigChanged;
    private UnitsManager um;

    public SedStackerNormalizer(SAMPController controller, UnitsManager unitsManager) {
        this.client = new SherpaClient(controller);
        this.controller = controller;
        this.um = unitsManager;
    }

    public void normalize(SedStack stack) throws Exception {
        normalize(stack, stack.getConf().getNormConfiguration());
    }

    public void normalize(SedStack stack, NormalizationConfiguration normConfig) throws Exception {

        this.normConfigChanged = false;

        if (stack.getSeds().isEmpty()) {
            NarrowOptionPane.showMessageDialog(null,
                    "Stack is empty. Please add SEDs to the stack to normalize.",
                    "Empty Stack",
                    NarrowOptionPane.ERROR_MESSAGE);
            throw new SedNoDataException();
        }

        try {
            client.findSherpa();
        } catch (SampException ex) {
            NarrowOptionPane.showMessageDialog(null,
                    "Error normalizing: " +
                            "Iris could not find the Sherpa process running in the background. Please check the Troubleshooting section in the Iris documentation.",
                    "Cannot connect to Sherpa",
                    NarrowOptionPane.ERROR_MESSAGE);
            throw new Exception("Sherpa not found");
        }

        // Create copy of stack and convert new stack to same units. First save the original units for later.
        List<String> xunits = stack.getSpectralUnits();
        List<String> yunits = stack.getFluxUnits();
        //SedStack nstack = stack.copy();

        String xunit = null;
        String yunit = null;
        if (normConfig.isIntegrate()) {
            if (normConfig.getStats().toLowerCase().equals("value")) {
                String val = normConfig.getIntegrateValueYUnits();
                if (val.equals("erg/s/cm2")) {
                    yunit = "erg/s/cm2/Angstrom";
                    xunit = "Angstrom";
                } else if (val.equals("Jy-Hz")) {
                    yunit = "Jy";
                    xunit = "Hz";
                } else if (val.equals("Watt/m2")) {
                    yunit = "Watt/m2/Hz";
                    xunit = "Hz";
                } else if (val.equals("erg/s")) {
                    yunit = "erg/s/cm2";
                    xunit = "Angstrom";
                } else {
                    yunit = "Jy-Hz";
                    xunit = "Hz";
                }
            } else {
                yunit = normConfig.getIntegrateValueYUnits();
                xunit = normConfig.getXUnits();
            }
        } else {
            xunit = normConfig.getAtPointXUnits();
            yunit = normConfig.getAtPointYUnits();
        }
        convertUnits(stack, xunit, yunit);

        SedStackerNormalizePayload payload = (SedStackerNormalizePayload) SAMPFactory.get(SedStackerNormalizePayload.class);

        for (int i = 0; i < stack.getSeds().size(); i++) {

            SegmentPayload segment = (SegmentPayload) SAMPFactory.get(SegmentPayload.class);

            segment.setX(stack.getSed(i).getSegment(0).getSpectralAxisValues());
            segment.setY(stack.getSed(i).getSegment(0).getFluxAxisValues());
            segment.setYerr((double[]) stack.getSed(i).getSegment(0).getDataValues(UTYPE.FLUX_STAT_ERROR));
            segment.setId(stack.getSed(i).getId());
            payload.addSegment(segment);

        }

        payload.setIntegrate(normConfig.isIntegrate());
        if (normConfig.isIntegrate()) {
            // control max and min
            Double xmax = normConfig.getXmax();
            Double xmin = normConfig.getXmin();
            if (!xmax.equals(Double.POSITIVE_INFINITY)) {
                xmax = um.convertX(new double[]{xmax}, normConfig.getXUnits(), xunit)[0];
                payload.setXmax(xmax.toString());
            } else {
                payload.setXmax("max");
            }
            if (!xmin.equals(Double.NEGATIVE_INFINITY)) {
                xmin = um.convertX(new double[]{xmin}, normConfig.getXUnits(), xunit)[0];
                payload.setXmin(xmin.toString());
            } else {
                payload.setXmin("min");
            }

            // If we switch from wavelength to frequency or energy space, we need
            // to switch xmin and xmax. We already check that the user-input
            // xmin is smaller than xmax in SedStackerFrame, so switching it here shouldn't
            // be a problem.
            if (xmin > xmax) {
                payload.setXmin(xmax.toString());
                payload.setXmax(xmin.toString());
            }

            // control normalization statistic (median, average, or value)
            if (normConfig.getStats().equals("Median")) {
                payload.setStats("median");
            } else if (normConfig.getStats().equals("Average")) {
                payload.setStats("avg");
            } else {
                payload.setStats("value");
            }
            payload.setY0(normConfig.getYValue());

        } else {

            payload.setX0(normConfig.getAtPointXValue());
            payload.setY0(normConfig.getAtPointYValue());

            // control normalization statistic (median, average, or value)
            if (normConfig.getAtPointStats().equals("Median")) {
                payload.setStats("median");
            } else if (normConfig.getAtPointStats().equals("Average")) {
                payload.setStats("avg");
            } else {
                payload.setStats("value");
            }
        }

        if (normConfig.isMultiply()) {
            payload.setNormOperator(0);
        } else {
            payload.setNormOperator(1);
        }

        SAMPMessage message = SAMPFactory.createMessage(NORMALIZE_MTYPE, payload, SedStackerNormalizePayload.class);

        Response rspns = controller.callAndWait(client.findSherpa(), message.get(), 10);
        if (client.isException(rspns)) {
            Exception ex = client.getException(rspns);
            throw ex;
        }

        SedStackerNormalizePayload response = (SedStackerNormalizePayload) SAMPFactory.get(rspns.getResult(), SedStackerNormalizePayload.class);

        int c = 0;
        int ct = 0;
        for (SegmentPayload segment : response.getSegments()) {

            stack.getSeds().get(c).getSegment(0).setSpectralAxisValues(segment.getX());
            stack.getSeds().get(c).getSegment(0).setFluxAxisValues(segment.getY());
            stack.getSeds().get(c).getSegment(0).setDataValues(segment.getYerr(), UTYPE.FLUX_STAT_ERROR);

            // If any SEDs were normalized with new normalization paramters, update the norm constant and hashcode.
            if (Integer.parseInt(stack.getSed(c).getAttachment(SedStackerAttachments.NORM_CONF_HASH).toString()) != normConfig.hashCode()) {
                stack.getSeds().get(c).addAttachment(SedStackerAttachments.NORM_CONSTANT, segment.getNormConstant());
                stack.getSeds().get(c).addAttachment(SedStackerAttachments.NORM_CONF_HASH, normConfig.hashCode());
                ct++;
            }
            c++;

        }

        if (ct > 0)
            this.normConfigChanged = true;

        // convert back to the original units of the Stack
        convertUnits(stack, xunits, yunits);

	/* if some SEDs were skipped during shifting because they had no 
	* redshift, tell the user which SEDs weren't redshifted.
	*/
        if (response.getExcludeds() != null && response.getExcludeds().size() > 0) {
            NarrowOptionPane.showMessageDialog(null,
                    "SEDs " + response.getExcludeds() + " were not normalized because the normalization parameters fall outside the SEDs' spectral range.",
                    "Unnormalized SEDs",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private void convertUnits(SedStack stack, String xUnits, String yUnits) throws SedException, UnitsException {

        for (int i = 0; i < stack.getSeds().size(); i++) {

            ExtSed sed = stack.getSeds().get(i);
            ExtSed nsed = ExtSed.flatten(sed, xUnits, yUnits);

            stack.getSeds().get(i).getSegment(0).setFluxAxisUnits(yUnits);
            stack.getSeds().get(i).getSegment(0).setSpectralAxisUnits(xUnits);
            stack.getSeds().get(i).getSegment(0).setFluxAxisValues(nsed.getSegment(0).getFluxAxisValues());
            stack.getSeds().get(i).getSegment(0).setSpectralAxisValues(nsed.getSegment(0).getSpectralAxisValues());
            stack.getSeds().get(i).getSegment(0).setDataValues((double[]) nsed.getSegment(0).getDataValues(UTYPE.FLUX_STAT_ERROR),
                    UTYPE.FLUX_STAT_ERROR);

        }
    }

    private void convertUnits(SedStack stack, List<String> xUnits, List<String> yUnits) throws SedException, UnitsException {
        for (int i = 0; i < stack.getSeds().size(); i++) {

            // convert the units with ExtSed.flatten()
            ExtSed sed = stack.getSeds().get(i);
            ExtSed nsed = ExtSed.flatten(sed, xUnits.get(i), yUnits.get(i)); // PROBLEM HERE!!!

            // set the converted spectral and flux values of each SED
            stack.getSeds().get(i).getSegment(0).setFluxAxisUnits(yUnits.get(i));
            stack.getSeds().get(i).getSegment(0).setSpectralAxisUnits(xUnits.get(i));
            stack.getSeds().get(i).getSegment(0).setFluxAxisValues(nsed.getSegment(0).getFluxAxisValues());
            stack.getSeds().get(i).getSegment(0).setSpectralAxisValues(nsed.getSegment(0).getSpectralAxisValues());
            stack.getSeds().get(i).getSegment(0).setDataValues((double[]) nsed.getSegment(0).getDataValues(UTYPE.FLUX_STAT_ERROR),
                    UTYPE.FLUX_STAT_ERROR);

        }
    }

    public boolean normConfigChanged() {

        return this.normConfigChanged;
    }

}
