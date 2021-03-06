/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicaleditor.controller.fileprocessors;

//import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import com.icl.saxon.TransformerFactoryImpl;
import com.icl.saxon.aelfred.SAXParserFactoryImpl;
import graphicaleditor.controller.GraphicalModeController;
import graphicaleditor.model.ASView;
import graphicaleditor.model.ClusterView;
import graphicaleditor.model.HostView;
import graphicaleditor.model.LinkView;
import graphicaleditor.model.RouteView;
import graphicaleditor.model.RouteViewRoom;
import graphicaleditor.model.RouterView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
//import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 *
 * @author KHANH
 */
public class XMLProcessor {

    List<ASView> asList = new ArrayList<>();
    Document doc;
    File inputFile;

    public XMLProcessor() {

    }

    public XMLProcessor(String fileName) {
            inputFile = new File(fileName);
            System.out.println("filename: " + fileName);
            init();
    }
    
    private void init() {
        try {
        DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
        } catch (SAXException ex) {
            Logger.getLogger(XMLProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void preprocessXml() {
            TextFileProcessor pro = new TextFileProcessor();
            List<String> lines = pro.read(inputFile);
            if (lines.get(1).contains("http://simgrid.gforge.inria.fr/simgrid/simgrid.dtd")) {
                lines.remove(1);
//                System.out.println("CONTAIN NHE CUNG!!!");
            }
            pro.write(inputFile, lines);
    }

    private void saveAndRefactorXml(File inputFile) {
        try {

            TextFileProcessor pro = new TextFileProcessor();
            List<String> lines = new ArrayList<>();

//            XPathFactory xpathFactory = XPathFactory.newInstance();
//            // XPath to find empty text nodes.
//            XPathExpression xpathExp = xpathFactory.newXPath().compile(
//                    "//text()[normalize-space(.) = '']");
//            NodeList emptyTextNodes = (NodeList) xpathExp.evaluate(doc, XPathConstants.NODESET);
//
//            // Remove each empty text node from document.
//            for (int i = 0; i < emptyTextNodes.getLength(); i++) {
//                Node emptyTextNode = emptyTextNodes.item(i);
//                emptyTextNode.getParentNode().removeChild(emptyTextNode);
//            }
            // add proper indentation
            TransformerFactory transformerFactory
                    = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);

//            Source source = new StreamSource(inputFile);
            StreamResult file = new StreamResult(inputFile);
            transformer.transform(source, file);

//            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(new File("src/graphicaleditor/res/input.xslt"));
            Transformer tf = transformerFactory.newTransformer(xslt);

            Source text = new StreamSource(inputFile);
            tf.transform(text, new StreamResult(inputFile));

            // add line doctype platform
//            pro = new TextFileProcessor();
            lines = pro.read(inputFile);
            lines.add(1, "<!DOCTYPE platform SYSTEM \"http://simgrid.gforge.inria.fr/simgrid/simgrid.dtd\">");
            pro.write(inputFile, lines);

        } 
//        catch (XPathExpressionException ex) {
//            Logger.getLogger(XMLProcessor.class.getName()).log(Level.SEVERE, null, ex);
//        } 
        catch (TransformerException ex) {
            Logger.getLogger(XMLProcessor.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    public void createRootElement() {
        Element ele = doc.createElement("platform");
        doc.appendChild(ele);
    }

    public void addHostXml(ASView as, HostView host) {

        preprocessXml();
        
        init();
        
        NodeList nList = doc.getElementsByTagName("AS");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node a = nList.item(temp);
            if (a.getNodeType() == Node.ELEMENT_NODE) {

                Element eleAs = (Element) a;
//                System.out.println("addhostxml: as id : " + eleAs.getAttribute("id"));
                if (eleAs.getAttribute("id").equalsIgnoreCase(as.getmId())) {
//                    System.out.println("addhostxml: found as");
                    // day chinh la AS can tim
                    Node newHost = buildNodeFromHostView(doc, host);
                    eleAs.appendChild(newHost);
                }
            }
        }

        // write the content on file
        saveAndRefactorXml(inputFile);

    }

    public void removeHostXml(ASView as, HostView host) {
        preprocessXml();
        
        init();
        
        NodeList nList = doc.getElementsByTagName("AS");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node a = nList.item(temp);
            if (a.getNodeType() == Node.ELEMENT_NODE) {

                Element eleAs = (Element) a;
//                System.out.println("removehostxml: as id : " + eleAs.getAttribute("id"));
                if (eleAs.getAttribute("id").equalsIgnoreCase(as.getmId())) {
//                    System.out.println("removehostxml: found as");
                    // day chinh la AS can tim
//                    Node newHost = buildNodeFromHostView(doc, host);
                    NodeList nListHost = eleAs.getElementsByTagName("host");
                    for (int i = 0; i < nListHost.getLength(); i++) {
                        Element foundHost = (Element) nListHost.item(i);
                        if (host.getmId().equalsIgnoreCase(foundHost.getAttribute("id"))) {
                            eleAs.removeChild(foundHost);
                        }
                    }
                }
            }
        }

        // write the content on file
        saveAndRefactorXml(inputFile);

    }

    private Node buildNodeFromHostView(Document d, HostView h) {
        Node n = d.createElement("host");
        Element e = (Element) n;
        e.setAttribute("id", h.getmId());
        e.setAttribute("speed", String.valueOf(h.getPower()));
//        e.setAttribute("state", h.getState());
//        e.setAttribute("availability", String.valueOf(h.getAvailability()));
//        e.setAttribute("pstate", String.valueOf(h.getPstate()));
//        e.setAttribute("core", String.valueOf(h.getCore()));
//        e.setAttribute("coordinates", String.valueOf(h.getCoordinates()));
        return n;
    }

    public void modifyHostXml(HostView newHost, String oldId) {
        preprocessXml();
        
        init();
        
        NodeList nList = doc.getElementsByTagName("AS");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node a = nList.item(temp);
            if (a.getNodeType() == Node.ELEMENT_NODE) {

                // modify host
                Element eleAs = (Element) a;
                NodeList nListHost = eleAs.getElementsByTagName("host");
                for (int i = 0; i < nListHost.getLength(); i++) {
                    Element foundHost = (Element) nListHost.item(i);
                    if (oldId.equalsIgnoreCase(foundHost.getAttribute("id"))) {
                        modifyHost(newHost, foundHost);
                    }

                }

                // modify route
                NodeList nListRoute = eleAs.getElementsByTagName("route");
                for (int i = 0; i < nListRoute.getLength(); i++) {
                    Element foundRoute = (Element) nListRoute.item(i);
                    if (foundRoute.getAttribute("src").equals(oldId)) {
                        foundRoute.setAttribute("src", newHost.getmId());
                    }

                    if (foundRoute.getAttribute("dst").equals(oldId)) {
                        foundRoute.setAttribute("dst", newHost.getmId());
                    }
                }
            }
        }

        saveAndRefactorXml(inputFile);

    }

    private void modifyHost(HostView newHost, Node oldHost) {
        preprocessXml();
        
        init();
        
        boolean reParse = false;
        if (!newHost.getmId().equals(((Element) oldHost).getAttribute("id"))) {
            reParse = true;
        }
        Element e = (Element) oldHost;
        e.setAttribute("id", newHost.getmId());
        e.setAttribute("spped", String.valueOf(newHost.getPower()));

        if (reParse) {
            parse();

        }
    }

    public void addASXml(ASView as) {
        
        preprocessXml();
        
        init();
        
        Node n = doc.getElementsByTagName("platform").item(0);
        if (n == null) {
            System.out.println("null");
        } else {
            n.appendChild(buildNodeFromASView(doc, as));
        }
//        appendChild();
//        doc.getDocumentElement().

        saveAndRefactorXml(inputFile);

    }

    private Node buildNodeFromASView(Document d, ASView as) {
        Node n = d.createElement("AS");
        Element e = (Element) n;
        e.setAttribute("id", as.getmId());
        e.setAttribute("routing", "Dijkstra");
        return n;
    }

    public void removeASXml(ASView as) {
        
        preprocessXml();
        
        init();
        
        NodeList nList = doc.getElementsByTagName("AS");
        Element rmv = null;
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node a = nList.item(temp);
            if (a.getNodeType() == Node.ELEMENT_NODE) {

                Element eleAs = (Element) a;
//                System.out.println("deleteasxml: found as id : " + eleAs.getAttribute("id"));
                if (eleAs.getAttribute("id").equals(as.getmId())) {
                    rmv = eleAs;
                }

            }
        }
        if (rmv != null) {
            doc.getDocumentElement().removeChild(rmv);
        }

        saveAndRefactorXml(inputFile);

    }

    public void removeRouteXml(RouteView rv) {
        
        preprocessXml();
        
        init();
        
        NodeList nList = doc.getElementsByTagName("AS");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node a = nList.item(temp);
            if (a.getNodeType() == Node.ELEMENT_NODE) {

                Element eleAs = (Element) a;
                NodeList nListRoute = eleAs.getElementsByTagName("route");
                for (int i = 0; i < nListRoute.getLength(); i++) {
                    Element foundRoute = (Element) nListRoute.item(i);
                    if (rv.getSrc().equals(foundRoute.getAttribute("src")) && rv.getDes().equals(foundRoute.getAttribute("dst"))) {
                        eleAs.removeChild(foundRoute);
                    }

                }

            }
        }

        saveAndRefactorXml(inputFile);

    }

