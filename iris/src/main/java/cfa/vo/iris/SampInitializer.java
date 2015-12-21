package cfa.vo.iris;

import cfa.vo.interop.*;

public class SampInitializer {
    private IrisApplication app;
    private SampService sampService;

    public SampInitializer(IrisApplication app) {
        this.app = app;
    }

    public void init() throws Exception {
        SAMPServiceBuilder builder = new SAMPServiceBuilder(app.getName())
                .withDescription(app.getDescription())
                .withResourceServer("sedImporter/")
                .withIcon(app.getSAMPIcon());
        sampService = new SampService(builder);
        sampService.setAutoRunHub(true);
        sampService.start();
    }

    public void setAutoRunHub(boolean autoRunHub) {
        sampService.setAutoRunHub(autoRunHub);
    }

    public void stop() {
        sampService.shutdown();
    }

    public SampService getSampService() {
        return sampService;
    }

}
