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

package cfa.vo.sed.filters;

import java.io.IOException;
import uk.ac.starlink.fits.ColFitsTableBuilder;
import uk.ac.starlink.fits.FitsTableBuilder;
import uk.ac.starlink.table.MultiTableBuilder;
import uk.ac.starlink.table.StarTableFactory;
import uk.ac.starlink.table.TableBuilder;
import uk.ac.starlink.table.TableSequence;
import uk.ac.starlink.util.DataSource;

/**
 *
 * @author olaurino
 */
public class FITSFilter extends AbstractMultiStarTableFilter {

    @Override
    protected MultiTableBuilder getTableBuilder() {
        return new FitsTableBuilder();
    }

    @Override
    public String getDescription() {
        return "Flexible Image Transport System";
    }

    @Override
    protected TableSequence makeTables(DataSource ds) throws IOException {
        StarTableFactory stf = new StarTableFactory();

        TableBuilder[] tb = new TableBuilder[]{new ColFitsTableBuilder(),
                                               new FitsTableBuilder()};

        stf.setDefaultBuilders(tb);

        return stf.makeStarTables(ds);
    }

    @Override
    public String getName() {
        return "FITS";
    }


}
