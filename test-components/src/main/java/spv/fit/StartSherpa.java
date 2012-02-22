package spv.fit;

/**
 * Created by IntelliJ IDEA.
 * User: busko
 * Date: 5/12/11
 * Time: 11:18 AM
 */
import spv.util.ExceptionHandler;
import spv.util.Include;
import spv.util.SpvLogger;
import spv.util.properties.SpvProperties;

import java.io.IOException;

import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.util.logging.Level;

public class StartSherpa extends AbstractFittingEngine {

    private String cmd[] = {"python2.6", "startsherpa.py"};
    private ProcessBuilder builder;
    private Process process;
    private Map<String, String> env;

    public StartSherpa() {

        String arch = System.getProperty("os.name");
        String cwd = System.getProperty("user.dir");

        String pythonPath = SpvProperties.GetProperty(Include.PYTHON_PATH);
        builder = new ProcessBuilder(pythonPath + "/bin/" + cmd[0], pythonPath + "/" + cmd[1]);
        env = builder.environment();
        env.put("PYTHONPATH", "");

        String libpath = "";
        if (arch.equalsIgnoreCase("Mac OS X")) {
            libpath = System.getenv("DYLD_LIBRARY_PATH");
            if (libpath != null) {
                libpath = pythonPath + "/lib:" + libpath;
            } else {
                libpath = pythonPath + "/lib";
            }
            env.put("DYLD_LIBRARY_PATH", libpath);
        } else {
            libpath = System.getenv("LD_LIBRARY_PATH");
            if (libpath != null) {
                libpath = pythonPath + "/lib:" + libpath;
            } else {
                libpath = pythonPath + "/lib";
            }
            env.put("LD_LIBRARY_PATH", libpath);
        }
        builder.redirectErrorStream(true);

        /*
        System.out.println("ENVIRONMENT:");
        for(String key : env.keySet()) {
        System.out.println(key + "=" + env.get(key));
        }
         */
    }

    @Override
    public void print(InputStream in) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(in)));

        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);
            }
            reader.close();

        } catch (IOException ie) {
            ExceptionHandler.handleException(ie);
        }
    }

    @Override
    public void run() {
        process = null;

        try {
            process = builder.start();
        } catch (SecurityException se) {
            ExceptionHandler.handleException(se);
        } catch (IOException ie) {
            ExceptionHandler.handleException(ie);
        } catch (NullPointerException ne) {
            ExceptionHandler.handleException(ne);
        } catch (IndexOutOfBoundsException iobe) {
            ExceptionHandler.handleException(iobe);
        }

        try {
            process.waitFor();
        } catch (InterruptedException ie) {
            SpvLogger.log(Level.INFO, "Interrupt: Shutting down Sherpa...");
            shutdown();
            return;
        }

        print(process.getInputStream());
    }

    @Override
    public void shutdown() {
        if (process != null) {
            process.destroy();
        }
    }
}
