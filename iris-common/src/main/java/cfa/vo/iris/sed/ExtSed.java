/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.sed;

import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.iris.logging.LogEntry;
import cfa.vo.iris.logging.LogEvent;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sedlib.common.SedParsingException;
import cfa.vo.sedlib.io.SedFormat;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author olaurino
 */
public class ExtSed extends Sed {

    private Map<String, Object> attachments = new TreeMap();
    private String id;
    private boolean managed = true;

    public ExtSed(String id) {
        this.id = id;
    }

    public ExtSed(String id, boolean managed) {
        this(id);
        this.managed = managed;
    }

    @Override
    public void addSegment(Segment segment) throws SedInconsistentException, SedNoDataException {
        super.addSegment(segment);
    }

    @Override
    public void addSegment(Segment segment, int offset) throws SedNoDataException, SedInconsistentException {
        super.addSegment(segment, offset);
        if (managed) {
            SegmentEvent.getInstance().fire(segment, new SegmentPayload(this, SedCommand.ADDED));
            LogEvent.getInstance().fire(this, new LogEntry("Segment added to SED: " + id, this));
        }
    }

    @Override
    public void addSegment(java.util.List<Segment> segments) throws SedInconsistentException, SedNoDataException {
        super.addSegment(segments);
    }

    @Override
    public void addSegment(java.util.List<Segment> segments, int offset) throws SedInconsistentException, SedNoDataException {
        super.addSegment(segments, offset);
        if (managed) {
            for (Segment segment : segments) {
                SegmentEvent.getInstance().fire(segment, new SegmentPayload(this, SedCommand.ADDED));
                LogEvent.getInstance().fire(this, new LogEntry("Segment added to SED: " + id, this));
            }
        }
    }

    @Override
    public void removeSegment(int i) {
        Segment seg = this.getSegment(i);
        super.removeSegment(i);
        if (managed) {
            SegmentEvent.getInstance().fire(seg, new SegmentPayload(this, SedCommand.REMOVED));
            LogEvent.getInstance().fire(this, new LogEntry("Segment removed from SED: " + id, this));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addAttachment(String attachmentId, Object attachment) {
        attachments.put(attachmentId, attachment);
    }

    public Object getAttachment(String attachmentId) {
        if (attachments.containsKey(attachmentId)) {
            return attachments.get(attachmentId);
        } else {
            return null;
        }
    }

    public void removeAttachment(String attachmentId) {
        attachments.remove(attachmentId);
    }

    @Override
    public String toString() {
        return id + " (Segments: " + this.getNumberOfSegments() + ")";
    }

    @Override
    public ExtSed clone() {
        ExtSed s = (ExtSed) super.clone();
        s.attachments = new HashMap();
        return s;
    }

    public boolean remove(Segment s) {
        boolean resp = super.segmentList.remove(s);
        if(managed)
            SegmentEvent.getInstance().fire(s, new SegmentPayload(this, SedCommand.REMOVED));
        return resp;
    }

    public int indexOf(Segment s) {
        return segmentList.indexOf(s);
    }

    public static ExtSed read(String filename, SedFormat format) throws SedParsingException, SedInconsistentException, IOException, SedNoDataException {
        Sed sed = Sed.read(filename, format);
        String[] path = filename.split(File.separator);
        String id = path[path.length - 1].split("\\.")[0];
        ExtSed s = new ExtSed(id);
        for (int i = 0; i < sed.getNumberOfSegments(); i++) {
            s.addSegment(sed.getSegment(i));
        }

        return s;
    }
}
