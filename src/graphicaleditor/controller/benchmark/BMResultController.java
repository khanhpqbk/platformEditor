/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicaleditor.controller.benchmark;

import graphicaleditor.controller.interfaces.Controller;
import graphicaleditor.controller.interfaces.IInit;
import graphicaleditor.model.benchmark.Benchmark;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 *
 * @author khanh
 */
public class BMResultController extends Controller implements IInit<List<Benchmark>> {
    
    @FXML
    private TextArea himeno;
    
    @FXML
    private TextArea graph500;
    
    @FXML
    private TextArea nas;

    @Override
    public void init(List<Benchmark> bms) {
        for(Benchmark bm: bms) {
            if(bm.getType().equalsIgnoreCase("himeno")) {
                himeno.setText(himeno.getText() + bm.getResult());
            } else if (bm.getType().equalsIgnoreCase("NAS")) {
                nas.setText(nas.getText() + "\n\n" + "Kernel: " + bm.getMoreInfo() + "\n"  + bm.getResult());
            } else if (bm.getType().equalsIgnoreCase("graph500")) {
                graph500.setText(graph500.getText() + bm.getResult());
            }
        }
    }
    
}
