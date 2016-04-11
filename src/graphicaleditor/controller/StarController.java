/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphicaleditor.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 *
 * @author KHANH
 */
/**
 *
 * @author KHANH
 */
public class StarController implements Initializable {
    
    @FXML
    private TextField asId;
    
    @FXML
    private TextField numOfHost;
    
    @FXML
    private Button okBtn;
    
    @FXML
    private Button cancelBtn;
    
    @FXML
    private FXMLDocumentController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public Button getOkBtn() {
        return okBtn;
    }

    public Button getCancelBtn() {
        return cancelBtn;
    }

    public TextField getAsId() {
        return asId;
    }

    public TextField getNumOfHost() {
        return numOfHost;
    }

    
    public void setParentController(FXMLDocumentController c) {
        this.parentController = c;
    }
    
}

