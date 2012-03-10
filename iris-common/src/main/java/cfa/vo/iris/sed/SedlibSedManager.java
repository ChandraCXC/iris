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
        sedMap.put(sed.getId(), sed);
        SedEvent.getInstance().fire(sed, SedCommand.ADDED);
        LogEvent.getInstance().fire(this, new LogEntry("SED added: "+sed.getId(), this));
        select(sed);
    }

    @Override
    public void remove(String id) {
        ExtSed sed = sedMap.get(id);
        sedMap.remove(id);
        SedEvent.getInstance().fire(sed, SedCommand.REMOVED);
        LogEvent.getInstance().fire(this, new LogEntry("SED removed: "+id, this));
    }

    @Override
    public void select(ExtSed sed) {
        this.selected = sed;
        SedEvent.getInstance().fire(sed, SedCommand.SELECTED);
        LogEvent.getInstance().fire(this, new LogEntry("SED selected: "+sed.getId(), this));
    }
//
//    @Override
//    public void SetDefaultSpectralUnits() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void SetDefaultFluxUnits() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    public ExtSed newSed(String id) {
        ExtSed sed = new ExtSed(id);
        sedMap.put(id, sed);
        SedEvent.getInstance().fire(sed, SedCommand.ADDED);
        LogEvent.getInstance().fire(this, new LogEntry("SED created: "+id, this));
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
        LogEvent.getInstance().fire(this, new LogEntry("SED changed: "+oldId+" -> "+newId, sed));
    }

//    public class ExtSed extends Sed {
//        private Map<String, Object> attachments = new TreeMap();
//
//        private String id;
//
//        public ExtSed(String id) {
//            this.id = id;
//        }
//
//        @Override
//        public void addSegment(Segment segment) throws SedInconsistentException, SedNoDataException {
//            super.addSegment(segment);
//        }
//
//        @Override
//        public void addSegment(Segment segment, int offset) throws SedNoDataException, SedInconsistentException {
//            super.addSegment(segment, offset);
//            SegmentEvent.getInstance().fire(segment, new SegmentPayload(this, SedCommand.ADDED));
//            LogEvent.getInstance().fire(this, new LogEntry("Segment added to SED: "+id, this));
//        }
//
//        @Override
//        public void addSegment(java.util.List<Segment> segments) throws SedInconsistentException, SedNoDataException {
//            super.addSegment(segments);
////            for(Segment segment : segments) {
////                SegmentEvent.getInstance().fire(segment, new SegmentPayload(this, SedCommand.ADDED));
////                LogEvent.getInstance().fire(this, new LogEntry("Segment added to SED: "+id, this));
////            }
//        }
//
//        @Override
//        public void addSegment(java.util.List<Segment> segments, int offset) throws SedInconsistentException, SedNoDataException {
//            super.addSegment(segments, offset);
//            for(Segment segment : segments) {
//                SegmentEvent.getInstance().fire(segment, new SegmentPayload(this, SedCommand.ADDED));
//                LogEvent.getInstance().fire(this, new LogEntry("Segment added to SED: "+id, this));
//            }
//        }
//
//        @Override
//        public void removeSegment(int i) {
//            Segment seg = this.getSegment(i);
//            super.removeSegment(i);
//            SegmentEvent.getInstance().fire(seg, new SegmentPayload(this, SedCommand.REMOVED));
//            LogEvent.getInstance().fire(this, new LogEntry("Segment removed from SED: "+id, this));
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public void addAttachment(String attachmentId, Object attachment) {
//            attachments.put(attachmentId, attachment);
//        }
//
//
//        public Object getAttachment(String attachmentId) {
//            if(attachments.containsKey(attachmentId))
//                return attachments.get(attachmentId);
//            else
//                return null;
//        }
//
//
//        public void removeAttachment(String attachmentId) {
//            attachments.remove(attachmentId);
//        }
//
//        @Override
//        public String toString() {
//            return id+" (Segments: "+this.getNumberOfSegments()+")";
//        }
//
//        @Override
//        public ExtSed clone() {
//            ExtSed s = (ExtSed) super.clone();
//            s.attachments = new HashMap();
//            return s;
//        }
//
//        public boolean remove(Segment s) {
//            boolean resp = super.segmentList.remove(s);
//            SegmentEvent.getInstance().fire(s, new SegmentPayload(this, SedCommand.REMOVED));
//            return resp;
//        }
//
//        public int indexOf(Segment s) {
//            return segmentList.indexOf(s);
//        }
//
//    }

}
