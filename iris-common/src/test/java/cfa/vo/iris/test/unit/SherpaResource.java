package cfa.vo.iris.test.unit;

import cfa.vo.interop.SampService;
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
        client = new SherpaClient(sampClient.getSampService());
        assertTrue(client.ping(60, 500));
    }

    protected void after() {
        client = null;
        sampClient.after();
    }

    public SherpaClient getClient() {
        return client;
    }

    public SampService getSampService() {
        return client.getService();
    }

}
