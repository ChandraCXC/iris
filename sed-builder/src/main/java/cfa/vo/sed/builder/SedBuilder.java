/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cfa.vo.sed.builder;

import cfa.vo.iris.AbstractDesktopItem;
import cfa.vo.iris.AbstractMenuItem;
import cfa.vo.iris.ICommandLineInterface;
import cfa.vo.iris.IMenuItem;
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.IrisApplication;
import cfa.vo.iris.IrisComponent;
import cfa.vo.iris.gui.NarrowOptionPane;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.iris.sed.SedlibSedManager.ExtSed;
import cfa.vo.sed.filters.FileFormatManager;
import cfa.vo.sed.gui.LoadSetupDialog;
import cfa.vo.sed.gui.PluginManager;
import cfa.vo.sed.gui.SedBuilderMainView;
import cfa.vo.sed.gui.SetupFrame;
import cfa.vo.sed.setup.ISetup;
import cfa.vo.sed.setup.SetupBean;
import cfa.vo.sed.setup.SetupManager;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import cfa.vo.sedlib.common.ValidationError;
import cfa.vo.sedlib.common.ValidationErrorEnum;
import cfa.vo.sedlib.io.SedFormat;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.AbstractMessageHandler;
import org.astrogrid.samp.client.HubConnection;
import org.astrogrid.samp.client.MessageHandler;

/**
 *
 * @author olaurino
 */
public class SedBuilder implements IrisComponent {

    private static IrisApplication iris;

    private static IWorkspace workspace;

    private static JFrame rootFrame;

    private static SedlibSedManager sedManager;

    private static SedBuilderMainView view;

    private PluginManager pManager;

