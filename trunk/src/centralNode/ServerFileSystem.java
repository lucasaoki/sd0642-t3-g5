package centralNode;

import java.io.File;
import java.io.IOException;

import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageFilterListener;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.JxtaBiDiPipe;
import net.jxta.util.JxtaServerPipe;
import utilitesFileSystem.FileManager;
import utilitesFileSystem.MsgFileSystem;
import utilitesFileSystem.PeerGroupID;
import utilitesFileSystem.PipeMensageUtilites;

public class ServerFileSystem implements PeerGroupID, Runnable {

	private final File home;
	private NetworkManager manager;
	private PeerGroup netPeerGroup;
	private JxtaServerPipe serverPipe;
	private PipeAdvertisement serverPipeAdv;

	private FileManager fileManager;
	private MsgFileSystem mensageFileSystem;
	private JxtaBiDiPipe bipipe[];

	private boolean executed;
	private int numNodes;

	ServerFileSystem() throws IOException, PeerGroupException {

		executed = true;
		numNodes = 0;

		fileManager = new FileManager();
		mensageFileSystem = new MsgFileSystem();

		home = new File(new File(".cache"), "server");

		manager = new NetworkManager(NetworkManager.ConfigMode.ADHOC, "Server",
				home.toURI());
		manager.startNetwork();

		netPeerGroup = manager.getNetPeerGroup();

		serverPipeAdv = ServerFileSystem.getPipeAdvertisement();
		serverPipe = new JxtaServerPipe(netPeerGroup, serverPipeAdv);

		// Thread t = new Thread(this);
		// t.start();
	}

	public static PipeAdvertisement getPipeAdvertisement() {

		PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory
				.newAdvertisement(PipeAdvertisement.getAdvertisementType());

		advertisement.setPipeID(PeerGroupID.serverID);
		advertisement.setType(PipeService.UnicastType);
		advertisement.setName("Server FileSystem");

		return advertisement;
	}

	// necessário para analisar as conexões que vão chegar
	public void run() {

		bipipe = new JxtaBiDiPipe[FileManager.NUM_MAX_NODE];

		while (executed && numNodes < FileManager.NUM_MAX_NODE) {
			try {
				bipipe[numNodes] = serverPipe.accept();
				if (bipipe[numNodes] != null) {
					// Thread t = new Thread(new ConnectionManager(bipipe));
					// t.start();
					numNodes++;
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public class ConnectionManager implements Runnable, PipeMsgListener {

		public ConnectionManager(JxtaBiDiPipe pipe) {
			this.pipe = pipe;
			pipe.setMessageListener(this);
			execution = true;
			mensageArrive = false;
		}

		@Override
		public void pipeMsgEvent(PipeMsgEvent event) {
			// TODO Auto-generated method stub
			Message msg = event.getMessage();

			function = mensageFileSystem.functionFromMessage(msg);
			if (function >= 0) {

				sender = mensageFileSystem.getSenderFromMessage(msg);
				receiver = mensageFileSystem.getReceiverFromMessage(msg);
				fileName = mensageFileSystem.getFileNameFromMessage(msg);

				mensageArrive = true;
			}

			switch (function) {
			case 0:

				status = fileManager.InsertFileNode(sender,
						mensageFileSystem.getFileNameFromMessage(msg));
				break;

			case 1:

				status = fileManager.RemoveFileNode(sender,
						mensageFileSystem.getFileNameFromMessage(msg));
				break;

			case 2:

				status = fileManager.MoveFileBetweenNodes(receiver, sender,
						mensageFileSystem.getFileNameFromMessage(msg));
				break;

			case 3:

				where = fileManager.FileNodePosition(mensageFileSystem
						.getFileNameFromMessage(msg));

				break;
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while (execution) {
					if (mensageArrive) {
						sendMessage();
					} else {
						Thread.sleep(3000);
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		void sendMessage() throws IOException {
			Message msg = new Message();
			Message openFile = new Message();

			String response;
			switch (function) {
			case 0:

				if (status)
					response = "file inserted";
				else
					response = "file exist";

				MsgFileSystem.createMessageCentralNodeFileSystem(msg,
						Integer.toString(-1), Integer.toString(sender),
						Integer.toString(function), fileName, response);
				break;

			case 1:

				if (status)
					response = "file removed";
				else
					response = "file doesn't exist";

				MsgFileSystem.createMessageCentralNodeFileSystem(msg,
						Integer.toString(-1), Integer.toString(sender),
						Integer.toString(function), fileName, response);

				break;

			case 2:

				MsgFileSystem.createMessageCentralNodeFileSystem(msg,
						Integer.toString(-1), Integer.toString(sender),
						Integer.toString(function), fileName,
						Integer.toString(receiver));

				break;

			case 3:

				MsgFileSystem.createMessageCentralNodeFileSystem(msg,
						Integer.toString(-1), Integer.toString(sender),
						Integer.toString(function), fileName,
						Integer.toString(where));

				break;

			case 4:

				MsgFileSystem.createMessageCentralNodeFileSystem(msg,
						Integer.toString(-1), Integer.toString(sender),
						PipeMensageUtilites.read, fileName,
						Integer.toString(receiver));

				MsgFileSystem.createMessageCentralNodeFileSystem(openFile,
						Integer.toString(-1), Integer.toString(receiver),
						PipeMensageUtilites.open, fileName,
						Integer.toString(sender));

				doConnection(msg, sender);
				openConnection(msg, receiver);

				break;

			case 5:

				MsgFileSystem.createMessageCentralNodeFileSystem(msg,
						Integer.toString(-1), Integer.toString(sender),
						PipeMensageUtilites.read, fileName,
						Integer.toString(receiver));

				MsgFileSystem.createMessageCentralNodeFileSystem(openFile,
						Integer.toString(-1), Integer.toString(receiver),
						PipeMensageUtilites.open, fileName,
						Integer.toString(sender));

				doConnection(msg, sender);
				openConnection(msg, receiver);
				break;
			}

			pipe.sendMessage(msg);
			mensageArrive = false;
		}

		private boolean execution;
		private boolean mensageArrive;
		private JxtaBiDiPipe pipe;

		private int sender;
		private int receiver;
		private int where;
		private int function;
		private String fileName;
		private boolean status;
	}

	public void sendBroadCasting(Message msg, int myself) throws IOException {

		int i = 0;
		for (; i < numNodes; i++) {
			bipipe[i].sendMessage(msg);
		}
	}

	public void openConnection(Message msg, int whoNeedOpen) throws IOException {
		bipipe[whoNeedOpen].sendMessage(msg);
	}

	public void doConnection(Message msg, int whoNeedOpen) throws IOException {
		bipipe[whoNeedOpen].sendMessage(msg);
	}

	public static void main(String[] args) throws PeerGroupException,
			IOException {
		// TODO Auto-generated method stub
		ServerFileSystem s = new ServerFileSystem();
	}

}
