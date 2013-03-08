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
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;

import spv.controller.SpectrumContainer;
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

    // Bug: when co-plotting for the first time, everything works fine. The refreshList() method
    // is called from the constructor. Inspecting the contents of the 'seds' variable, we see that
    // all sed objects in the list have their Target name properly set.
    //
    // Now, after getting the first co-plot, we select on single Sed in the SedBuilder window. This
    // causes an event that triggers the SedListener above. The 'source' Sed passed to it has its
    // Target name already blanked out. The, refreshList gets executed, and if we inspect the 'seds'
    // list again, we see that one of the Seds in the list, the same that was passed to the event
    // handler above, has its Target name blanked out.
    //
    // From that point on, everything seems to break, since the point IDs become garbled.
    //
    // This is leading me to suspect of a bug in the SedManager.

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

        ExtSed multipleSed = new ExtSed("", false); // non-managed SED

        // The co-plotted SED requires that its name starts with a pre-defined
        // prefix, so it can be recognized and handled properly downstream.

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

                multipleSed.addSegment(segmentList);

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
