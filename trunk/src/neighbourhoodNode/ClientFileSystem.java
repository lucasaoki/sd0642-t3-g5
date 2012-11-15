package neighbourhoodNode;

import java.io.File;

import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.platform.NetworkManager;
import net.jxta.util.JxtaBiDiPipe;
import utilitesFileSystem.FileManager;
import utilitesFileSystem.MsgFileSystem;

public class ClientFileSystem implements PipeMsgListener {

	private final File home;
	private NetworkManager manager;
	private PeerGroup peerGroup;
	private JxtaBiDiPipe bipipe;
	
	private FileManager onlyRead;
	private MsgFileSystem msgFileSystem;
	
	public ClientFileSystem(){
		home = new File(new File(".cache"),"client");
	}
	
	@Override
	public void pipeMsgEvent(PipeMsgEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
