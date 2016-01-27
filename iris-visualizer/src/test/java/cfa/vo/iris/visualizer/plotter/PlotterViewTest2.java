///*
// * Copyright 2016 Chandra X-Ray Observatory.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package cfa.vo.iris.visualizer.plotter;
//
//import cfa.vo.iris.IrisComponent;
//import cfa.vo.iris.sed.ExtSed;
//import cfa.vo.iris.test.unit.AbstractComponentGUITest;
//import cfa.vo.iris.visualizer.VisualizerComponent;
//import cfa.vo.iris.visualizer.plotter.PlotterView;
//import cfa.vo.iris.visualizer.stil.StilPlotter;
//import cfa.vo.sedlib.io.SedFormat;
//import java.awt.Color;
//import java.awt.Container;
//import java.awt.PopupMenu;
//import uk.ac.starlink.ttools.plot.MarkStyle;
//import uk.ac.starlink.ttools.plot.MarkStyles;
//import uk.ac.starlink.ttools.plot.StyleSet;
//
//import org.uispec4j.*;
//
//import javax.swing.*;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import uk.ac.starlink.ttools.plot2.task.PlotDisplay;
//
///**
// *
// * @author jbudynk
// */
//public class PlotterViewTest2 extends AbstractComponentGUITest {
//
//    final static StyleSet[] PROFILES = new StyleSet[] {
//        MarkStyles.spots( "Small spots", 3 ),
//        MarkStyles.spots( "Large spots", 5 ),
//        MarkStyles.openShapes( "Small open shapes", 3, Color.BLACK ),
//        MarkStyles.openShapes( "Large open shapes", 5, null ),
//        MarkStyles.filledShapes( "Small filled shapes", 3, Color.RED ),
//        MarkStyles.filledShapes( "Large filled shapes", 5, null ),
//    };
//    
//    private VisualizerComponent comp;
//    private ExtSed sed;
//
//    public PlotterViewTest2() {
//        comp = new VisualizerComponent();
//    }
//
//    public void testProfiles() {
//        checkProfiles( PROFILES );
//    }
//
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
//
//    public void testAddSed() throws InterruptedException {
//        
//        sed = ExtSed.read(getClass().getResource("/test_data/3c273.vot").getFile(), SedFormat.VOT);
//
//        PlotterView view = new PlotterView(name, app, ws);
//        StilPlotter plot = view.getPlot();
//        
//        PlotDisplay display = plot.getPlotDisplay();
//        display;
//
//        //plot.setLayout( new FlowLayout() );
//        plot.add( new MarkSamples() );
//        plot.add( new ProfileSamples() );
//        view.pack();
//        view.setVisible( true );
//        Thread.currentThread().sleep( 2000 );
//        view.dispose();
//            
////        catch ( HeadlessException e ) {
////            System.out.println( "Headless environment - not running testAddSed" );
////        }
//    }
//
//    @Override
//    protected IrisComponent getComponent() {
//        return comp;
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