    public void removeRouteRoomXml(RouteViewRoom rv) {
        NodeList nList = doc.getElementsByTagName("AS");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node a = nList.item(temp);
            if (a.getNodeType() == Node.ELEMENT_NODE) {

                Element eleAs = (Element) a;
                NodeList nListRoute = eleAs.getElementsByTagName("route");
                for (int i = 0; i < nListRoute.getLength(); i++) {
                    Element foundRoute = (Element) nListRoute.item(i);
                    if (rv.getSrc().equals(foundRoute.getAttribute("src")) && rv.getDes().equals(foundRoute.getAttribute("dst"))) {
                        eleAs.removeChild(foundRoute);
                    }

                }

            }
        }

        saveAndRefactorXml(inputFile);

    }

    public void addRouterXml(ASView as, RouterView rv) {
        
        preprocessXml();
        
        init();
        
        NodeList nList = doc.getElementsByTagName("AS");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node a = nList.item(temp);
            if (a.getNodeType() == Node.ELEMENT_NODE) {

                Element eleAs = (Element) a;
//                System.out.println("addrouterxml: as id : " + eleAs.getAttribute("id"));
                if (eleAs.getAttribute("id").equalsIgnoreCase(as.getmId())) {
//                    System.out.println("addrouterxml: found as");
                    // day chinh la AS can tim
                    Node newRouter = buildNodeFromRouterView(doc, rv);
                    eleAs.appendChild(newRouter);
                }
            }
        }

        // write the content on file
        saveAndRefactorXml(inputFile);

    }

    private Node buildNodeFromRouterView(Document d, RouterView rv) {
        Node n = d.createElement("router");
        Element e = (Element) n;
        e.setAttribute("id", rv.getmId());
        return n;
    }

    public void removeRouterXml(RouterView rv) {
        preprocessXml();
        
        init();
        
        NodeList nList = doc.getElementsByTagName("AS");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node a = nList.item(temp);
            if (a.getNodeType() == Node.ELEMENT_NODE) {

                Element eleAs = (Element) a;
                NodeList nListRoute = eleAs.getElementsByTagName("router");
                for (int i = 0; i < nListRoute.getLength(); i++) {
                    Element foundRouter = (Element) nListRoute.item(i);
                    if (rv.getmId().equals(foundRouter.getAttribute("id"))) {
                        eleAs.removeChild(foundRouter);
                    }

                }

            }
        }

        saveAndRefactorXml(inputFile);

    }

    public void modifyRouterXml(RouterView newRouter, String oldId) {
        preprocessXml();
        
        init();
        
        NodeList nList = doc.getElementsByTagName("AS");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node a = nList.item(temp);
            if (a.getNodeType() == Node.ELEMENT_NODE) {

                // modify host
                Element eleAs = (Element) a;
                NodeList nListHost = eleAs.getElementsByTagName("host");
                for (int i = 0; i < nListHost.getLength(); i++) {
                    Element foundRouter = (Element) nListHost.item(i);
                    if (oldId.equalsIgnoreCase(foundRouter.getAttribute("id"))) {
                        foundRouter.setAttribute("id", newRouter.getmId());
                    }

                }

                // modify route
                NodeList nListRoute = eleAs.getElementsByTagName("route");
                for (int i = 0; i < nListRoute.getLength(); i++) {
                    Element foundRoute = (Element) nListRoute.item(i);
                    if (foundRoute.getAttribute("src").equals(oldId)) {
                        foundRoute.setAttribute("src", newRouter.getmId());
                    }

                    if (foundRoute.getAttribute("dst").equals(oldId)) {
                        foundRoute.setAttribute("dst", newRouter.getmId());
                    }
                }
            }
        }

        saveAndRefactorXml(inputFile);

    }

    public void addRouteXml(ASView as, RouteView rv) {
        preprocessXml();
        
        init();
        
        NodeList nList = doc.getElementsByTagName("AS");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node a = nList.item(temp);
            if (a.getNodeType() == Node.ELEMENT_NODE) {

                Element eleAs = (Element) a;
//                System.out.println("addroutexml: as id : " + eleAs.getAttribute("id"));
                if (eleAs.getAttribute("id").equalsIgnoreCase(as.getmId())) {
//                    System.out.println("addroutexml: found as");
                    // day chinh la AS can tim
                    buildNodeAndAppendNodeFromRouteView(doc, rv, eleAs);
//                    eleAs.appendChild(newRoute);
                }
            }
        }

        // write the content on file
        saveAndRefactorXml(inputFile);

    }

    public void addRouteRoomXml(ASView as, RouteViewRoom rv) {
        NodeList nList = doc.getElementsByTagName("AS");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node a = nList.item(temp);
            if (a.getNodeType() == Node.ELEMENT_NODE) {

                Element eleAs = (Element) a;
//                System.out.println("addroutexml: as id : " + eleAs.getAttribute("id"));
                if (eleAs.getAttribute("id").equalsIgnoreCase(as.getmId())) {
//                    System.out.println("addroutexml: found as");
                    // day chinh la AS can tim
                    buildNodeAndAppendNodeFromRouteViewRoom(doc, rv, eleAs);
//                    eleAs.appendChild(newRoute);
                }
            }
        }

        // write the content on file
        saveAndRefactorXml(inputFile);

    }

    private void buildNodeAndAppendNodeFromRouteView(Document d, RouteView rv, Element asEle) {
        Node n = d.createElement("route");
        Element e = (Element) n;
        e.setAttribute("src", rv.getSrc());
        e.setAttribute("dst", rv.getDes());
//        e.setAttribute("symmetry", "yes");
        asEle.appendChild(e);

        if (rv.getLinks().size() > 0) {
            for (LinkView link : rv.getLinks()) {
                Node newNode = d.createElement("link");
                Element lnNew = (Element) newNode;
                lnNew.setAttribute("id", link.getmId());
                lnNew.setAttribute("bandwidth", String.valueOf(link.getBandwidth()));
                lnNew.setAttribute("latency", String.valueOf(link.getLatency()));
//                lnNew.setAttribute("state", link.getState());
//                lnNew.setAttribute("sharing_policy", link.getSharingPolicy());
                asEle.appendChild(lnNew);
            }

            for (LinkView link : rv.getLinks()) {
                Node l = d.createElement("link_ctn");
                Element ln = (Element) l;
                ln.setAttribute("id", link.getmId());
                e.appendChild(ln);
            }
        } else {
            int id = new Random().nextInt(10000) + 100;
            Node newNode = d.createElement("link");
            Element lnNew = (Element) newNode;
            lnNew.setAttribute("id", "link" + id);
            lnNew.setAttribute("bandwidth", "" + 1250000000);
            lnNew.setAttribute("latency", String.valueOf(0));
//            lnNew.setAttribute("state", "ON");
//            lnNew.setAttribute("sharing_policy", "");
            asEle.appendChild(lnNew);

            Node l = d.createElement("link_ctn");
            Element ln = (Element) l;
            ln.setAttribute("id", "link" + id);
            e.appendChild(ln);
        }

    }

    private void buildNodeAndAppendNodeFromRouteViewRoom(Document d, RouteViewRoom rv, Element asEle) {
        Node n = d.createElement("route");
        Element e = (Element) n;
        e.setAttribute("src", rv.getSrc());
        e.setAttribute("dst", rv.getDes());
        e.setAttribute("symmetry", "yes");
        asEle.appendChild(e);

        for (LinkView link : rv.getLinks()) {
            Node newNode = d.createElement("link");
            Element lnNew = (Element) newNode;
            lnNew.setAttribute("id", link.getmId());
            lnNew.setAttribute("bandwidth", String.valueOf(link.getBandwidth()));
            lnNew.setAttribute("latency", String.valueOf(link.getLatency()));
            lnNew.setAttribute("state", link.getState());
            lnNew.setAttribute("sharing_policy", link.getSharingPolicy());
            asEle.appendChild(lnNew);
        }

        for (LinkView link : rv.getLinks()) {
            Node l = d.createElement("link_ctn");
            Element ln = (Element) l;
            ln.setAttribute("id", link.getmId());
            e.appendChild(ln);
        }

    }

