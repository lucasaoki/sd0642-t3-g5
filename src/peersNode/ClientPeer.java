/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersNode;

import centralNode.ServerFileSystem;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jxta.discovery.DiscoveryService;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.logging.Logging;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.util.JxtaBiDiPipe;

/**
 *
 * @author lucasaoki
 */
public class ClientPeer implements PipeMsgListener {

    private final static transient Logger LOG = Logger.getLogger(ServerFileSystem.class.getName());
    private final File home;
    private NetworkManager manager = null;
    private PeerGroup netPeerGroup = null;
    private PeerAdvertisement myPeerAdv = null;
    private PeerGroupAdvertisement myPeerGroupAdv = null;
    private DiscoveryService myDiscoveryService = null;
    private JxtaBiDiPipe pipe = null;
    private final static String completeLock = "completeLock";
    private int count = 0;

    public ClientPeer() {

        home = new File(new File(".cache"), "client");
        try {
            manager = new NetworkManager(NetworkManager.ConfigMode.EDGE, "ClientPeer", home.toURI());
            System.out.println("[+]Starting JXTA");
            manager.startNetwork();
            System.out.println("[+]JXTA Started");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        netPeerGroup = manager.getNetPeerGroup();
        try {
            pipe = new JxtaBiDiPipe();
            pipe.setReliable(true);
            System.out.println("[+]Waiting for a rendezvous connection");
            boolean waitForRendezvous = Boolean.valueOf(System.getProperty("RDWAIT", "false"));

            if (waitForRendezvous) {
                manager.waitForRendezvousConnection(0);
            }
            pipe.connect(netPeerGroup, null, ServerFileSystem.getPipeAdvertisement(), 60000, this);
            System.out.println("[+]JXTA BiDiPipe pipe created");
            waitUntilCompleted();

        } catch (IOException e) {
            System.out.println("[-]Failed to bind the JxtaBiDiPipe due to the following exception");
            e.printStackTrace();
            System.exit(-1);

        }
        //log.append("[+]Launching into JXTA Network...\n");
        getServices();
    }

    @Override
    public void pipeMsgEvent(PipeMsgEvent event) {
        Message msg;
        Message response;
        try {
            // grab the message from the event
            msg = event.getMessage();
            if (msg == null) {
                if (Logging.SHOW_FINE && LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Received an empty message, returning");
                }
                return;
            }
            if (Logging.SHOW_FINE && LOG.isLoggable(Level.FINE)) {
                LOG.fine("Received a response");
            }
            // get the message element named SenderMessage
            MessageElement msgElement = msg.getMessageElement(/*SenderMessage, SenderMessage*/",");
            // Get message
            if (msgElement.toString() == null) {
                System.out.println("null msg received");
            } else {
                //System.out.println("Got Message :"+ msgElement.toString());
                count++;
            }
            response = msg.clone();
//System.out.println("Sending response to " + msgElement.toString());
            pipe.sendMessage(response);
// If JxtaServerPipeExample.ITERATIONS # of messages received, it is
// no longer needed to wait. notify main to exit gracefully
            if (count >= 10/*ServerFileSystem.ITERATIONS*/) {
                synchronized (completeLock) {
                    completeLock.notify();
                }
            }
        } catch (Exception e) {
            if (Logging.SHOW_FINE && LOG.isLoggable(Level.FINE)) {
                LOG.fine(e.toString());
            }
        }
    }

    private void waitUntilCompleted() {
        try {
            System.out.println("[+]Waiting for Messages.");
            synchronized (completeLock) {
                completeLock.wait();
            }
            pipe.close();
            System.out.println("[+]Done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getServices() {
        //Obtaining JXTA Services from JXTA Global group
        //log.append("[+]Obtaining Peer Group Services.\n");
        myDiscoveryService = netPeerGroup.getDiscoveryService();
    }
    
    private void stop() {
        manager.stopNetwork();
    }

    public static void main(String args[]) {
        ClientPeer client = new ClientPeer();
        client.stop();
    }
}
