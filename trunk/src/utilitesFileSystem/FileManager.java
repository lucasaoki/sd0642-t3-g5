package utilitesFileSystem;

import java.util.Vector;

import jxta.nodes.UtilitesNodes;

public class FileManager implements UtilitesNodes{

	private Vector[] fileNames;
	private Vector<String> filesInUse;
	
	public FileManager() {

		fileNames = new Vector[NUM_NODES];
		for (int i = 0; i < NUM_NODES; i++)
			fileNames[i] = new Vector<String>();

		filesInUse = new Vector<String>();
	}

	public int FileNodePosition(String fileName) {
		int response = -1;

		for (int i = 0; i < NUM_NODES; i++)
			if (fileNames[i].contains(fileName))
				return i;

		return response;
	}

	@SuppressWarnings("unchecked")
	public boolean InsertFileNode(int node, String fileName) {

		if (node >= 0 && node < NUM_NODES && FileNodePosition(fileName) < 0) {
			fileNames[node].add(fileName);
			return true;
		}

		return false;
	}

	public boolean RemoveFileNode(int node, String fileName) {

		if (node >= 0 && node < NUM_NODES 
				&& !FileInUse(fileName) && FileNodePosition(fileName) >= 0 ) {
			fileNames[node].remove(fileName);
			return true;
		}

		return false;
	}

	public String getAllFileName(){
		
		String result = "";
		
		for(int i=0;i<NUM_NODES;i++){
			for(int j=0;j<fileNames[i].size();j++){
				result += fileNames[i].get(j);
				result += " ";
			}
		}
		
		return result;
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
