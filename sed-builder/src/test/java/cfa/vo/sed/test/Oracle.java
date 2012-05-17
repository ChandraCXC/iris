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

/***********************************************************************
*
* File: Oracle.java
*
* Author:  olaurino              Created: Mon Apr 4 12:26:00 EST 2011
*
* National Virtual Observatory; contributed by Center for Astrophysics
*
***********************************************************************/

package cfa.vo.sed.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Class for arbitrary property checks, for testing purposes
 * 
 */
public class Oracle extends HashMap<String, Object>{

    /**
     * This method checks that the <code>property</code> of <code>testedObject</code> equals the correct value (the @link{HashMap} value associated with the <code>property</code>)
     *
     * @param testedObject
     * @param property
     * @throws OracleFailException
     * @throws OracleException
     */
    public void testEquals(Object testedObject, String property) throws OracleFailException, OracleException {
        try {
            Object expected = this.get(property);
            Object observed = this.get(testedObject, property);
            if (expected == null)
            {
            	if (observed != null)
              	    throw new OracleFailException ("expected null, but got "+observed);          
            }
            else if(!expected.equals(observed))
                throw new OracleFailException("expected "+expected+", but got "+observed);
        } catch (Exception ex) {
            throw new OracleException(ex);
        }
    }

    private Object get(Object testedObject, String key) throws Exception {
        String[] _fields = key.split("\\.");
        ArrayList<String> fields = new ArrayList(Arrays.asList(_fields));

        return get(testedObject, fields);
        
    }

    private Object get(Object testedObject, ArrayList<String> fields) throws Exception {
        String field = fields.get(0);
        String initial;
        String methodName;
        Method method;
        Object response;

        Integer index = null;

        try
        {
            index = Integer.valueOf (field);
        }
        catch (NumberFormatException e)
        {
            index = null;
        }

        // allow for lists and arrays to be members of an object. The syntax to
        // search a list is ...class_name.list_name.list_index.class_name....
        if ((index != null) && (testedObject instanceof List))
            response = ((List)testedObject).get (index);
        else if ((index != null) && (testedObject.getClass().isArray ()))
            // this isn't robust since it doesn't work on primitives but it is
            // good enough for current use
            response = ((Object[])testedObject)[index];
        else
        {
            initial = field.substring(0, 1);
            methodName = "get"+field.replaceFirst(initial, initial.toUpperCase());
            method = testedObject.getClass().getMethod(methodName);
            response = method.invoke(testedObject);
        }

        fields.remove(0);

        if(fields.isEmpty()) {
            return response;
        }
        
        return get(response, fields);
    }

    /**
     *
     * This method tests the <code>testedObject</code> against all the properties included in the current instance of the Oracle.
     * All properties are assumed to be tested through the testEquals method
     *
     * @param segment
     * @throws Exception
     */
    public void test(Object testedObject) throws Exception {
        for(String key : keySet()) {
            testEquals(testedObject, key);
        }
    }

}
