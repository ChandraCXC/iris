package cfa.vo.iris.fitting;

import cfa.vo.sherpa.IFitConfiguration;
import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.utils.UTYPE;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sherpa.Data;
import cfa.vo.sherpa.SherpaFitConfiguration;
import cfa.vo.sherpa.models.CompositeModel;
import cfa.vo.sherpa.models.UserModel;
import cfa.vo.sherpa.optimization.Method;
import cfa.vo.sherpa.stats.Stat;

import java.util.ArrayList;
import java.util.List;

public class FitConfigurationBean implements IFitConfiguration {
    private CompositeModel model;
    private Stat stat;
    private Method method;
    private List<UserModel> userModelList = new ArrayList<>();

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
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public List<UserModel> getUserModelList() {
        return userModelList;
    }

    @Override
    public boolean addUserModel(UserModel model) {
        return this.userModelList.add(model);
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
}
