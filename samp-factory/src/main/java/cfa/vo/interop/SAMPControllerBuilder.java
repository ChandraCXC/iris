package cfa.vo.interop;

import java.net.URL;

/**
 * Build {@link SAMPController} instances.
 *
 * The builder has a fluent API, for instance:
 * <code>
 *     SAMPController controller = SAMPControllerBuilder("test")
 *              .withDescription("description")
 *              .withIcon(iconURL)
 *              .build()
 * </code>
 *
 * Multiple build methods are provided that may optionally start the execution of the controller.
 */
public class SAMPControllerBuilder {
    private final String name;
    private String description;
    private URL icon = SAMPController.class.getResource("/iris_button_tiny.png");
    private boolean withGui = false;
    private boolean withResourceServer = false;
    private String serverRoot = "/";

    /**
     * The simplest builder (and {@link SAMPController}) has just a name. It will default to
     * having a description equal to the name, an icon equal to the iris one,
     * no gui, and no resource server.
     * @param name The name that will be displayed in the SAMP Hub window
     */
    public SAMPControllerBuilder(String name) {
        this.name = name;
        this.description = name;
    }

    /**
     * Get the name of the controller being built
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set the description of the controller to be displayed
     * @param description
     * @return
     */
    public SAMPControllerBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Set the icon of the controller
     * @param iconURL
     * @return
     */
    public SAMPControllerBuilder withIcon(URL iconURL) {
        this.icon = iconURL;
        return this;
    }

    /**
     * Set whether the controller being built should be represented by a GUI
     * @param withGui
     * @return
     */
    public SAMPControllerBuilder withGui(boolean withGui) {
        //FIXME this is a leftover of the refactoring, and should go into the HubController instead.
        this.withGui = withGui;
        return this;
    }

    /**
     * Set whether the controller should start and internal HTTP server
     * @param serverRoot the root path of the resource server
     * @return
     */
    public SAMPControllerBuilder withResourceServer(String serverRoot) {
        this.withResourceServer = true;
        if (serverRoot != null) {
            this.serverRoot = serverRoot;
        }
        return this;
    }

    public String getDescription() {
        return description;
    }

    public URL getIcon() {
        return icon;
    }

    public boolean isWithGui() {
        return withGui;
    }

    public boolean isWithResourceServer() {
        return withResourceServer;
    }

    public String getServerRoot() {
        return serverRoot;
    }

    /**
     * build the controller and try to start it. If the controller (or the resource server)
     * cannot be started and connected to a hub, an exception will be thrown.
     * @param timeoutMillis a timeout value in milliseconds
     * @return a SAMPController instance corresponding to the configuration represented by this builder
     * @throws Exception if the controller cannot be started and connected before the timeout expires
     */
    public SAMPController buildAndStart(long timeoutMillis) throws Exception {
        SAMPController controller = build();
        if(!controller.start(timeoutMillis)) {
            throw new Exception("cannot start or connect SAMP controller");
        }
        return controller;
    }

    /**
     * build the controller without trying to start it.
     * @return
     */
    public SAMPController build() {
        return new SAMPController(SAMPControllerBuilder.this);
    }
}
