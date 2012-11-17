package jxta.nodes;

import java.io.File;
import java.io.IOException;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
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

	private PipeAdvertisement getPipeAdvertisement(int node) {

		PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory
				.newAdvertisement(PipeAdvertisement.getAdvertisementType());

		advertisement.setPipeID(IDFactory
				.newPipeID(PeerGroupID.defaultNetPeerGroupID));
		advertisement.setType(PipeService.UnicastType);
		advertisement.setName(name+"_"+Integer.toString(node));

		return advertisement;
	}

	public NodeServerFileSystem() {
		try {
						
			manager = new NetworkManager(NetworkManager.ConfigMode.RENDEZVOUS,
					"Server FileSystem",
					new File(new File(".cache"), "Server").toURI());

			manager.startNetwork();

			peerGroup = manager.getNetPeerGroup();
			discovery = peerGroup.getDiscoveryService();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PeerGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() throws InterruptedException {
		long lifetime = 60 * 2 * 1000L;
		long waittime = 60 * 100L;
		long expiration = 60 * 2 * 1000L;

//		PipeAdvertisement pipeAdv = getPipeAdvertisement();
		while (true) {
//			try {
////				discovery.publish(pipeAdv, lifetime, expiration);
////				discovery.remotePublish(pipeAdv, expiration);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			discovery.remotePublish(pipeAdv, expiration);
//			Thread.sleep(waittime);
		}
	}
	
	public void InitializeBiDiPipe(){
		
	}

	class ConnectionToClient{

		public ConnectionToClient(int index){
			try {
				this.index = index;
				serverPipe = new JxtaServerPipe(peerGroup, pipeNeighborhoodAdv[index]);
				serverPipe.setPipeTimeout(0);
				pipeToNeighborhood[index] =  serverPipe.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private int index;
		private JxtaServerPipe serverPipe;
	}
	
	class ConnectionHandler implements PipeMsgListener{
		@Override
		public void pipeMsgEvent(PipeMsgEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public static void main(String args[]) throws InterruptedException {
		NodeServerFileSystem ns = new NodeServerFileSystem();

		ns.start();
	}
}
