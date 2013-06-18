/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.science.interpolation;

import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sherpa.SherpaClient;
import org.astrogrid.samp.Response;
import spv.util.UnitsException;
import spv.util.XUnits;
import spv.util.YUnits;

/**
 *
 * @author olaurino
 */
public class SherpaInterpolator {

    private SherpaClient client;
    private SedlibSedManager manager;
    private SAMPController controller;
    private static String INTERPOLATE_MTYPE = "spectrum.interpolate";

    public SherpaInterpolator(SAMPController controller, SedlibSedManager manager) {
        this.client = new SherpaClient(controller);
        this.manager = manager;
        this.controller = controller;
    }

    public ExtSed interpolate(ExtSed sed, InterpolationConfig interpConf) throws Exception {
        client.findSherpa();

        if (sed.getNumberOfSegments() == 0) {
            throw new SedNoDataException();
        }

        String sherpaId = client.getSherpaId();

        if (sherpaId == null) {
            NarrowOptionPane.showMessageDialog(null,
                    "Iris could not find the Sherpa process running in the background. Please check the Troubleshooting section in the Iris documentation.",
                    "Cannot connect to Sherpa",
                    NarrowOptionPane.ERROR_MESSAGE);
            throw new Exception("Sherpa not found");
        }
        
        ExtSed newsed = SedBuilder.flatten(sed, "Angstrom", "Jy");
        
        String intervUnits = interpConf.getUnits();
        Double xmin = interpConf.getXMin();
        Double xmax = interpConf.getXMax();
        
        if(xmin>Double.NEGATIVE_INFINITY)
            xmin = convertXValues(new double[]{xmin}, intervUnits, "Angstrom")[0];
        
        if(xmax<Double.POSITIVE_INFINITY)
            xmax = convertXValues(new double[]{xmax}, intervUnits, "Angstrom")[0];
        
        interpConf.setXMin(Math.min(xmin, xmax));
        interpConf.setXMax(Math.max(xmin, xmax));

        interpConf.setX(newsed.getSegment(0).getSpectralAxisValues());
        interpConf.setY(newsed.getSegment(0).getFluxAxisValues());
        SAMPMessage message = SAMPFactory.createMessage(INTERPOLATE_MTYPE, interpConf, InterpolationPayload.class);
        Response rspns = controller.callAndWait(sherpaId, message.get(), 10);
        if (client.isException(rspns)) {
            Exception ex = client.getException(rspns);
            throw ex;
        }

        InterpolationPayload response = (InterpolationPayload) SAMPFactory.get(rspns.getResult(), InterpolationPayload.class);
                
        if(xmin>Double.NEGATIVE_INFINITY)
            xmin = convertXValues(new double[]{xmin}, "Angstrom", intervUnits)[0];
        
        if(xmax<Double.POSITIVE_INFINITY)
            xmax = convertXValues(new double[]{xmax}, "Angstrom", intervUnits)[0];

        interpConf.setXMin(Math.min(xmin, xmax));
        interpConf.setXMax(Math.max(xmin, xmax));
        
        double[] x = convertXValues(response.getX(), "Angstrom", intervUnits);
        
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

    private double[] concat(double[] a, double[] b) {
        int aLen = a.length;
        int bLen = b.length;
        double[] c = new double[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    private double[] getSpectralValues(Segment segment) throws SedNoDataException, UnitsException {
        double[] values = segment.getSpectralAxisValues();
        return convertXValues(values, segment.getSpectralAxisUnits(), "Angstrom");
    }

    private double[] convertXValues(double[] values, String fromUnits, String toUnits) throws UnitsException {
        return XUnits.convert(values, new XUnits(fromUnits), new XUnits(toUnits));
    }

    private double[] convertYValues(double[] yvalues, double[] xvalues, String fromYUnits, String fromXUnits, String toUnits) throws UnitsException {
        return YUnits.convert(yvalues, xvalues, new YUnits(fromYUnits), new XUnits(fromXUnits), new YUnits(toUnits), true);
    }
}
