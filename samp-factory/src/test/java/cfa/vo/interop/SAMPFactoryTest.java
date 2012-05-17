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

package cfa.vo.interop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.astrogrid.samp.Message;

/**
 *
 * @author olaurino
 */
public class SAMPFactoryTest extends TestCase {

    private SAMPController controller;
    
    public SAMPFactoryTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("jsamp.hub.profiles", "std");
        controller = new SAMPController("test", "test", SAMPController.class.getResource("/iris_button_tiny.png").toString());
        controller.start(false);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        controller.stop();
    }

    /**
     * Test of get method, of class SAMPFactory.
     */
    public void testGet_Class() {
        System.out.println("get");
        TestInterface result = (TestInterface) SAMPFactory.get(TestInterface.class);
        List<String> methods = new ArrayList();
        List<String> exp_methods = new ArrayList();

        for(Method m : result.getClass().getDeclaredMethods())
            methods.add(m.getName());

        for(Method m : TestInterface.class.getDeclaredMethods())
            exp_methods.add(m.getName());

        assertTrue(methods.containsAll(exp_methods));

        result.setName("test name");
        result.setArray(new double[]{0.1, 0.2});

        result.getNested().setSomething("test something");

        assertEquals("test name", result.getName());
        assertTrue(Arrays.equals(new double[]{0.1, 0.2}, result.getArray()));
        assertEquals("test something", result.getNested().getSomething());

        result.addThing("Three");

        assertTrue(result.getThings().contains("Three"));


    }

    /**
     * Test of createMessage method, of class SAMPFactory.
     */
    public void testCreateMessage() throws Exception {
        System.out.println("createMessage");
        String mtype = "test";
        Object instance = getInstance();

        SAMPMessage result = SAMPFactory.createMessage(mtype, instance, TestInterface.class);
        Message message = result.get();
        assertEquals("test", message.getMType());
        Map obj = message.getParams();
        assertEquals("Test Name", obj.get("name"));
        assertEquals(EncodeDoubleArray.encodeBase64(new double[]{1., 2., 3.}, false), obj.get("array"));
        Map nested = (Map) obj.get("nested");
        assertEquals("Test Something", nested.get("something"));

    }

    /**
     * Test of get method, of class SAMPFactory.
     */
    public void testGet_SAMPMessage_Class() throws Exception {
        System.out.println("get");

        String mtype = "test";
        Object instance = getInstance();

        SAMPMessage message = SAMPFactory.createMessage(mtype, instance, TestInterface.class);

        TestInterface result = (TestInterface) SAMPFactory.get(message, TestInterface.class);
        assertEquals("Test Name", result.getName());
        assertTrue(Arrays.equals(new double[]{1., 2., 3.}, result.getArray()));
        assertEquals("Test Something", result.getNested().getSomething());
    }

    /**
     * Test of get method, of class SAMPFactory.
     */
    public void testGet_Map_Class() throws Exception {
        System.out.println("get");

        String mtype = "test";
        Object instance = getInstance();

        SAMPMessage message = SAMPFactory.createMessage(mtype, instance, TestInterface.class);

        Map map = message.get().getParams();

        TestInterface result = (TestInterface) SAMPFactory.get(map, TestInterface.class);
        
        assertEquals("Test Name", result.getName());
        assertTrue(Arrays.equals(new double[]{1., 2., 3.}, result.getArray()));
        assertEquals("Test Something", result.getNested().getSomething());

    }

    private interface TestInterface {
        String getName();
        void setName(String setName);
        double[] getArray();
        void setArray(double[] values);
        NestedInterface getNested();
        void setNested(NestedInterface obj);
        List<String> getThings();
        void addThing(String thing);
    }

    private interface NestedInterface {
        String getSomething();
        void setSomething(String value);
    }

    private TestInterface getInstance() {
        return new TestInterface() {

            @Override
            public String getName() {
                return "Test Name";
            }

            @Override
            public void setName(String setName) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public double[] getArray() {
                return new double[]{1., 2., 3.};
            }

            @Override
            public void setArray(double[] values) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public NestedInterface getNested() {
                return new NestedInterface() {

                    @Override
                    public String getSomething() {
                        return "Test Something";
                    }

                    @Override
                    public void setSomething(String value) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
            }

            @Override
            public void setNested(NestedInterface obj) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public List<String> getThings() {
                return Arrays.asList(new String[]{"One", "Two"});
            }

            @Override
            public void addThing(String thing) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

}
