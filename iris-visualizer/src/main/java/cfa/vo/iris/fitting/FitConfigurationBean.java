package cfa.vo.iris.fitting;

import cfa.vo.iris.fitting.custom.DefaultCustomModel;
import cfa.vo.sherpa.IFitConfiguration;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.utils.UTYPE;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sherpa.Data;
import cfa.vo.sherpa.SherpaFitConfiguration;
import cfa.vo.sherpa.models.CompositeModel;
import cfa.vo.sherpa.models.Model;
import cfa.vo.sherpa.models.UserModel;
import cfa.vo.sherpa.optimization.Method;
import cfa.vo.sherpa.stats.Stat;

import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FitConfigurationBean implements IFitConfiguration {
    private CompositeModel model;
    private Stat stat;
    private Method method;
    private List<UserModel> userModelList = new ArrayList<>();
    private Logger logger = Logger.getLogger(FitConfigurationBean.class.getName());

    public static final String PROP_FIT = "fit";
    private transient final PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);

    public FitConfigurationBean() {
        this.model = SAMPFactory.get(CompositeModel.class);
    }

    @Override
    public CompositeModel getModel() {
        return model;
    }

    @Override
    public void setModel(CompositeModel model) {
        this.model = model;
    }

    @Override
    public Stat getStat() {
        return stat;
    }

    @Override
    public void setStat(Stat stat) {
        this.stat = stat;
        fireChange();
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public void setMethod(Method method) {
        this.method = method;
        fireChange();
    }

    @Override
    public List<UserModel> getUserModelList() {
        return userModelList;
    }

    @Override
    public boolean addUserModel(UserModel model) {
        boolean retVal = this.userModelList.add(model);
        fireChange();
        return retVal;
    }

    public boolean addUserModel(DefaultCustomModel m, String id) {
        boolean retVal = addUserModel(m.makeUserModel(id));
        Model mod = m.makeModel(id);
        model.addPart(mod);
        addToExpression(id);
        fireChange();
        return retVal;
    }

    public void addModel(Model m, String id) {
        model.addPart(m);
        addToExpression(id);
    }

    @Override
    public void setUserModelList(List<UserModel> userModelList) {
        this.userModelList = userModelList;
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

    private void fireChange() {
        propertyChangeSupport.firePropertyChange(PROP_FIT, null, this);
    }

    private void addToExpression(String id) {
        String expression = model.getName();
        if (expression == null || expression.isEmpty()) {
            logger.info("model expression is empty, resetting to "+id);
            model.setName(id);
        } else {
            logger.info("adding model component " + id + " to expression");
            model.setName(model.getName() + " + " + id);
        }
    }
}
