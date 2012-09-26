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
        seds = sedManager.getSeds();

        addPlotButton();
        buildList();

        // These handlers ensure that the Sed list kept internally
        // by this class is always in synch with the main Sed
        // storage that lives in the Sed manager.

        SedEvent.getInstance().add(new SedListener() {
            public void process(final ExtSed source, SedCommand payload) {
                seds = sedManager.getSeds();
                buildList();
            }
        });

        SegmentEvent.getInstance().add(new SegmentListener() {
            public void process(Segment source, final SegmentEvent.SegmentPayload payload) {
                seds = sedManager.getSeds();
                buildList();
            }
        });

        MultipleSegmentEvent.getInstance().add(new MultipleSegmentListener() {
            public void process(List< Segment > source, final SegmentEvent.SegmentPayload payload) {
                seds = sedManager.getSeds();
                buildList();
            }
        });
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

        ExtSed multipleSed = sedManager.newSed("Co-plot");

        int[] indices = sedsList.getSelectedIndices();
        for (int i = 0; i < indices.length; i++) {
            ExtSed sed = seds.get(indices[i]);

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

        //todo add attachment with valid spectrum container.
        // must call  idm.display(container, id);   to prevent
        // event being fired.

//        idm.display(multipleSed, multipleSed.getId());
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
        listScroller.setPreferredSize(Include.SPLIST_WINDOW_SIZE);

        tabbed_pane.removeAll();

        tabbed_pane.addTab ("SEDs", null, listScroller, "Display SEDs");
    }
}
