package jxta.nodes;

import java.io.File;
import java.io.IOException;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.impl.shell.bin.remotepublish.remotepublish;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.JxtaBiDiPipe;
import net.jxta.util.JxtaServerPipe;

public class NodeServerFileSystem implements UtilitesNodes {

	private final String name = "Server";
	private NetworkManager manager;
	private PeerGroup peerGroup;
	private DiscoveryService discovery;
	private PipeAdvertisement pipeNeighborhoodAdv[];
	private JxtaBiDiPipe pipeToNeighborhood[];

	/**
	 * Cria um Advertisemente para informar da existencia desse nó. Necessário
	 * para que outros clientes se comunique com o Servidor.
	 * **/
	private PipeAdvertisement getPipeAdvertisement(int node) {

		PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory
				.newAdvertisement(PipeAdvertisement.getAdvertisementType());

		advertisement.setPipeID(IDFactory
				.newPipeID(PeerGroupID.defaultNetPeerGroupID));
		advertisement.setType(PipeService.UnicastType);
		advertisement.setName(name + "_" + Integer.toString(node));

		return advertisement;
	}

	public NodeServerFileSystem() {
		try {

			pipeToNeighborhood = new JxtaBiDiPipe[NUM_NODES];
			pipeNeighborhoodAdv = new PipeAdvertisement[NUM_NODES];

			for (int i = 0; i < NUM_NODES; i++) {
				pipeToNeighborhood[i] = null;
				pipeNeighborhoodAdv[i] = null;
			}

			manager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
					"Server FileSystem",
					new File(new File(".cache"), "Server").toURI());

			// Permite q ele veja a própria rede
			NetworkConfigurator config = manager.getConfigurator();
			config.setUseMulticast(true);

			manager.startNetwork();

			peerGroup = manager.getNetPeerGroup();
			// não deixa o nó virar Rendezvous
			peerGroup.getRendezVousService().setAutoStart(false);
			discovery = peerGroup.getDiscoveryService();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PeerGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Responsavel pelo inicio da cadeia produtiva no 
	 * Server.
	 * */
	public void start() throws InterruptedException {
		InitializeBiDiPipe();

		for (int i = 0; i < NUM_NODES; i++) {
			Thread t = new Thread(new ConnectionToClient(i));
			t.start();
		}

		Thread t = new Thread(new PublishAdvertisement());
		t.start();
	}

	/*
	 * Inicializa os Advertisement
	 * para os nós clientes saberem da existência 
	 * do nó central.
	 * */	
	private void InitializeBiDiPipe() {
		for (int i = 0; i < NUM_NODES; i++) {
			pipeNeighborhoodAdv[i] = getPipeAdvertisement(i);
		}
	}

	/*
	 * Classe Responsavel pela 
	 * conexão entre o servidor e o Cliente
	 * */
	class ConnectionToClient implements Runnable {

		private int index;
		private JxtaServerPipe serverPipe;

		public ConnectionToClient(int index) {
			try {
				this.index = index;
				serverPipe = new JxtaServerPipe(peerGroup,
						pipeNeighborhoodAdv[index]);
				serverPipe.setPipeTimeout(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				pipeToNeighborhood[index] = serverPipe.accept();
				Thread t = new Thread(new ConnectionHandler(
						pipeToNeighborhood[index]));
				t.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Classe Responsavel pela
	 * comunicação entre servidor e cliente
	 * */
	class ConnectionHandler implements PipeMsgListener, Runnable {

		private JxtaBiDiPipe pipe;
		private boolean executed;

		public ConnectionHandler(JxtaBiDiPipe pipe) {
			this.pipe = pipe;
			pipe.setMessageListener(this);
			executed = true;
		}

		@Override
		synchronized public void pipeMsgEvent(PipeMsgEvent event) {
			// TODO Auto-generated method stub

		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (executed) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * Classe Responsavel pela publicação
	 * dos advertisement referentes ao no central.
	 * */
	class PublishAdvertisement implements Runnable {

		@Override
		public void run() {

			long lifetime = 60 * 2 * 1000L;
			long waittime = 60 * 3 * 1000L;
			long expiration = 60 * 2 * 1000L;

			while (true) {
				try {
					for (int i = 0; i < NUM_NODES; i++) {
						System.out.println("Divulgando Advertisement");
						discovery.publish(pipeNeighborhoodAdv[i], lifetime,
								expiration);
						discovery.remotePublish(pipeNeighborhoodAdv[i],
								expiration);
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

	}

	public static void main(String args[]) throws InterruptedException {
		NodeServerFileSystem ns = new NodeServerFileSystem();
		ns.start();

		while (true)
			;
	}
}
