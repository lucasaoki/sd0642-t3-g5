package FileLib;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;

/**
 *
 * @author seiji
 */
public class FileAssist  {

    FileInputStream fin;    //Stream to read stream
    FileOutputStream fout;    //Stream to write stream
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

    public byte[] getByte(String fileName){
    	byte fileContent[] = null;
    	
    	return fileContent;
    }
    
    public int update(String name) {
        try {
            set_id_input(name);
            fout = new FileOutputStream(file, true);

            Scanner scan = new Scanner(System.in);
            fout.write(scan.nextByte());                    //OBS o usuario de ve escrever algo...
            fout.close();
        } catch (IOException e) {
            System.err.print("Error - Unable to write to file");
            System.exit(-1);
        }
        return 0;
    }

    public int  update(String name, String str) {
        try {
            set_id_input(name);
            file = new File(name_file);
            if (file.exists()) {
                fout = new FileOutputStream(name_file, true);
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