/*
 * This software is distributed under a BSD license,
 * as described in the LICENSE file at the top
 * source directory in the Specview source code base.
 */
package spv.components;

/**
 * Created by IntelliJ IDEA. User: busko Date: 9/6/12 Time: 2:28 PM
 */
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.events.*;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.utils.IList;
import cfa.vo.sed.builder.SedBuilder;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.TextParam;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import java.util.logging.Level;
import java.util.logging.Logger;

import spv.glue.PlottableSEDSegmentedSpectrum;
import spv.util.ErrorDialog;
import spv.util.Include;
import spv.util.MultiplePanelGUI;
import spv.util.UnitsException;

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
            public void process(List< Segment> source, final SegmentEvent.SegmentPayload payload) {
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
                try {
                    goCoPlot();
                } catch (SedInconsistentException ex) {
                    new ErrorDialog("SEDs are incompatible and cannot be plotted together");
                } catch (SedNoDataException ex) {
                    new ErrorDialog("One of the SEDs is empty. Cannot coplot SEDs.");
                } catch (SedException ex) {
                    new ErrorDialog(ex.toString());
                }
            }
        });
    }

    private void goCoPlot() throws SedInconsistentException, SedNoDataException, SedException {

        ExtSed multipleSed = new ExtSed("", false); // non-managed SED

        // The co-plotted SED requires that its name starts with a pre-defined
        // prefix, so it can be recognized and properly handled downstream.

        StringBuffer sbuffer = new StringBuffer(PlottableSEDSegmentedSpectrum.COPLOT_IDENT);

        int[] indices = sedsList.getSelectedIndices();

        for (int i = 0; i < indices.length; i++) {

            ExtSed sed = seds.get(indices[i]);

            String sedId = sed.getId();
            sbuffer.append(" ");
            sbuffer.append(sedId);
            try {
                Segment newSegment = SedBuilder.flatten(sed, "Angstrom", "Jy").getSegment(0);
                multipleSed.addSegment(newSegment);
                newSegment.createTarget().createName().setValue(sedId);
            } catch (UnitsException ex) {
                new ErrorDialog("<html>The SEDs seem incompatible because their units are not interconvertible.<br/>They cannot be coplotted.");
                Logger.getLogger(IrisCoplotManager.class.getName()).log(Level.SEVERE, null, ex);
                return;
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

        tabbed_pane.addTab("SEDs", null, listScroller, "Display SEDs");
    }
}
