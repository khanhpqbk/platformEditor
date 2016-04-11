/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicaleditor.controller;

import graphicaleditor.model.HostView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author KHANH
 */
public class HostDetailController implements Initializable {

    @FXML
    private TextField id;

    @FXML
    private TextField power;

    @FXML
    private TextField state;

    @FXML
    private TextField pstate;

    @FXML
    private TextField core;

    @FXML
    private TextField availability;

    @FXML
    private Button btnOk;

    @FXML
    private Button btnCancel;
    
    @FXML
    private Label warningLbl;

    private HostView hostView;

    private GraphicalModeController parentController;

    public void setParentController(GraphicalModeController g) {
        this.parentController = g;
    }
    public void setHostView(HostView h) {
        this.hostView = h;
    }

    public void init(HostView h) {
        this.hostView = h;
        id.setText(hostView.getmId());
        power.setText(String.valueOf(hostView.getPower()));
        state.setText(String.valueOf(hostView.getState()));
        pstate.setText(String.valueOf(hostView.getPstate()));
        availability.setText(String.valueOf(hostView.getAvailability()));
        core.setText(String.valueOf(hostView.getCore()));

        id.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                //            System.out.println("textfield changed from " + oldValue + " to " + newValue);
                XMLProcessor p = new XMLProcessor(parentController.getParentController().getSelectedFile().getAbsolutePath());
                p.parse();
                if (p.getHostRouterIdList().contains(newValue)) {
                    warningLbl.setText("Duplicate host id!");
                    warningLbl.setTextFill(Color.RED);
                    btnOk.setDisable(true);
                } else {
                    warningLbl.setText("");
                    btnOk.setDisable(false);
                }
            } catch (SAXException ex) {
                Logger.getLogger(HostDetailController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(HostDetailController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(HostDetailController.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        id.setText(hostView.getId());
//        power.setText(String.valueOf(hostView.getPower()));
//        state.setText(String.valueOf(hostView.isState()));
    }

    public HostView createHostFromFields() {
        HostView h = new HostView(null);
        h.setmId(id.getText());
        h.setPower(Integer.parseInt(power.getText()));
        h.setState(state.getText());
        h.setAvailability(Double.parseDouble(availability.getText()));
        h.setPstate(Double.parseDouble(pstate.getText()));
        h.setCore(Integer.parseInt(core.getText()));
//        h.setCoordinates(Integer.parseInt(coo));
        return h;
    }

    public TextField getId() {
        return id;
    }

    public TextField getPower() {
        return power;
    }

    public TextField getState() {
        return state;
    }

    public Button getBtnOk() {
        return btnOk;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    public HostView getHostView() {
        return hostView;
    }

}
