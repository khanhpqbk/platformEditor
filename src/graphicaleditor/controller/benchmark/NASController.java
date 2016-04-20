/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicaleditor.controller.benchmark;

import graphicaleditor.controller.interfaces.AbstractDialogController;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

/**
 *
 * @author KHANH
 */
public class NASController extends AbstractDialogController {

    @FXML
    private ComboBox<String> kernelCombobox;

    @FXML
    private ComboBox<String> classCombobox;

    @FXML
    private ComboBox<Integer> numprocsCombobox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    
    boolean changeKernel = false;
    boolean changeClass = false;

    public void init() {
//        boolean change = false;
        kernelCombobox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                    changeKernel = true;
                    if (changeClass) {
                        setNumprocs();
                    }
                }
            }
        );
        
        classCombobox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                    changeClass = true;
                    if (changeKernel) {
                        setNumprocs();
                    }
                }
            }
        );
        
        
//        switch (kernelCombobox.getValue()) {
//            case "bt":
//                System.out.println("bt");
//                break;
//            case "cg":
//                System.out.println("cg");
//                break;
//            case "is":
//                System.out.println("is");
//                break;
//            case "ep":
//                System.out.println("ep");
//                break;
//            default:
//                break;
//        }
    }
    
    private void setNumprocs() {
        String kernel = kernelCombobox.getValue();
        String klass = classCombobox.getValue();
//        System.out.println(kernel + "___" + klass);
        if (kernel.equals("bt")) {
            if (klass.equals("S")) {
                setMaxNumprocs(16, "bt");
            } else {
                setMaxNumprocs(121, "bt");
            } 
        } else {
            if (klass.equals("S")) {
                setMaxNumprocs(16, "others");
            } else  {
                setMaxNumprocs(128, "others");
            } 
            
        }
    }
    
    private void setMaxNumprocs(int value, String kernel) {
        Integer[] intsOther = new Integer[] {1, 2, 4, 8, 16, 32, 64, 128};
        Integer[] intsBt = new Integer[] {1, 4, 9, 16, 25, 36, 49, 64, 81, 100, 121};
        numprocsCombobox.getItems().clear();
        if (kernel.equals("bt")) {
            for (int i = 0; i < 12; i++) {
                if (intsBt[i] <= value)
                    numprocsCombobox.getItems().add(intsBt[i]);
                else break;
            }
        } else {
            for (int i = 0; i < 12; i++) {
                if (intsOther[i] <= value)
                    numprocsCombobox.getItems().add(intsOther[i]);
                else break;
            }
        }
        
    }

    public ComboBox<String> getKernelCombobox() {
        return kernelCombobox;
    }

    public void setKernelCombobox(ComboBox<String> kernelCombobox) {
        this.kernelCombobox = kernelCombobox;
    }

    public ComboBox<String> getClassCombobox() {
        return classCombobox;
    }

    public void setClassCombobox(ComboBox<String> classCombobox) {
        this.classCombobox = classCombobox;
    }

    public ComboBox<Integer> getNumprocsCombobox() {
        return numprocsCombobox;
    }

    public void setNumprocsCombobox(ComboBox<Integer> numprocsCombobox) {
        this.numprocsCombobox = numprocsCombobox;
    }

}
