package jxta.nodes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.Message;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.OutputPipeEvent;
import net.jxta.pipe.OutputPipeListener;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.JxtaBiDiPipe;
import utilitesFileSystem.MsgFileSystem;
import utilitesFileSystem.PipeMensageUtilites;
import utilitesFileSystem.UtilitesMsgFileSystem;
import FileLib.FileAssist;
import FileLib.Interface;

public class NodeClientFileSystem implements DiscoveryListener, UtilitesNodes,
		UtilitesMsgFileSystem {

	private NetworkManager manager;
	private PeerGroup peerGroup;

	private PipeAdvertisement myPipeAdv[];
	private PipeAdvertisement pipeCentralAdv;
	private PipeAdvertisement anotherPipeAdv[];

	private DiscoveryService discovery;
	private PipeService pipeService;
	private JxtaBiDiPipe pipeToServer;

	private InputPipe input[];
	private OutputPipe output[];

	private int nodeName;
	private String delimenters = "[_]";

	private MsgFileSystem msgFileSystem;
	private Interface fileInterface;
	private FileAssist fileAssist;
	
	public NodeClientFileSystem(int nodeName) {

		Logger.getLogger("net.jxta").setLevel(Level.SEVERE);

		msgFileSystem = new MsgFileSystem();
		fileInterface = new Interface();
		fileAssist = new FileAssist();
		
		this.nodeName = nodeName;
		input = new InputPipe[NUM_NODES];
		output = new OutputPipe[NUM_NODES];
		myPipeAdv = new PipeAdvertisement[NUM_NODES];
		anotherPipeAdv = new PipeAdvertisement[NUM_NODES];
		pipeCentralAdv = null;
		pipeToServer = null;

		for (int i = 0; i < NUM_NODES; i++) {
			input[i] = null;
			output[i] = null;
			myPipeAdv[i] = null;
			anotherPipeAdv[i] = null;
		}

		try {

			manager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
					Integer.toString(nodeName), new File(new File(".cache"),
							Integer.toString(nodeName)).toURI());

			NetworkConfigurator config = manager.getConfigurator();
			config.setUseMulticast(true);

			manager.startNetwork();

			peerGroup = manager.getNetPeerGroup();
			peerGroup.getRendezVousService().setAutoStart(false);

			discovery = peerGroup.getDiscoveryService();
			pipeService = peerGroup.getPipeService();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PeerGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * Inicializa os advertisement para fazer comunicação com demais nós
	 * juntamente com o inputPipe para receber comunicação externa
	 */
	public void initiliazeInputPipe() {
		for (int i = 0; i < NUM_NODES; i++) {
			String str = Integer.toString(nodeName) + "_" + Integer.toString(i);

			myPipeAdv[i] = getPipeAdvertisement(str);
			System.out.println(myPipeAdv[i].toString());
			try {
				input[i] = pipeService.createInputPipe(myPipeAdv[i],
						new MsgListenerNodes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Cria um Advertisemente para informar da existencia desse nó. Necessário
	 * para que outros pipe se comuniquem *
	 */
	private PipeAdvertisement getPipeAdvertisement(String nodeName) {

		PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory
				.newAdvertisement(PipeAdvertisement.getAdvertisementType());

		advertisement.setPipeID(IDFactory
				.newPipeID(PeerGroupID.defaultNetPeerGroupID));
		advertisement.setType(PipeService.UnicastType);
		advertisement.setName(nodeName);

		return advertisement;
	}

	/*
	 * Responsavel pela comunicação com os demais nó quando uma mensagem chega a
	 * ele, a mesma é atendida através dessa classe
	 */
	class MsgListenerNodes implements PipeMsgListener {

		@Override
		synchronized public void pipeMsgEvent(PipeMsgEvent event) {
			InputStream is = null;
			try {
				// TODO Auto-generated method stub
				Message msg = event.getMessage();

				int function = msgFileSystem.functionFromMessage(msg);
				switch (function) {
				case READ_FILE:
					
					int count = 0;
					is = msgFileSystem.getInputStreamFromMessage(msg);
					while (is.read() != -1) {
						count++; // qtde de bytes lidos. Não sei se vai
									// precisar, talvez se precisar "debugar"
					}
					
					System.out.println(is.toString());
					
					break;
				case WRITE_FILE:

					break;
				}
			} catch (IOException ex) {
				Logger.getLogger(NodeClientFileSystem.class.getName()).log(
						Level.SEVERE, null, ex);
			} finally {
				try {
					is.close();
				} catch (IOException ex) {
					Logger.getLogger(NodeClientFileSystem.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}

		}
	}

	/*
	 * Responsavel pela comunicação com o nó Servidor quando uma mensagem chega
	 * a ele, a mesma é atendida através dessa classe
	 */
	class MsgListenerServer implements PipeMsgListener {

		@Override
		synchronized public void pipeMsgEvent(PipeMsgEvent event) {
			// TODO Auto-generated method stub
			Message msg = event.getMessage();
			Message message = new Message();
			
			String response = null;
			String fileName = null;
			int node;

			int function = msgFileSystem.functionFromMessage(msg);

			switch (function) {
			case CREATE_MSG:
				response = msgFileSystem.getResponseFromMessage(msg);
				if (response.equals(PipeMensageUtilites.okCreate)) {
					// Create file
					fileInterface.get_data('c',
							msgFileSystem.getFileNameFromMessage(msg), null);
					System.out.println("Create the file: "
							+ msgFileSystem.getFileNameFromMessage(msg));
				} else {
					// Nao cria o arquivo
					System.out.println("Can not create the file: "
							+ msgFileSystem.getFileNameFromMessage(msg));
				}

				break;

			case DELETE_MSG:
				response = msgFileSystem.getResponseFromMessage(msg);
				if (response.equals(PipeMensageUtilites.okRemove)) {
					// deleta o arquivo
					fileInterface.get_data('d',
							msgFileSystem.getFileNameFromMessage(msg), null);
					System.out.println("Delete the file: "
							+ msgFileSystem.getFileNameFromMessage(msg));
				} else {
					System.out.println("Can not delete the file: "
							+ msgFileSystem.getFileNameFromMessage(msg));
				}
				break;

			case READ_MSG:

				response = msgFileSystem.getResponseFromMessage(msg);
				node = msgFileSystem.getSenderFromMessage(msg);
				String f = msgFileSystem.getFileNameFromMessage(msg);
				
				try {
					output[node].send(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			case WRITE_MSG:
				response = msgFileSystem.getResponseFromMessage(msg);
				fileName = msgFileSystem.getFileNameFromMessage(msg);
				
				try {
					output[Integer.parseInt(response)].send(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
	}

	/*
	 * Metodo Responsavel pela descoberta do servidor e dos outros nós Todo
	 * Advertsement que chega do tipo Pipe ele tenta processar
	 */
	@Override
	synchronized public void discoveryEvent(DiscoveryEvent event) {
		// TODO Auto-generated method stub
		DiscoveryResponseMsg res = event.getResponse();

		Enumeration<Advertisement> e = res.getAdvertisements();

		while (e.hasMoreElements()) {
			try {
				// System.out.println("Aqui " + e.getClass().getName());
				PipeAdvertisement pipeAdv = (PipeAdvertisement) e.nextElement();

				String name = pipeAdv.getName();
				String[] tokens = name.split(delimenters);

				if (!tokens[0].equals("Server")) {
					int firstDigit = Integer.parseInt(tokens[0]);
					int secondDigit = Integer.parseInt(tokens[1]);

					if (firstDigit != nodeName) {
						if (secondDigit == nodeName) {
							if (anotherPipeAdv[secondDigit] == null) {
								anotherPipeAdv[secondDigit] = pipeAdv;
								try {
									pipeService.createOutputPipe(pipeAdv,
											new OutputCreater(secondDigit));
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
					}
				} else {

					int secondDigit = Integer.parseInt(tokens[1]);
					if ((secondDigit == nodeName) && (pipeCentralAdv == null)) {
						pipeCentralAdv = pipeAdv;
						try {
							pipeToServer = new JxtaBiDiPipe(peerGroup,
									pipeCentralAdv, time_connection,
									new MsgListenerServer(), true);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			} catch (ClassCastException cce) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
	}

	class OutputCreater implements OutputPipeListener {

		public OutputCreater(int node) {
			this.node = node;
		}

		@Override
		public void outputPipeEvent(OutputPipeEvent event) {
			// TODO Auto-generated method stub
			output[node] = event.getOutputPipe();
		}

		private int node;
	}

	public void sendMessageForServerFileSystem(Message msg) throws IOException {
		pipeToServer.sendMessage(msg);
	}

	/*
	 * Classe Responsavel por ficar ouvidno a rede a procura de novos
	 * Advertisement
	 */
	class DiscoveryAdvertisementRunnable implements Runnable {

		public DiscoveryAdvertisementRunnable(DiscoveryListener listener) {
			// TODO Auto-generated constructor stub
			this.listener = listener;
		}

		@Override
		public void run() {
			long waittime = 60 * 100L;
			discovery.addDiscoveryListener(listener);

			while (exec) {
				System.out.println("Descobrindo PipeAdvetisement");
				discovery.getRemoteAdvertisements(null, DiscoveryService.ADV,
						"Name", null, NUM_NODES * 10, null);
				try {
					Thread.sleep(waittime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private boolean exec = true;
		private DiscoveryListener listener;
	}

	/*
	 * Classe Responsavel por ficar enviando dados na rede sobre os
	 * Advertisement pertencentes a esse nó
	 */
	class PublishAdvertisement implements Runnable {

		@Override
		public void run() {
			long lifetime = 60 * 2 * 1000L;
			long waittime = 60 * 3 * 1000L;
			long expiration = 60 * 2 * 1000L;

			// TODO Auto-generated method stub
			while (exec) {
				try {
					System.out.println("Informado PipeAdvetisement");
					for (int i = 0; i < NUM_NODES; i++) {
						// System.out.println(myPipeAdv[i].toString());
						discovery.publish(myPipeAdv[i], lifetime, expiration);
						discovery.remotePublish(myPipeAdv[i], expiration);
					}
					Thread.sleep(waittime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private boolean exec = true;
	}

	/*
	 * Começa toda a cadeia produtiva dos nos
	 */
	public void start() {
		initiliazeInputPipe();

		Thread t1 = new Thread(new DiscoveryAdvertisementRunnable(this));
		Thread t2 = new Thread(new PublishAdvertisement());
		t1.start();
		t2.start();
	}

	public static void main(String args[]) throws IOException,
			InterruptedException {
		NodeClientFileSystem nc = new NodeClientFileSystem(0);
		nc.start();

		Thread.sleep(10000);

		String sender = "0";
		String receiver = "-1";
		String fileName1 = "jaca.txt";

		Message msg1 = new Message();
		Message msg2 = new Message();

		MsgFileSystem.createMessageCentralNodeFileSystem(msg1, sender,
				receiver, PipeMensageUtilites.create, fileName1, "");

		MsgFileSystem.createMessageCentralNodeFileSystem(msg2, sender,
				receiver, PipeMensageUtilites.delete, fileName1, "");

		nc.sendMessageForServerFileSystem(msg1);
		Thread.sleep(2000);
		nc.sendMessageForServerFileSystem(msg2);
		while (true)
			;
	}
}
