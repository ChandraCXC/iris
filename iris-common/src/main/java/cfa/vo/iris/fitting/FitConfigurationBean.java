package cfa.vo.iris.fitting;

import cfa.vo.iris.fitting.custom.DefaultCustomModel;
import cfa.vo.iris.gui.widgets.ModelExpressionVerifier;
import cfa.vo.sherpa.IFitConfiguration;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.utils.UTYPE;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sherpa.Data;
import cfa.vo.sherpa.SherpaFitConfiguration;
import cfa.vo.sherpa.models.CompositeModel;
import cfa.vo.sherpa.models.CompositeModelTreeModel;
import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.UserModel;
import cfa.vo.sherpa.optimization.Method;
import cfa.vo.sherpa.stats.Stat;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import javax.annotation.Nonnull;
import javax.swing.tree.TreeModel;
import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class FitConfigurationBean implements IFitConfiguration {
    private CompositeModel model;
    private Stat stat;
    private Method method;
    private ObservableList<UserModel> userModelList = ObservableCollections.observableList(new ArrayList<UserModel>());
    private ModelExpressionVerifier verifier = new ModelExpressionVerifier();
    private Logger logger = Logger.getLogger(FitConfigurationBean.class.getName());

    public static final String PROP_MODEL = "model";
    public static final String PROP_STAT = "stat";
    public static final String PROP_METHOD = "method";
    public static final String PROP_USERMODELLIST = "userModelList";
    public static final String PROP_EXPRESSION = "expression";
    private transient final PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);

    public FitConfigurationBean() {
        this.model = SAMPFactory.get(CompositeModel.class);
    }

    @Override
    @Nonnull
    public CompositeModel getModel() {
        return model;
    }

    @Override
    public void setModel(CompositeModel model) {
        CompositeModel oldModel = this.model;
        this.model = model;
        propertyChangeSupport.firePropertyChange(PROP_MODEL, oldModel, model);
    }

    @Override
    public Stat getStat() {
        return stat;
    }

    @Override
    public void setStat(Stat stat) {
        Stat oldStat = this.stat;
        this.stat = stat;
        propertyChangeSupport.firePropertyChange(PROP_STAT, oldStat, stat);
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public void setMethod(Method method) {
        Method oldMethod = this.method;
        this.method = method;
        propertyChangeSupport.firePropertyChange(PROP_METHOD, oldMethod, method);
    }

    @Override
    public List<UserModel> getUserModelList() {
        return Collections.unmodifiableList(userModelList);
    }

    @Override
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

    @Override
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

    public SherpaFitConfiguration make(ExtSed sed) throws SedException, UnitsException {
        // FIXME this duplicates the code in SherpaClient. They should probably both use the same class.
        Data data = SAMPFactory.get(Data.class);
        data.setName("fitdata");
        ExtSed flat = ExtSed.flatten(sed, "Angstrom", "photon/s/cm2/Angstrom");
        data.setX(flat.getSegment(0).getSpectralAxisValues());
        data.setY(flat.getSegment(0).getFluxAxisValues());
        data.setStaterror((double[]) flat.getSegment(0).getCustomDataValues(UTYPE.FLUX_STAT_ERROR));

        SherpaFitConfiguration conf = SAMPFactory.get(SherpaFitConfiguration.class);
        conf.addDataset(data);

        conf.addModel(model);

        for (UserModel m : userModelList) {
            conf.addUsermodel(m);
        }

        conf.setStat(stat);

        conf.setMethod(method);

        return conf;
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener )
    {
        propertyChangeSupport.addPropertyChangeListener( listener );
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
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