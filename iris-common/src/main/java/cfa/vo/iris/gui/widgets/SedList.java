/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.iris.gui.widgets;

import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.iris.events.SegmentListener;
import cfa.vo.iris.sed.ISedManager;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sedlib.Segment;
import java.awt.EventQueue;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author olaurino
 */
public final class SedList extends JList {
    private SedListModel seds = new SedListModel();

    private ExtSed selectedSed;


    public SedList(final ISedManager<ExtSed> manager) {
        setModel(seds);
        for (ExtSed sed : manager.getSeds()) {
            seds.addElement(sed);
        }

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setSelectedSed(manager.getSelected());

        int i = seds.getIndex(selectedSed);
        this.setSelectionInterval(i, i);
        this.ensureIndexIsVisible(i);

        addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    ExtSed selected = (ExtSed) SedList.this.getSelectedValue();
                    if (selected != null) {
                        if (selected != selectedSed) {
                            setSelectedSed(selected);
                            manager.select(selected);
                        }
                    }
                }
            }
        });




    }

    public ExtSed getSelectedSed() {
        return selectedSed;
    }

    public void setSelectedSed(ExtSed sed) {
        this.selectedSed = sed;
    }

    public void update() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                SedList.this.updateUI();
            }
        });
    }

    

    private class SedListModel extends DefaultListModel {

        public SedListModel() {
            super();
            SegmentEvent.getInstance().add(new SegmentListener() {

                @Override
                public void process(Segment source, SegmentPayload payload) {
                    SedList.this.update();
                }
            });

            SedEvent.getInstance().add(new SedListener() {

                @Override
                public void process(ExtSed source, SedCommand payload) {
                    switch (payload) {
                        case CHANGED:
                            SedList.this.update();
                            break;
                        case ADDED:
                            addElement(source);
                            break;
                        case REMOVED:
                            SedList.this.setEnabled(false);

                            int i = getIndex(source);
                            if(i!=-1)
                                removeElementAt(i);

                            SedList.this.setEnabled(true);
                            break;

                        case SELECTED:
                            if (selectedSed != source || selectedSed == null) {
                                selectedSed = source;
                                i=getIndex(source);
                                SedList.this.setSelectionInterval(i, i);
                                SedList.this.ensureIndexIsVisible(i);
                            }
                            break;
                    }
                }
            });

        }

        public int getIndex(ExtSed sed) {
            for (int i = 0; i < getSize(); i++) {
                if (getElementAt(i) == sed) {
                    return i;
                }
            }
            return -1;
        }
    }
}
