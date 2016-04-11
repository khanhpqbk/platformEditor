/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicaleditor.controller;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author KHANH
 */
public class Test {

    public static void main(String[] args) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, TransformerException {
        File inputFile = new File("C:\\hihi");
//        System.out.println("filename: " + fileName);
        DocumentBuilderFactory dbFactory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        XPathFactory xpathFactory = XPathFactory.newInstance();
// XPath to find empty text nodes.
        XPathExpression xpathExp = xpathFactory.newXPath().compile(
                "//text()[normalize-space(.) = '']");
        NodeList emptyTextNodes = (NodeList) xpathExp.evaluate(doc, XPathConstants.NODESET);

// Remove each empty text node from document.
        for (int i = 0; i < emptyTextNodes.getLength(); i++) {
            Node emptyTextNode = emptyTextNodes.item(i);
            emptyTextNode.getParentNode().removeChild(emptyTextNode);
        }
        
        TransformerFactory transformerFactory
                = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(doc);
//         System.out.println("-----------Modified File-----------");
        StreamResult file = new StreamResult(inputFile);
        transformer.transform(source, file);
    }
}
