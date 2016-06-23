/**
 * Copyright (C) 2012, 2015 Smithsonian Astrophysical Observatory
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

import cfa.vo.interop.*;
import cfa.vo.iris.events.MultipleSegmentEvent;
import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.iris.fitting.FitConfiguration;
import cfa.vo.iris.interop.SedServerResource;
import cfa.vo.iris.interop.VaoMessage;
import cfa.vo.iris.logging.LogEntry;
import cfa.vo.iris.logging.LogEvent;
import cfa.vo.iris.sed.quantities.AxisMetadata;
import cfa.vo.iris.sed.quantities.SPVYQuantity;
import cfa.vo.iris.sed.quantities.SPVYUnit;
import cfa.vo.iris.sed.quantities.XUnit;
import cfa.vo.iris.units.UnitsManager;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.utils.Default;
import cfa.vo.iris.utils.UTYPE;
import cfa.vo.sedlib.*;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sedlib.common.SedParsingException;
import cfa.vo.sedlib.io.SedFormat;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.astrogrid.samp.client.SampException;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExtSed extends Sed {

    private Map<String, Object> attachments = new TreeMap<>();
    private String id;
    private boolean managed = true;

    @Nonnull
    public FitConfiguration getFit() {
        if (fit == null) {
            fit = new FitConfiguration();
        }
        return fit;
    }

    public void setFit(FitConfiguration fit) {
        this.fit = fit;
        SedEvent.getInstance().fire(this, SedCommand.CHANGED);
    }

    private FitConfiguration fit = new FitConfiguration();
    private static UnitsManager uf = Default.getInstance().getUnitsManager();

    public ExtSed(String id) {
        this.id = id;
    }

    public ExtSed(String id, boolean managed) {
        this(id);
        this.managed = managed;
    }

    public boolean isManaged() {
        return this.managed;
    }

    public void setManaged(boolean managed) {
        this.managed = managed;
    }
    
    public List<Segment> getSegments() {
        return Collections.unmodifiableList(new ArrayList<>(segmentList));
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
            SedEvent.getInstance().fire(this, SedCommand.CHANGED);
            LogEvent.getInstance().fire(this, new LogEntry("Segment added to SED: " + id, this));
        }
    }

    private void addSegmentSilently(Segment segment, int offset) throws SedNoDataException, SedInconsistentException {
        super.addSegment(segment, offset);
    }

    @Override
    public void addSegment(java.util.List<Segment> segments) throws SedInconsistentException, SedNoDataException {
        super.addSegment(segments);
    }

    @Override
    public void addSegment(java.util.List<Segment> segments, int offset) throws SedInconsistentException, SedNoDataException {
        for (Segment segment : segments) {
            addSegmentSilently(segment, offset);
        }
        if (managed) {
            MultipleSegmentEvent.getInstance().fire(segments, new SegmentPayload(this, SedCommand.ADDED));
            SedEvent.getInstance().fire(this, SedCommand.CHANGED);
            LogEvent.getInstance().fire(this, new LogEntry("Segments added to SED: " + id, this));
        }
    }

    @Override
    public void removeSegment(int i) {
        Segment seg = this.getSegment(i);
        super.removeSegment(i);
        if (managed) {
            SegmentEvent.getInstance().fire(seg, new SegmentPayload(this, SedCommand.REMOVED));
            SedEvent.getInstance().fire(this, SedCommand.CHANGED);
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
        s.attachments = new TreeMap<>();
        return s;
    }

    public boolean remove(Segment s) {
        boolean resp = super.segmentList.remove(s);
        if (managed) {
            SegmentEvent.getInstance().fire(s, new SegmentPayload(this, SedCommand.REMOVED));
            SedEvent.getInstance().fire(this, SedCommand.CHANGED);
            LogEvent.getInstance().fire(this, new LogEntry("Segments removed from SED: " + id, this));
        }
        return resp;
    }

    public boolean remove(List<Segment> segments) {
        boolean resp = true;
        for (Segment s : segments) {
            resp &= super.segmentList.remove(s);
        }

        if (managed) {
            MultipleSegmentEvent.getInstance().fire(segments, new SegmentPayload(this, SedCommand.REMOVED));
            SedEvent.getInstance().fire(this, SedCommand.CHANGED);
            LogEvent.getInstance().fire(this, new LogEntry("Segments removed from SED: " + id, this));
        }
        return resp;
    }

    public int indexOf(Segment s) {
        return segmentList.indexOf(s);
    }

    public static ExtSed read(String filename, SedFormat format) throws SedParsingException, SedInconsistentException, IOException, SedNoDataException {
        return read(filename, format, true);
    }
    
    public static ExtSed read(String filename, SedFormat format, boolean managed) throws SedParsingException, SedInconsistentException, IOException, SedNoDataException {
        Sed sed = Sed.read(filename, format);
        String[] path = filename.split(File.separator);
        String id = path[path.length - 1].split("\\.")[0];
        return wrap(sed, id, managed);
    }

    public static ExtSed read(InputStream stream, SedFormat format) throws SedParsingException, SedInconsistentException, IOException, SedNoDataException {
        return read(stream, format, true);
    }

    public static ExtSed read(InputStream stream, SedFormat format, boolean managed) throws SedParsingException, SedInconsistentException, IOException, SedNoDataException {
        Sed sed = Sed.read(stream, format);
        String id = stream.toString();
        return wrap(sed, id, managed);
    }

    private static ExtSed wrap(Sed sed, String id, boolean managed) throws SedNoDataException, SedInconsistentException {
        ExtSed s = new ExtSed(id, managed);
        for (int i = 0; i < sed.getNumberOfSegments(); i++) {
            s.addSegment(sed.getSegment(i));
        }

        return s;
    }

    public void checkChar() {
        for (int i = 0; i < this.getNumberOfSegments(); i++) {
            Segment segment = this.getSegment(i);
            List<Double> spectral;
            try {
                spectral = Arrays.asList(ArrayUtils.toObject(segment.getSpectralAxisValues()));
                double min = Collections.min(spectral);
                double max = Collections.max(spectral);
                segment.createChar().createSpectralAxis().createCoverage().createBounds().createRange().createMin().setValue(min);
                segment.createChar().createSpectralAxis().createCoverage().createBounds().createRange().createMax().setValue(max);
                segment.createChar().createSpectralAxis().createCoverage().createBounds().createExtent().setValue(max - min);
            } catch (SedNoDataException ex) {
                Logger.getLogger(ExtSed.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    
    public static ExtSed flatten(ExtSed sed, String xunit, String yunit) throws cfa.vo.sedlib.common.SedException, UnitsException {
        if (sed.getNumberOfSegments() == 0) {
            throw new SedNoDataException();
        }

        double xvalues[] = {};
        double yvalues[] = {};
        double staterr[] = {};

        Target target = new Target();

        for (int i = 0; i < sed.getNumberOfSegments(); i++) {
            Segment oldSegment = sed.getSegment(i);
            if (oldSegment.isSetTarget()) {
                Target t = oldSegment.getTarget();
                if (t.isSetName() && !t.getName().getValue().equals("UNKNOWN")) {
                    target.setName((TextParam)t.getName().clone());
                    if (t.isSetPos()) {
                        target.setPos((PositionParam)t.getPos().clone());
                    }
                }
            }

            double[] xoldvalues = oldSegment.getSpectralAxisValues();
            double[] yoldvalues = oldSegment.getFluxAxisValues();
            double[] erroldvalues = (double[]) oldSegment.getDataValues(UTYPE.FLUX_STAT_ERROR);
            String xoldunits = oldSegment.getSpectralAxisUnits();
            String yoldunits = oldSegment.getFluxAxisUnits();
            double[] ynewvalues = convertYValues(yoldvalues, xoldvalues, yoldunits, xoldunits, yunit);
            yvalues = concat(yvalues, ynewvalues);
            if (erroldvalues != null) {
                double[] errnewvalues = uf.convertErrors(erroldvalues, yoldvalues, xoldvalues, uf.newYUnits(yoldunits), uf.newXUnits(xoldunits), uf.newYUnits(yunit));
//                double[] errnewvalues = convertYValues(erroldvalues, xoldvalues, yoldunits, xoldunits, yunit);
                staterr = concat(staterr, errnewvalues);
            }
            double[] xnewvalues = convertXValues(xoldvalues, xoldunits, xunit);
            xvalues = concat(xvalues, xnewvalues);
        }

        Segment segment = new Segment();
        segment.setSpectralAxisValues(xvalues);
        segment.setFluxAxisValues(yvalues);
        segment.setDataValues(staterr, UTYPE.FLUX_STAT_ERROR);
        segment.setTarget(target);
        segment.setSpectralAxisUnits(xunit);
        segment.setFluxAxisUnits(yunit);
        String xucd = null;
        for (XUnit u : XUnit.values()) {
            if (u.getString().contains(xunit)) {
                xucd = u.getUCD();
            }
        }
        segment.createChar().createSpectralAxis().setUcd(xucd);

        String yucd = null;
        for (SPVYUnit u : SPVYUnit.values()) {
            if (u.getString().equals(yunit)) {
                for (SPVYQuantity q : SPVYQuantity.values()) {
                    if (q.getPossibleUnits().contains(u)) {
                        yucd = (new AxisMetadata(q, u)).getUCD();
                    }
                }
            }
        }
        segment.createChar().createFluxAxis().setUcd(yucd);

        ExtSed newSed = new ExtSed("Exported", false);
        newSed.addSegment(segment);
        newSed.checkChar();

        return newSed;
    }
    
    private static double[] concat(double[] a, double[] b) {
        int aLen = a.length;
        int bLen = b.length;
        double[] c = new double[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    private static double[] convertXValues(double[] values, String fromUnits, String toUnits) throws UnitsException {
        return uf.convertX(values, uf.newXUnits(fromUnits), uf.newXUnits(toUnits));
    }

    private static double[] convertYValues(double[] yvalues, double[] xvalues, String fromYUnits, String fromXUnits, String toUnits) throws UnitsException {
        return uf.convertY(yvalues, xvalues, uf.newYUnits(fromYUnits), uf.newXUnits(fromXUnits), uf.newYUnits(toUnits));
    }

    /**
     * This convenience method builds a new
     * SedMessage, creates a new resource to be served by the internal HTTPServer and
     * sends the SedMessage to the hub.
     *
     * @throws SampException
     */
    public void sendSedMessage(SampService service) throws SampException {
        try {

            if(service.getSampClient().getConnection().getSubscribedClients("table.load.votable").isEmpty())
                throw new SampException("No clients can receive the SAMP Message");

            for (int i = 1; i < getNumberOfSegments()+1; i++) {

                String n = i==1 ? "" : String.valueOf(i);
                String id = "ExportedSegment" +n;
                Sed s = new Sed();

                s.addSegment(getSegment(i-1));

                VaoMessage msg = (VaoMessage) SAMPFactory.get(VaoMessage.class);
                msg.setName(getId()+"Segment"+n);
                msg.setTableId(id);
                msg.getVaoPayload().setMessageType("sed");
                msg.getVaoPayload().setSenderId("iris");
                String filename = id + ".vot";
                URL url = service.addResource(filename, new SedServerResource(s));
                msg.setUrl(url.toString());

                SAMPMessage message = SAMPFactory.createMessage("table.load.votable", msg, VaoMessage.class);
                ((Map)message.get().getParam("vao-payload")).put("sed-id", getId());
                service.sendMessage(message);
            }

        } catch (Exception ex) {
            Logger.getLogger(ExtSed.class.getName()).log(Level.SEVERE, null, ex);
            throw new SampException(ex.getMessage());
        }

    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ExtSed)) {
            return false;
        }
        
        // Two ExtSeds with different Ids are not equal.
        ExtSed other = (ExtSed) o;
        if (!StringUtils.equals(id, other.getId())) {
            return false;
        }
        
        return super.equals(o);
    }
}
