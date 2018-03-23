package envasador;

import java.util.Scanner;

public class Envasador {

	public static void main(String[] args) {
		Connection con = new Connection();
		Scanner sc = new Scanner(System.in);
		boolean parar = false;
		while(!parar) {
			mostrarMenu();
			int num = sc.nextInt();
			switch (num) {
			case 1:
				con.listItems();
				break;
			case 2:
				con.itemBuy();
				break;
			case 3:
				con.itemSell();
				break;			
			case 4:
				con.closeConnection();
				parar = true;
				break;
			case 5:
				con.itemAdd();
				break;
			case 6:
				con.itemDel();
				break;
			}
			
		}
	}

	public static void mostrarMenu() {
		System.out.println("--------------------");
		System.out.println("Escull una opci�:");
		System.out.println("1- Llistar productes");
		System.out.println("2- Comprar");
		System.out.println("3- Vendre");
		System.out.println("4- Close");
		System.out.println("--------------------");
	}
}
