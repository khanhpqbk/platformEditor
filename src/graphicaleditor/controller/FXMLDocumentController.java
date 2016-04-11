/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicaleditor.controller;

import graphicaleditor.model.ASView;
import graphicaleditor.model.Host;
import graphicaleditor.model.HostView;
import graphicaleditor.model.LinkView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author KHANH
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private MenuBar menuBar;

    @FXML
    private TextModeController textModeController;

    @FXML
    private GraphicalModeController graphicalModeController;

    private File selectedFile;

    @FXML
    private MenuItem turnOnOff;

    

    /**
     * Handle action related to "About" menu item.
     *
     * @param event Event on "About" menu item.
     */
    @FXML
    private void handleAboutAction(final ActionEvent event) {
        provideAboutFunctionality();
    }

    @FXML
    private void handleExitAction() {
        provideExitFunctionality();
    }

    @FXML
    private void genRing() {
        graphicalModeController.clearView();
        textModeController.clearText();
        System.out.println("gen ring");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader().getResource(
                            "graphicaleditor/view/ring.fxml"
                    )
            );
            final Stage dialog = new Stage();
            Parent root = (Parent) loader.load();

            RingController controller
                    = loader.<RingController>getController();
            controller.setParentController(this);

            controller.getOkBtn().setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    FileChooser chooser = new FileChooser();
                    selectedFile = chooser.showSaveDialog(null);
                    generatePlatformElement();
                    try {
                        XMLProcessor processor = new XMLProcessor(selectedFile.getAbsolutePath());
                        processor.generateRingTopo(controller.getAsId().getText(), Integer.parseInt(controller.getNumOfHost().getText()));
                        processor.parse();
                        graphicalModeController.renderOutsideView(processor);
                    } catch (SAXException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ParserConfigurationException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dialog.close();
                    textModeController.loadFileToTextEditor(selectedFile.getAbsolutePath());

                }

            });

            controller.getCancelBtn().setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
//                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    dialog.close();
                }
            });

            Scene scene = new Scene(root, 500, 400);

//            dialog.initStyle(StageStyle.UTILITY);
            dialog.setScene(scene);
            dialog.show();

        } catch (IOException ex) {
            Logger.getLogger(GraphicalModeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handle action related to input (in this case specifically only responds
     * to keyboard event CTRL-A).
     *
     * @param event Input event.
     */
    @FXML
    private void handleKeyInput(final InputEvent event) throws InterruptedException, ExecutionException, SAXException, IOException, ParserConfigurationException {
        if (event instanceof KeyEvent) {
            final KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.A) {
                provideAboutFunctionality();
            } else if (keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.F4) {
                provideExitFunctionality();
            } else if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.O) {
                provideOpenFunctionality();
            } else if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.S) {
                provideSaveFunctionality();
            } else if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.B) {
//                textModeController.saveFileFromTextEditor(selectedFile.getAbsolutePath());
                XMLProcessor parser = new XMLProcessor(selectedFile.getAbsolutePath());
                parser.parse();
                graphicalModeController.clearView();
                graphicalModeController.renderOutsideView(parser);
                graphicalModeController.setIsOutside(true);
            } else if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.N) {
                provideNewFileFunctionality();
            } else if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.T) {
                // TEST GEN TOPO
                selectedFile = new File("C:\\mesh231.xml");
                generatePlatformElement();
                XMLProcessor xMLProcessor = new XMLProcessor(selectedFile.getAbsolutePath());
                xMLProcessor.generateMeshTopo("asid", 2, 3, 1);
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                if (graphicalModeController.getAddRareaCount() >= 0) {
                    graphicalModeController.setAddRareaCount(-1);
                    System.out.println("exit add restricted area mode");
                }
            }
        }
    }

    @FXML
    private void open() {
        try {
            provideOpenFunctionality();
        } catch (InterruptedException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void newFile() {
        provideNewFileFunctionality();
    }

    @FXML
    private void genStar() {
        graphicalModeController.clearView();
        textModeController.clearText();
        System.out.println("gen star");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader().getResource(
                            "graphicaleditor/view/star.fxml"
                    )
            );
            final Stage dialog = new Stage();
            Parent root = (Parent) loader.load();

            StarController controller
                    = loader.<StarController>getController();
            controller.setParentController(this);

            controller.getOkBtn().setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    FileChooser chooser = new FileChooser();
                    selectedFile = chooser.showSaveDialog(null);
                    generatePlatformElement();
                    try {
                        XMLProcessor processor = new XMLProcessor(selectedFile.getAbsolutePath());
                        ASView as = new ASView(null, controller.getAsId().getText());
                        processor.generateStarTopo(false, as, Integer.parseInt(controller.getNumOfHost().getText()),
                                "", "", 0, 0, 0);
                        processor.parse();
                        graphicalModeController.renderOutsideView(processor);
                        textModeController.loadFileToTextEditor(selectedFile.getAbsolutePath());
                    } catch (SAXException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ParserConfigurationException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dialog.close();

                }

            });

            controller.getCancelBtn().setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
//                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    dialog.close();
                }
            });

            Scene scene = new Scene(root, 500, 400);

