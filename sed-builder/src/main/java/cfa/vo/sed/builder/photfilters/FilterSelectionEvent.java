/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.builder.photfilters;

import cfa.vo.iris.events.GenericEvent;
import cfa.vo.iris.events.SedCommand;

/**
 *
 * @author olaurino
 */
public class FilterSelectionEvent extends GenericEvent<PhotometryFilter, FilterSelectionListener, SedCommand>{
    private static class Holder {
        private static final FilterSelectionEvent INSTANCE = new FilterSelectionEvent();
    }

    public static FilterSelectionEvent getInstance() {
        return Holder.INSTANCE;
    }
}
