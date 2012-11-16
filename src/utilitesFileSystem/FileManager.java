package utilitesFileSystem;

import java.util.Vector;

public class FileManager {

	public final static int NUM_MAX_NODE = 15;
	@SuppressWarnings("rawtypes")
	private Vector[] fileNames;
	private Vector<String> filesInUse;
	
	public FileManager() {

		fileNames = new Vector[NUM_MAX_NODE];
		for (int i = 0; i < NUM_MAX_NODE; i++)
			fileNames[i] = new Vector<String>();

		filesInUse = new Vector<String>();
	}

	public int FileNodePosition(String fileName) {
		int response = -1;

		for (int i = 0; i < NUM_MAX_NODE; i++)
			if (fileNames[i].contains(fileName))
				return i;

		return response;
	}

	@SuppressWarnings("unchecked")
	public boolean InsertFileNode(int node, String fileName) {

		if (node >= 0 && node < NUM_MAX_NODE && FileNodePosition(fileName) < 0) {
			fileNames[node].add(fileName);
			return true;
		}

		return false;
	}

	public boolean RemoveFileNode(int node, String fileName) {

		if (node >= 0 && node < NUM_MAX_NODE 
				&& !FileInUse(fileName) && FileNodePosition(fileName) >= 0 ) {
			fileNames[node].remove(fileName);
			return true;
		}

		return false;
	}

	public boolean insertFileInUse(String fileName){
		return filesInUse.add(fileName);
	}
	
	public boolean removeFileInUse(String fileName){
		return filesInUse.remove(fileName);
	}
	
	private boolean FileInUse(String FileName){
		return filesInUse.contains(FileName);
	}
	
	public boolean MoveFileBetweenNodes(int destiny, int origin, String fileName) {

		return (RemoveFileNode(origin, fileName) && InsertFileNode(destiny,
				fileName));
	}

}
