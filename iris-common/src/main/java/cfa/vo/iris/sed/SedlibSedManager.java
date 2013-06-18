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
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.logging.LogEntry;
import cfa.vo.iris.logging.LogEvent;
import cfa.vo.iris.utils.IList;
import cfa.vo.iris.utils.List;
import java.util.TreeMap;

/**
 *
 * @author olaurino
 */
public class SedlibSedManager implements ISedManager<ExtSed> {

    private TreeMap<String, ExtSed> sedMap = new TreeMap();
    private ExtSed selected;

    @Override
    public IList<ExtSed> getSeds() {
        return new List(sedMap.values());
    }

    @Override
    public boolean existsSed(ExtSed sed) {
        return sedMap.containsValue(sed);
    }

    @Override
    public void addAttachment(ExtSed sed, String attachmentId, Object attachment) {
        sed.addAttachment(attachmentId, attachment);
    }

    @Override
    public void removeAttachment(ExtSed sed, String attachmentId) {
        sed.removeAttachment(attachmentId);
    }

    @Override
    public void removeAttachment(String id, String attachmentId) {
        sedMap.get(id).removeAttachment(attachmentId);
    }

    @Override
    public void addAttachment(String id, String attachmentId, Object attachment) {
        sedMap.get(id).addAttachment(attachmentId, attachment);
    }

    @Override
    public Object getAttachment(ExtSed sed, String attachmentId) {
        return sed.getAttachment(attachmentId);
    }

    @Override
    public Object getAttachment(String id, String attachmentId) {
        return sedMap.get(id).getAttachment(attachmentId);
    }

    @Override
    public ExtSed getSelected() {
        return selected;
    }

    @Override
    public void add(ExtSed sed) {
        String id = sed.getId();
        int c = 0;
        while (existsSed(id + (c == 0 ? "" : "." + c))) {
            c++;
        }
        sed.setId(id + (c == 0 ? "" : "." + c));
        
        sedMap.put(sed.getId(), sed);
        SedEvent.getInstance().fire(sed, SedCommand.ADDED);
        LogEvent.getInstance().fire(this, new LogEntry("SED added: " + sed.getId(), this));
        select(sed);
    }

    @Override
    public void remove(String id) {
        ExtSed sed = sedMap.get(id);
        sedMap.remove(id);
        SedEvent.getInstance().fire(sed, SedCommand.REMOVED);
        LogEvent.getInstance().fire(this, new LogEntry("SED removed: " + id, this));
        if(this.getSeds().isEmpty()) {
            ExtSed newsed = this.newSed("Sed");
            this.select(newsed);
        } else {
            this.select(sedMap.lastEntry().getValue());
        }
    }

    @Override
    public void select(ExtSed sed) {
        this.selected = sed;
        SedEvent.getInstance().fire(sed, SedCommand.SELECTED);
        LogEvent.getInstance().fire(this, new LogEntry("SED selected: " + sed.getId(), this));
    }

    @Override
    public ExtSed newSed(String id) {
        int c = 0;
        while (existsSed(id + (c == 0 ? "" : "." + c))) {
            c++;
        }
        id = id + (c == 0 ? "" : "." + c);
        ExtSed sed = new ExtSed(id);
        sedMap.put(id, sed);
        SedEvent.getInstance().fire(sed, SedCommand.ADDED);
        LogEvent.getInstance().fire(this, new LogEntry("SED created: " + id, this));
        select(sed);
        return sed;
    }

    @Override
    public boolean existsSed(String id) {
        return sedMap.containsKey(id);
    }

    @Override
    public void rename(ExtSed sed, String newId) {
        String oldId = sed.getId();
        sedMap.remove(oldId);
        sed.setId(newId);
        sedMap.put(newId, sed);
        SedEvent.getInstance().fire(sed, SedCommand.CHANGED);
        LogEvent.getInstance().fire(this, new LogEntry("SED changed: " + oldId + " -> " + newId, sed));
    }

}