//            dialog.initStyle(StageStyle.UTILITY);
            dialog.setScene(scene);
            dialog.show();

        } catch (IOException ex) {
            Logger.getLogger(GraphicalModeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void genTorus() {
        graphicalModeController.clearView();
        textModeController.clearText();
        System.out.println("gen torus");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader().getResource(
                            "graphicaleditor/view/torus.fxml"
                    )
            );
            final Stage dialog = new Stage();
            Parent root = (Parent) loader.load();

            TorusController controller
                    = loader.<TorusController>getController();
            controller.setParentController(this);

            controller.getOkBtn().setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    FileChooser chooser = new FileChooser();
                    selectedFile = chooser.showSaveDialog(null);
                    generatePlatformElement();
                    try {
                        XMLProcessor processor = new XMLProcessor(selectedFile.getAbsolutePath());
                        processor.generateTorusTopo(controller.getAsId().getText(),
                                Integer.parseInt(controller.getX().getText()),
                                Integer.parseInt(controller.getY().getText()),
                                Integer.parseInt(controller.getZ().getText()));
                        processor.parse();
                        graphicalModeController.renderOutsideView(processor);
                    } catch (SAXException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ParserConfigurationException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dialog.close();
                    textModeController.loadFileToTextEditor(selectedFile.getAbsolutePath());

                }

            });

            controller.getCancelBtn().setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
//                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    dialog.close();
                }
            });

            Scene scene = new Scene(root, 500, 400);

//            dialog.initStyle(StageStyle.UTILITY);
            dialog.setScene(scene);
            dialog.show();

        } catch (IOException ex) {
            Logger.getLogger(GraphicalModeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void genMesh() {
        graphicalModeController.clearView();
        textModeController.clearText();
        System.out.println("gen mesh");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader().getResource(
                            "graphicaleditor/view/mesh.fxml"
                    )
            );
            final Stage dialog = new Stage();
            Parent root = (Parent) loader.load();

            MeshController controller
                    = loader.<MeshController>getController();
            controller.setParentController(this);

            controller.getOkBtn().setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    FileChooser chooser = new FileChooser();
                    selectedFile = chooser.showSaveDialog(null);
                    generatePlatformElement();
                    try {
                        XMLProcessor processor = new XMLProcessor(selectedFile.getAbsolutePath());
                        processor.generateMeshTopo(controller.getAsId().getText(),
                                Integer.parseInt(controller.getX().getText()),
                                Integer.parseInt(controller.getY().getText()),
                                Integer.parseInt(controller.getZ().getText()));
                        processor.parse();
                        graphicalModeController.renderOutsideView(processor);
                    } catch (SAXException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ParserConfigurationException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dialog.close();
                    textModeController.loadFileToTextEditor(selectedFile.getAbsolutePath());

                }

            });

            controller.getCancelBtn().setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
//                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    dialog.close();
                }
            });

            Scene scene = new Scene(root, 500, 400);

//            dialog.initStyle(StageStyle.UTILITY);
            dialog.setScene(scene);
            dialog.show();

        } catch (IOException ex) {
            Logger.getLogger(GraphicalModeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void turnOnOffRoomMode() {
        if (graphicalModeController.isRoomMode()) {
            graphicalModeController.setRoomMode(false);
            graphicalModeController.clearView();
            
            graphicalModeController.renderInsideView(graphicalModeController.getASNow());
            turnOnOff.setText("Turn on");
        } else {
            graphicalModeController.setRoomMode(true);
            graphicalModeController.clearView();
            
            graphicalModeController.renderInsideView(graphicalModeController.getASNow());
            turnOnOff.setText("Turn off");
        }
    }

    @FXML
    private void addRestrictedArea() {
        System.out.println("Enter add Restricted area mode");
        graphicalModeController.onAddRestrictedAreaMode();
    }

    private void provideAboutFunctionality() {
        System.out.println("You clicked on About!");
    }

    private void provideExitFunctionality() {
        System.out.println("Exiting app...");
        System.exit(-1);
    }

    private void provideNewFileFunctionality() {
        System.out.println("New");
        FileChooser fileChooser = new FileChooser();
        selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            graphicalModeController.clearView();
            textModeController.clearText();
            generatePlatformElement();
            textModeController.loadFileToTextEditor(selectedFile.getAbsolutePath());
        }

    }

    public void generatePlatformElement() {
        try {
            FileWriter fileWriter = null;

            fileWriter = new FileWriter(selectedFile);
            fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
                    + "<platform version=\"3\">" + "</platform>");
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(XMLProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void provideOpenFunctionality() throws InterruptedException, ExecutionException, SAXException, IOException, ParserConfigurationException {
        System.out.println("Open");

        FileChooser fileChooser = new FileChooser();
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            graphicalModeController.clearView();
            textModeController.clearText();
            textModeController.loadFileToTextEditor(selectedFile.getAbsolutePath());
            XMLProcessor parser = new XMLProcessor(selectedFile.getAbsolutePath());
            parser.parse();
//                System.out.println(parser.getAsList().get(0).getRouteList());
            graphicalModeController.renderOutsideView(parser);
        } else {

        }
    }

    private void provideSaveFunctionality() {
        System.out.println("Saved");
        if (selectedFile != null) {
            textModeController.saveFileFromTextEditor(selectedFile.getAbsolutePath());
        }
    }

    @Override
    public void initialize(java.net.URL arg0, ResourceBundle arg1) {
        menuBar.setFocusTraversable(true);
        textModeController.setParentController(this);
        graphicalModeController.setParentController(this);
    }

    public TextModeController getTextModeController() {
        return textModeController;
    }

    public GraphicalModeController getGraphicalModeController() {
        return graphicalModeController;
    }

    public File getSelectedFile() {
        return selectedFile;
    }
}
