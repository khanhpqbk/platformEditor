/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphicaleditor.controller.benchmark;

import graphicaleditor.controller.FXMLDocumentController;
import graphicaleditor.controller.interfaces.DialogController;
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
public class HimenoController extends DialogController {

    @FXML
    private TextField numOfProcs;

    public TextField getNumOfProcs() {
        return numOfProcs;
    }

    public void setNumOfProcs(TextField numOfProcs) {
        this.numOfProcs = numOfProcs;
    }
    
    

}
