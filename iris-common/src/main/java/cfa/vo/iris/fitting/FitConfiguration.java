package cfa.vo.iris.fitting;

import cfa.vo.iris.fitting.custom.DefaultCustomModel;
import cfa.vo.iris.gui.widgets.ModelExpressionVerifier;
import cfa.vo.sherpa.*;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.utils.UTYPE;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sherpa.models.*;
import cfa.vo.sherpa.optimization.Method;
import cfa.vo.sherpa.optimization.OptimizationMethod;
import cfa.vo.sherpa.stats.Stat;
import cfa.vo.sherpa.stats.Stats;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import javax.annotation.Nonnull;
import javax.swing.tree.TreeModel;
import java.beans.PropertyChangeSupport;

import java.util.*;
import java.util.logging.Logger;

public class FitConfiguration {
    private CompositeModel model;
    private Stat stat;
    private Method method;
    private Confidence confidence;
    private Double rStat;
    private Integer nFev;
    private Double qVal;
    private Integer numPoints;
    private Double statVal;
    private Integer dof;

    private ObservableList<UserModel> userModelList = ObservableCollections.observableList(new ArrayList<UserModel>());
    private ModelExpressionVerifier verifier = new ModelExpressionVerifier();
    private Logger logger = Logger.getLogger(FitConfiguration.class.getName());

    public static final String DATA_NAME = "fitdata";
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
    private transient final PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);

    public FitConfiguration() {
        model = SAMPFactory.get(CompositeModel.class);
        confidence = SAMPFactory.get(Confidence.class);
        confidence.setSigma(1.6);
        confidence.setName("conf");
        stat = Stats.Chi2;
        method = OptimizationMethod.LevenbergMarquardt;
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
        ObservableList oldList = this.userModelList;
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
    }

    public SherpaFitConfiguration make(ExtSed sed) throws SedException, UnitsException {
        // FIXME this duplicates the code in SherpaClient. They should probably both use the same class.
        Data data = SAMPFactory.get(Data.class);
        data.setName(DATA_NAME);
        ExtSed flat = ExtSed.flatten(sed, "Angstrom", "erg/s/cm2/Angstrom");
        data.setX(flat.getSegment(0).getSpectralAxisValues());
        data.setY(flat.getSegment(0).getFluxAxisValues());
        data.setStaterror((double[]) flat.getSegment(0).getDataValues(UTYPE.FLUX_STAT_ERROR));

        SherpaFitConfiguration conf = SAMPFactory.get(SherpaFitConfiguration.class);
        conf.addDataset(data);

        conf.addModel(model);

        for (UserModel m : userModelList) {
            conf.addUsermodel(m);
        }

        conf.setStat(stat);

        conf.setMethod(method);

        conf.setConfidence(confidence);

        return conf;
    }

    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener )
    {
        propertyChangeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener(java.beans.PropertyChangeListener listener )
    {
        propertyChangeSupport.removePropertyChangeListener( listener );
    }

    private void removeUserModel(Model m) {
        boolean retVal = false;
        for (UserModel um : new ArrayList<>(userModelList)) {
            String mId = getId(m);
            String umId = getId(um);
            if (mId.equals(umId)) {
                retVal = userModelList.remove(um);
                break;
            }
        }
        if (retVal) {
            propertyChangeSupport.firePropertyChange(PROP_EXPRESSION, null, getExpression());
        }
    }

    private String getId(Model m) {
        return stripId(m.getName());
    }

    private String getId(UserModel m) {
        return stripId(m.getName());
    }

    private String stripId(String name) {
        return name.split("\\.")[1];
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
}
