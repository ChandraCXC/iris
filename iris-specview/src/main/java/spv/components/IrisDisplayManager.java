/*
 * This software is distributed under a BSD license,
 * as described in the LICENSE file at the top
 * source directory in the Specview source code base.
 */
package spv.components;

/**
 * Created by IntelliJ IDEA. User: busko Date: Nov 18, 2011 Time: 9:25:07 AM
 */
import cfa.vo.iris.IWorkspace;
import cfa.vo.iris.events.*;
import cfa.vo.iris.gui.GUIUtils;
import cfa.vo.iris.logging.LogEntry;
import cfa.vo.iris.logging.LogEvent;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.sedlib.Sed;
import cfa.vo.sedlib.Segment;
import cfa.vo.sedlib.common.SedInconsistentException;
import cfa.vo.sedlib.common.SedNoDataException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.astrogrid.samp.gui.GuiHubConnector;
import spv.controller.SecondaryController2;
import spv.controller.SpectrumContainer;
import spv.controller.SpvImageWriter;
import spv.controller.display.SecondaryDisplayManager;
import spv.glue.*;
import spv.graphics.GraphicsCanvas;
import spv.graphics.WCSCursor;
import spv.spectrum.SEDMultiSegmentSpectrum;
import spv.spectrum.SpectrumException;
import spv.util.*;
import spv.util.properties.SpvProperties;
import spv.view.FittingPlotWidget;
import spv.view.PlotStatus;
import spv.view.PlotWidget;

/*
 *  Revision history:
 *  ----------------
 *
 *  18 Nov 11  -  Implemented (IB)
 */
/**
 * This class provides support for Iris-specific display requirements.
 *
 *
 * @author Ivo Busko (Space Telescope Science Institute)
 * @version 1.0 - 18Nov11
 */
public class IrisDisplayManager extends SecondaryDisplayManager implements SedListener {

    public static final String FIT_MODEL = "fit.model";
    private SecondaryController2 secondaryController;
    private SpectrumVisualEditor visualEditor;
    private GuiHubConnector connection;
    private SedlibSedManager manager;
    private IWorkspace ws;
    private ExtSed sedDisplaying;
    private IrisCoplotManager coplotManager;
    private IrisDisplayManager self; // for use in innner classes
    private Map<String, PlotStatus> plotStatusStorage;
    private IrisVisualizer visualizer;
    private static java.util.List<String> ignoredImageFormats = new ArrayList<String>();

    static {
        ignoredImageFormats.add("jpeg");
        ignoredImageFormats.add("wbmp");
    }

    public IrisDisplayManager(SedlibSedManager manager, IWorkspace ws, IrisVisualizer visualizer) {
        this.manager = manager;
        this.ws = ws;
        this.visualizer = visualizer;
        self = this;

        plotStatusStorage = new HashMap<String, PlotStatus>();

        WCSCursor.getInstance().addObserver(this);
    }

    void setConnection(GuiHubConnector connection) {
        this.connection = connection;
    }

    /**
     * This is the main method used to display Sed instances.
     *
     * @param sed the Sed instance. If <code>null</code>, an empty frame is
     * loaded on the display.
     * @param name the string to be used as frame title.
     */
    void display(ExtSed sed, String name) {
        if (sed != null) {

            SpectrumContainer container = (SpectrumContainer) sed.getAttachment(FIT_MODEL);

            // Seds may come without associated data, such as when we build a composite Sed
            // to perform a co-plot operation. In those cases we need to attach a valid
            // container to the Sed.
            if (container == null) {
                try {
                    if (visualizer.buildAttachment(sed)) {
                        return;
                    }
                } catch (SedNoDataException e) {
                    e.printStackTrace();
                } catch (SedInconsistentException e) {
                    e.printStackTrace();
                } catch (SpectrumException e) {
                    e.printStackTrace();
                }

                // now, get the container so we can use it down below.

                container = (SpectrumContainer) sed.getAttachment(FIT_MODEL);

                SEDMultiSegmentSpectrum sms = (SEDMultiSegmentSpectrum) container.getSpectrum();

//                if (sms.getName().startsWith(PlottableSEDSegmentedSpectrum.COPLOT_IDENT)) {
//
//                    // force the object ID on the plot to be an empty string. This
//                    // is assuming that the sed resulted from a co-plot operation.
//                    // This breaks the original Sed data held inside the sedlib data
//                    // structure, since Iris apparently only passes around a reference
//                    // to it, not copies. the result is that the app misbehaves after
//                    // that.
//
////                    sms.getTarget().setName(new TextParam(" "));
//
//                    sed.getSegment(0).getTarget().setName(new TextParam(" "));
//                }
            }

            removeVisualEditor();

            String sedId = sed.getId();

            display(container, sedId);

            sedDisplaying = sed;

            LogEvent.getInstance().fire(this, new LogEntry("SED displayed:  " + name, this));
        }
    }

