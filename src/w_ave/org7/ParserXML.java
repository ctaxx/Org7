package w_ave.org7;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import w_ave.org7.item.Item;
import w_ave.org7.item.ListItem;

public class ParserXML {

	ArrayListEx arrayList;
	Document doc;

	public ParserXML() {
		arrayList = new ArrayListEx();
	}

	public ArrayListEx parse() {
		doc = null;
		try {
			doc = getDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
		workWithNodes(doc.getChildNodes(), arrayList, null);
		return arrayList;

	}

	/** складываем элементы из nodeList в arrayList */
	private void workWithNodes(NodeList nodeList, ArrayList<Item> arrayList,
			Item ancestor) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if ((node.getNodeType() == Node.ELEMENT_NODE)
					& (!node.getNodeName().equals("timestamp"))) {
				NamedNodeMap attributes = node.getAttributes();
				Node nameAttribute = attributes.getNamedItem("name");

				/**
				 * создание элемента, пересыпание данных, св€зывание с предком и
				 * с узлами DOM
				 */

				ListItem item = new ListItem(nameAttribute.getNodeValue());
				item.setAncestor(ancestor);
				item.setNode(node);
				arrayList.add(item);

				// print(""+ nameAttribute.getNodeValue());

				if (node.getChildNodes() != null) {
					workWithNodes(node.getChildNodes(), item.getChilds(), item);
				}
			}
		}
	}

	private Document getDocument() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new File("/sdcard/Org7context.txt"));
	}

	public void saveListToXML() {
		try {
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);
			StreamResult streamResult = new StreamResult(new File(
					"/sdcard/Org7context.txt"));

			transformer.transform(domSource, streamResult);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Node createNode(String text) {
		Element node = doc.createElement("method");

		Attr attr = doc.createAttribute("name");
		attr.setValue(text);
		node.setAttributeNode(attr);

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		// Date date = new Date();

		Attr dateAttr = doc.createAttribute("createdate");
		// dateAttr.setValue(date.toString());
		dateAttr.setValue("" + calendar.get(Calendar.DAY_OF_MONTH) + "-"
				+ (calendar.get(Calendar.MONTH) + 1) + "-"
				+ calendar.get(Calendar.YEAR));
		node.setAttributeNode(dateAttr);

		return node;
	}

	public Node createNode(String text, String path) {
		Element node = doc.createElement("method");

		Attr attr = doc.createAttribute("name");
		attr.setValue(text);
		node.setAttributeNode(attr);

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		// Date date = new Date();

		Attr dateAttr = doc.createAttribute("createdate");
		// dateAttr.setValue(date.toString());
		dateAttr.setValue("" + calendar.get(Calendar.DAY_OF_MONTH) + "-"
				+ (calendar.get(Calendar.MONTH) + 1) + "-"
				+ calendar.get(Calendar.YEAR));
		node.setAttributeNode(dateAttr);

		Attr pathAttr = doc.createAttribute("path");
		pathAttr.setValue(path);
		node.setAttributeNode(pathAttr);

		Attr extAttr = doc.createAttribute("type");
		extAttr.setValue(path.substring(path.lastIndexOf('.') + 1));
		node.setAttributeNode(extAttr);

		return node;
	}

	public Node createTimeStamp(String date) {
		Element node = doc.createElement("timestamp");

		Attr attr = doc.createAttribute("begin");
		attr.setValue(date);
		node.setAttributeNode(attr);

		attr = doc.createAttribute("name");
		attr.setValue("sub");
		node.setAttributeNode(attr);

		attr = doc.createAttribute("state");
		attr.setValue("play");
		node.setAttributeNode(attr);
		return node;
	}
}
