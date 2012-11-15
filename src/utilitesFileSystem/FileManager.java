package utilitesFileSystem;

import java.util.ArrayList;
import java.util.Vector;

public class FileManager {

	public final static int NUM_MAX_NODE = 15;
	private Vector[] fileNames;

	public FileManager() {

		fileNames = new Vector[NUM_MAX_NODE];
		for (int i = 0; i < NUM_MAX_NODE; i++)
			fileNames[i] = new Vector<String>();

	}

	public int FileNodePosition(String fileName) {
		int response = -1;

		for (int i = 0; i < NUM_MAX_NODE; i++)
			if (fileNames[i].contains(fileName))
				return i;

		return response;
	}

	public boolean InsertFileNode(int node, String fileName) {

		if (node >= 0 || node < NUM_MAX_NODE && FileNodePosition(fileName) < 0) {
			fileNames[node].add(fileName);
			return true;
		}

		return false;
	}

	public boolean RemoveFileNode(int node, String fileName) {

		if (node >= 0 || node < NUM_MAX_NODE) {
			fileNames[node].add(fileName);
			return true;
		}

		return false;
	}

	public boolean MoveFileBetweenNodes(int destiny, int origin, String fileName) {

		return (RemoveFileNode(origin, fileName) && InsertFileNode(destiny,
				fileName));
	}

}
