package cfa.vo.interop;

import java.net.URL;

/**
 * Build {@link SampService} instances.
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
public class SAMPServiceBuilder {
    private final String name;
    private String description;
    private URL icon = SampService.class.getResource("/iris_button_tiny.png");
    private boolean withResourceServer = false;
    private String serverRoot = "/";

    /**
     * The simplest builder (and {@link SampService}) has just a name. It will default to
     * having a description equal to the name, an icon equal to the iris one,
     * no gui, and no resource server.
     * @param name The name that will be displayed in the SAMP Hub window
     */
    public SAMPServiceBuilder(String name) {
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
    public SAMPServiceBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Set the icon of the controller
     * @param iconURL
     * @return
     */
    public SAMPServiceBuilder withIcon(URL iconURL) {
        this.icon = iconURL;
        return this;
    }

    /**
     * Set whether the controller should start and internal HTTP server
     * @param serverRoot the root path of the resource server
     * @return
     */
    public SAMPServiceBuilder withResourceServer(String serverRoot) {
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

    public boolean isWithResourceServer() {
        return withResourceServer;
    }

    public String getServerRoot() {
        return serverRoot;
    }

    /**
     * build the service.
     * @return
     */
    public SampService build() {
        return new SampService(SAMPServiceBuilder.this);
    }
}
