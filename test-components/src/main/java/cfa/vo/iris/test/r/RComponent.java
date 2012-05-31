/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cfa.vo.iris.test.r;

import cfa.vo.iris.AbstractDesktopItem;
import cfa.vo.iris.ICommandLineInterface;
import cfa.vo.iris.IMenuItem;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.NullCommandLineInterface;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.iris.events.SegmentListener;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedNoDataException;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.astrogrid.samp.client.MessageHandler;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author olaurino
 */
public class RComponent implements IrisComponent {

    private IrisApplication app;
    private IWorkspace ws;
    private Rengine re;

    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
        this.app = app;
        this.ws = workspace;
    }

    @Override
    public String getName() {
        return "RComponent";
    }

    @Override
    public String getDescription() {
        return "R Test Component";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("r");
    }

    @Override
    public void initCli(IrisApplication app) {
    }

    @Override
    public List<IMenuItem> getMenus() {
        return new RMenus();
    }

    @Override
    public List<MessageHandler> getSampHandlers() {
        return new ArrayList();
    }

    @Override
    public void shutdown() {
        if (re != null) {
            re.eval("save.image(\"~/.vao/iris/r.image\")");
            re.end();
        }
    }

    private class RMenus extends ArrayList<IMenuItem> {

        public RMenus() {
            add(new AbstractDesktopItem("R", "R Test Integration", "/rlogo.jpg", "/tool_tiny.png") {

                @Override
                public void onClick() {
                    if(re==null) {
                        re = new Rengine(new String[]{}, false, new TextConsole());
                        if (!re.waitForR()) {
                            System.out.println("Cannot load R");
                            return;
                        }

                        if(re.eval("segment_handler")==null) {
                            re.eval("cat(\"Creating new segment_handler\\n\")");
                            re.eval("segment_handler<-function(x,y){print(table(x,y))}");
                        }

                        SegmentEvent.getInstance().add(new SegmentListener() {

                            @Override
                            public void process(Segment source, SegmentPayload payload) {
                                try {
                                    double[] x = source.getSpectralAxisValues();
                                    double[] y = source.getFluxAxisValues();
                                    re.assign("x", x);
                                    re.assign("y", y);
                                    re.eval("cat(\"\\nR: Received new segment, invoking handler\\n\")");
                                    re.eval("segment_handler(x,y)");
                                    re.eval("save.image(\"~/.vao/iris/r.image\")");
                                } catch (SedNoDataException ex) {
                                    Logger.getLogger(RComponent.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private class TextConsole implements RMainLoopCallbacks {

        @Override
        public void rWriteConsole(Rengine re, String text, int oType) {
            System.out.print(text);
        }

        @Override
        public void rBusy(Rengine re, int which) {
            System.out.println("rBusy(" + which + ")");
        }

        @Override
        public String rReadConsole(Rengine re, String prompt, int addToHistory) {
            System.out.print(prompt);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String s = br.readLine();
                return (s == null || s.length() == 0) ? s : s + "\n";
            } catch (Exception e) {
                System.out.println("jriReadConsole exception: " + e.getMessage());
            }
            return null;
        }

        @Override
        public void rShowMessage(Rengine re, String message) {
            System.out.println("rShowMessage \"" + message + "\"");
        }

        @Override
        public String rChooseFile(Rengine re, int newFile) {
            FileDialog fd = new FileDialog(new Frame(), (newFile == 0) ? "Select a file" : "Select a new file", (newFile == 0) ? FileDialog.LOAD : FileDialog.SAVE);
            fd.show();
            String res = null;
            if (fd.getDirectory() != null) {
                res = fd.getDirectory();
            }
            if (fd.getFile() != null) {
                res = (res == null) ? fd.getFile() : (res + fd.getFile());
            }
            return res;
        }

        @Override
        public void rFlushConsole(Rengine re) {
        }

        @Override
        public void rLoadHistory(Rengine re, String filename) {
        }

        @Override
        public void rSaveHistory(Rengine re, String filename) {
        }
    }
}
