package FileLib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * 
 * @author seiji
 */
public class FileAssist {

	FileInputStream fin; // Stream to read stream
	FileOutputStream fout; // Stream to write stream
	File file;
	private String name_file;
	String strFileContent;

	public static void main(String[] args) {

		FileAssist a = new FileAssist();
		String name = "teste.txt";
		a.create(name);
		a.update(name, "hahaiud");
		System.out.println(a.read(name));
		a.delete(name);
	}

	public String get_id_input() {
		return this.name_file;
	}

	public void set_id_input(String name) {
		this.name_file = name;
	}

	public String read(String name) {

		byte fileContent[] = new byte[(int) file.length()];
		try {
			set_id_input(name);
			fin = new FileInputStream(file);
			fin.read(fileContent);

			strFileContent = new String(fileContent);
			fin.close();
		} catch (IOException e) {
			System.err.print("Error - Unable to write to file");
			System.exit(-1);
		}
		return strFileContent;
	}

	public byte[] getByteFromFile(String fileName) {

		byte fileContent[] = null;

		File file = new File(fileName);
		try {

			FileInputStream fin = new FileInputStream(file);
			fileContent = new byte[fin.available()];

			fin.read(fileContent);

			fin.close();
		} catch (IOException e) {
			System.err.print("Error - Unable to write to file");
			System.exit(-1);
		}

		return fileContent;
	}

	public int update(String name) {
		try {
			set_id_input(name);
			fout = new FileOutputStream(file, true);

			Scanner scan = new Scanner(System.in);
			fout.write(scan.nextByte()); // OBS o usuario de ve escrever algo...
			fout.close();
		} catch (IOException e) {
			System.err.print("Error - Unable to write to file");
			System.exit(-1);
		}
		return 0;
	}

	public int update(String name, InputStream in) {

		File file = new File(name);
		if (file.exists()) {
			try {
				
				byte data[] = new byte[in.available()];
				in.read(data);
				String str = new String(data);
				
				return update(name, str);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return 1;
		}
		return 0;

	}

	public int update(String name, String str) {
		try {
//			set_id_input(name);
			file = new File(name);
			if (file.exists()) {
				fout = new FileOutputStream(name, true);
				fout.write(str.getBytes());
				fout.close();
				return 1;
			} else {
				return 0;
			}
		} catch (IOException e) {
			System.err.print("Error - Unable to write to file");
			System.exit(-1);
		}
		return 0;
	}

	public int delete(String name) {
		set_id_input(name);
		file = new File(name);
		if (file.exists()) {
			file.delete();
			return 1;
		} else {
			return 0;
		}
	}

	public int create(String name, String str) {
		try {
			set_id_input(name);
			file = new File(name_file);
			file.createNewFile();
			update(name, str);
		} catch (IOException e) {
			System.err.print("Error - Unable to write to file");
			System.exit(-1);
		}
		return 0;
	}

	public int create(String name) {
		try {
			set_id_input(name);
			file = new File(name_file);
			file.createNewFile();
//			update(name, " ");
			
		} catch (IOException e) {
			System.err.print("Error - Unable to write to file");
			System.exit(-1);
		}
		return 0;
	}

	public void read_file(String temp) {
		System.out.println(temp);
	}

}
