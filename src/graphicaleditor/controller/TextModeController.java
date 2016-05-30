/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicaleditor.controller;

import graphicaleditor.controller.fileprocessors.TextFileProcessor;
import graphicaleditor.controller.fileprocessors.XMLProcessor;
import graphicaleditor.model.ASView;
import graphicaleditor.model.HostView;
import graphicaleditor.model.RouteView;
import graphicaleditor.model.RouteViewRoom;
import graphicaleditor.model.RouterView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
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

    private boolean start = false;

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
        init();
    }

    private void init() {

    }

    @SuppressWarnings(
            "NestedAssignment")
    public void loadTextModeAndHostFilePane(String fileName) {

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                lines.add(line);
            }
        } catch (IOException e) {

        }
        textAreaTextMode.clear();
        parentController.getGraphicalModeController().renderHostFilePane();
        
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
            Logger.getLogger(TextModeController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

//        StringTokenizer tokenizer = new StringTokenizer(text);
//        List<String> lines = new ArrayList<>();
//        while(tokenizer.hasMoreTokens()) {
//            lines.add(tokenizer.nextToken());
//        }
        return file;
    }
}
