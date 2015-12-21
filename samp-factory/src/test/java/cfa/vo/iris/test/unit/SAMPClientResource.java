package cfa.vo.iris.test.unit;

import cfa.vo.interop.SAMPServiceBuilder;
import cfa.vo.interop.SampService;
import org.junit.rules.ExternalResource;

import static org.junit.Assert.assertTrue;

public class SAMPClientResource extends ExternalResource {
    private SampService sampService;
    private SAMPServiceBuilder builder;

    public SAMPClientResource(SAMPServiceBuilder builder) {
        this.builder = builder;
    }

    public SAMPClientResource() {
        this.builder = new SAMPServiceBuilder("Test")
                .withDescription("Test Hub");
    }

    @Override
    protected void before() throws Throwable {
        sampService = builder.build();
        sampService.start();
        boolean connected = false;
        for (int i=0; i<30; i++) {
            try {
                assertTrue(sampService.isSampUp());
                connected = true;
                break;
            } catch (AssertionError e) {
                Thread.sleep(500);
            }
        }
        assertTrue(connected);
    }

    @Override
    protected void after() {
        sampService.shutdown();
    }

    public SampService getSampService() {
        return sampService;
    }
}
