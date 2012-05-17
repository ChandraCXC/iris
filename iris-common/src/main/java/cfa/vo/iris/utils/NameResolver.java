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

package cfa.vo.iris.utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import jsky.catalog.BasicQueryArgs;
import jsky.catalog.Catalog;
import jsky.catalog.QueryArgs;
import jsky.catalog.TableQueryResult;
import jsky.catalog.skycat.SkycatConfigFile;
import jsky.coords.WorldCoords;

/**
 *
 * @author olaurino
 */
public class NameResolver {
    private List<Catalog> cats = new ArrayList();

    public NameResolver() {
        URL config_url = getClass().getResource("/resources/skycat.cfg");
        SkycatConfigFile config_file = new SkycatConfigFile("Resolvers", config_url);
        List<Catalog> cl = config_file.getNameServers();
        for(int i=cl.size()-1; i>=0; i--) {
            cats.add(cl.get(i));
        }
    }

    public List<Catalog> getCatalogs() {
        return cats;
    }

    public Position resolve(Catalog cat, String name) throws RuntimeException, IOException {
        QueryArgs args = new BasicQueryArgs(cat);
        args.setId(name);
        TableQueryResult result = (TableQueryResult) cat.query(args);
        WorldCoords coords = (WorldCoords) result.getCoordinates(0);
        return new Position(coords.getRaDeg(), coords.getDecDeg());
    }

    public class Position {
        private Double ra;
        private Double dec;

        public Position(Double ra, Double dec) {
            this.ra = ra;
            this.dec = dec;
        }

        public Double getDec() {
            return dec;
        }

        public Double getRa() {
            return ra;
        }
        
    }

    private static NameResolver resolver;

    public static NameResolver getInstance() {
        if(resolver == null)
            resolver = new NameResolver();

        return resolver;
    }
}
