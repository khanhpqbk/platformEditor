/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package graphicaleditor.controller.benchmark;

import graphicaleditor.controller.interfaces.AbstractDialogController;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 *
 * @author KHANH
 */
public class Graph500Controller extends AbstractDialogController {
    @FXML
    private TextField numprocs;
    
    @FXML
    private TextField scale;
    
    @FXML
    private TextField edgeFactor;
    
    @FXML
    private TextField engine;

    public TextField getNumprocs() {
        return numprocs;
    }

    public void setNumprocs(TextField numprocs) {
        this.numprocs = numprocs;
    }

    public TextField getScale() {
        return scale;
    }

    public void setScale(TextField scale) {
        this.scale = scale;
    }

    public TextField getEdgeFactor() {
        return edgeFactor;
    }

    public void setEdgeFactor(TextField edgeFactor) {
        this.edgeFactor = edgeFactor;
    }

    public TextField getEngine() {
        return engine;
    }

    public void setEngine(TextField engine) {
        this.engine = engine;
    }
    
    
    
}
