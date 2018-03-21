package envasador;

import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;

public class Connection {
	XQConnection connection;
	Scanner sc = new Scanner(System.in);

	public Connection() {
		XQDataSource xqs = null;

		try {
			xqs = (XQDataSource) Class.forName("net.xqj.exist.ExistXQDataSource").newInstance();
			//xqs.setProperty("serverName", "192.168.16.5");
			xqs.setProperty("serverName", "localhost");
			xqs.setProperty("port", "8080");
			xqs.setProperty("user", "admin");
			xqs.setProperty("password", "root");
			//xqs.setProperty("password", "smx");

			connection = xqs.getConnection();
			System.out.println("Connexió establerta amb SGBD ");

		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		} catch (XQException e) {
			e.printStackTrace();
		}
	}

	public void listItems() {
		try {
			// Creem XQExpression:
			XQExpression xqe = connection.createExpression();

			XQResultSequence resultIds = xqe.executeQuery(Strings.sId);
			XQResultSequence resultNames = xqe.executeQuery(Strings.sNoms);
			XQResultSequence resultPrices = xqe.executeQuery(Strings.sPreus);
			XQResultSequence resultAmount = xqe.executeQuery(Strings.sAmount);

			System.out.println("\nSTOCK ACTUAL:\n-------------");

			while (resultIds.next() && resultNames.next() && resultPrices.next() && resultAmount.next()) {
				printAllElements(resultIds.getItemAsStream());
				printAllElements(resultNames.getItemAsStream());
				printAllElements(resultPrices.getItemAsStream());
				printAllElements(resultAmount.getItemAsStream());
				System.out.println();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void itemDel() {
		try {
			
			XQExpression xqe = connection.createExpression();

			System.out.println("DELETE PRODUCTE\n----------------");
			
			String sTipus = itemGranelEnvas();

			int iID = idProducte(sTipus);
			
			String query = "for $x in doc("+Strings.ruta+")/stock/magatzem/producte[ ./id_prod="  + iID + "] "
					+ "return delete node $x";
			
			xqe.executeCommand(query);
			
		} catch ( Exception e ) {
			e.getStackTrace();
		}
	}

	public void itemAdd() {
		
		String sName = null;
		String sPreu = "0.00€/u";
		int iQuantitat = 0;
		
		try {
		
			XQExpression xqe = connection.createExpression();

			System.out.println("\nADD PRODUCTE\n----------------");
			
			String sTipus = itemGranelEnvas();

			int iID = idProducte(sTipus);
			
			System.out.println( "Introdueix nom del producte: ");
			sName = sc.next();
			
			String query = "for $x in doc("+Strings.ruta+")/stock/magatzem[ ./id="  + 0	+ "] "
					+ "return insert node <producte>"
					+ "<tipus>" + sTipus + "</tipus>"
					+ "<id_prod>" + iID + "</id_prod>"
					+ "<name>" + sName + "</name>"
					+ "<preu>" + sPreu + "</preu>"
					+ "<amount>" + iQuantitat + "</amount>"
					+ "</producte> into $x";
			
			xqe.executeCommand(query);
			
		} catch ( Exception e ) {
			e.getStackTrace();
		}
		
	}

	public void itemBuy() {
		try {
			XQExpression xqe = connection.createExpression();

			System.out.println("\nCOMPRAR PRODUCTE\n----------------");

			String sTipus = itemGranelEnvas();

			int iID = idProducte(sTipus);

			String sID = "for $x in doc(\""+Strings.ruta+"\")/stock/magatzem/producte where $x/id_prod= " + iID	+ " return ($x/name,$x/amount)";

			XQResultSequence resultIds = xqe.executeQuery(sID);

			boolean flag = false;
			ArrayList<String> llista;
			while (resultIds.next()) {
				flag = true;
				llista = getAllElements(resultIds.getItemAsStream());
				for(int i=0;i<llista.size();i++) {
					System.out.println(llista.get(i));
				}
			}

			if(flag) {
				int quantitat = getQuantitat(xqe,iID);

				System.out.print("Quantitat a comprar: ");
				int iQuant = sc.nextInt();
				String query = "update value doc(\""+Strings.ruta+"\")/stock/magatzem/producte[./id_prod='" + iID + "']/amount with '" + (quantitat + iQuant) + "'";
				xqe.executeCommand(query);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public int getQuantitat(XQExpression xqe, int iID) {
		String sAmount = "for $x in doc(\""+Strings.ruta+"\")/stock/magatzem/producte where $x/id_prod= " + iID	+ " return ($x/amount)";
		XQResultSequence resultAmount;
		try {
			resultAmount = xqe.executeQuery(sAmount);
			while (resultAmount.next()) {
				ArrayList<String> llista = getAllElements(resultAmount.getItemAsStream());
				for(int i=0;i<llista.size();i++) {
					if(i == 1)
						return Integer.parseInt(llista.get(i));
				}
			}
		} catch (XQException e) {
			e.printStackTrace();
		}

		return iID;
	}

	public void itemSell() {
		try {
			XQExpression xqe = connection.createExpression();

			System.out.println("\nCOMPRAR PRODUCTE\n----------------");

			String sTipus = itemGranelEnvas();

			int iID = idProducte(sTipus);

			String sID = "for $x in doc(\"" + Strings.ruta + "\")/stock/magatzem/producte where $x/id_prod= " + iID
					+ " return ($x/name,$x/amount)";

			XQResultSequence resultIds = xqe.executeQuery(sID);

			boolean flag = false;
			ArrayList<String> llista;
			while (resultIds.next()) {
				flag = true;
				llista = getAllElements(resultIds.getItemAsStream());
				for(int i=0;i<llista.size();i++) {
					System.out.println(llista.get(i));
				}
			}

			if(flag) {
				int quantitat = getQuantitat(xqe,iID);

				System.out.print("Quantitat a vendre: ");
				int iQuant = sc.nextInt();
				
				if( quantitat >= iQuant ) {
					String query = "update value doc(\""+Strings.ruta+"\")/stock/magatzem/producte[./id_prod='" + iID + "']/amount with '" + (quantitat - iQuant ) + "'";
					xqe.executeCommand(query);
				} else {
					System.out.println( "Quantitat invalida!" );
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String itemGranelEnvas() {
		String sTipus = "";

		do {
			System.out.println("Producte Envasat[E] o Granel[G]:");
			sTipus = sc.next();

			if (sTipus.equalsIgnoreCase("E") || sTipus.equalsIgnoreCase("G")) {
			} else {
				System.out.println("Valor invalid!");
				sTipus = "";
			}
		} while (sTipus.equals(""));

		return sTipus;
	}

	public int idProducte(String sTipus) {
		int iID = -1;

		do {
			System.out.println("Id del producte:");
			iID = sc.nextInt();

			if (sTipus.equalsIgnoreCase("E") && !(iID % 2 == 0) || sTipus.equalsIgnoreCase("G") && (iID % 2 == 0)) {

				System.out.println("Id invalida!");
				iID = -1;
			}
		} while (iID == -1);
		return iID;
	}

	public void closeConnection() {
		try {
			if (connection != null)
				connection.close();
		} catch (XQException xe) {
			xe.printStackTrace();
		}
	}

	void printAllElements(XMLStreamReader reader) {
		try {
			while (reader.hasNext()) {
				reader.next();
				if (reader.getEventType() == XMLStreamConstants.START_ELEMENT && !reader.getLocalName().equals("")) {
					System.out.print(reader.getLocalName() + ": ");
				}
				if (getNextElement(reader) != "")
					System.out.println(getNextElement(reader));
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	ArrayList<String> getAllElements(XMLStreamReader reader) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			while (reader.hasNext()) {
				reader.next();
				if (reader.getEventType() == XMLStreamConstants.START_ELEMENT && !reader.getLocalName().equals("")) {
					list.add(reader.getLocalName() + ": ");
				}
				if (getNextElement(reader) != "")
					list.add(getNextElement(reader));
			}
			return list;
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return list;
	}

	String getNextElement(XMLStreamReader reader) {
		if (reader.getEventType() == XMLStreamConstants.CHARACTERS)
			return reader.getText();
		return "";
	}
}
