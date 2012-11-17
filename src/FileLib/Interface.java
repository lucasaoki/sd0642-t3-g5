/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FileLib;

import java.util.Scanner;

/**
 * 
 * @author seiji
 */
public class Interface {

	private Scanner scan;
	private FileAssist file_ = new FileAssist();

	public static void main(String[] args) {
		Interface inter = new Interface();
		inter.get_data('x', "teste.txt", "HHHHHHHHHH");

		// inter.get_data(args[0],args[1],args[2]);
	}

	public void Interface() {
	}

	public void read_str(String str) { // Le o arquivo recebido do outro no.
		System.out.println("/n" + str);
	}

	public void menu() {
		String strStat;
		String name;
		int menu;
		char stat = 'o';

		while ('q' != stat) {
			menu = 0;
			while (menu == 0) {
				System.out.println("Stat");
				scan = new Scanner(System.in);
				strStat = scan.next();
				stat = strStat.charAt(0);

				System.out.println("File name");
				scan = new Scanner(System.in);
				name = scan.next();

				System.out.println("All Correct: yes(1) - no(0)");
				scan = new Scanner(System.in);
				menu = scan.nextInt();
			}
		}
	}

	public void get_data(char stat, String name, String str) {
		switch (stat) {
		case 'u': // update
			file_.update(name, str);
			break;
		case 'd': // delete
			file_.delete(name);
			break;

		case 'm': // move
			// funcao de mover
			// apos move-lo pode-se deletar o arquivo
			file_.delete(name);
			break;
		case 'c': // create
			file_.create(name);
			break;
		case 'r': // read se ele estiver nesse no pode-se printa-lo
			System.out.println(file_.read(name));
			break;
		}
		// }
	}
}