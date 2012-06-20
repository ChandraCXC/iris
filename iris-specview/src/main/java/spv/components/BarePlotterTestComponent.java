
package spv.components;

import cfa.vo.iris.*;
import cfa.vo.iris.events.MultipleSegmentEvent;
import cfa.vo.iris.events.MultipleSegmentListener;
import cfa.vo.iris.events.SegmentEvent.SegmentPayload;
import cfa.vo.iris.sed.ExtSed;
import cfa.vo.iris.events.SedEvent;
import cfa.vo.iris.events.SedListener;
import cfa.vo.iris.events.SedCommand;
import cfa.vo.iris.events.SegmentEvent;
import cfa.vo.iris.events.SegmentListener;
import cfa.vo.iris.sed.SedlibSedManager;
import cfa.vo.sedlib.Segment;
import org.astrogrid.samp.client.MessageHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: busko
 * Date: May 14, 2012
 * Time: 8:34:21 AM
 */

public class BarePlotterTestComponent implements IrisComponent {

    private IWorkspace ws;
    private SedlibSedManager manager;
    private Plotter currentPlotter;

    private static final String ATTACHMENT_ID = "spv:plotter";

    @Override
    public void init(IrisApplication app, IWorkspace workspace) {
        ws = workspace;
        manager = (SedlibSedManager) workspace.getSedManager();

//        SpvInitialization spvinit = new SpvInitialization(new String[]{}, null);
//        SpvProperties.SetProperty(Include.APP_NAME, "Iris");
//        spvinit.initialize(null, false);

        SedEvent.getInstance().add(new SedListener() {
            @Override
            public void process(final ExtSed source, SedCommand payload) {
                if(payload.equals(SedCommand.SELECTED))
                    display(source);
            }
        });

        SegmentEvent.getInstance().add(new SegmentListener() {

            @Override
            public void process(Segment source, SegmentPayload payload) {
                if(payload.getSedCommand().equals(SedCommand.ADDED)) {
                    Plotter plotter = getPlotter(payload.getSed());
                    plotter.addSegment(source);
                    return;
                }
                if(payload.getSedCommand().equals(SedCommand.REMOVED)) {
                    Plotter plotter = getPlotter(payload.getSed());
                    plotter.removeSegment(source);
                    return;
                }
                if(payload.getSedCommand().equals(SedCommand.CHANGED)) {
                    Plotter plotter = getPlotter(payload.getSed());
                    plotter.reset();
                    return;
                }
            }
        });

        MultipleSegmentEvent.getInstance().add(new MultipleSegmentListener() {

            @Override
            public void process(List<Segment> sources, SegmentPayload payload) {
                if(payload.getSedCommand().equals(SedCommand.ADDED)) {
                    Plotter plotter = getPlotter(payload.getSed());
                    plotter.addSegments(sources);
                    return;
                }
                if(payload.getSedCommand().equals(SedCommand.REMOVED)) {
                    Plotter plotter = getPlotter(payload.getSed());
                    plotter.removeSegments(sources);
                    return;
                }
                if(payload.getSedCommand().equals(SedCommand.CHANGED)) {
                    Plotter plotter = getPlotter(payload.getSed());
                    plotter.reset();
                    return;
                }
            }
        });


    }

    @Override
    public String getName() {
        return "Test plotter";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public ICommandLineInterface getCli() {
        return new NullCommandLineInterface("ch");
    }

    @Override
    public void initCli(IrisApplication app) {

    }

    @Override
    public List<IMenuItem> getMenus() {
        return new TestMenus();
    }

    @Override
    public List<MessageHandler> getSampHandlers() {
        return new ArrayList();
    }

    @Override
    public void shutdown() {

    }

    private void display(ExtSed sed) {
        if(currentPlotter!=null)
            currentPlotter.getFrame().setVisible(false);

        currentPlotter = getPlotter(sed);

        currentPlotter.display();
    }

    private Plotter getPlotter(ExtSed sed) {
        Plotter plotter = (Plotter) sed.getAttachment(ATTACHMENT_ID);
        if(plotter == null) {
            plotter = new SpecviewPlotter(sed);
            sed.addAttachment(ATTACHMENT_ID, plotter);
            ws.addFrame(plotter.getFrame());
        }
        return plotter;
    }

    private class TestMenus extends ArrayList<IMenuItem> {
        public TestMenus() {
            add(new AbstractDesktopItem("Bare Plotter", "Simple Test Plotter", "/tool.png", "/tool_tiny.png") {

                @Override
                public void onClick() {
                    if(manager.getSelected()!=null) {
                        ExtSed sed = manager.getSelected();
                        display(sed);
                    }
                }
            });
        }
    }
}



