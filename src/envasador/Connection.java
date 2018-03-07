package envasador;

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

			String sNoms = "doc('envasador.xml')/stock/magatzem/producte/name";
			String sPreus = "doc('envasador.xml')/stock/magatzem/producte/preu";
			// cad = "doc('xqj/mondial.xml')/mondial/country/name";

			System.out.println("Executant instrucció:\n" + sNoms);
			XQResultSequence resultNames = xqe.executeQuery(sNoms);
			XQResultSequence resultPrices = xqe.executeQuery(sPreus);

			// Mostrem resultats, un a un, convertits a String
			System.out.println("\nResultats:");

			while (resultNames.next() && resultPrices.next()) {

				XMLStreamReader xsrNames = resultNames.getItemAsStream();
				XMLStreamReader xsrPrices = resultPrices.getItemAsStream();

				printAllElements(xsrNames);
				printAllElements(xsrPrices);

			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally { /* Tanquem connexió en qualsevol cas */
			try {
				if (connection != null)
					connection.close();
			} catch (XQException xe) {
				xe.printStackTrace();
			}
		}
	}
	
	void printAllElements(XMLStreamReader reader) {
		try {
			while (reader.hasNext()) {
				reader.next();
				if (reader.getEventType() == XMLStreamConstants.START_ELEMENT && !reader.getLocalName().equals("")) {
					System.out.print(reader.getLocalName()+":");
				}
				if (getNextElement(reader) != "")	
					System.out.println("\t" + getNextElement(reader));
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
