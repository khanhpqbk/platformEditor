/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicaleditor.controller.benchmark;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.sun.javafx.scene.control.skin.VirtualFlow.ArrayLinkedList;
import graphicaleditor.controller.FXMLDocumentController;
import graphicaleditor.controller.fileprocessors.TextFileProcessor;
import graphicaleditor.controller.interfaces.AbstractDialogController;
import graphicaleditor.controller.fileprocessors.XMLProcessor;
import graphicaleditor.model.ASView;
import graphicaleditor.model.HostView;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Callback;

/**
 *
 * @author KHANH
 */
public class HostFileController extends AbstractDialogController {

    @FXML
    private ListView listView;

    @FXML
    private Button btn;
    
    @FXML
    private Button savedFileBtn;

    @FXML
    private Label lbl;
    
    @FXML
    private Label savedLbl;
    
    private XMLProcessor p;

    private File selectedFile = null;
    private File savedFile;
    private CheckBox cb = null;
    
    public void setParentController(FXMLDocumentController c) {
        super.setParentController(c);
        c.setHostFileController(this);
    }

    public void init() {

        btn.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                FileChooser fc = new FileChooser();
                selectedFile = fc.showOpenDialog(null);
                if (selectedFile != null) {
                    lbl.setText(selectedFile.getName());
                    setListView();
                    parentController.getGraphicalModeController().renderOutsideView(p);
                    parentController.setSeletedFile(selectedFile);
                }
            }
        });
        
        savedFileBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                FileChooser fc = new FileChooser();
                savedFile = fc.showSaveDialog(null);
                if (savedFile != null) {
                    savedLbl.setText(savedFile.getName());
                }
            }
        });

    }

    private List<String> getList(ASView as) {
        List<String> l = new ArrayList<>();
        for (HostView h : as.getHostList()) {
            if (h.isSelected()) {
                l.add(h.getmId());
            }
        }
        return l;
    }

    private void setListView() {
        p = new XMLProcessor(selectedFile.getAbsolutePath());
        p.parse();
        ASView as = p.getAsList().get(0);
//        ArrayLinkedList<String> ll = new ArrayLinkedList<>();
        listView.setItems(FXCollections.observableArrayList(as.getHostList()));
        listView.setCellFactory(new Callback<ListView<HostView>, ListCell<HostView>>() {
            @Override
            public ListCell<HostView> call(ListView<HostView> list) {
                return new ColorRectCell();
            }
        }
        );
    }

    class ColorRectCell extends ListCell<HostView> {

        @Override
        public void updateItem(HostView item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                cb = new CheckBox();

                cb.setSelected(item.isSelected());
                Label lbl = new Label(item.getmId());
                HBox rect = new HBox(cb, lbl);
                setGraphic(rect);
                
                cb.selectedProperty().addListener(new ChangeListener<Boolean>() {

                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        item.setSelected(newValue);
                    }
                });
            }
        }
    }
    
    public CheckBox getCb() {
        return cb;
    }
    
    public ListView getListView() {
        return listView;
    }
    
    public XMLProcessor getXMLProcessor() {
        return p;
    }
        
    public File getSavedFile() {
        return savedFile;
    }
    
    public File getSelectedFile() {
        return selectedFile;
    }
}
