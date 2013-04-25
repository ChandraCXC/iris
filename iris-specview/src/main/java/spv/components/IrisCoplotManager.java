/*
 * This software is distributed under a BSD license,
 * as described in the LICENSE file at the top
 * source directory in the Specview source code base.
 */

package spv.components;

/**
 * Created by IntelliJ IDEA.
 * User: busko
 * Date: 9/6/12
 * Time: 2:28 PM
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.events.*;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.utils.IList;
import cfa.vo.sedlib.Point;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.TextParam;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;

import spv.glue.PlottableSEDSegmentedSpectrum;
import spv.util.Include;
import spv.util.MultiplePanelGUI;


public class IrisCoplotManager extends MultiplePanelGUI {

    private SedlibSedManager sedManager;
    private IList<ExtSed> seds;
    private JList sedsList;
    private IrisDisplayManager idm;

    public IrisCoplotManager(IWorkspace ws, IrisDisplayManager idm, String title) {
        super(title, false);

        this.idm = idm;

        sedManager = (SedlibSedManager) ws.getSedManager();

        refreshList();

        addPlotButton();

        // These handlers ensure that the Sed list kept internally
        // by this class is always in sync with the main Sed
        // storage that lives in the Sed manager.

        SedEvent.getInstance().add(new SedListener() {
            public void process(final ExtSed source, SedCommand payload) {
                refreshList();
            }
        });

        SegmentEvent.getInstance().add(new SegmentListener() {
            public void process(Segment source, final SegmentEvent.SegmentPayload payload) {
                refreshList();
            }
        });

        MultipleSegmentEvent.getInstance().add(new MultipleSegmentListener() {
            public void process(List< Segment > source, final SegmentEvent.SegmentPayload payload) {
                refreshList();
            }
        });
    }

    private void refreshList() {
        seds = sedManager.getSeds();
        buildList();
    }

    private void addPlotButton() {
        JButton button = new JButton("Co-plot");
        dismissPanel.add(button, 0);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                goCoPlot();
            }
        });
    }

    private void goCoPlot() {

        ExtSed originalSed = new ExtSed("", false); // non-managed SED

        ExtSed multipleSed = originalSed.clone();  // this must be done *after* the object gets populated, I believe.

        // The co-plotted SED requires that its name starts with a pre-defined
        // prefix, so it can be recognized and properly handled downstream.

        StringBuffer sbuffer = new StringBuffer(PlottableSEDSegmentedSpectrum.COPLOT_IDENT);

        int[] indices = sedsList.getSelectedIndices();

        for (int i = 0; i < indices.length; i++) {

            ExtSed sed = seds.get(indices[i]);

            String sedId = sed.getId();
            sbuffer.append(" ");
            sbuffer.append(sedId);

            int nsegs = sed.getNumberOfSegments();
            List<Segment> segmentList = new ArrayList<Segment>(nsegs);

            for (int j = 0; j < nsegs; j++) {
                Segment segment = sed.getSegment(j);
                segmentList.add(segment);
            }
            try {
                Segment flatSegment = buildFlatSegment(segmentList);
                multipleSed.addSegment(flatSegment);

                // manipulating the segment ID in the co-plotted SED.
                Segment multipleSedSegment = multipleSed.getSegment(multipleSed.getNumberOfSegments() - 1);
                TextParam targetName = multipleSedSegment.getTarget().getName();
                String targetNameValue = targetName.getValue();

                int colonIndex = targetNameValue.indexOf(":");
                if (colonIndex > 0) {
                    targetNameValue = targetNameValue.substring(0, colonIndex);
                }
                if ( ! targetNameValue.contains(sedId)) {
                    targetNameValue += ":" + sedId;
                }
                targetName.setValue(targetNameValue);

            } catch (SedInconsistentException e) {
                e.printStackTrace();
            } catch (SedNoDataException e) {
                e.printStackTrace();  
            }
        }

        String sedID = sbuffer.toString();
        multipleSed.setId(sedID);

        idm.display(multipleSed, sedID);
    }

    private Segment buildFlatSegment(List<Segment> segmentList) {

        List<Point> allPoints = new ArrayList<Point>();

        for (int i = 0; i < segmentList.size(); i++) {
            Segment segment = segmentList.get(i);

            List<Point> points = segment.getData().getPoint();
            allPoints.addAll(points);
        }

        // todo this makes the flat segment inherit all its metadata from
        // the first segment in the list. This probably has to be
        // changed, although I don't know how. Is metadata from a
        // segment directly copiable to a point?
        Segment firstSegment = segmentList.get(0);
        Segment result = (Segment) firstSegment.clone();
        result.getData().setPoint(allPoints);

        return result;
    }

    private void buildList() {
        Object[] data = new Object[seds.size()];
        for (int i = 0; i < seds.size(); i++) {
            ExtSed sed = seds.get(i);
            data[i] = sed;
        }
        sedsList = new JList(data);

        sedsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sedsList.setVisibleRowCount(-1);
        JScrollPane listScroller = new JScrollPane(sedsList);
        listScroller.setPreferredSize(Include.IRIS_SPLIST_WINDOW_SIZE);

        tabbed_pane.removeAll();

        tabbed_pane.addTab ("SEDs", null, listScroller, "Display SEDs");
    }
}