    public static void show() {
        if(view==null) {
            view = new SedBuilderMainView(sedManager, workspace.getRootFrame());
            workspace.addFrame(view);
        }
        view.show();
        try {
            view.setIcon(false);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(SedBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void init(IrisApplication app, IWorkspace ws) {
        iris = app;
        workspace = ws;
        rootFrame = workspace.getRootFrame();
        this.sedManager = (SedlibSedManager) workspace.getSedManager();

        FileFormatManager.getInstance().init();
    }

    @Override
    public String getName() {
        return "SED Builder";
    }

    @Override
    public String getDescription() {
        return "VAO SED Builder";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new SedBuilderCli();
    }

    @Override
    public List<IMenuItem> getMenus() {
        return new BuilderMenuItems();
    }

    @Override
    public List<MessageHandler> getSampHandlers() {
        List<MessageHandler> list = new ArrayList();
        list.add(new SAMPTableHandler());
        return list;
    }

    public static IrisApplication getApplication() {
        return iris;
    }

    public static IWorkspace getWorkspace() {
        return workspace;
    }

    private class BuilderMenuItems extends ArrayList<IMenuItem> {
        public BuilderMenuItems() {
            add(new AbstractDesktopItem("File|Build SED", "Load SED data from several different sources", "/scratch.png", "/scratch_tiny.png") {

                @Override
                public void onClick() {
                    SedBuilder.show();
                }

            });

            add(new AbstractMenuItem("Plugins..", "Manage custom file filters plug-ins", false, "/plugin.png", "/plugin.png") {

                @Override
                public void onClick() {
                    if(pManager==null) {
                        pManager = new PluginManager();
                        workspace.addFrame(pManager);
                    }
                    pManager.show();
                    try {
                        pManager.setIcon(false);
                    } catch (PropertyVetoException ex) {
                        Logger.getLogger(SedBuilder.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            });

            add(new AbstractMenuItem("Load setup file...", "Load a SED from a previously saved Setup File", false, "/tool.png", "/tool_tiny.png") {

                @Override
                public void onClick() {
                    new LoadSetupDialog(rootFrame, sedManager).setVisible(true);
                }

            });

        }
    }

    private class SAMPTableHandler extends AbstractMessageHandler {

        public SAMPTableHandler() {
            super(new String[]{"table.load.votable",
                                "table.load.fits",
                                });
        }

        @Override
        public Map processCall(HubConnection hc, String string, Message msg) throws MalformedURLException {
            String formatName = msg.getMType().toLowerCase().equals("table.load.votable") ? "VOTABLE" : "FITS";
            URL url = new URL((String) msg.getParam("url"));
            ExtSed sed = sedManager.getSelected() != null ? sedManager.getSelected() : sedManager.newSed("SAMP");
            try {
                    Sed s = Sed.read(url.openStream(), SedFormat.VOT);
                    List<ValidationError> validErrors = new ArrayList();
                    s.validate(validErrors);
                    for (ValidationError error : validErrors) {
                        if (error.getError().equals(ValidationErrorEnum.MISSING_DATA_FLUXAXIS_VALUE)) {
                            return doImport(url, formatName, sed);
                        }
                        if (error.getError().equals(ValidationErrorEnum.MISSING_DATA_SPECTRALAXIS_VALUE)) {
                            return doImport(url, formatName, sed);
                        }
                        if (error.getError().equals(ValidationErrorEnum.MISSING_CHAR_FLUXAXIS_UCD)) {
                            return doImport(url, formatName, sed);
                        }
                        if (error.getError().equals(ValidationErrorEnum.MISSING_CHAR_FLUXAXIS_UNIT)) {
                            return doImport(url, formatName, sed);
                        }

                    }
                    for (int i = 0; i < s.getNumberOfSegments(); i++) {
                        sed.addSegment(s.getSegment(i));
                    }
            }catch (Exception ex) {
                Logger.getLogger(SedBuilder.class.getName()).log(Level.SEVERE, null, ex);
                NarrowOptionPane.showMessageDialog(rootFrame,
                            ex.getMessage(),
                            "Import Error",
                            NarrowOptionPane.ERROR_MESSAGE);
            }

            return null;
        }

        private Map doImport(URL url, String formatName, ExtSed sed) {
            try {
                    //SED not valid
                    SetupBean c = new SetupBean();
                    c.setPositionInFile(0);
                    c.setFileLocation(url.toString());
                    c.setFormatName(formatName);
                    SetupFrame sf = new SetupFrame(sedManager, c, sed);
                    workspace.addFrame(sf);
                    sf.setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(SedBuilder.class.getName()).log(Level.SEVERE, null, ex);
                    NarrowOptionPane.showMessageDialog(rootFrame,
                            ex.getMessage(),
                            "Import Error",
                            NarrowOptionPane.ERROR_MESSAGE);
                }
            return null;
        }

    }

    private class SedBuilderCli implements ICommandLineInterface {

        protected List<ISetup> confList;

        @Override
        public String getName() {
            return "builder";
        }

        @Override
        public void call(String[] args) {

            SedFormat format;
            File outputFile;

            if(args.length > 0) {
                if(args.length < 2) {
                    System.err.println("Usage: sedimporter config_file output_file [output_format].");
                    return;
                } else {
                    String formatS = args.length==2 ? "VOT" : args[2];
                    try {
                        URL url;
                        if(args[0].contains("://")) {
                            url = new URL(args[0]);
                        } else {
                            File f = new File(args[0]);
                            url = new URL("file://"+f.getAbsolutePath());
                        }
                        confList = SetupManager.read(url);
                    } catch (IOException ex) {
                        System.err.println("Error reading file "+args[0]+": "+ex.getMessage());
                        return;
                    } catch (Exception ex) {
                        System.err.println("Generic error reading file "+args[0]+": "+ex.getMessage());
                        return;
                    }
                    try {
                        outputFile = new File(args[1]);
                        if(outputFile.exists() && !outputFile.canWrite()) {
                            System.err.println("Error: file "+args[1]+" is not writable.");
                            return;
                        }
                    } catch (Exception ex) {
                        System.err.println("Error opening file "+args[1]+": "+ex.getMessage());
                        return;
                    }
                    try {
                        format = SedFormat.valueOf(formatS.toUpperCase());
                    } catch (Exception ex) {
                        System.err.println("No such a format: "+formatS+". Please use 'vot' or 'fits'.");
                        return;
                    }


                    List<Segment> segments = null;
                    try {
                        System.out.println();
                        System.out.println("Building segments...");
                        System.out.println();
                        segments = SegmentImporter.getSegments(confList);
                    } catch (Exception ex) {
                        System.err.println("Error while building segments: "+ex.getMessage());
                        return;
                    }
                    System.out.println();
                    System.out.println("Building SED...");
                    Sed sed = new Sed();
                    try {
                        try {
                            sed.addSegment(segments);
                        } catch (SedNoDataException ex) {
                            Logger.getLogger(SedBuilder.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (SedInconsistentException ex) {
                        System.err.println("Error: segments are inconsistent: "+ ex.getMessage());
                        return;
                    }
                    try {
                        System.out.println();
                        System.out.println("Writing SED to "+outputFile.getAbsolutePath()+"...");
                        sed.write(new FileOutputStream(outputFile), format);
                        System.out.println();
                        System.out.println("DONE.");
                    } catch (Exception ex) {
                        System.err.println("Error while serializing SED: "+ex.getMessage());
                    }


                }
            }
        }
    }

}
