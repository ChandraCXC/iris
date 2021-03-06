/**
 * Copyright (C) 2012, 2015 Smithsonian Astrophysical Observatory
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

package cfa.vo.sherpa.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=ParameterImpl.class)
public interface Parameter {
    String getName();

    void setName(String name);

    Double getVal();

    void setVal(Double value);

    Double getMin();

    void setMin(Double min);

    Double getMax();

    void setMax(Double max);

    Integer getFrozen();

    void setFrozen(Integer frozen);

    Integer getHidden();

    void setHidden(Integer hidden);

    Integer getAlwaysfrozen();

    void setAlwaysfrozen(Integer alwaysfrozen);

    String getUnits();

    void setUnits(String units);

    String getLink();

    void setLink(String link);
}
