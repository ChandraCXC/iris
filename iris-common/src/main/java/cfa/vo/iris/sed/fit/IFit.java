package cfa.vo.iris.sed.fit;

import cfa.vo.sherpa.models.CompositeModel;
import cfa.vo.sherpa.models.UserModel;

import java.util.List;

/**
 * Created by olaurino on 10/28/15.
 */
public interface IFit {
    CompositeModel getModel();
    List<UserModel> getUserModels();
    String getExpression();
}
