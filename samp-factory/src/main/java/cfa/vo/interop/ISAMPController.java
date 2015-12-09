package cfa.vo.interop;

import org.astrogrid.samp.Message;
import org.astrogrid.samp.Response;
import org.astrogrid.samp.client.HubConnection;
import org.astrogrid.samp.client.MessageHandler;
import org.astrogrid.samp.client.SampException;
import org.astrogrid.samp.httpd.ServerResource;

import java.net.URL;
import java.util.Map;

public interface ISAMPController {
    void stop();

    void start() throws Exception;

    void sendMessage(SAMPMessage message) throws SampException;

    void addConnectionListener(SAMPConnectionListener listener);

    void addMessageHandler(MessageHandler handler);

    Response callAndWait(String id, Map message, int timeout) throws SampException;

    Map getClientMap();

    HubConnection getConnection() throws SampException;

    URL addResource(String filename, ServerResource serverResource);
}
