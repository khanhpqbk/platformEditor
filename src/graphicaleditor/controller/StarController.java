/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphicaleditor.controller;

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
/**
 *
 * @author KHANH
 */
public class StarController extends DialogController {
    
    @FXML
    private TextField asId;
    
    @FXML
    private TextField numOfHost;

    public TextField getAsId() {
        return asId;
    }

    public TextField getNumOfHost() {
        return numOfHost;
    }
    
}

