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

package cfa.vo.sherpa;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olaurino
 */
public class SherpaService {
    private ProcessBuilder pb;

    private Process p;

    private int timeout;

    public SherpaService(String sherpaDir, int timeout) {
        this.timeout = timeout;
        String cmd = sherpaDir+"/bin/python2.6";
        String arg = sherpaDir+"/startsherpa.py";
        pb = new ProcessBuilder(cmd, arg).redirectErrorStream(true);
        Map<String, String> env = pb.environment();
        env.put("DYLD_LIBRARY_PATH", sherpaDir+"/lib");
        env.put("LD_LIBRARY_PATH", sherpaDir+"/lib");
        env.put("PYTHONPATH", "");
    }

    public void start() throws Exception {

        new Thread(new SherpaThread()).start();

        for(int i=0; i<timeout && p==null; i++) {
            Thread.sleep(1000);
        }

        if(p==null)
            throw new Exception("Could not start Sherpa");

        Thread.sleep(5000);
    }

    public void stop() throws InterruptedException, IOException {

        Integer pid;

        try {
            Field f = p.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            pid = (Integer) f.get(p);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't detect pid", e);
        }

        ProcessBuilder killbill = new ProcessBuilder("kill", "-2", pid.toString());
        killbill.redirectErrorStream();
        Process kill = killbill.start();
        kill.waitFor();

        p.destroy();

    }

    private class SherpaThread implements Runnable {

        @Override
        public void run() {
            try {
                p = pb.start();
                Logger.getLogger(SherpaService.class.getName()).log(Level.INFO, "Sherpa started");
            } catch (IOException ex) {
                Logger.getLogger(SherpaService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
