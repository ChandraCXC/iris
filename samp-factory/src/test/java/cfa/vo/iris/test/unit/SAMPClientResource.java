package cfa.vo.iris.test.unit;

import cfa.vo.interop.ISAMPController;
import cfa.vo.interop.SAMPController;
import cfa.vo.interop.SAMPControllerBuilder;
import org.junit.rules.ExternalResource;

import static org.junit.Assert.assertTrue;

public class SAMPClientResource extends ExternalResource {
    private SAMPController controller;
    private SAMPControllerBuilder builder;
    private long timeoutMillis = 30000;

    public SAMPClientResource(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public SAMPClientResource(SAMPControllerBuilder builder, long timeoutMillis) {
        this.builder = builder;
        this.timeoutMillis = timeoutMillis;
    }

    public SAMPClientResource(SAMPControllerBuilder builder) {
        this.builder = builder;
    }

    public SAMPClientResource() {
        this.builder = new SAMPControllerBuilder("Test")
                .withDescription("Test Hub");
    }

    @Override
    protected void before() throws Throwable {
        controller = builder.buildAndStart(timeoutMillis);
        assertTrue(controller.isConnected());
    }

    @Override
    protected void after() {
        controller.stop();
    }

    public ISAMPController getHubController() {
        return controller;
    }
}
