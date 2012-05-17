/**
 * Copyright (C) 2012 Smithsonian Astrophysical Observatory
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.builder;

import java.util.List;

/**
 *
 * @author olaurino
 */
public class SegmentMetadata implements ISegmentMetadata {

    private List<ISegmentParameter> paramList;

    private List<ISegmentColumn> columnList;

    public SegmentMetadata(List<ISegmentParameter> paramList, List<ISegmentColumn> columnList) {
        this.paramList = paramList;
        this.columnList = columnList;
    }

    public List<ISegmentParameter> getParameters() {
        return paramList;
    }

    public List<ISegmentColumn> getColumns() {
        return columnList;
    }

    @Override
    public ISegmentParameter getParameterByUtype(String utype) {
        for(ISegmentParameter param : paramList) {
            if(param.getUtype()!=null)
                if(param.getUtype().endsWith(utype))
                    return param;
        }
        return new NullSegmentParameter();
    }

}
