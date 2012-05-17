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

import cfa.vo.iris.utils.NameResolver;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsky.catalog.BasicQueryArgs;
import jsky.catalog.Catalog;
import jsky.catalog.QueryArgs;
import jsky.catalog.TableQueryResult;
import jsky.coords.WorldCoords;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author olaurino
 */
public class NameResolverTest {

    public NameResolverTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getCatalogs method, of class NameResolver.
     */
    @Test
    public void testGetCatalogs() {
        System.out.println("getCatalogs");
        NameResolver instance = new NameResolver();
        List<Catalog> cats = instance.getCatalogs();
        for(Catalog cat : cats) {
            QueryArgs args = new BasicQueryArgs(cat);
            args.setId("nothing");
            try {
                TableQueryResult result = (TableQueryResult) cat.query(args);
                WorldCoords coordinates = (WorldCoords) result.getCoordinates(0);
                System.out.println(cat);
                System.out.println(coordinates);
            } catch (IOException ex) {
                Logger.getLogger(NameResolverTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
                Assert.assertTrue(ex.getMessage().contains("not present") || ex.getMessage().contains("not recognized"));
            }
        }
    }

}