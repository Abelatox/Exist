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

			XQResultSequence resultNames = xqe.executeQuery(Strings.sNoms);
			XQResultSequence resultPrices = xqe.executeQuery(Strings.sPreus);
			XQResultSequence resultAmount = xqe.executeQuery(Strings.sAmount);

			System.out.println("\nStock actual:");

			while (resultNames.next() && resultPrices.next() && resultAmount.next()) {
				printAllElements(resultNames.getItemAsStream());
				printAllElements(resultPrices.getItemAsStream());
				printAllElements(resultAmount.getItemAsStream());
				System.out.println();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
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
					System.out.print(reader.getLocalName()+": ");
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
