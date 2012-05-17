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

package cfa.vo.iris.smoketest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @author olaurino
 */
public abstract class AbstractSmokeTest {

    protected int TIMEOUT;

    public AbstractSmokeTest(int timeout) {
        this.TIMEOUT = timeout;
    }

    protected void check(boolean check, String msg) throws Exception {
        if (!check) {
            log("Error: " + msg);
            throw new Exception(msg);
        }
    }

    protected void log(String msg) {
        System.out.println(msg);
    }

    protected void waitUntil(String objS, Boolean condition, String failMessage) throws Exception {
        for (int i = 0; i < TIMEOUT && !this.getClass().getDeclaredField(objS).get(this).equals(condition); i++) {
            Thread.sleep(1000);
        }
        check(this.getClass().getDeclaredField(objS).get(this).equals(condition), failMessage);
    }

    protected void waitUntil(Object obj, String methodName, String failMessage) throws Exception {
        Method method = obj.getClass().getMethod(methodName);
        for (int i = 0; i < TIMEOUT && !((Boolean) method.invoke(obj)); i++) {
            Thread.sleep(1000);
        }
        check((Boolean) method.invoke(obj), failMessage);
    }

    protected void waitUntilNotNull(String objS, String failMessage) throws Exception {
        Field f = this.getClass().getDeclaredField(objS);
        f.setAccessible(true);
        for (int i = 0; i < TIMEOUT && f.get(this) == null; i++) {
            Thread.sleep(1000);
        }
        check(f.get(this) != null, failMessage);
    }

    protected abstract void exit();

}
