/**
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
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
package cfa.vo.iris.fitting;

import cfa.vo.iris.fitting.custom.DefaultCustomModel;
import cfa.vo.iris.gui.widgets.ModelExpressionVerifier;
import cfa.vo.sherpa.*;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.sed.SedException;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.units.XUnit;
import cfa.vo.iris.units.spv.XUnits;
import cfa.vo.sherpa.models.*;
import cfa.vo.sherpa.optimization.Method;
import cfa.vo.sherpa.optimization.OptimizationMethod;
import cfa.vo.sherpa.stats.Stat;
import cfa.vo.sherpa.stats.Statistic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import javax.annotation.Nonnull;
import javax.swing.tree.TreeModel;
import java.beans.PropertyChangeSupport;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@JsonIgnoreProperties({"treeModel", "modelValid", "expression", "sedVersion"})
public class FitConfiguration {
    public static final String PROP_MODEL = "model";
    public static final String PROP_STAT = "stat";
    public static final String PROP_METHOD = "method";
    public static final String PROP_RSTAT = "rStat";
    public static final String PROP_NFEV = "nFev";
    public static final String PROP_QVAL = "qVal";
    public static final String PROP_NUMPOINTS = "numPoints";
    public static final String PROP_STATVAL = "statVal";
    public static final String PROP_DOF = "dof";
    public static final String PROP_USERMODELLIST = "userModelList";
    public static final String PROP_EXPRESSION = "expression";
    public static final String PROP_CONFIDENCE = "confidence";

    public static final String ROOT_MODELS_STRING = CompositeModelTreeModel.ROOT_STRING;

    private CompositeModel model;
    private List<FittingRange> fittingRanges;
    private Stat stat;
    private Method method;
    private Confidence confidence;
    private ConfidenceResults confResults;
    private Double rStat;
    private Integer nFev;
    private Double qVal;
    private Integer numPoints;
    private Double statVal;
    private Integer dof;
    private int sedVersion;

    private ObservableList<UserModel> userModelList = ObservableCollections.observableList(new ArrayList<UserModel>());
    private ModelExpressionVerifier verifier = new ModelExpressionVerifier();
    private Logger logger = Logger.getLogger(FitConfiguration.class.getName());

    private transient final PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);

    public FitConfiguration() {
        fittingRanges = new ArrayList<>();
        init();
    }

    @Nonnull
    public CompositeModel getModel() {
        return model;
    }

    public void setModel(CompositeModel model) {
        CompositeModel oldModel = this.model;
        this.model = model;
        propertyChangeSupport.firePropertyChange(PROP_MODEL, oldModel, model);
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        Stat oldStat = this.stat;
        this.stat = stat;
        propertyChangeSupport.firePropertyChange(PROP_STAT, oldStat, stat);
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        Method oldMethod = this.method;
        this.method = method;
        propertyChangeSupport.firePropertyChange(PROP_METHOD, oldMethod, method);
    }

    public ConfidenceResults getConfidenceResults() {
        return confResults;
    }

    public void setConfidenceResults(ConfidenceResults confResults) {
        ConfidenceResults old = this.confResults;
        this.confResults = confResults;
        propertyChangeSupport.firePropertyChange(PROP_CONFIDENCE, old, confResults);
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public void setConfidence(Confidence confidence) {
        Confidence old = this.confidence;
        this.confidence = confidence;
        propertyChangeSupport.firePropertyChange(PROP_CONFIDENCE, old, confidence);
    }

    public Double getrStat() {
        return rStat;
    }

    public void setrStat(Double rStat) {
        Double oldrStat = this.rStat;
        this.rStat = rStat;
        propertyChangeSupport.firePropertyChange(PROP_RSTAT, oldrStat, rStat);
    }

    public Integer getnFev() {
        return nFev;
    }

    public void setnFev(Integer nFev) {
        Integer oldNFev = this.nFev;
        this.nFev = nFev;
        propertyChangeSupport.firePropertyChange(PROP_NFEV, oldNFev, nFev);
    }

    public Double getqVal() {
        return qVal;
    }

    public void setqVal(Double qVal) {
        Double oldQVal = this.qVal;
        this.qVal = qVal;
        propertyChangeSupport.firePropertyChange(PROP_QVAL, oldQVal, qVal);
    }

    public Integer getNumPoints() {
        return numPoints;
    }

    public void setNumPoints(Integer numPoints) {
        Integer old = this.numPoints;
        this.numPoints = numPoints;
        propertyChangeSupport.firePropertyChange(PROP_NUMPOINTS, old, numPoints);
    }

    public Double getStatVal() {
        return statVal;
    }

    public void setStatVal(Double statVal) {
        Double old = this.statVal;
        this.statVal = statVal;
        propertyChangeSupport.firePropertyChange(PROP_STATVAL, old, statVal);
    }

    public Integer getDof() {
        return dof;
    }

    public void setDof(Integer dof) {
        Integer old = this.dof;
        this.dof = dof;
        propertyChangeSupport.firePropertyChange(PROP_DOF, old, dof);
    }

    public List<UserModel> getUserModelList() {
        return Collections.unmodifiableList(userModelList);
    }

    public boolean addUserModel(UserModel model) {
        boolean retVal = this.userModelList.add(model);
        if (retVal) {
            propertyChangeSupport.firePropertyChange(PROP_USERMODELLIST, null, model);
        }
        return retVal;
    }

    public boolean addUserModel(DefaultCustomModel m, String id) {
        boolean retVal = addUserModel(m.makeUserModel(id));
        Model mod = m.makeModel(id);
        model.addPart(mod);
        addToExpression(id);
        return retVal;
    }

    public void addModel(Model m) {
        model.addPart(m);
        addToExpression(m.getName().split("\\.")[1]);
    }

    public boolean removeModel(Model m) {
        boolean retVal = model.getParts().remove(m);
        removeUserModel(m);
        if (retVal) {
            propertyChangeSupport.firePropertyChange(PROP_USERMODELLIST, null, model);
        }
        return retVal;
    }

    public void setUserModelList(List<UserModel> userModelList) {
        ObservableList<UserModel> oldList = this.userModelList;
        this.userModelList = ObservableCollections.observableList(userModelList);
        propertyChangeSupport.firePropertyChange(PROP_USERMODELLIST, oldList, userModelList);
    }

    public TreeModel getTreeModel() {
        return new CompositeModelTreeModel(this);
    }

    public boolean isModelValid() {
        return verifier.verify(this);
    }

    public void setExpression(String expression) {
        String oldExpression = getModel().getName();
        getModel().setName(expression);
        propertyChangeSupport.firePropertyChange(PROP_EXPRESSION, oldExpression, expression);
    }

    public String getExpression() {
        String expression = getModel().getName();
        if ( expression != null && !expression.isEmpty()  )
            return expression;
        else {
            return "No Model";
        }
    }
    
    /**
     * Add a fitting range to the Fit configuration
     * @param fittingRange 
     */
    public void addFittingRange(FittingRange fittingRange) {

        // convert to Angstroms
        // TODO: update this to convert to user preferences later on
        XUnit oldUnit = new XUnits(fittingRange.getXUnit().getString());
        XUnit newUnit = new XUnits(SherpaClient.X_UNIT);
        
        try {
            fittingRange.setXUnit(cfa.vo.iris.sed.quantities.XUnit.getFromUnitString(newUnit.toString()));
            double tmpStart = XUnits.convert(new double[]{fittingRange.getStartPoint()}, oldUnit, newUnit)[0];
            double tmpEnd = XUnits.convert(new double[]{fittingRange.getEndPoint()}, oldUnit, newUnit)[0];
            
            // verify fitting range is in sorted order (low to high)
            if (tmpEnd < tmpStart) {
                // switch start and end points
                fittingRange.setStartPoint(tmpEnd);
                fittingRange.setEndPoint(tmpStart);
            } else {
                // original order is correct
                fittingRange.setStartPoint(tmpStart);
                fittingRange.setEndPoint(tmpEnd);
            }
        } catch (UnitsException | SedException ex) {
            Logger.getLogger(FitConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.fittingRanges.add(fittingRange);
    }
    
    /**
     * Return a list of the fit configuration fitting ranges
     * @return List of fitting ranges
     */
    public List<FittingRange> getFittingRanges() {
        return this.fittingRanges;
    }
    
    /**
     * Remove the ith fitting range from the model
     * @param i 
     */
    public void removeFittingRange(int i) {
        fittingRanges.remove(i);
    }
    
    /**
     * Remove a list of indices from the fitting ranges
     * @param indices - list of indices of fitting ranges to remove
     */
    public void removeFittingRanges(int[] indices) {
        int ct = 0;
        for (int i : indices) {
            // remove the (i - ct)th range in the FitCOnfiguration since each
            // iteration makes the list of ranges smaller by one.
            fittingRanges.remove(i-ct);
            ct++;
        }
    }
    
    /**
     * Remove the given fitting range from the model
     * @param fittingRange 
     */
    public void removeFittingRange(FittingRange fittingRange) {
        fittingRanges.remove(fittingRange);
    }
    
    /**
     * Remove all the fitting ranges from the model
     */
    public void clearFittingRanges() {
        fittingRanges.clear();
    }

    public int getSedVersion() {
        return sedVersion;
    }

    public void setSedVersion(int sedVersion) {
        this.sedVersion = sedVersion;
    }

    public void integrateResults(FitResults results) {
        Map<String, Parameter> params = new HashMap<>();
        for (Model m : model.getParts()) {
            for (Parameter p : m.getPars()) {
                params.put(p.getName(), p);
            }
        }

        List<String> parNames = results.getParnames();
        double[] parVals = results.getParvals();

        for (int i=0; i<parVals.length; i++) {
            String name = parNames.get(i);
            params.get(name).setVal(parVals[i]);
        }

        setrStat(results.getRstat());
        setnFev(results.getNfev());
        setqVal(results.getQval());
        setNumPoints(results.getNumpoints());
        setStatVal(results.getStatval());
        setDof(results.getDof().intValue());

        setConfidenceResults(null);
    }

    public void reset() {
        init();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(DefaultModel.toString(model));
        resultsToString(builder);
        confToString(builder);
        userModelsToString(builder);

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof FitConfiguration)) return false;

        FitConfiguration that = (FitConfiguration) o;

        // This is a hack required by the lack of proper classes in the SAMP-exchanged interfaces,
        // as there is no easy way to implement `equals` working with concrete implementations (e.g. DefaultModel, dynamic
        // proxies and asm-generated bytecode from Jackson.
        // FIXME introduce classes and factories for SAMP-backing interfaces
        return that.toString().equals(toString());
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(17, 37)
                // Non primitive return types must manually specify which sub-objects to 
                // include in order to maintain equality.
                .append(model.getName())
                .append(confidence.getConfig().getSigma())
                .append(confidence.getName())
                .append(stat)
                .append(method)
                .append(rStat)
                .append(nFev)
                .append(qVal)
                .append(numPoints)
                .append(statVal)
                .append(dof)
                .append(userModelList);
        
        // If confidence results are available append them as well
        if (confResults != null) {
            hcb.append(confResults.getParmaxes())
                .append(confResults.getParmins())
                .append(confResults.getParvals());
        }
        
        // Must manually include param and values in hashCode computation
        if (CollectionUtils.isNotEmpty(model.getParts())) {
            for (Model part : model.getParts()) {
                for (Parameter param : part.getPars()) {
                    hcb.append(HashCodeBuilder.reflectionHashCode(param));
                }
            }
        }
        
        return hcb.toHashCode();
    }

    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener )
    {
        propertyChangeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener(java.beans.PropertyChangeListener listener )
    {
        propertyChangeSupport.removePropertyChangeListener( listener );
    }

    private void init() {
        setModel(SAMPFactory.get(CompositeModel.class));
        setConfidence(SAMPFactory.get(Confidence.class));
        confidence.getConfig().setSigma(1.6);
        confidence.setName("conf");
        setConfidenceResults(SAMPFactory.get(ConfidenceResults.class));
        setStat(Statistic.Chi2);
        setMethod(OptimizationMethod.LevenbergMarquardt);
        clearFittingRanges();
        setStatVal(null);
        setDof(null);
        setnFev(null);
        setNumPoints(null);
        setqVal(null);
        setrStat(null);
        setUserModelList(new ArrayList<UserModel>());
    }

    private void removeUserModel(Model m) {
        boolean retVal = false;
        for (UserModel um : new ArrayList<>(userModelList)) {
            String mId = DefaultModel.findId(m);
            String umId = DefaultModel.findId(um);
            if (mId.equals(umId)) {
                retVal = userModelList.remove(um);
                break;
            }
        }
        if (retVal) {
            propertyChangeSupport.firePropertyChange(PROP_EXPRESSION, null, getExpression());
        }
    }

    private void addToExpression(String id) {
        String expression = model.getName();
        if (expression == null || expression.isEmpty()) {
            logger.info("model expression is empty, resetting to "+id);
            setExpression(id);
        } else {
            logger.info("adding model component " + id + " to expression");
            setExpression(model.getName() + " + " + id);
        }
    }

    private void confToString(StringBuilder builder) {
        if (confResults != null) {
            List<ConfResultsConverter.ParameterLimits> parameterLimitsList =
                    new ConfResultsConverter().convertForward(confResults);
            builder.append(String.format("\nConfidence Limits at %4.2f sigma (%4.2f%%):\n", confResults.getSigma(), confResults.getPercent()));
            for(ConfResultsConverter.ParameterLimits limits : parameterLimitsList) {
                builder.append(String.format("%24s: (%12.5E, %12.5E)\n", limits.getName(), limits.getLowerLimit(), limits.getUpperLimit()));
            }
        }
    }

    private void resultsToString(StringBuilder builder) {
        builder.append("\nFit Results:\n")
                .append(formatDouble("Final Fit Statistic", getStatVal()))
                .append(formatDouble("Reduced Statistic", getrStat()))
                .append(formatDouble("Probability (Q-value)", getqVal()))
                .append(formatInt("Degrees of Freedom", getDof()))
                .append(formatInt("Data Points", getNumPoints()))
                .append(formatInt("Function Evaluations", getnFev()))
                .append("\n")
                .append(formatString("Optimizer", getMethod().toString()))
                .append(formatString("Statistic (Cost function)", getStat().toString()))
        ;
    }

    private void userModelsToString(StringBuilder builder) {
        builder.append("\nUser Models:\n");

        for (UserModel m : userModelList) {
            builder.append(String.format("\t\t%s: (file: %s, function: %s)\n", m.getName(), m.getFile(), m.getFunction()));
        }
    }

    private String formatDouble(String name, Double value) {
        return String.format("\t\t%26s = %12.5E\n", name, value);
    }

    private String formatInt(String name, Integer value) {
        return String.format("\t\t%26s = %d\n", name, value);
    }

    private String formatString(String name, String value) {
        return String.format("\t\t%26s = %s\n", name, value);
    }
}
