package cfa.vo.iris.test.unit;

import javax.swing.SwingUtilities;

import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedNoDataException;

/**
 * Class for helpful utility functions to be used in unit testing.
 *
 */
public class TestUtils {
    
    public static Segment createSampleSegment(double[] x, double[] y) throws SedNoDataException {
        Segment segment = new Segment();
        segment.setFluxAxisValues(y);
        segment.setFluxAxisUnits("Jy");
        segment.createChar().createFluxAxis().setUcd("ucdf");
        segment.setSpectralAxisValues(x);
        segment.setSpectralAxisUnits("Angstrom");
        segment.getChar().createSpectralAxis().setUcd("ucds");
        return segment;
        
    }
    
    public static  Segment createSampleSegment() throws SedNoDataException  {
        double[] x = new double[]{1.0, 2.0, 3.0};
        double[] y = new double[]{1.0, 2.0, 3.0};
        return createSampleSegment(x, y);
    }
    
    /**
     * For tests that use the mvc/swing infrastructure to make changes, it can be necessary to retry 
     * certain verification steps if they rely on changes occurring in the UI.
     * 
     */
    public static void invokeWithRetry(int maxRetries, long wait, Runnable runnable) throws Exception {
        Exception last = null;
        for (int i=0; i<maxRetries; i++) {
            try {
                SwingUtilities.invokeAndWait(runnable);
                return;
            } catch (Exception e) {
                last = e;
                Thread.sleep(wait);
            }
        }
        throw last;
    }
}
