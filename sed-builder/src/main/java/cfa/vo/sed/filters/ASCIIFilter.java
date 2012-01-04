/**
 * Copyright (C) 2011 Smithsonian Astrophysical Observatory
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

package cfa.vo.sed.filters;

import java.io.IOException;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.TableFormatException;
import uk.ac.starlink.table.formats.AsciiStarTable;
import uk.ac.starlink.util.DataSource;

/**
 *
 * @author olaurino
 */
public class ASCIIFilter extends AbstractSingleStarTableFilter {

    @Override
    public StarTable makeStarTable(DataSource ds) throws TableFormatException, IOException {
        return new AsciiStarTable(ds);
    }

    @Override
    public String getDescription() {
        return "Generic non structured ASCII files, e.g. space separated or tab separated values";
    }

    @Override
    public String getName() {
        return "ASCII Table";
    }

}
