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
public class FileAssist {

    FileInputStream fin;    //Stream to read stream
    FileOutputStream fout;    //Stream to write stream
    File file;
    private String name_file;

    public static void main(String[] args) {
        
        FileAssist a = new FileAssist();
        String name = "teste.txt";
        a.delete(name);
    }

    public String get_id_input() {
        return this.file;
    }

    public void set_id_input(String name) {
        this.name_file = name;
    }

    public void read(String name) {
        int content;
        try {
            byte fileContent[] = new byte[(int) file.length()];
            set_id_input(name);
            fin = new FileInputStream(file);
            fin.read(fileContent);
            String strFileContent = new String(fileContent);
            System.out.println(strFileContent);
            fin.close();
        } catch (IOException e) {
            System.err.print("Error - Unable to write to file");
            System.exit(-1);
        }
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

    public int update(String name, String str) {
        try {
            set_id_input(name);
            fout = new FileOutputStream(file, true);
            fout.write(str.getBytes());
            fout.close();
        } catch (IOException e) {
            System.err.print("Error - Unable to write to file");
            System.exit(-1);
        }
        return 0;
    }

    public int delete(String name) {
        set_id_input(name);
        file = new File(name);
            file.delete();
        return 0;
    }

    public int create(String name, String str) {
        try {
            file = new File(name);
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
            file = new File(name);
            file.createNewFile();
        } catch (IOException e) {
            System.err.print("Error - Unable to write to file");
            System.exit(-1);
        }
        return 0;
    }
}
