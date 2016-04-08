package cfa.vo.iris.fitting;

import cfa.vo.iris.gui.widgets.AbstractGridPanel;

import javax.swing.*;

public class FitResultsPanel extends AbstractGridPanel {
    private JTextField rStat;
    private JTextField nFev;
    private JTextField qval;
    private JTextField numPoints;
    private JTextField statVal;
    private JTextField dof;

    private FitConfigurationBean fit;

    public final static String PROP_FIT = "fit";

    public FitConfigurationBean getFit() {
        return fit;
    }

    public void setFit(FitConfigurationBean fit) {
        this.fit = fit;
        firePropertyChange(PROP_FIT, null, fit);
    }

    @Override
    protected void initComponents() {
        statVal = addTextField("Final Fit Statistic");
        rStat = addTextField("Reduced Statistic");
        qval = addTextField("Probability (Q-value)");
        nFev = addTextField("Number of Evaluations");
        numPoints = addTextField("Number of Points");
        dof = addTextField("Degrees of Freedom");
        this.setEnabled(false);
    }

    @Override
    protected void initBindings() {
        createBinding(FitConfigurationBean.PROP_STATVAL, statVal, "text");
        createBinding(FitConfigurationBean.PROP_QVAL, qval, "text");
        createBinding(FitConfigurationBean.PROP_NUMPOINTS, numPoints, "text");
        createBinding(FitConfigurationBean.PROP_DOF, dof, "text");
        createBinding(FitConfigurationBean.PROP_NFEV, nFev, "text");
        createBinding(FitConfigurationBean.PROP_RSTAT, rStat, "text");
    }

    @Override
    protected int getRows() {
        return 6;
    }

    @Override
    protected int getCols() {
        return 2;
    }

    @Override
    protected String getBindingRoot() {
        return "fit";
    }
}