//    private Node buildNodeFromRouteRoomView(Document d, RouteViewRoom rv) {
//        Node n = d.createElement("route");
//        Element e = (Element) n;
//        e.setAttribute("src", rv.getSrc());
//        e.setAttribute("dst", rv.getDes());
//        for (LinkView link : rv.getLinks()) {
//            Node l = d.createElement("link_ctn");
//            Element ln = (Element) n;
//            ln.setAttribute("id", link.getmId());
//            // TODO ...
//            e.appendChild(ln);
//        }
//        return n;
//    }
    public void parse() {

        NodeList nList = doc.getElementsByTagName("AS");
//        ASView as = null;
        for (int temp = 0; temp < nList.getLength(); temp++) {
            // parse tung AS mot

            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eleAS = (Element) nNode;
                ASView as = new ASView(new Image(new File("src/graphicaleditor/res/as.jpg").toURI().toString(), GraphicalModeController.VIEW_SIZE, GraphicalModeController.VIEW_SIZE, true, true), eleAS.getAttribute("id"));
                String id = eleAS.getAttribute("id");
//                System.out.println("id = " + id);
                NodeList hosts = eleAS.getElementsByTagName("host");
                as.setHostList(parseHosts(hosts));
                NodeList links = eleAS.getElementsByTagName("link");
                as.setLinkList(parseLinks(links));
                NodeList routes = eleAS.getElementsByTagName("route");
                as.setRouteList(parseRoutes(routes));
                as.setRouteRoomList(parseRouteRooms(routes));
                NodeList routers = eleAS.getElementsByTagName("router");
                as.setRouterList(parseRouters(routers));
                NodeList clusters = eleAS.getElementsByTagName("cluster");
                as.setClusterList(parseCluster(clusters));
                // chuyen sang topo link
                if (as.getClusterList() != null && as.getClusterList().size() > 0) {
                    ClusterView cv = as.getClusterList().get(0);
                    String topo = (as.getClusterList().get(0).getTopo());
                    StringTokenizer st = new StringTokenizer(cv.getRadical(), "-");
                    int numOfHost = Integer.parseInt(st.nextToken()) - Integer.parseInt(st.nextToken()) - 1;
                    numOfHost = -numOfHost;
                    switch (topo) {
                        case "FLAT":

                            generateStarTopo(true, as, numOfHost, cv.getPrefix(), cv.getSuffix(), cv.getLatency(), cv.getPower(),
                                    cv.getBandwidth());
                            break;
                        case "FAT_TREE":
                            break;
                        case "TORUS":
                            break;
                        default:
                            generateStarTopo(true, as, numOfHost, cv.getPrefix(), cv.getSuffix(), cv.getLatency(), cv.getPower(),
                                    cv.getBandwidth());
                            break;
                    }
                }
                as.setmId(eleAS.getAttribute("id"));
                asList.add(as);
            }

        }
