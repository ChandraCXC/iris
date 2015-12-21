package cfa.vo.interop;

import org.astrogrid.samp.httpd.HttpServer;
import org.astrogrid.samp.httpd.ResourceHandler;
import org.astrogrid.samp.httpd.ServerResource;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceServer {
    private Logger logger = Logger.getLogger(ResourceServer.class.getName());

    private String serverRoot;
    private HttpServer server;
    private ResourceHandler handler;

    public ResourceServer(String serverRoot) {
        this.serverRoot = serverRoot;
    }

    public void start() {
        try {
            server = new HttpServer();
            handler = new ResourceHandler(server, serverRoot);
            server.addHandler(handler);
            server.start();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Cannot start Resource Server. SAMP Export functionality will not be available", ex);
        }
    }

    public URL addResource(String filename, ServerResource serverResource) {
        return handler.addResource(filename, serverResource);
    }
}
