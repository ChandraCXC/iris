package cfa.vo.iris.test.unit;

import cfa.vo.interop.ISAMPController;
import cfa.vo.sherpa.SherpaClient;
import org.junit.rules.ExternalResource;
import static org.junit.Assert.*;

public class SherpaResource extends ExternalResource {
    private SherpaClient client;
    private SAMPClientResource sampClient;

    public SherpaResource() {
        sampClient = new SAMPClientResource();
    }

    @Override
    protected void before() throws Throwable {
        sampClient.before();
        client = SherpaClient.create(sampClient.getHubController());
        assertTrue(client.ping());
    }

    protected void after() {
        client = null;
        sampClient.after();
    }

    public SherpaClient getClient() {
        return client;
    }

    public ISAMPController getSAMPController() {
        return client.getController();
    }

}