    public void display(SpectrumContainer container, String id) {

        // prevent 1-point data to be displayed. Note that in that case
        // there are 2 spectral bins because of the segment separators.
        if (container.getSpectrum().getNBins() <= 2) {
            remove(container.getSpectrum().getName());
            secondaryController = null;
            return;
        }

        // this widget will display the new Sed.
        PlotWidget pw = buildPlotWidget(container, false, null);

        String spectrmName = container.getSpectrum().getName();
        if (spectrmName.startsWith(PlottableSEDSegmentedSpectrum.COPLOT_IDENT)) {
            // blank the target name in case of co-plot
            PlottableSEDSegmentedSpectrum plottedObject = (PlottableSEDSegmentedSpectrum) pw.getPlottedObject();
            plottedObject.setRootObjectCommonName("CO-PLOT");
        } else {
            // or make it be the Sed identification string.
            try {
                PlottableSEDSegmentedSpectrum plottedObject = (PlottableSEDSegmentedSpectrum) pw.getPlottedObject();
                plottedObject.setRootObjectCommonName(id);
            } catch (ClassCastException e) {
            }
        }



        // turn off everything cursor, as per Iris request.
        pw.setSystemCursor();
        pw.setCursorArrows(false);
        SpvProperties.SetProperty(Include.CURSOR_ARROWS, "false");

        // put in callback to invoke metadata browser.
        MetadataDisplay metadataDisplay = new MetadataDisplay();
        pw.setCommand(Callback.META_DATA, metadataDisplay);

        if (secondaryController == null) {
            secondaryController = new SecondaryController2(pw, this);
        } else {

            // here we remove the listeners from the existing plot
            // widget. Then we get its plot status and store it for
            // use later on.
            PlotWidget plotWidget = secondaryController.getPlotWidget();
            plotWidget.removeListeners();

            PlotStatus plotStatus = plotWidget.getPlotStatus();
            plotStatusStorage.put(sedDisplaying.getId(), plotStatus);

            // if there is a plot status associated with the Sed
            // to be displayed, use it.
            PlotStatus ps = plotStatusStorage.get((id));
            if (ps != null) {
                ps.invalidateGraphicsAttributes();
                pw.setPlotStatus(ps);
            }

            secondaryController.loadWidget(pw);
        }

        // Force plot title to be the Sed ID string.

        if (!spectrmName.startsWith(PlottableSEDSegmentedSpectrum.COPLOT_IDENT)) {
            GraphicsCanvas canvas = pw.getMainCanvas();
            canvas.setOriginalObjectGraphicsID(id);
        }

        // the metadata display needs a reference to the desktop
        // pane so it can add itself to it. Make it here to ensure
        // a valid desktop exists.
        metadataDisplay.setDesktopPane(ws.getDesktop());
    }

    public PlotWidget getPlotWidget() {
        if (secondaryController != null) {
            return secondaryController.getPlotWidget();
        }
        return null;
    }

    public void remove(String name) {
        if (secondaryController != null) {
            secondaryController.remove(name);

            PlotWidget plotWidget = secondaryController.getPlotWidget();
            Command[] commands = plotWidget.getCommands();
            Command command = commands[Callback.META_DATA.ord];
            if (command instanceof MetadataDisplay) {
                if (visualEditor != null) {
                    JFrame jFrame = visualEditor.getFrame();
                    if (jFrame != null) {
                        jFrame.setVisible(false);
                    }
                    visualEditor = null;
                }
            }
        }

        visualizer.invalidateModel(sedDisplaying);
        visualizer.disposeCurrentFrame();

        secondaryController = null;
    }

    void setDesktopMode(boolean desktopMode) {
        SpvProperties.SetSessionProperty(Include.DESKTOP_MODE, desktopMode ? "true" : "false");
    }

