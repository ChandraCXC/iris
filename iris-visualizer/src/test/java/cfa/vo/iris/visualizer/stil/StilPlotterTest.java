/*
 * Copyright 2016 Chandra X-Ray Observatory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cfa.vo.iris.visualizer.stil;

import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.stil.SegmentStarTableAdapter;
import cfa.vo.iris.sed.stil.StarTableAdapter;
import cfa.vo.sed.test.App;
import cfa.vo.sed.test.Ws;
import cfa.vo.sedlib.ISegment;
import cfa.vo.sedlib.io.SedFormat;
import java.awt.Color;
import java.lang.reflect.Field;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.task.StringParameter;
import uk.ac.starlink.ttools.plot2.PlotLayer;
import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
import uk.ac.starlink.ttools.task.MapEnvironment;

/**
 *
 * @author jbudynk
 */
public class StilPlotterTest { //extends AbstractComponentGUITest {
    
//    private VisualizerComponent comp;
    private ExtSed sed;
    private Ws ws = new Ws();
    private final App app = new App();
    private StarTableAdapter<ISegment> adapter;
    
    public StilPlotterTest() {
        //comp = new VisualizerComponent();
        adapter = new SegmentStarTableAdapter();
        
    }

    @Test
    public void testAddSed() throws Exception {
        
        sed = ExtSed.read(getClass().getResource("/test_data/3c273.vot").getFile(), SedFormat.VOT);

        StilPlotter plot = new StilPlotter(app, ws, adapter);
        plot.reset(sed);
        PlotDisplay display = plot.getPlotDisplay();
        
        // check that plot env is correctly set
        MapEnvironment env = plot.getEnv();
        for (String string : env.getNames()) {
            System.err.println(string);
        }

        StringParameter par = new StringParameter("color");
        env.acquireValue(par);
        assertEquals(par.objectValue(env), "blue");
        
        // using reflection to access layers in plot display
        Field layers_ = PlotDisplay.class.getDeclaredField("layers_");
        layers_.setAccessible(true);
        PlotLayer[] layers = (PlotLayer[]) layers_.get(display);
        
        assertTrue(!ArrayUtils.isEmpty(layers));
        
        // loop through layers to check various plot properties
        for (PlotLayer layer : layers) {
            StarTable table = layer.getDataSpec().getSourceTable();
            assertEquals(layer.getOpt().getSingleColor().getRGB(), Color.RED);
        }
    }
    // create a stubbed PlotLayer
        
//        // check that StarTable was added to plotter
//        Field dataStore_ = PlotDisplay.class.getDeclaredField("dataStore_");
//        dataStore_.setAccessible(true);
//        DataStore dataStore = (DataStore) layers_.get("layers_");

        //plot.setLayout( new FlowLayout() );
//        plot.add( new MarkSamples() );
//        plot.add( new ProfileSamples() );
//        view.pack();
//        view.setVisible( true );
//        Thread.currentThread().sleep( 2000 );
//        view.dispose();
            
//        catch ( HeadlessException e ) {
//            System.out.println( "Headless environment - not running testAddSed" );
//        }
//    }

    //@Override
//    protected IrisComponent getComponent() {
//        return comp;
//    }
    
//        public void testProfiles() {
//        checkProfiles( PROFILES );
//    }

//    public void checkProfiles( StyleSet[] profiles ) {
//        for ( int i = 0; i < profiles.length; i++ ) {
//            StyleSet profile = profiles[ i ];
//            for ( int j = 0; j < 16; j++ ) {
//                MarkStyle style = (MarkStyle) profile.getStyle( j );
//
//                assertEquals( style, profile.getStyle( j ) );
//                for ( int k = 0; k < 16; k++ ) {
//                    if ( k == j ) {
//                        assertEquals( style, profile.getStyle( k ) );
//                    }
//                    else {
//                        assertTrue( style != profile.getStyle( k ) );
//                    }
//                }
//
//                MarkStyle copy = style.getShapeId()
//                                .getStyle( style.getColor(), style.getSize() );
//                copy.setOpaqueLimit( style.getOpaqueLimit() );
//                assertEquals( style, copy );
//            }
//        }
//    }
//}
//
//
//class MarkSamples extends JPanel {
//    final static int STEP = 16;
//    final static int SIZES = 8;
//
//    final static MarkShape[] SHAPES = new MarkShape[] {
//        MarkShape.OPEN_CIRCLE,
//        MarkShape.FILLED_CIRCLE,
//        MarkShape.OPEN_SQUARE,
//        MarkShape.FILLED_SQUARE,
//        MarkShape.OPEN_DIAMOND,
//        MarkShape.FILLED_DIAMOND,
//        MarkShape.OPEN_TRIANGLE_UP,
//        MarkShape.FILLED_TRIANGLE_UP,
//        MarkShape.OPEN_TRIANGLE_DOWN,
//        MarkShape.FILLED_TRIANGLE_DOWN,
//        MarkShape.CROSS,
//        MarkShape.CROXX,
//    };
//
//    MarkSamples() {
//        setBackground( Color.WHITE );
//        setPreferredSize( new Dimension( STEP * ( SIZES + 2 ) + 1,
//                                         STEP * ( SHAPES.length + 2 ) + 1 ) );
//    }
//
//    protected void paintComponent( Graphics g ) {
//        super.paintComponent( g );
//
//        Graphics g2 = (Graphics2D) g;
//
//        g2.setColor( Color.GRAY );
//
//        for ( int i = 0; i < 1000; i += STEP ) {
//            g2.drawLine( 0, i, 1000, i );
//            g2.drawLine( i, 0, i, 1000 );
//        }
//
//        Color color = Color.BLACK;
//        for ( int i = 0; i < SIZES; i++ ) {
//            int size = i + 1;
//            for ( int j = 0; j < SHAPES.length; j++ ) {
//                MarkStyle style = SHAPES[ j ].getStyle( color, size );
//                TestCase.assertEquals( style,
//                                       SHAPES[ j ].getStyle( color, size ) );
//                style.drawMarker( g2, ( i + 1 ) * STEP,
//                                      ( j + 1 ) * STEP );
//            }
//        }
//    }
//}
//
//class ProfileSamples extends JPanel {
//    final static int STEP = 16;
//    final static int ITEMS = 16;
//    final static StyleSet[] PROFILES = MarkerTest.PROFILES;
//
//    ProfileSamples() {
//        setBackground( Color.WHITE );
//        setPreferredSize( new Dimension( STEP * ( ITEMS + 2 ) + 1,
//                                         STEP * ( PROFILES.length + 2 ) + 1 ) );
//    }
//
//    protected void paintComponent( Graphics g ) {
//        super.paintComponent( g );
//
//        Graphics2D g2 = (Graphics2D) g;
//        g2.setColor( Color.GRAY );
//
//        for ( int i = 0; i < 1000; i += STEP ) {
//            g2.drawLine( 0, i, 1000, i );
//            g2.drawLine( i, 0, i, 1000 );
//        }
//
//        for ( int i = 0; i < ITEMS; i++ ) {
//            for ( int j = 0; j < PROFILES.length; j++ ) {
//                ((MarkStyle) PROFILES[ j ].getStyle( i ))
//               .drawMarker( g2, ( i + 1 ) * STEP, ( j + 1 ) * STEP );
//            }
//        }
//    }
//}
    
}
