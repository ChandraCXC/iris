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

import com.google.common.base.CaseFormat;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Proxy class used by the SAMPFactory to back instantiated objects.
 * @author olaurino
 */
public class SAMPProxy implements InvocationHandler {

    private Map map = new HashMap();

    private Class clazz;

    public Class getProxiedClass() {
        return clazz;
    }

    public SAMPProxy(Class clazz) {
        this.clazz = clazz;
        for(Method method : clazz.getMethods()) {
            String objectName = objectName(method);
            String mName = method.getName();
            if(!returnsPrimitive(method) && mName.matches("^get.*|^set.*|^add.*") && !returnsList(method) && !returnsArray(method)) {
                try {
                    Object obj;
                    if(method.getReturnType().isInterface())
                        obj = Proxy.newProxyInstance(getClass().getClassLoader(),
                            new Class[]{method.getReturnType()},
                            new SAMPProxy(method.getReturnType()));
                    else
                        obj = Proxy.newProxyInstance(getClass().getClassLoader(),
                            method.getReturnType().getInterfaces(),
                            new SAMPProxy(method.getReturnType().getInterfaces()[0]));
                    map.put(objectName, obj);
                } catch (Exception ex) {
                    Logger.getLogger(SAMPProxy.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public SAMPProxy(Map<String, Object> inputMap, Class clazz) throws Exception {

        this.clazz = clazz;

        for(Method method : clazz.getMethods()) {
            if(!method.getName().startsWith("get")||unsupported(method))
                continue;
            String name = objectName(method).toLowerCase();
            Class returnType = method.getReturnType();
            if(returnsPrimitive(method)) {
                
                if(inputMap.containsKey(name)) {
                    Object res = convert(inputMap.get(name), returnType);
                    map.put(name, res);
                }
                continue;
            }
            if(returnType.isArray()) {
                String array = (String) inputMap.get(name);
                Object arr = decode(array);
                map.put(name, arr);
                continue;
            }
            if(List.class.isAssignableFrom(returnType)) {
                if(inputMap.containsKey(name)) {
                    List list = (List) inputMap.get(name);
                
                    if(list!=null)
                        if(!list.isEmpty()) {
                            List theList = new ArrayList();
                            for(Object el : list) {
                                String addName = addname(method);
                                Class objClass = null;
                                for(Method m : clazz.getMethods()) {
                                    if(m.getName().equals(addName))
                                        objClass = m.getParameterTypes()[0];
                                }
                                Object res;
                                if(!isPrimitive(objClass))
                                    res = Proxy.newProxyInstance(SAMPProxy.class.getClassLoader(), new Class[]{objClass}, new SAMPProxy((Map)el, objClass));
                                else
                                    res = el;

                                theList.add(res);
                            }
                            map.put(name, theList);
                        }
                }
                continue;
            }
            Object res = Proxy.newProxyInstance(SAMPProxy.class.getClassLoader(), new Class[]{returnType}, new SAMPProxy((Map)inputMap.get(name), returnType));
            if(res!=null)
                map.put(name, res);
        }

    }
    
    Random random = new Random();
    
    @Override
    public Object invoke(Object o, Method method, Object[] os) throws Throwable {
        if(method.getName().startsWith("get")&&!unsupported(method))
            return get(o, method, os);
        if(method.getName().startsWith("set")) {
            set(o, method, os);
            return null;
        }
        if(method.getName().startsWith("add")) {
            add(o, method, os);
            return null;
        }
        if(method.getName().equals("toString"))
            return toString(o);
        
        if(method.getName().equals("equals"))
            return Boolean.FALSE;
        
        if(method.getName().equals("hashCode"))
            return random.nextInt();

        throw new Exception("Unsupported method: " +method.getName());
    }

    private Object get(Object o, Method method, Object[] args) {
        String objName = objectName(method);
        if(map.containsKey(objName))
            return map.get(objName);

        return null;
    }

    private void set(Object o, Method method, Object[] args) {//FIXME underscores
        map.put(objectName(method), args[0]);
    }

    private void add(Object o, Method method, Object[] args) {
        String objName = objectName(method);
        if(!map.containsKey(objName+"s")) {
            List list = new ArrayList();
            map.put(objName+"s", list);
        }
        List list = (List) map.get(objName+"s");
        list.add(args[0]);
    }

    public static boolean returnsPrimitive(Method method) {
        Class clazz = method.getReturnType();
        if(isPrimitive(clazz))
            return true;

        return false;
    }

    public static boolean isPrimitive(Class clazz) {
        if(clazz.isPrimitive())
            return true;
        if(Number.class.isAssignableFrom(clazz))
            return true;
        if(clazz.equals(String.class))
            return true;
        if(clazz.equals(Boolean.class))
            return true;

        return false;
    }

    private static String objectName(Method method) {
        String name = method.getName().replaceFirst("^get|^set|^add", "");
        name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, name);
        return name;
    }

    public static Object deserialize(SAMPMessage message, Class clazz) throws Exception {
        return Proxy.newProxyInstance(SAMPProxy.class.getClassLoader(), new Class[]{clazz}, new SAMPProxy(message.get(), clazz));
    }

    public static Map serialize(Object object, Class clazz) throws Exception{

        Map<String, Object> map = new HashMap();
        for(Method method : clazz.getMethods()) {
            if(!method.getName().startsWith("get"))
                continue;
            Class returnType = method.getReturnType();
            if(returnsPrimitive(method)) {
                Object res = method.invoke(object);
                if(res!=null) {
                    map.put(objectName(method).toLowerCase(), res.toString());
                }
                continue;
            }
            if(returnType.isArray()) {
                Object array = method.invoke(object);
                String arr = encode(array);
                map.put(objectName(method).toLowerCase(), arr);
                continue;
            }
            if(List.class.isAssignableFrom(returnType)) {
                List list = (List) method.invoke(object);
                if(list!=null) {
                    if(!list.isEmpty()) {
                        List<Object> theList = new ArrayList();
                        for(Object el : list) {
                            Class innerClass;
                            if(Proxy.isProxyClass(el.getClass())) {
                                SAMPProxy p = (SAMPProxy) Proxy.getInvocationHandler(el);
                                innerClass = p.getProxiedClass();
                            } else {
                                innerClass = el.getClass();
                                if(!innerClass.isInterface()) {
                                    innerClass = innerClass.getInterfaces()[0];
                                }
                            }
                            theList.add(serialize(el, innerClass));
                        }
                        map.put(objectName(method).toLowerCase(), theList);
                    } else {
                        map.put(objectName(method), new ArrayList());
                    }
                } else {
                    map.put(objectName(method), new ArrayList());
                }
                continue;
            }
            Object res = method.invoke(object);
            if(res!=null)
                if(!res.getClass().equals(Class.class))
                    map.put(objectName(method).toLowerCase(), serialize(res, returnType));
                else
                    map.put(objectName(method).toLowerCase(), ((Class)res).getCanonicalName());
        }
        return map;

    }

    private static String encode(Object array) {
        try {
            return EncodeDoubleArray.encodeBase64((double[]) array, false);
        } catch(Exception ex) {
            return "";
        }
    }

    private static Object decode(String array) {
        try {
            return EncodeDoubleArray.decodeBase64(array, false);
        } catch (Exception ex) {
            return null;
        }
    }

    private Object convert(Object obj, Class returnType) throws Exception {
        if(Number.class.isAssignableFrom(returnType)) {
            String objS = (String) obj;
            if(objS.toLowerCase().equals("nan"))
                objS = "NaN";
            Method m = returnType.getMethod("valueOf", String.class);
            return m.invoke(null, objS);
        }

        if(Boolean.class.isAssignableFrom(returnType)) {
            String o = (String) obj;
            if(o.equals("0"))
                o = "false";
            else
                o = "true";
            Method m = returnType.getMethod("valueOf", String.class);
            return m.invoke(null, o);
        }

        if(returnType.equals(String.class))
            return obj;



        return null;
    }

    private String addname(Method method) {
        String name = objectName(method);
        name = name.replaceAll("s$", "");
        name = name.substring(0, 1).toUpperCase()+name.substring(1);
        name = "add"+name;
        return name;
    }

    private String toString(Object obj) {
        if(!Proxy.getInvocationHandler(obj).getClass().equals(SAMPProxy.class))
            return obj.toString();
        try {
            Method m = obj.getClass().getMethod("getName");
            return (String) m.invoke(obj);
        } catch (Exception ex2) {
            return "";
        }
        
    }

    public static boolean returnsList(Method method) {
        return Collection.class.isAssignableFrom(method.getReturnType());
    }

    public static boolean returnsArray(Method method) {
        return method.getReturnType().isArray();
    }

    private boolean unsupported(Method method) {
        return unsupp.contains(method.getName());
    }

    private Unsupported unsupp = new Unsupported();

    private class Unsupported extends ArrayList {
        public Unsupported() {
            super();
            add("getClass");
        }
    }

}
