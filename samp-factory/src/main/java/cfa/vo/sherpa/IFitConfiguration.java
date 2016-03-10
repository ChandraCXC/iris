/*
 * Copyright 2016 Chandra X-Ray Observatory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cfa.vo.sherpa;

import cfa.vo.sherpa.models.CompositeModel;
import cfa.vo.sherpa.models.UserModel;
import cfa.vo.sherpa.optimization.Method;
import cfa.vo.sherpa.stats.Stat;
import java.util.List;

/**
 *
 * @author olaurino
 */
public interface IFitConfiguration {

    boolean addUserModel(UserModel model);

    Method getMethod();

    CompositeModel getModel();

    Stat getStat();

    List<UserModel> getUserModelList();

    void setMethod(Method method);

    void setModel(CompositeModel model);

    void setStat(Stat stat);

    void setUserModelList(List<UserModel> userModelList);
    
}
