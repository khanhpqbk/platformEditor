/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicaleditor.controller.benchmark;

import graphicaleditor.controller.interfaces.AbstractDialogController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 *
 * @author KHANH
 */
public class BMController extends AbstractDialogController {

    @FXML
    private CheckBox useHimeno;

    @FXML
    private CheckBox useNAS;

    @FXML
    private CheckBox useGraph500;

    @FXML
    private TextField himenoNumprocs;

    @FXML
    private TextField graph500Numprocs;

    @FXML
    private TextField scale;

    @FXML
    private TextField edgeFactor;

    @FXML
    private TextField engine;

    @FXML
    private ComboBox<String> kernel;

    @FXML
    private ComboBox<String> klass;

    @FXML
    private ComboBox<Integer> NASNumprocs;

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

    public TextField getHimenoNumprocs() {
        return himenoNumprocs;
    }

    public void setHimenoNumprocs(TextField himenoNumprocs) {
        this.himenoNumprocs = himenoNumprocs;
    }

    public TextField getGraph500Numprocs() {
        return graph500Numprocs;
    }

    public void setGraph500Numprocs(TextField graph500Numprocs) {
        this.graph500Numprocs = graph500Numprocs;
    }

    public ComboBox<String> getKernel() {
        return kernel;
    }

    public void setKernel(ComboBox<String> kernel) {
        this.kernel = kernel;
    }

    public ComboBox<String> getKlass() {
        return klass;
    }

    public void setKlass(ComboBox<String> klass) {
        this.klass = klass;
    }

    public ComboBox<Integer> getNASNumprocs() {
        return NASNumprocs;
    }

    public void setNASNumprocs(ComboBox<Integer> NASNumprocs) {
        this.NASNumprocs = NASNumprocs;
    }

    public CheckBox getUseHimeno() {
        return useHimeno;
    }

    public void setUseHimeno(CheckBox useHimeno) {
        this.useHimeno = useHimeno;
    }

    public CheckBox getUseGraph500() {
        return useGraph500;
    }

    public void setUseGraph500(CheckBox useGraph500) {
        this.useGraph500 = useGraph500;
    }

    public CheckBox getUseNAS() {
        return useNAS;
    }

    public void setUseNAS(CheckBox useNAS) {
        this.useNAS = useNAS;
    }

    boolean changeKernel = false;
    boolean changeClass = false;

    public void init() {
        himenoNumprocs.setDisable(true);
        graph500Numprocs.setDisable(true);
        edgeFactor.setDisable(true);
        scale.setDisable(true);
        engine.setDisable(true);
        NASNumprocs.setDisable(true);
        kernel.setDisable(true);
        klass.setDisable(true);

        useHimeno.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                himenoNumprocs.setDisable(!newValue);
            }
        });

        useGraph500.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                graph500Numprocs.setDisable(!newValue);
                edgeFactor.setDisable(!newValue);
                scale.setDisable(!newValue);
                engine.setDisable(!newValue);
            }
        });

        useNAS.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                NASNumprocs.setDisable(!newValue);
                kernel.setDisable(!newValue);
                klass.setDisable(!newValue);
            }
        });

        kernel.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                changeKernel = true;
                if (changeClass) {
                    setNumprocs();
                }
            }
        }
        );

        klass.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                changeClass = true;
                if (changeKernel) {
                    setNumprocs();
                }
            }
        }
        );
    }

    private void setNumprocs() {
        String kernelStr = kernel.getValue();
        String klassStr
                = klass.getValue();
//        System.out.println(kernel + "___" + klass);
        if (kernelStr.equals("bt")) {
            if (klassStr.equals("S")) {
                setMaxNumprocs(16, "bt");
            } else {
                setMaxNumprocs(121, "bt");
            }
        } else {
            if (klassStr.equals("S")) {
                setMaxNumprocs(16, "others");
            } else {
                setMaxNumprocs(128, "others");
            }

        }
    }

    private void setMaxNumprocs(int value, String kernel) {
        Integer[] intsOther = new Integer[]{1, 2, 4, 8, 16, 32, 64, 128};
        Integer[] intsBt = new Integer[]{1, 4, 9, 16, 25, 36, 49, 64, 81, 100, 121};
        NASNumprocs.getItems().clear();
        if (kernel.equals("bt")) {
            for (int i = 0; i < 11; i++) {
                if (intsBt[i] <= value) {
                    NASNumprocs.getItems().add(intsBt[i]);
                } else {
                    break;
                }
            }
        } else {
            for (int i = 0; i < 8; i++) {
                if (intsOther[i] <= value) {
                    NASNumprocs.getItems().add(intsOther[i]);
                } else {
                    break;
                }
            }
        }

    }

}
