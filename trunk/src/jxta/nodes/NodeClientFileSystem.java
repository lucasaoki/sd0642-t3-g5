package jxta.nodes;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import utilitesFileSystem.MsgFileSystem;
import utilitesFileSystem.PipeMensageUtilites;
import utilitesFileSystem.UtilitesMsgFileSystem;

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
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.JxtaBiDiPipe;
import net.jxta.util.PipeUtilities;

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

	public NodeClientFileSystem(int nodeName) {

		msgFileSystem = new MsgFileSystem();

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
	 * para que outros pipe se comuniquem
	 * **/
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
			// TODO Auto-generated method stub
			Message msg = event.getMessage();

			// as únicas funcoes que os clientes trocam, é ler
			// de um outro arquivo e atualizar o 
			int function = msgFileSystem.functionFromMessage(msg);
			
			switch(function){
			case READ_FILE:
				break;	
				
			case WRITE_FILE:
				break;
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
			String response = null;

			int function = msgFileSystem.functionFromMessage(msg);

			switch (function) {
			case CREATE_MSG:
				response = msgFileSystem.getResponseFromMessage(msg);
				if (response.equals(PipeMensageUtilites.okCreate)) {

				} else {

				}

				break;

			case DELETE_MSG:
				response = msgFileSystem.getResponseFromMessage(msg);
				if (response.equals(PipeMensageUtilites.okRemove)) {

				} else {

				}
				break;

			case READ_MSG:
				response = msgFileSystem.getResponseFromMessage(msg);
				// vai receber e reenviar a mensagem para o no correto
				// esta no campo sender?
				break;

			case WRITE_MSG:
				response = msgFileSystem.getResponseFromMessage(msg);
				// Para quem tem q enviar o e atualizar o
				// arquivo
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

	public static void main(String args[]) {
		NodeClientFileSystem nc = new NodeClientFileSystem(0);
		nc.start();

		while (true)
			;
	}

}