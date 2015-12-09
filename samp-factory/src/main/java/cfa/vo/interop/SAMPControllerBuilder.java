package cfa.vo.interop;

import java.net.URL;

public class SAMPControllerBuilder {
    private final String name;
    private String description;
    private URL icon = SAMPController.class.getResource("/iris_button_tiny.png");
    private boolean withGui = false;
    private boolean withResourceServer = false;
    private String serverRoot = "/";

    public SAMPControllerBuilder(String name) {
        this.name = name;
        this.description = name;
    }

    public String getName() {
        return name;
    }

    public SAMPControllerBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public SAMPControllerBuilder withIcon(URL iconURL) {
        this.icon = iconURL;
        return this;
    }

    public SAMPControllerBuilder withGui(boolean withGui) {
        this.withGui = withGui;
        return this;
    }

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

    public SAMPController buildAndStart(long timeoutMillis) throws Exception {
        SAMPController controller = build();
        controller.start(timeoutMillis);
        return controller;
    }

    public SAMPController build() {
        return new SAMPController(SAMPControllerBuilder.this);
    }
}
