package neighbourhoodNode;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import advertisementFileFactory.FileAdvertisement;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.Message;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.JxtaBiDiPipe;
import utilitesFileSystem.FileManager;
import utilitesFileSystem.MsgFileSystem;
import utilitesFileSystem.PipeMensageUtilites;
import FileLib.Interface;

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
	private DiscoveryService disco;

	public ClientFileSystem() throws IOException, PeerGroupException {

		AdvertisementFactory.registerAdvertisementInstance(
				FileAdvertisement.getAdvertisementType(),
				new FileAdvertisement.Instantiator());

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
		disco = peerGroup.getDiscoveryService();



		// centralNode = ServerFileSystem.getPipeAdvertisement();
		// pipe = new JxtaBiDiPipe(peerGroup, centralNode, 20000, this, true);
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
				face.get_data('c', msgFileSystem.getFileNameFromMessage(msg),
						null);
				System.out.println("File create: "
						+ msgFileSystem.getFileNameFromMessage(msg));
			} else {
				System.out.println("File doesn't create");
			}
			break;
		case 1:
			response = msgFileSystem.getResponseFromMessage(msg);
			if (response.equals(PipeMensageUtilites.okRemove)) {
				// deletou o arquivo
				face.get_data('d', msgFileSystem.getFileNameFromMessage(msg),
						null);
				System.out.println("File delete: "
						+ msgFileSystem.getFileNameFromMessage(msg));
			} else {
				// informa que nao deletou o arquivo
				System.out.println("File doesn't delete: "
						+ msgFileSystem.getFileNameFromMessage(msg));
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
		while (true) {
			face.menu();

		}
	}

	public class localDiscoveryListener implements DiscoveryListener {
		@Override
		synchronized public void discoveryEvent(DiscoveryEvent event) {
			// TODO Auto-generated method stub
			DiscoveryResponseMsg respMsg = event.getResponse();

			Advertisement adv;
			Enumeration en = respMsg.getAdvertisements();

			if (en != null) {
				while (en.hasMoreElements()) {
					adv = (Advertisement) en.nextElement();
					System.out.println(adv);
				}
			}

		}

	}
	

	public void start() throws InterruptedException {
		long waittime = 60 * 100L;

		disco.addDiscoveryListener(new localDiscoveryListener());
		disco.getRemoteAdvertisements(null, DiscoveryService.ADV, null, null,
				1, null);

		while (true) {

			Thread.sleep(waittime);
			disco.getRemoteAdvertisements(null, DiscoveryService.ADV, "Name",
					"Server FileSystem", 1, null);
//			 while(true);
		}
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

		MsgFileSystem.createMessageCentralNodeFileSystem(msg1, sender,
				receiver, PipeMensageUtilites.create, fileName1, "");

		MsgFileSystem.createMessageCentralNodeFileSystem(msg2, sender,
				receiver, PipeMensageUtilites.create, fileName2, "");

		c.start();
		// c.sendMessageForServerFileSystem(msg1);
		// c.sendMessageForServerFileSystem(msg2);

		// Thread t = new Thread(c);
		// t.start();
	}
}