    // Lame override of base class. This is all just to force the residuals
    // plot to never show up when displaying non-fitted data. It's a consequence
    // of re-purposing the pan canvas as a residuals plot area.
    protected PlotWidget getPlotWidgetFromFactory(PlottableSpectrum plottable) {
        if (plottable instanceof PlottableFittedSpectrum && SpvProperties.GetProperty(Include.APP_NAME).equals(Include.IRIS_APP_NAME)) {
            return new FittingPlotWidget(plottable, false);
        }

        return new SEDBasicPlotWidget(plottable, false);
    }

    // GUI stuff.
    JInternalFrame getInternalFrame() {
        if (secondaryController != null) {
            JInternalFrame internalFrame = secondaryController.getInternalFrame();

            if (internalFrame.getJMenuBar() == null) {
                addMenuBar(internalFrame);
            }

            return internalFrame;
        } else {
            // Returns a fake frame since no data is being displayed.
            JInternalFrame fakeFrame = new JInternalFrame(Include.IRIS_APP_NAME, true, true, true, true);
            JPanel contentPane = (JPanel) fakeFrame.getRootPane().getContentPane();
            makeSpecviewIDPanel(contentPane);
            fakeFrame.setSize(Include.DEFAULT_EMPTY_WINDOW_SIZE);
            return fakeFrame;
        }
    }

    private void addMenuBar(JInternalFrame internalFrame) {
        JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu saveMenu = new JMenu("Save plot to image file.");
        populateImageFormatMenu(saveMenu);
        fileMenu.add(saveMenu);
        bar.add(fileMenu);

        JMenu displayMenu = new JMenu("Display");
        JMenuItem coplotMenuItem = new JMenuItem("Co-plot");
        ActionListener listener = new CoplotListenerAdapter();
        coplotMenuItem.addActionListener(listener);
        displayMenu.add(coplotMenuItem);
        bar.add(displayMenu);

        internalFrame.setJMenuBar(bar);
    }

    private void populateImageFormatMenu(JMenu menu) {

        String[] suffixes = ImageIO.getWriterFileSuffixes();

        for (int i = 0; i < suffixes.length; i++) {
            if (!ignoredImageFormats.contains(suffixes[i])) {
                JMenuItem menuItem = new JMenuItem(suffixes[i]);
                menu.add(menuItem);

                ActionListener listener = new ImageFileActionListenerAdapter(suffixes[i]);
                menuItem.addActionListener(listener);
            }
        }
    }

    public void saveAsImage(String suffix) {
        String filename = getSaveFileName("." + suffix);
        JComponent component = secondaryController.getPlotWidget().getDisplayComponent();
        SpvImageWriter writer = new SpvImageWriter();
        try {
            writer.write(component, filename, suffix);
        } catch (IOException e) {
            ExceptionHandler.handleException(e);
        }
    }

    private String getSaveFileName(String suffix1) {
        String result = null;
        MemoryJFileChooser chooser = new MemoryJFileChooser();
        chooser.setFileFilter(new SPVFilter(suffix1, null, null));

        int i = chooser.showSaveDialog(secondaryController.getInternalFrame());

        if (i == MemoryJFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file == null) {
                new ErrorDialog("Cannot open file: " + file);
                return result;
            }

            result = chooser.getSelectedFile().getPath();

            int j = result.lastIndexOf('.');
            if (j < 0 || j >= result.length()) {
                result += suffix1;
            }
        }

