package cfa.vo.iris.utils;

/**
 * Created by olaurino on 10/21/15.
 */
public class Position {
    private Double ra;
    private Double dec;

    public Position(Double ra, Double dec) {
        this.ra = ra;
        this.dec = dec;
    }

    public Double getDec() {
        return dec;
    }

    public Double getRa() {
        return ra;
    }

}
