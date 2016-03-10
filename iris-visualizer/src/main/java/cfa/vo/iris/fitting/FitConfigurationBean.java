package cfa.vo.iris.fitting;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.units.UnitsException;
import cfa.vo.iris.utils.UTYPE;
import cfa.vo.sedlib.common.SedException;
import cfa.vo.sherpa.Data;
import cfa.vo.sherpa.FitConfiguration;
import cfa.vo.sherpa.models.CompositeModel;
import cfa.vo.sherpa.models.UserModel;
import cfa.vo.sherpa.optimization.Method;
import cfa.vo.sherpa.stats.Stat;

import java.util.ArrayList;
import java.util.List;

public class FitConfigurationBean {
    private ExtSed sed;
    private CompositeModel model;
    private Stat stat;
    private Method method;
    private List<UserModel> userModelList = new ArrayList<>();

    public ExtSed getSed() {
        return sed;
    }

    public void setSed(ExtSed sed) {
        this.sed = sed;
    }

    public CompositeModel getModel() {
        return model;
    }

    public void setModel(CompositeModel model) {
        this.model = model;
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public List<UserModel> getUserModelList() {
        return userModelList;
    }

    public boolean addUserModel(UserModel model) {
        return this.userModelList.add(model);
    }

    public void setUserModelList(List<UserModel> userModelList) {
        this.userModelList = userModelList;
    }

    public FitConfiguration make() throws SedException, UnitsException {
        // FIXME this duplicates the code in SherpaClient. They should probably both use the same class.
        Data data = SAMPFactory.get(Data.class);
        data.setName("fitdata");
        ExtSed flat = ExtSed.flatten(sed, "Angstrom", "photon/s/cm2/Angstrom");
        data.setX(flat.getSegment(0).getSpectralAxisValues());
        data.setY(flat.getSegment(0).getFluxAxisValues());
        data.setStaterror((double[]) flat.getSegment(0).getCustomDataValues(UTYPE.FLUX_STAT_ERROR));

        FitConfiguration conf = SAMPFactory.get(FitConfiguration.class);
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
