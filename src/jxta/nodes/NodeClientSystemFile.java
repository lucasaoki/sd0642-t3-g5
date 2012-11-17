package jxta.nodes;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;

public class NodeClientSystemFile {

	public static final int NUM_NODES = 1;

	private NetworkManager manager;
	private PeerGroup peerGroup;
	private PipeAdvertisement myPipeAdv[];
	private PipeAdvertisement pipeCentralAdv;
	private PipeAdvertisement anotherPipeAdv[];
	private DiscoveryService discovery;
	private PipeService pipeService;
	private InputPipe input[];
	private OutputPipe output[];
	private int nodeName;

	public NodeClientSystemFile(int nodeName) {

		this.nodeName = nodeName;
		input = new InputPipe[NUM_NODES];
		output = new OutputPipe[NUM_NODES];
		myPipeAdv = new PipeAdvertisement[NUM_NODES];
		anotherPipeAdv = new PipeAdvertisement[NUM_NODES];

		for (int i = 0; i < NUM_NODES; i++) {
			input[i] = null;
			output[i] = null;
			myPipeAdv[i] = null;
			anotherPipeAdv[i] = null;
		}

		try {
			manager = new NetworkManager(NetworkManager.ConfigMode.RENDEZVOUS,
					Integer.toString(nodeName), new File(new File(".cache"),
							Integer.toString(nodeName)).toURI());

			manager.startNetwork();

			peerGroup = manager.getNetPeerGroup();
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

	private PipeAdvertisement getPipeAdvertisement(String nodeName) {

		PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory
				.newAdvertisement(PipeAdvertisement.getAdvertisementType());

		advertisement.setPipeID(IDFactory
				.newPipeID(PeerGroupID.defaultNetPeerGroupID));
		advertisement.setType(PipeService.UnicastType);
		advertisement.setName(nodeName);

		return advertisement;
	}

	class MsgListenerNodes implements PipeMsgListener {

		@Override
		public void pipeMsgEvent(PipeMsgEvent arg0) {
			// TODO Auto-generated method stub

		}
	}

	class MsgListenerServer implements PipeMsgListener {

		@Override
		public void pipeMsgEvent(PipeMsgEvent arg0) {
			// TODO Auto-generated method stub

		}
	}

	class DiscoveryAdvertisementListener implements DiscoveryListener {

		@Override
		public void discoveryEvent(DiscoveryEvent event) {
			// TODO Auto-generated method stub
			DiscoveryResponseMsg res = event.getResponse();

			Enumeration<Advertisement> e = res.getAdvertisements();
			// System.out.println(e.toString());

			while (e.hasMoreElements()) {
				try {
					// System.out.println("Aqui "); //entrando aqui o bixo esta
					PipeAdvertisement pipeAdv = (PipeAdvertisement) e
							.nextElement();

					String name = pipeAdv.getName();
					System.out.println(name);

				} catch (ClassCastException cce) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}

			}
		}
	}

	class DiscoveryAdvertisementRunnable implements Runnable {

		// public DiscoveryAdvertisementRunnable(DiscoveryService discovery) {
		// // TODO Auto-generated constructor stub
		// this.discovery = discovery;
		// }

		@Override
		public void run() {
			long waittime = 60 * 100L;
			discovery
					.addDiscoveryListener(new DiscoveryAdvertisementListener());
			// TODO Auto-generated method stub
			while (exec) {
				System.out.println("Descobrindo PipeAdvetisement");
				discovery.getRemoteAdvertisements(null, DiscoveryService.ADV,
						"Name", "_"+Integer.toString(nodeName), 10, null);
				try {
					Thread.sleep(waittime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private boolean exec = true;
		// private DiscoveryService discovery;
	}

	class PublishAdvertisement implements Runnable {

		// public PublishAdvertisement(DiscoveryService discovery, int num,
		// PipeAdvertisement myPipeAdv[]) {
		// this.discovery = discovery;
		// this.NUM_NODES = num;
		// this.myPipeAdv = myPipeAdv;
		// }

		@Override
		public void run() {
			long lifetime = 60 * 2 * 1000L;
			long waittime = 60 * 100L;
			long expiration = 60 * 2 * 1000L;
			// TODO Auto-generated method stub
			while (exec) {
				try {
					System.out.println("Informado PipeAdvetisement");
					for (int i = 0; i < NUM_NODES; i++) {
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

		//
		private boolean exec = true;
	}

	public void start() {
		initiliazeInputPipe();

		Thread t1 = new Thread(new DiscoveryAdvertisementRunnable());
		Thread t2 = new Thread(new PublishAdvertisement());

		t1.start();
		t2.start();
	}

	public static void main(String args[]) {
		NodeClientSystemFile nc = new NodeClientSystemFile(1);
		nc.start();

		while (true)
			;
	}
}