        return result;
    }

    private void makeSpecviewIDPanel(JPanel panel) {
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new java.awt.Color(255, 255, 255));

        writeIrisIDPanel(titlePanel);

        panel.removeAll();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(Include.DEFAULT_EMPTY_WINDOW_SIZE);
        panel.setBackground(new java.awt.Color(0, 0, 0));
        {
            String strConstraint;
            strConstraint = "Center";
            panel.add(titlePanel, strConstraint, -1);
        }
    }

    private void writeIrisIDPanel(JPanel titlePanel) {
        titlePanel.setLayout(new BorderLayout());

        URL resource = getClass().getResource(Include.IRIS_ID_ICON);
        ImageIcon icon = new ImageIcon(resource);
        JLabel label = new JLabel(icon);
        label.setBorder(BorderFactory.createEmptyBorder());
        label.setBackground(Color.white);
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setBackground(Color.white);
        labelPanel.setBorder(BorderFactory.createEmptyBorder());
        labelPanel.add(label, BorderLayout.CENTER);
        titlePanel.add(labelPanel, BorderLayout.CENTER);

        JLabel versionLabel = new JLabel(Include.IRIS_SUBTITLE);
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        versionLabel.setForeground(Color.black);
        versionLabel.setFont(new Font("dialog", 1, 16));
    }

    GuiHubConnector getSAMPConnector() {
        return connection;
    }

    public void process(ExtSed extSed, SedCommand sedCommand) {
    }

    public SpectrumVisualEditor getVisualEditor() {
        return visualEditor;
    }

    public void removeVisualEditor() {
        if (visualEditor != null) {
            visualEditor.getJFrame().setVisible(false);
            visualEditor = null;
        }
    }

    public ExtSed getDisplaying() {
        return sedDisplaying;
    }

    public void update(Observable originator, Object object) {
        if (originator instanceof WCSCursor) {
//            System.out.println ("IrisDisplayManager  line: 370  - " + object);
//            String element_name = null;
//            if (object instanceof String) {
//                Enumeration enumeration = getSpectrumList();
//                while (enumeration.hasMoreElements()) {
//                    Object hold = enumeration.nextElement();
//                    if (hold instanceof String) {
//                        element_name = (String)hold;
//                        if (element_name.equals(object)) {
//                            break;
//                        }
//                    }
//                }
//                Color color = spColors.get(element_name);
//                boolean makeFrame = false;
//                String desktopMode = SpvProperties.GetProperty(Include.DESKTOP_MODE);
//                if (desktopMode == null || !desktopMode.equals("true")) {
//                    makeFrame = true;
//                }
//                new SEDSegmentedSpectrumVisualEditor (self, element_name, true, color, null, makeFrame);
//            }
        }
    }

    // Metadata button. This button is not present in Iris 1.0. Its
    // purpose is to give access to the table with metadata and data
    // for the entire SED. This function was available in Iris 1.0
    // from the Coplot window instead.
    private class MetadataDisplay implements Command {

        public void execute(Object arg) {

            if (arg instanceof PlottableSEDSegmentedSpectrum) {

                // Use this metadata/data browser when not fitting.

                visualEditor = new SEDSpectrumVisualEditor(
                        (PlottableSEDSegmentedSpectrum) arg, null, true, Color.red, null, false,
                        secondaryController.getPlotWidget(), visualizer.getSAMPConnection());

                // Attach a listener to the visual editor so Seds
                // can be extracted from the one being displayed.
                ((SEDSpectrumVisualEditor) visualEditor).setCommand(new OnExtractCommand());

            } else if (arg instanceof PlottableSEDFittedSpectrum) {

                // Use this metadata/data browser when fitting a model.

                visualEditor = new SEDFittedSpectrumVisualEditor(
                        (PlottableSEDFittedSpectrum) arg, null, Color.red, null);
            }

            JInternalFrame frame = visualEditor.getJFrame().getInternalFrame();
            ws.addFrame(frame);
            try {
                frame.setSelected(true);
            } catch (PropertyVetoException e) {
            }
        }

        public void setDesktopPane(JDesktopPane desktop) {
            SpectrumVisualEditor.SetDesktopPane(desktop);
        }
    }

    // This class responds to the Extract button in the metadata browser.
    private class OnExtractCommand implements Command {

        public void execute(Object o) {
            if (o instanceof Sed) {
                Sed sed = (Sed) o;

                ExtSed extSed = manager.newSed("FilterSed");

                int numberOfSegments = sed.getNumberOfSegments();
                for (int i = 0; i < numberOfSegments; i++) {
                    Segment segment = sed.getSegment(i);
                    try {
                        extSed.addSegment(segment);
                    } catch (SedInconsistentException e) {
                        ExceptionHandler.handleException(e);
                    } catch (SedNoDataException e) {
                        ExceptionHandler.handleException(e);
                    }
                }
            }
        }
    }

    class ImageFileActionListenerAdapter implements ActionListener {

        private String suffix;

        ImageFileActionListenerAdapter(String suffix) {
            this.suffix = suffix;
        }

        public void actionPerformed(ActionEvent e) {
            saveAsImage(suffix);
        }
    }

    class CoplotListenerAdapter implements ActionListener {

        public void actionPerformed(ActionEvent ev) {

            if (coplotManager == null) {
                coplotManager = new IrisCoplotManager(ws, self, "Co-plot");
            }

            MemoryJFrame memoryFrame = coplotManager.getJFrame();
            JInternalFrame frame = memoryFrame.getInternalFrame();
            frame.setSize(Include.IRIS_SPLIST_WINDOW_SIZE);
            ws.addFrame(frame);

            GUIUtils.moveToFront(frame);
        }
    }
}
