package jxta.nodes;

import java.io.File;
import java.io.IOException;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;

public class NodeServerFileSystem {

	private final int NUM_NODES = 5;
	private NetworkManager manager;
	private PeerGroup peerGroup;
	private DiscoveryService discovery;

	private PipeAdvertisement getPipeAdvertisement() {

		PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory
				.newAdvertisement(PipeAdvertisement.getAdvertisementType());

		advertisement.setPipeID(IDFactory
				.newPipeID(PeerGroupID.defaultNetPeerGroupID));
		advertisement.setType(PipeService.UnicastType);
		advertisement.setName("Server");

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

		PipeAdvertisement pipeAdv = getPipeAdvertisement();
		while (true) {
			try {
				discovery.publish(pipeAdv, lifetime, expiration);
				discovery.remotePublish(pipeAdv, expiration);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			discovery.remotePublish(pipeAdv, expiration);
			Thread.sleep(waittime);
		}
	}

	public static void main(String args[]) throws InterruptedException {
		NodeServerFileSystem ns = new NodeServerFileSystem();

		ns.start();
	}
}
