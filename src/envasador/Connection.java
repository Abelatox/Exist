package envasador;

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

	public static String RUTA = "envasador.xml";

	public Connection() {
		XQDataSource xqs = null;

		try {
			xqs = (XQDataSource) Class.forName("net.xqj.exist.ExistXQDataSource").newInstance();
			xqs.setProperty("serverName", "localhost");
			xqs.setProperty("port", "8080");
			xqs.setProperty("user", "admin");
			// xqs.setProperty("password", "root");
			xqs.setProperty("password", "smx");

			connection = xqs.getConnection();
			System.out.println("Connexi� establerta amb SGBD ");

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

	public void itemBuy() {
		try {
			XQExpression xqe = connection.createExpression();

			System.out.println("\nCOMPRAR PRODUCTE\n----------------");

			String sTipus = itemGranelEnvas();

			int iID = idProducte(sTipus);

			String sID = "for $x in doc(\"" + RUTA + "\")/stock/magatzem/producte where $x/id_prod= " + iID	+ " return ($x/name,$x/amount)";

			XQResultSequence resultIds = xqe.executeQuery(sID);
			while (resultIds.next()) {
				printAllElements(resultIds.getItemAsStream());
			}
			System.out.print("Amount: ");
			int iQuant = sc.nextInt();
			
		} catch (Exception e) {
		}

	}

	public void itemSell() {
		try {
			XQExpression xqe = connection.createExpression();

			System.out.println("\nCOMPRAR PRODUCTE\n----------------");

			String sTipus = itemGranelEnvas();

			int iID = idProducte(sTipus);

			String sID = "for $x in doc(\"" + RUTA + "\")/stock/magatzem/producte where $x/id_prod= " + iID
					+ " return ($x/name,$x/amount)";

			XQResultSequence resultIds = xqe.executeQuery(sID);
			while (resultIds.next()) {
				printAllElements(resultIds.getItemAsStream());
			}

		} catch (Exception e) {
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

			if (sTipus.equalsIgnoreCase("E") && (iID % 2 == 0) || sTipus.equalsIgnoreCase("G") && !(iID % 2 == 0)) {

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

	String getNextElement(XMLStreamReader reader) {
		if (reader.getEventType() == XMLStreamConstants.CHARACTERS)
			return reader.getText();
		return "";
	}
}
