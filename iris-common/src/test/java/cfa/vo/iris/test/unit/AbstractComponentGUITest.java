/**
 * Copyright (C) 2015 Smithsonian Astrophysical Observatory
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
package cfa.vo.iris.test.unit;

import cfa.vo.iris.IrisComponent;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedNoDataException;

import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

/**
 * This abstract class makes it easier to unit-test a single component.
 *
 * Implementing classes can return a single {@link IrisComponent} instance rather
 * than a list of them.
 */
public abstract class AbstractComponentGUITest extends AbstractGUITest {

    @Override
    protected List<IrisComponent> getComponents() {
        return Arrays.asList(new IrisComponent[]{getComponent()});
    }
    
    protected abstract IrisComponent getComponent();
    
    // Common Util functions
    protected Segment createSampleSegment(double[] x, double[] y) throws SedNoDataException {
        Segment segment = new Segment();
        segment.setFluxAxisValues(y);
        segment.setFluxAxisUnits("Jy");
        segment.createChar().createFluxAxis().setUcd("ucdf");
        segment.setSpectralAxisValues(x);
        segment.setSpectralAxisUnits("Angstrom");
        segment.getChar().createSpectralAxis().setUcd("ucds");
        return segment;
        
    }
    
    protected Segment createSampleSegment() throws SedNoDataException  {
        double[] x = new double[]{1.0, 2.0, 3.0};
        double[] y = new double[]{1.0, 2.0, 3.0};
        return createSampleSegment(x, y);
    }
    
    /**
     * For tests that use the mvc/swing infrastructure to make changes, it can be necessary to retry 
     * certain verification steps if they rely on changes occurring in the UI.
     * 
     */
    protected static void invokeWithRetry(int maxRetries, long wait, Runnable runnable) throws Exception {
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
