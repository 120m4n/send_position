import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;

public class TestXML {
    // given a HashMap of key-value pairs, create an XML file
    public static void createXML(HashMap<String, String> data, String fileName) {
        try {
            // Create a DOM document builder
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.createElement("scores");

            // add the data to the document
            for (String key : data.keySet()) {
                Element score = document.createElement("score");
                Element newKey = document.createElement("key");
                Element newValue = document.createElement("value");

                newKey.setTextContent(key);
                newValue.setTextContent(data.get(key));

                score.appendChild(newKey);
                score.appendChild(newValue);
                root.appendChild(score);
            }
            document.appendChild(root);

            // Write the DOM structure to a file:
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(new File(fileName)));

            System.out.println("File saved!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // given an XML file, return a HashMap of key-value pairs
    public static HashMap<String, String> readXML(String fileName) {
        HashMap<String, String> data = new HashMap<>();
        try {
            // Create a DOM document builder
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(new File(fileName));

            // Get the root element
            Element root = document.getDocumentElement();

            // Get all the child elements
            NodeList scores = root.getChildNodes();

            // Iterate through the child elements
            for (int i = 0; i < scores.getLength(); i++) {
                Node score = scores.item(i);
                if (score instanceof Element) {
                    NodeList scoreInfo = score.getChildNodes();
                    String key = "";
                    String value = "";
                    for (int j = 0; j < scoreInfo.getLength(); j++) {
                        Node info = scoreInfo.item(j);
                        if (info instanceof Element) {
                            String content = info.getLastChild().getTextContent().trim();
                            if (info.getNodeName().equals("key")) {
                                key = content;
                            } else if (info.getNodeName().equals("value")) {
                                value = content;
                            }
                        }
                    }
                    data.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }



    public static void main(String[] args) throws Exception {
        // create a hashmap to store the data
        HashMap<String, String> data = new HashMap<String, String>();
        // add data to the hashmap
        data.put("sofia", "125");
        data.put("jones", "100");
        data.put("abigail", "1000");
        data.put("tonija", "500");

        // create the XML file
        createXML(data, "src/scores.xml");

        // read the XML file
        HashMap<String, String> data2 = readXML("src/scores.xml");
        // print the data
        for (String key : data2.keySet()) {
            System.out.println(key + " " + data2.get(key));
        }

    }
}
