/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.builder.science;

import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sherpa.SherpaClient;
import org.astrogrid.samp.Response;
import spv.util.UnitsException;
import spv.util.XUnits;

/**
 *
 * @author olaurino
 */
public class SherpaRedshifter {

    private SherpaClient client;
    private SedlibSedManager manager;
    private SAMPController controller;
    private static String REDSHIFT_MTYPE = "spectrum.redshift.calc";

    public SherpaRedshifter(SAMPController controller, SedlibSedManager manager) {
        this.client = new SherpaClient(controller);
        this.manager = manager;
        this.controller = controller;
    }

    

    public ExtSed shift(ExtSed sed, Double fromRedshift, Double toRedshift) throws Exception {

        client.findSherpa();
        
        if(sed.getNumberOfSegments()==0)
            throw new SedNoDataException();
        
        String sherpaId = client.getSherpaId();
        
        if (sherpaId == null) {
            NarrowOptionPane.showMessageDialog(null,
                    "Iris could not find the Sherpa process running in the background. Please check the Troubleshooting section in the Iris documentation.",
                    "Cannot connect to Sherpa",
                    NarrowOptionPane.ERROR_MESSAGE);
            throw new Exception("Sherpa not found");
        }

        ExtSed newSed = manager.newSed(sed.getId() + "_" + toRedshift);

        for (int i = 0; i < sed.getNumberOfSegments(); i++) {
            Segment segment = (Segment) sed.getSegment(i).clone();
            double[] values = getSpectralValues(segment);
            RedshiftPayload payload = (RedshiftPayload) SAMPFactory.get(RedshiftPayload.class);
            payload.setX(values);
            payload.setY(segment.getFluxAxisValues());
            payload.setFromRedshift(fromRedshift);
            payload.setToRedshift(toRedshift);
            SAMPMessage message = SAMPFactory.createMessage(REDSHIFT_MTYPE, payload, RedshiftPayload.class);
            newSed.addSegment(segment);
            Response rspns = controller.callAndWait(sherpaId, message.get(), 10);
            if (client.isException(rspns)) {
                Exception ex = client.getException(rspns);
                throw ex;
            }

            RedshiftPayload response = (RedshiftPayload) SAMPFactory.get(rspns.getResult(), RedshiftPayload.class);
            double[] x = response.getX();
            segment.setSpectralAxisValues(convertValues(x, "Angstrom", segment.getSpectralAxisUnits()));
            segment.setFluxAxisValues(response.getY());
        }

        newSed.checkChar();
        
        return newSed;
    }

    private double[] getSpectralValues(Segment segment) throws SedNoDataException, UnitsException {
        double[] values = segment.getSpectralAxisValues();
        return convertValues(values, segment.getSpectralAxisUnits(), "Angstrom");
    }

    private double[] convertValues(double[] values, String fromUnits, String toUnits) throws UnitsException {
        return XUnits.convert(values, new XUnits(fromUnits), new XUnits(toUnits));
    }

    
}