//        asList.add(as);
    }

    private List<ClusterView> parseCluster(NodeList clusters) {
        List<ClusterView> clusterList = new ArrayList<>();
        for (int i = 0; i < clusters.getLength(); i++) {
            Element clusterEle = (Element) clusters.item(i);
            ClusterView cv = new ClusterView();
            cv.setId(clusterEle.getAttribute("id"));
            cv.setPrefix(clusterEle.getAttribute("prefix"));
            cv.setSuffix(clusterEle.getAttribute("suffix"));
            cv.setRadical(clusterEle.getAttribute("radical"));
            cv.setTopo(clusterEle.getAttribute("topo"));
            cv.setLatency(clusterEle.getAttribute("lat").isEmpty() ? 0 : Integer.parseInt(clusterEle.getAttribute("lat")));
            cv.setPower(clusterEle.getAttribute("power").isEmpty() ? 0 : Integer.parseInt(clusterEle.getAttribute("power")));
            cv.setBandwidth(clusterEle.getAttribute("bw").isEmpty() ? 0 : Integer.parseInt(clusterEle.getAttribute("bw")));
            clusterList.add(cv);
        }

        return clusterList;
    }

    private List<RouterView> parseRouters(NodeList routers) {
        List<RouterView> routerList = new ArrayList<>();
        for (int i = 0; i < routers.getLength(); i++) {
            // parse tung host mot
            if (routers.item(i).getNodeType() == Node.ELEMENT_NODE) {

                Element eleRouter = (Element) routers.item(i);
                String routerId = eleRouter.getAttribute("id");

                int routerCoord = eleRouter.getAttribute("coordinates").isEmpty() ? 0 : Integer.parseInt(eleRouter.getAttribute("coordinates"));
//                double hostPstate = eleRouter.getAttribute("pstate").isEmpty() ? 0.0 : Double.parseDouble(eleRouter.getAttribute("pstate"));
//                double hostAvailability = eleRouter.getAttribute("availability").isEmpty() ? 0.0 : Double.parseDouble(eleRouter.getAttribute("availability"));
//                int hostCore = eleRouter.getAttribute("core").isEmpty() ? 0 : Integer.parseInt(eleRouter.getAttribute("core"));
//                String hostState = eleRouter.getAttribute("state");
                RouterView router = new RouterView(new Image(new File("src/graphicaleditor/res/router.jpg").toURI().toString(), GraphicalModeController.VIEW_SIZE, GraphicalModeController.VIEW_SIZE, true, true),
                        routerId);
//                System.out.println(host.toString());
                routerList.add(router);
            }

        }

        return routerList;
    }

    private List<RouteView> parseRoutes(NodeList routes) {
        List<RouteView> routeList = new ArrayList<>();
        for (int i = 0; i < routes.getLength(); i++) {
            // parse tung host mot
            if (routes.item(i).getNodeType() == Node.ELEMENT_NODE) {

                Element eleRoute = (Element) routes.item(i);
                String src = eleRoute.getAttribute("src");
                String des = eleRoute.getAttribute("dst");
                NodeList links = eleRoute.getElementsByTagName("link_ctn");
                List<LinkView> linkViews = new ArrayList<>();
                for (int j = 0; j < links.getLength(); j++) {
                    Node link = links.item(j);
//                    Node id = link.getAttributes().getNamedItem("id");
//                    id
                    Element linkEle = (Element) link;
                    String idLink = linkEle.getAttribute("id");
                    LinkView linkView = new LinkView(idLink, 0, 0, "", "");
                    linkViews.add(linkView);
                }

                RouteView route = new RouteView(src, des, linkViews);
//                System.out.println(link.toString());
                routeList.add(route);
            }

        }
        return routeList;
    }

    private List<RouteViewRoom> parseRouteRooms(NodeList routes) {
        List<RouteViewRoom> routeRoomList = new ArrayList<>();
        for (int i = 0; i < routes.getLength(); i++) {
            // parse tung host mot
            if (routes.item(i).getNodeType() == Node.ELEMENT_NODE) {

                Element eleRoute = (Element) routes.item(i);
                String src = eleRoute.getAttribute("src");
                String des = eleRoute.getAttribute("dst");
                NodeList links = eleRoute.getElementsByTagName("link_ctn");
                List<LinkView> linkViews = new ArrayList<>();
                for (int j = 0; j < links.getLength(); j++) {
                    Node link = links.item(j);
//                    Node id = link.getAttributes().getNamedItem("id");
//                    id
                    Element linkEle = (Element) link;
                    String idLink = linkEle.getAttribute("id");
                    LinkView linkView = new LinkView(idLink, 0, 0, "", "");
                    linkViews.add(linkView);
                }

                RouteViewRoom route = new RouteViewRoom(src, des, linkViews);
//                System.out.println(link.toString());
                routeRoomList.add(route);
            }

        }
        return routeRoomList;
    }

    private List<LinkView> parseLinks(NodeList links) {
        List<LinkView> linkList = new ArrayList<>();
        for (int i = 0; i < links.getLength(); i++) {
            // parse tung host mot
            if (links.item(i).getNodeType() == Node.ELEMENT_NODE) {

                Element eleHost = (Element) links.item(i);
                String linkId = eleHost.getAttribute("id");
                int linkBw = eleHost.getAttribute("bandwidth").isEmpty() ? 0 : Integer.parseInt(eleHost.getAttribute("bandwidth"));
                String sharingPolicy = eleHost.getAttribute("sharing_policy");
                double linkLat = eleHost.getAttribute("latency").isEmpty() ? 0.0 : Double.parseDouble(eleHost.getAttribute("latency"));
//                double hostAvailability = eleHost.getAttribute("availability").isEmpty() ? 0.0 : Double.parseDouble(eleHost.getAttribute("availability"));
//                int hostCore = eleHost.getAttribute("core").isEmpty() ? 0 : Integer.parseInt(eleHost.getAttribute("core"));
                String linkState = eleHost.getAttribute("state");
                LinkView link = new LinkView(linkId, linkBw, linkLat, linkState, sharingPolicy);
//                System.out.println(link.toString());
                linkList.add(link);
            }

        }
        return linkList;
    }

    private List<HostView> parseHosts(NodeList hosts) {
        List<HostView> hostList = new ArrayList<>();
        for (int i = 0; i < hosts.getLength(); i++) {
            // parse tung host mot
            if (hosts.item(i).getNodeType() == Node.ELEMENT_NODE) {

                Element eleHost = (Element) hosts.item(i);
                String hostId = eleHost.getAttribute("id");
                int hostPower = eleHost.getAttribute("speed").isEmpty() ? 1000000000 : Integer.parseInt(eleHost.getAttribute("speed"));
                int hostCoord = eleHost.getAttribute("coordinates").isEmpty() ? 0 : Integer.parseInt(eleHost.getAttribute("coordinates"));
                double hostPstate = eleHost.getAttribute("pstate").isEmpty() ? 0.0 : Double.parseDouble(eleHost.getAttribute("pstate"));
                double hostAvailability = eleHost.getAttribute("availability").isEmpty() ? 0.0 : Double.parseDouble(eleHost.getAttribute("availability"));
                int hostCore = eleHost.getAttribute("core").isEmpty() ? 1 : Integer.parseInt(eleHost.getAttribute("core"));
                String hostState = eleHost.getAttribute("state");
                HostView host = new HostView(new Image(new File("src/graphicaleditor/res/host.jpg").toURI().toString(), GraphicalModeController.VIEW_SIZE, GraphicalModeController.VIEW_SIZE, true, true),
                        hostId, hostPower, hostState, hostPstate, hostAvailability, hostCore, hostCoord);
//                System.out.println(host.toString());
                hostList.add(host);
            }

        }

        return hostList;
    }

    ///////////////////////////////////////////////////////////////
    // functions for generating new topology
    public void generateStarTopo(boolean forCluster, ASView asView, int numOfHost,
            String prefix, String suffix, int lat, int power, int bw) {
        Element as = (Element) buildNodeFromASView(doc, asView);
//        as.setAttribute("id", asId);
        if (!forCluster) {
            doc.getDocumentElement().appendChild(as);
        }
//        RouterView rv = new RouterView(new Image(new File("src/graphicaleditor/res/router.jpg").toURI().toString(), GraphicalModeController.VIEW_SIZE, GraphicalModeController.VIEW_SIZE, true, true),
//                "main_router");
        HostView hv = new HostView(new Image(new File("src/graphicaleditor/res/host.jpg").toURI().toString(), GraphicalModeController.VIEW_SIZE, GraphicalModeController.VIEW_SIZE, true, true), "main_router",
                power,
                "ON",
                0,
                1.0,
                1,
                0);
        Node h = (Node) buildNodeFromHostView(doc, hv);
        if (forCluster) {
            asView.getHostList().add(hv);
        } else {
            as.appendChild(h);
        }
        for (int i = 0; i < numOfHost; i++) {
            HostView hostview = new HostView(new Image(new File("src/graphicaleditor/res/host.jpg").toURI().toString(), GraphicalModeController.VIEW_SIZE, GraphicalModeController.VIEW_SIZE, true, true), prefix + i + suffix,
                    power,
                    "ON",
                    0,
                    1.0,
                    1,
                    0);
            Node host = buildNodeFromHostView(doc,
                    hostview
            );
            if (forCluster) {
                asView.getHostList().add(hostview);
            } else {
                as.appendChild(host);
            }

        }

        for (int i = 0; i < numOfHost; i++) {
            ArrayList<LinkView> links = new ArrayList<>();
            links.add(new LinkView("link" + i, bw, lat, "ON", ""));
            RouteView rtv = new RouteView("main_router", prefix + i + suffix, links);
            if (forCluster) {
                asView.getRouteList().add(rtv);
            } else {
                buildNodeAndAppendNodeFromRouteView(doc,
                        rtv, as);
            }

        }

        if (!forCluster) {
            saveAndRefactorXml(inputFile);
        }

    }

    public void generateRingTopo(String asId, int numOfHost) {
        Element as = doc.createElement("AS");
        as.setAttribute("id", asId);
        as.setAttribute("routing", "Dijkstra");
        doc.getDocumentElement().appendChild(as);

        for (int i = 0; i < numOfHost; i++) {
            Node host = buildNodeFromHostView(doc,
                    new HostView(new Image(new File("src/graphicaleditor/res/host.jpg").toURI().toString(), GraphicalModeController.VIEW_SIZE, GraphicalModeController.VIEW_SIZE, true, true),
                            "host" + i,
                            1000000000,
                            "ON",
                            0,
                            1.0,
                            1,
                            0));
            as.appendChild(host);

        }

        for (int i = 0; i < numOfHost; i++) {
            ArrayList<LinkView> links = new ArrayList<>();
            links.add(new LinkView("link" + i, 1250000000, 0, "ON", "SHARED"));
            RouteView rv = new RouteView("host" + i, "host" + (i + 1) % numOfHost, links);
            buildNodeAndAppendNodeFromRouteView(doc,
                    rv, as);
//            as.appendChild(route);
//            buildNodeFromRouteViewStep2(doc, rv, (Element) route);
        }

        saveAndRefactorXml(inputFile);

    }

    public void generateTorusTopo(String asId, int x, int y, int z) {
        Element as = doc.createElement("AS");
        as.setAttribute("id", asId);
        as.setAttribute("routing", "Dijkstra");
        doc.getDocumentElement().appendChild(as);

        // gen host
        genHost(as, x, y, z);

        // gen routes
        genRoute(as, x, y, z);

        saveAndRefactorXml(inputFile);

    }

    public void generateMeshTopo(String asId, int x, int y, int z) {
        Element as = doc.createElement("AS");
        as.setAttribute("id", asId);
        as.setAttribute("routing", "Dijkstra");
        doc.getDocumentElement().appendChild(as);

        // gen torus
        genHost(as, x, y, z);
        genRoute(as, x, y, z);

        // remove redundant from torus
        rmvRedundantRoute(as, x, y, z);

        saveAndRefactorXml(inputFile);

    }

    private void rmvRedundantRoute(Element as, int x, int y, int z) {
        int i = 0, j = 0, k = 0;
        NodeList routes = as.getElementsByTagName("route");
        if (x >= 3) {
            for (j = 0; j < y; j++) {
                for (k = 0; k < z; k++) {
                    for (int m = 0; m < routes.getLength(); m++) {
                        Element route = (Element) routes.item(m);
                        if ((route.getAttribute("src").equals("host0" + j + "" + k) && route.getAttribute("dst").equals("host" + (x - 1) + "" + j + "" + k))
                                || (route.getAttribute("dst").equals("host0" + j + "" + k) && route.getAttribute("src").equals("host" + (x - 1) + "" + j + "" + k))) {
                            as.removeChild(route);
                        }
                    }
                }
            }
        }

        if (y >= 3) {
            for (i = 0; i < x; i++) {
                for (k = 0; k < z; k++) {
                    for (int m = 0; m < routes.getLength(); m++) {
                        Element route = (Element) routes.item(m);
                        if ((route.getAttribute("src").equals("host" + i + "0" + "" + k) && route.getAttribute("dst").equals("host" + i + "" + (y - 1) + "" + k))
                                || (route.getAttribute("dst").equals("host" + i + "0" + "" + k) && route.getAttribute("src").equals("host" + i + "" + (y - 1) + "" + k))) {
                            as.removeChild(route);
                        }
                    }
                }
            }
        }

        if (z >= 3) {
            for (i = 0; i < x; i++) {
                for (j = 0; j < y; j++) {
                    for (int m = 0; m < routes.getLength(); m++) {
                        Element route = (Element) routes.item(m);
                        if ((route.getAttribute("src").equals("host" + i + "" + j + "0") && route.getAttribute("dst").equals("host" + i + "" + j + "" + (z - 1)))
                                || (route.getAttribute("dst").equals("host" + i + "" + j + "0") && route.getAttribute("src").equals("host" + i + "" + j + "" + (z - 1)))) {
                            as.removeChild(route);
                        }
                    }
                }
            }
        }

    }

    private void genHost(Element as, int x, int y, int z) {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                for (int k = 0; k < z; k++) {
                    Node host = buildNodeFromHostView(doc,
                            new HostView(new Image(new File("src/graphicaleditor/res/host.jpg").toURI().toString(), GraphicalModeController.VIEW_SIZE, GraphicalModeController.VIEW_SIZE, true, true),
                                    "host" + i + "" + j + "" + k,
                                    1000000000,
                                    "ON",
                                    0,
                                    1.0,
                                    1,
                                    0));
                    as.appendChild(host);
                }
            }
        }
    }

    private void genRoute(Element as, int x, int y, int z) {
        int i = 0, j = 0, k = 0;

        int m = 0;

        if (z > 2) {
            for (i = 0; i < x; i++) {

                for (j = 0; j < y; j++) {

                    for (k = 0; k < z; k++) {
                        ArrayList<LinkView> links = new ArrayList<>();
                        links.add(new LinkView("link" + m, 1250000000, 0, "ON", ""));
                        buildNodeAndAppendNodeFromRouteView(doc,
                                new RouteView("host" + i + "" + j + "" + k, "host" + i + "" + j + "" + ((k + 1) % z), links), as);
                        m++;
                    }
                }

            }
        } else if (z == 2) {
            for (i = 0; i < x; i++) {

                for (j = 0; j < y; j++) {

//                    for (k = 0; k < z; k++) {
                    ArrayList<LinkView> links = new ArrayList<>();
                    links.add(new LinkView("link" + m, 1250000000, 0, "ON", ""));
                    buildNodeAndAppendNodeFromRouteView(doc,
                            new RouteView("host" + i + "" + j + "0", "host" + i + "" + j + "1", links), as);
                    m++;
//                    as.appendChild(routez);
//                    }
                }

            }
        }

        if (y > 2) {
            for (i = 0; i < x; i++) {
                for (k = 0; k < z; k++) {
                    for (j = 0; j < y; j++) {
                        ArrayList<LinkView> links = new ArrayList<>();
                        links.add(new LinkView("link" + m, 1250000000, 0, "ON", ""));
                        buildNodeAndAppendNodeFromRouteView(doc,
                                new RouteView("host" + i + "" + j + "" + k, "host" + i + "" + ((j + 1) % y) + "" + k, links), as);
                        m++;
//                        as.appendChild(routey);
                    }
                }
            }
        } else if (y == 2) {
            for (i = 0; i < x; i++) {
                for (k = 0; k < z; k++) {
//                    for (j = 0; j < y; j++) {
                    ArrayList<LinkView> links = new ArrayList<>();
                    links.add(new LinkView("link" + m, 1250000000, 0, "ON", ""));
                    buildNodeAndAppendNodeFromRouteView(doc,
                            new RouteView("host" + i + "0" + "" + k, "host" + i + "1" + "" + k, links), as);
                    m++;
//                    as.appendChild(routey);
//                    }
                }
            }
        }

        if (x > 2) {
            for (j = 0; j < y; j++) {
                for (k = 0; k < z; k++) {
                    for (i = 0; i < x; i++) {
                        ArrayList<LinkView> links = new ArrayList<>();
                        links.add(new LinkView("link" + m, 1250000000, 0, "ON", ""));
                        buildNodeAndAppendNodeFromRouteView(doc,
                                new RouteView("host" + i + "" + j + "" + k, "host" + ((i + 1) % x) + "" + j + "" + k, links), as);
                        m++;
//                        as.appendChild(routex);
                    }
                }
            }
        } else if (x == 2) {
            for (j = 0; j < y; j++) {
                for (k = 0; k < z; k++) {
//                    for (i = 0; i < x; i++) {
                    ArrayList<LinkView> links = new ArrayList<>();
                    links.add(new LinkView("link" + m, 1250000000, 0, "ON", ""));
                    buildNodeAndAppendNodeFromRouteView(doc,
                            new RouteView("host" + "0" + "" + j + "" + k, "host" + "1" + "" + j + "" + k, links), as);
                    m++;
//                    as.appendChild(routex);
//                    }
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////
    // for Benchmarking
    private void genHimenoBM(int numprocs, String himenoClass) {
        Element hmn = doc.createElement("benchmark");
        hmn.setAttribute("type", "himeno");
        hmn.setAttribute("numprocs", String.valueOf(numprocs));
        hmn.setAttribute("class", himenoClass);

        doc.getDocumentElement().appendChild(hmn);

    }

    public void genBM(boolean useHimeno, boolean useGraph500, boolean useNAS, int HimenoNumprocs, String himenoClass,
            int graph500Numprocs, int scale,
            String kernel, String klass, int NASNumprocs) {
        if (useHimeno) {
            genHimenoBM(HimenoNumprocs, himenoClass);
        }

        if (useGraph500) {
            genGraph500BM(graph500Numprocs, scale);
        }

        if (useNAS) {
            genNASBM(kernel, klass, NASNumprocs);
        }

        saveAndRefactorXml(inputFile);

    }

    private void genGraph500BM(int numprocs, int scale) {
        Element hmn = doc.createElement("benchmark");
        hmn.setAttribute("type", "graph500");
        hmn.setAttribute("numprocs", String.valueOf(numprocs));
        hmn.setAttribute("scale", String.valueOf(scale));
        doc.getDocumentElement().appendChild(hmn);

    }

    private void genNASBM(String kernel, String klass, int numprocs) {
        Element hmn = doc.createElement("benchmark");
        hmn.setAttribute("type", "NAS");
        hmn.setAttribute("kernel", String.valueOf(kernel));
        hmn.setAttribute("class", klass);
        hmn.setAttribute("numprocs", String.valueOf(numprocs));
        doc.getDocumentElement().appendChild(hmn);

    }

    //////////////////////////////////////////////////////////////
    // getters, setters
    public List<ASView> getAsList() {
        return asList;
    }

    public void setAsList(List<ASView> asList) {
        this.asList = asList;
    }

    ///////////////////////////////////////////////////////////////////
    // some utility functions
    /**
     * only call after parse()
     *
     */
    public List<String> getHostRouterIdList(boolean includeRouter) {
        List<String> listHostRouterId = new ArrayList<>();
        for (ASView as : asList) {
            if (as.getHostList().size() > 0) {
                List<HostView> list = as.getHostList();
                for (HostView h : list) {
                    listHostRouterId.add(h.getmId());
                }
            }

            if (includeRouter) {
                if (as.getRouteList().size() > 0) {
                    List<RouterView> list = as.getRouterList();
                    for (RouterView r : list) {
                        listHostRouterId.add(r.getmId());
                    }
                }
            }
        }

        return listHostRouterId;
    }

}
