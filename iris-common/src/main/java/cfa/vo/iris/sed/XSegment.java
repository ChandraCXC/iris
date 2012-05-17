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

package cfa.vo.iris.sed;

import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.XSegmentEvent;
import cfa.vo.iris.utils.AbstractIterator;
import cfa.vo.iris.utils.IList;
import cfa.vo.iris.utils.IPredicate;
import cfa.vo.iris.utils.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olaurino
 */
public class XSegment extends List<SpectroPhotometricPoint> implements IXSegment<SpectroPhotometricPoint> {

    private IList<IXSed> seds = new List();

    private ISpectralSegment metadata;

    public XSegment(String id, IXSed sed) {
        super(id);
        try {
            seds.add(sed);
        } catch (SedException ex) {//This exception can't be thrown because there can be no mismatch in a single element.
            Logger.getLogger(XSegment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public XSegment(String id, IXSed sed, IList<SpectroPhotometricPoint> elements) {
        super(id, elements);
        try {
            seds.add(sed);
        } catch (SedException ex) {//This exception can't be thrown because there can be no mismatch in a single element.
            Logger.getLogger(XSegment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Iterator iterator() {
        return new AbstractIterator(super.iterator()) {

            @Override
            protected void fireEvent() {
                XSegmentEvent.getInstance().fire(XSegment.this, SedCommand.REMOVED);
            }
        };
    }

    @Override
    public boolean add(SpectroPhotometricPoint point) throws SedException {
        System.out.println("Adding Point "+point+" to Segment "+this);
        boolean resp = super.add(point);
        if(resp) {
            XSegmentEvent.getInstance().fire(this, SedCommand.CHANGED);
        }

        return resp;
    }

    @Override
    public boolean addAll(Collection<? extends SpectroPhotometricPoint> elements) throws SedException {
        System.out.println("Adding Points "+elements+" to Segment "+this);
        boolean resp = super.addAll(elements);
        if(resp)
            XSegmentEvent.getInstance().fire(this, SedCommand.CHANGED);

        return resp;
    }

    @Override
    public void add(int position, SpectroPhotometricPoint element) throws SedException {
        System.out.println("Adding Point "+element+" to Segment "+this+ " in position "+position);
        super.add(position, element);
        XSegmentEvent.getInstance().fire(this, SedCommand.CHANGED);
    }

    @Override
    public boolean remove(SpectroPhotometricPoint element) {
        System.out.println("Removing Point "+element+" from Segment "+this);
        boolean resp = super.remove(element);
        XSegmentEvent.getInstance().fire(this, SedCommand.REMOVED);
        
        return resp;
    }

    @Override
    public IList<SpectroPhotometricPoint> filter(IPredicate<SpectroPhotometricPoint> predicate, String id) {
        return List.filter(this, predicate, id);
    }

    @Override
    public IList<IXSed> getSeds() {
        return seds;
    }

    @Override
    public void add(IXSed sed) {
        try {
            seds.add(sed);
        } catch (SedException ex) {//This exception can't be thrown because there is no consistency check for SEDs
            Logger.getLogger(XSegment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
