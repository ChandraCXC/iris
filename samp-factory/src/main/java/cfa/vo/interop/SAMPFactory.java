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

package cfa.vo.interop;

import java.lang.reflect.Proxy;
import java.util.Map;
import org.astrogrid.samp.Message;

/**
 *
 * This class provides factory methods for SAMP message manipulation. The methods
 * allow to seamlessly serialize java objects into SAMP messages, or viceversa.
 *
 * This class can be used to create domain specific APIs that use SAMP as the backend
 * communication protocol.
 *
 * Interfaces must be "bean-like", i.e. made of getters and setters. Other methods will be ignored.
 *
 * Primitive types and their wrapper classes are serialized according to the SAMP specs.
 *
 * Complex attributes can be serialized as nested maps if they implement an interface.
 *
 * Arrays are serialized as base64 strings.
 *
 * @author olaurino
 */
public class SAMPFactory {
    /**
     * Instantiate a new Object according to the interface exposed by clazz. The new object
     * will be empty and serializable as a SAMP message.
     *
     * @param clazz The interface of the resulting object.
     * @return An instance of a SAMP serializable object.
     */
    public static Object get(Class clazz) {
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new SAMPProxy(clazz));
    }
    
    /**
     *
     * Given an instance of an object and an interface specification, this method
     * creates a SAMP message serialization of the object.
     *
     * @param mtype The mtype of the resulting message
     * @param instance The instance to be serialized
     * @param clazz The interface according to which the instance has to be serialized
     * @return The SAMP message representing the instance
     * @throws Exception
     */
    public static SAMPMessage createMessage(String mtype, Object instance, Class clazz) throws Exception {
        Message message = new Message(mtype);

        message.setParams(SAMPProxy.serialize(instance, clazz));

        return new SimpleSAMPMessage(message);
    }

    /**
     * Decode a SAMP message according to an interface specification and return
     * an instance implementing the specified interface
     *
     * @param message The SAMP message to be decoded
     * @param clazz The interface to be implemented by the returned instance
     * @return An intance of the specified interface
     * @throws Exception
     */
    public static Object get(SAMPMessage message, Class clazz) throws Exception {
        return Proxy.newProxyInstance(SAMPFactory.class.getClassLoader(), new Class[]{clazz}, new SAMPProxy(message.get().getParams(), clazz));
    }

    /**
     * Return an instance of an object serialized as a Map, according to an interface
     *
     * @param map The map that serializes the object
     * @param clazz The interface to be implemented by the returned instance
     * @return An instance of the specified interface
     * @throws Exception
     */
    public static Object get(Map map, Class clazz) throws Exception {
        return Proxy.newProxyInstance(SAMPFactory.class.getClassLoader(), new Class[]{clazz}, new SAMPProxy(map, clazz));
    }
}
