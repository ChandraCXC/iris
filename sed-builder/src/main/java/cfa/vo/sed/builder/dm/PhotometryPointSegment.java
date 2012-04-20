/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.sed.builder.dm;

import cfa.vo.sed.setup.validation.AbstractValidable;
import cfa.vo.sed.setup.validation.AbstractValidableParent;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author olaurin
 */
public class PhotometryPointSegment extends AbstractValidableParent implements SedSegment {

    public PhotometryPointSegment() {
        this.point = new PhotometryPoint();
        this.target = new ExtendedTarget();
    }

    public PhotometryPointSegment(PhotometryPoint point) {
        this.point = point;
        this.target = new ExtendedTarget();
    }

    public PhotometryPointSegment(ExtendedTarget target, PhotometryPoint point) {
        this.target = target;
        this.point = point;
    }

    private ExtendedTarget target;
    public static final String PROP_TARGET = "target";

    /**
     * Get the value of target
     *
     * @return the value of target
     */
    public ExtendedTarget getTarget() {
        return target;
    }

    /**
     * Set the value of target
     *
     * @param target new value of target
     */
    public final void setTarget(ExtendedTarget target) {
        ExtendedTarget oldTarget = this.target;
        this.target = target;
        propertyChangeSupport.firePropertyChange(PROP_TARGET, oldTarget, target);
    }
    private PhotometryPoint point;
    public static final String PROP_POINT = "point";

    /**
     * Get the value of point
     *
     * @return the value of point
     */
    public PhotometryPoint getPoint() {
        return point;
    }

    /**
     * Set the value of point
     *
     * @param point new value of point
     */
    public final void setPoint(PhotometryPoint point) {
        PhotometryPoint oldPoint = this.point;
        this.point = point;
        propertyChangeSupport.firePropertyChange(PROP_POINT, oldPoint, point);
    }

    public Segment get() throws SedException {
        if (!target.validate().isValid() || !point.validate().isValid()) {
            throw new SedException("PhotometryPoint is invalid!");
        }

        Segment s = new Segment();

        target.addTo(s);
        point.addTo(s);

        return s;

    }
    private List<AbstractValidable> children;

    @Override
    protected List<AbstractValidable> getValidableChildren() {
        if (children == null) {
            children = new ArrayList();
            children.add(target);
            children.add(point);
        }

        return children;
    }

    private Segment segment;

    @Override
    public Segment addTo(ExtSed sed) throws SedException {

        if(segment!=null) 
            sed.remove(segment);
        else
            segment = new Segment();

        target.addTo(segment);
        point.addTo(segment);

        return segment;

    }
}
