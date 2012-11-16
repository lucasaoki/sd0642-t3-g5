package neighbourhoodNode;

import java.io.File;
import java.io.IOException;

import net.jxta.endpoint.Message;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.JxtaBiDiPipe;
import utilitesFileSystem.FileManager;
import utilitesFileSystem.MsgFileSystem;
import utilitesFileSystem.PipeMensageUtilites;
import FileLib.Interface;
import centralNode.ServerFileSystem;

public class ClientFileSystem implements PipeMsgListener, Runnable {

    private final File home;
    private NetworkManager manager;
    private PeerGroup peerGroup;
    private JxtaBiDiPipe pipe;
    private PipeAdvertisement centralNode;
    private FileManager onlyRead;
    private MsgFileSystem msgFileSystem;
    private int function;
    private boolean create = false;
    private boolean delete = false;
    private boolean move = false;
    private boolean where = false;
    private Interface face;
    
    public ClientFileSystem() throws IOException, PeerGroupException {

        msgFileSystem = new MsgFileSystem();
        face = new Interface();
        
        home = new File(new File(".cache"), "client");

        manager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
                "ClientFileSystem", home.toURI());
        manager.startNetwork();

        boolean waitForRendezvous = Boolean.valueOf(System.getProperty(
                "RDWAIT", "false"));

        if (waitForRendezvous) {
            manager.waitForRendezvousConnection(0);
        }

        peerGroup = manager.getNetPeerGroup();

        centralNode = ServerFileSystem.getPipeAdvertisement();
        pipe = new JxtaBiDiPipe(peerGroup, centralNode, 20000, this, true);
    }

    public void sendMessageForServerFileSystem(Message msg) throws IOException {
        pipe.sendMessage(msg);
    }

    @Override
    public void pipeMsgEvent(PipeMsgEvent event) {
        // TODO Auto-generated method stub
        Message msg = event.getMessage();

        function = msgFileSystem.functionFromMessage(msg);
        String response, fileName;

        switch (function) {
            case 0:
                response = msgFileSystem.getResponseFromMessage(msg);
                if (response.equals(PipeMensageUtilites.okCreate)) {
                    // cria o arquivo
                    face.get_data('c', msgFileSystem.getFileNameFromMessage(msg), null);

                    System.out.println("1 " + response + " " + function
                            + " " + msgFileSystem.getFileNameFromMessage(msg));
                    create = true;
                } else {
                    System.out.println("2 " + response + " " + function
                            + " " + msgFileSystem.getFileNameFromMessage(msg));
                    // informa que nao criou o arquivo

                }
                //	System.exit(0);
                break;
            case 1:
                response = msgFileSystem.getResponseFromMessage(msg);
                if (response.equals(PipeMensageUtilites.okRemove)) {
                    // deletou o arquivo
                    System.out.println("1 " + response + " " + function
                            + " " + msgFileSystem.getFileNameFromMessage(msg));
                    delete = true;
                } else {
                    // informa que nao deletou o arquivo
                    System.out.println("2 " + response + " " + function
                            + " " + msgFileSystem.getFileNameFromMessage(msg));
                }
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
    }

    public static void main(String args[]) throws PeerGroupException,
            IOException, InterruptedException {

        ClientFileSystem c = new ClientFileSystem();

        String sender = "1";
        String receiver = "-1";
        String fileName1 = "jaca.txt";
        String fileName2 = "teste.txt";

        Message msg1 = new Message();
        Message msg2 = new Message();

        MsgFileSystem.createMessageCentralNodeFileSystem(msg1, sender, receiver,
                PipeMensageUtilites.create, fileName1, "");

        MsgFileSystem.createMessageCentralNodeFileSystem(msg2, sender, receiver,
                PipeMensageUtilites.create, fileName2, "");

        c.sendMessageForServerFileSystem(msg1);

        //Thread.sleep(5000);
        c.sendMessageForServerFileSystem(msg2);

        while (true);

    }
}
