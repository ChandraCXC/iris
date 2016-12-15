package cfa.vo.iris.test.unit;

import javax.swing.SwingUtilities;

import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedNoDataException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.concurrent.Executor;

/**
 * Class for helpful utility functions to be used in unit testing.
 *
 */
public class TestUtils {
    
    public static Segment createSampleSegment(double[] x, double[] y, String xUnits, String yUnits) throws SedNoDataException {
        Segment segment = new Segment();
        segment.setFluxAxisValues(y);
        segment.setFluxAxisUnits(yUnits);
        segment.createChar().createFluxAxis().setUcd("ucdf");
        segment.setSpectralAxisValues(x);
        segment.setSpectralAxisUnits(xUnits);
        segment.getChar().createSpectralAxis().setUcd("ucds");
        return segment;
        
    }

    public static Segment createSampleSegment(double[] x, double[] y) throws SedNoDataException {
        return createSampleSegment(x, y, "Angstrom", "Jy");
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
            } catch (AssertionError e) {
                last = new RuntimeException(e);
                Thread.sleep(wait);
            }
        }
        throw last;
    }

    /**
     * Convenience method for reading baseline test files from resource paths
     *
     * @param requestingClass The class requesting the resource. Resource can be found with paths relative to the argument
     * @param path String path of the resource. May be relative or absolute
     * @return String representing the file contents
     * @throws Exception
     */
    public static String readFile(Class requestingClass, String path) throws Exception {
        String p = requestingClass.getResource(path).getFile();
        return FileUtils.readFileToString(new File(p));
    }
    
    public static class SingleThreadExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }
}
