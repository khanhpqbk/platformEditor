/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicaleditor.controller;

import graphicaleditor.controller.xml.TextFileReader;
import graphicaleditor.controller.xml.XMLProcessor;
import graphicaleditor.model.ASView;
import graphicaleditor.model.HostView;
import graphicaleditor.model.RouteView;
import graphicaleditor.model.RouteViewRoom;
import graphicaleditor.model.RouterView;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 *
 * @author KHANH
 */
public class TextModeController implements Initializable {

    private static final Logger LOG = Logger.getLogger(TextModeController.class.getName());

    @FXML
    private TextArea textAreaTextMode;

    private FXMLDocumentController parentController;

    public void setParentController(FXMLDocumentController c) {
        this.parentController = c;
    }

    public void clearText() {
        textAreaTextMode.clear();
    }

    public void addASXml(ASView as) {
        System.out.println("add AS xml");

        XMLProcessor parser = new XMLProcessor(parentController.getSelectedFile().getAbsolutePath());
        parser.addASXml(as);

    }

    public void addHostXml(ASView as, HostView host) {
        System.out.println("add host xml");
        XMLProcessor parser = new XMLProcessor(parentController.getSelectedFile().getAbsolutePath());
        parser.addHostXml(as, host);
    }

    public void removeASXml(ASView as) {
        XMLProcessor parser = new XMLProcessor(parentController.getSelectedFile().getAbsolutePath());
        parser.removeASXml(as);

    }

    public void removeHostXml(ASView as, HostView host) {
        XMLProcessor parser = new XMLProcessor(parentController.getSelectedFile().getAbsolutePath());
        parser.removeHostXml(as, host);

    }

    public void modifyHostXml(HostView newHost, String oldId) {
        XMLProcessor parser = new XMLProcessor(parentController.getSelectedFile().getAbsolutePath());
        parser.modifyHostXml(newHost, oldId);

    }

    public void addRouteXml(ASView as, RouteView rv) {
        XMLProcessor parser = new XMLProcessor(parentController.getSelectedFile().getAbsolutePath());
        parser.addRouteXml(as, rv);
    }

    public void removeRouteXml(RouteView rv) {
        XMLProcessor parser = new XMLProcessor(parentController.getSelectedFile().getAbsolutePath());
        parser.removeRouteXml(rv);
    }

    public void addRouteRoomXml(ASView as, RouteViewRoom rv) {
        XMLProcessor parser = new XMLProcessor(parentController.getSelectedFile().getAbsolutePath());
        parser.addRouteRoomXml(as, rv);
    }

    public void removeRouteRoomXml(RouteViewRoom rv) {
        XMLProcessor parser = new XMLProcessor(parentController.getSelectedFile().getAbsolutePath());
        parser.removeRouteRoomXml(rv);
    }

    public void addRouterXml(ASView as, RouterView rv) {
        XMLProcessor parser = new XMLProcessor(parentController.getSelectedFile().getAbsolutePath());
        parser.addRouterXml(as, rv);
    }

    public void removeRouterXml(RouterView rv) {
        XMLProcessor parser = new XMLProcessor(parentController.getSelectedFile().getAbsolutePath());
        parser.removeRouterXml(rv);
    }

    public void modifyRouterXml(RouterView oldR, RouterView newR) {
        XMLProcessor parser = new XMLProcessor(parentController.getSelectedFile().getAbsolutePath());
        parser.modifyRouterXml(newR, oldR.getmId());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @SuppressWarnings(
            "NestedAssignment")
    public void loadFileToTextEditor(String fileName) {

        Future<List<String>> future;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        TextFileReader reader = new TextFileReader();

        future = executorService.submit(new Callable<List<String>>() {
            public List<String> call() throws Exception {
                return reader.read(new File(fileName));
            }
        });

        List<String> lines = new ArrayList<>();
        try {
            lines = future.get();
        } catch (InterruptedException ex) {
            Logger.getLogger(TextModeController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(TextModeController.class.getName()).log(Level.SEVERE, null, ex);
        }

        executorService.shutdownNow();
        textAreaTextMode.clear();
        for (String line : lines) {
            textAreaTextMode.appendText(line + "\n");
        }

    }

    public File saveFileFromTextEditor(String fileName) {
        File file = new File(fileName);
        String text = textAreaTextMode.getText();

        StringTokenizer tokenizer = new StringTokenizer(text, "\n");
        List<String> lines = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            lines.add(tokenizer.nextToken());
        }

        FileWriter fw;
        try {
            fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
//                System.out.println(line);
            }
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(TextModeController.class.getName()).log(Level.SEVERE, null, ex);
        }

//        StringTokenizer tokenizer = new StringTokenizer(text);
//        List<String> lines = new ArrayList<>();
//        while(tokenizer.hasMoreTokens()) {
//            lines.add(tokenizer.nextToken());
//        }
        return file;
    }
}
