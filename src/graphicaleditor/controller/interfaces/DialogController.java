/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphicaleditor.controller.interfaces;

import graphicaleditor.controller.FXMLDocumentController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 *
 * @author KHANH
 */
public abstract class DialogController implements Initializable {
    protected FXMLDocumentController parentController;
    
    @FXML
    protected Button okBtn;
    
    @FXML
    protected Button cancelBtn;
    
    public void setParentController(FXMLDocumentController c) {
        parentController = c;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public Button getOkBtn() {
        return okBtn;
    }

    public void setOkBtn(Button okBtn) {
        this.okBtn = okBtn;
    }

    public Button getCancelBtn() {
        return cancelBtn;
    }

    public void setCancelBtn(Button cancelBtn) {
        this.cancelBtn = cancelBtn;
    }
    
    

}
