/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package spv.components;

import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedNoDataException;
import java.awt.Graphics;
import java.util.List;
import javax.swing.JInternalFrame;
import spv.graphics.AxisCanvas;
import spv.graphics.AxisType;
import spv.graphics.CursorCanvas;
import spv.graphics.DataCanvas;
import spv.graphics.DataSet;
import spv.graphics.GraphicsAttributes;
import spv.graphics.GraphicsCanvas;
import spv.graphics.GraphicsException;
import spv.graphics.Symbol;
import spv.graphics.WCSBoxCanvas;
import spv.graphics.WCSSettingsCanvas;
import spv.util.Include;
import spv.util.UnitsException;
import spv.util.XUnits;
import spv.util.YUnits;

/**
 *
 * @author olaurino
 */
public class SpecviewPlotter extends JInternalFrame implements Plotter  {
    private ExtSed sed;
    private GraphicsCanvas canvas;

    public SpecviewPlotter(ExtSed sed) {
        super(Include.IRIS_APP_NAME, true, true, true, true);

        this.sed = sed;

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setSize(new java.awt.Dimension(600, 400));

        canvas = newCanvas();

        getContentPane().add(canvas.getJComponent());
    }

    private GraphicsCanvas newCanvas() {
        GraphicsCanvas c = new DataCanvas();
        c = new WCSBoxCanvas(c);
//        canvas = new CursorCanvas(canvas);
        c = new AxisCanvas(c);
        c = new WCSSettingsCanvas(c);
        c.setAxisType(AxisType.LOGLOG);
        c.setXUnits(new XUnits("Hz"));
        c.setYUnits(new YUnits("Jy"));
        c.setTitles("Spectral Axis", "Flux Axis");
        return c;
    }

    public void addSegment(Segment segment) {
        reset();
    }

    public void removeSegment(Segment segment) {
        reset();
    }

    @Override
    public void setTitle(String title) {
        
    }

    public void reset() {
        try {
            getContentPane().remove(canvas.getJComponent());
            canvas = newCanvas();
            for(int i=0; i<sed.getNumberOfSegments(); i++) {
                DataSet ds = new IrisDataSet(sed.getSegment(i));
                canvas.attachDataSet(ds);
            }
            getContentPane().add(canvas.getJComponent());
            canvas.getJComponent().updateUI();
            updateUI();
        } catch (Exception ex) {
            NarrowOptionPane.showMessageDialog(null, "Error: "+ex.getMessage(), "Error", NarrowOptionPane.ERROR_MESSAGE);
        }
    }

    public void display() {
        if(sed.getNumberOfSegments()>0) {
            int n = 0;
            for(int i = 0; i < sed.getNumberOfSegments(); i++)
                n+=sed.getSegment(i).getLength();

            if(n>0)
                setVisible(true);
        }
    }

    public void addSegments(List<Segment> segments) {
        for(Segment segment : segments)
            addSegment(segment);
    }

    public void removeSegments(List<Segment> segments) {
        for(Segment segment : segments) {
            removeSegment(segment);
        }
    }

    public JInternalFrame getFrame() {
        return this;
    }

    private class IrisDataSet extends DataSet {
        public IrisDataSet(Segment segment) throws SedNoDataException, GraphicsException, UnitsException {
            super(XUnits.convert(segment.getSpectralAxisValues(), new XUnits(segment.getSpectralAxisUnits()), (XUnits)canvas.getXUnits()),
                  segment.getFluxAxisValues());
            GraphicsAttributes ga = new GraphicsAttributes();
            ga.setStroke(null);
            ga.setSymbol(Symbol.SQUARE);
            setGraphicsAttributes(ga);
            setHistogram(false);
            setID("A");
        }
    }

}
