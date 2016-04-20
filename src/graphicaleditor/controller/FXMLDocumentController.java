/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicaleditor.controller;

import graphicaleditor.controller.xml.XMLProcessor;
import graphicaleditor.controller.gentopo.TorusController;
import graphicaleditor.controller.gentopo.MeshController;
import graphicaleditor.controller.gentopo.RingController;
import graphicaleditor.controller.gentopo.StarController;
import graphicaleditor.controller.benchmark.Graph500Controller;
import graphicaleditor.controller.benchmark.HimenoController;
import graphicaleditor.controller.benchmark.NASController;
import graphicaleditor.controller.interfaces.AbstractDialogController;
import graphicaleditor.controller.interfaces.IHandler;
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
import javafx.scene.control.Dialog;
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
    public void showDialog(boolean isGen, String message, int width, int height, String fxmlName, IHandler okHandler, Stage d, HostView h) {
        if (isGen) {
            graphicalModeController.clearView();
            textModeController.clearText();
        }
        System.out.println(message);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getClassLoader().getResource(
                            "graphicaleditor/view/" + fxmlName + ".fxml"
                    )
            );
//            final Stage dialog = new Stage();
            Parent root = (Parent) loader.load();

            AbstractDialogController controller
                    = loader.<AbstractDialogController>getController();
            if (controller instanceof HostDetailController) {
                HostDetailController c = (HostDetailController) controller;
                c.setHostView(h);
                c.init(h);
            } else if (controller instanceof NASController) {
                NASController c = (NASController) controller;
                c.init();
            }
            controller.setParentController(this);

            controller.getOkBtn().setOnMouseClicked(
                    new EventHandler<MouseEvent>() {

                        @Override
                        public void handle(MouseEvent event) {
                            FileChooser chooser = new FileChooser();
                            selectedFile = chooser.showSaveDialog(null);
                            
                            if (isGen) {
                                generatePlatformElement();
                            }
                            okHandler.handle(controller);
                            d.close();
                            if (isGen) {
                                textModeController.loadFileToTextEditor(selectedFile.getAbsolutePath());
                            }

                        }

                    }
            );

            controller.getCancelBtn().setOnMouseClicked((MouseEvent event) -> {
                d.close();
            });

            Scene scene = new Scene(root, width, height);

//            dialog.initStyle(StageStyle.UTILITY);
            d.setScene(scene);
            d.show();

        } catch (IOException ex) {
            Logger.getLogger(GraphicalModeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void genRing() {
        final Stage d = new Stage();
        showDialog(true, "ring", 400, 400, "gentopo/ring", new IHandler() {

            @Override
            public void handle(AbstractDialogController controller) {
                RingController c = (RingController) controller;

                XMLProcessor processor = new XMLProcessor(selectedFile.getAbsolutePath());
                processor.generateRingTopo(c.getAsId().getText(), Integer.parseInt(c.getNumOfHost().getText()));
                processor.parse();
                graphicalModeController.renderOutsideView(processor);

            }
        }, d, null);
    }

    @FXML
    private void genStar() {
        final Stage d = new Stage();
        showDialog(true, "star", 400, 400, "gentopo/star", new IHandler() {

            @Override
            public void handle(AbstractDialogController controller) {
                StarController c = (StarController) controller;
                XMLProcessor processor = new XMLProcessor(selectedFile.getAbsolutePath());
                ASView as = new ASView(null, c.getAsId().getText());
                processor.generateStarTopo(false, as, Integer.parseInt(c.getNumOfHost().getText()),
                        "", "", 0, 0, 0);
                processor.parse();
                graphicalModeController.renderOutsideView(processor);
                textModeController.loadFileToTextEditor(selectedFile.getAbsolutePath());

            }
        }, d, null);
    }

    @FXML
    private void genTorus() {

        final Stage d = new Stage();
        showDialog(true, "torus", 400, 400, "gentopo/torus", new IHandler() {

            @Override
            public void handle(AbstractDialogController controller) {
                TorusController c = (TorusController) controller;
                XMLProcessor processor = new XMLProcessor(selectedFile.getAbsolutePath());
                processor.generateTorusTopo(c.getAsId().getText(),
                        Integer.parseInt(c.getX().getText()),
                        Integer.parseInt(c.getY().getText()),
                        Integer.parseInt(c.getZ().getText()));
                processor.parse();
                graphicalModeController.renderOutsideView(processor);

            }
        }, d, null);
    }

    @FXML
    private void genMesh() {
        final Stage d = new Stage();
        showDialog(true, "genMesh", 400, 400, "gentopo/mesh", new IHandler() {

            @Override
            public void handle(AbstractDialogController controller) {
                MeshController c = (MeshController) controller;
                XMLProcessor processor = new XMLProcessor(selectedFile.getAbsolutePath());
                processor.generateMeshTopo(c.getAsId().getText(),
                        Integer.parseInt(c.getX().getText()),
                        Integer.parseInt(c.getY().getText()),
                        Integer.parseInt(c.getZ().getText()));
                processor.parse();
                graphicalModeController.renderOutsideView(processor);

            }
        }, d, null);
    }

    private void generateBMElement(String type) {
        try {
            FileWriter fileWriter = null;

            fileWriter = new FileWriter(selectedFile);
            fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
                    + "<benchmark version=\"3\" type=\"" + type + "\" >" + "</benchmark>");
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(XMLProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void himenoBM() {
        final Stage d = new Stage();
        showDialog(false, "himeno", 400, 200, "benchmark/himeno", new IHandler() {

            @Override
            public void handle(AbstractDialogController controller) {
                HimenoController c = (HimenoController) controller;
                generateBMElement("himeno");
                XMLProcessor p = new XMLProcessor(selectedFile.getAbsolutePath());
                p.genHimenoBM(Integer.parseInt(c.getNumOfProcs().getText()));
            }
        }, d, null);
    }

    @FXML
    private void graph500BM() {
        final Stage d = new Stage();
        showDialog(false, "graph500", 400, 400, "benchmark/graph500", new IHandler() {

            @Override
            public void handle(AbstractDialogController controller) {
                Graph500Controller c = (Graph500Controller) controller;
                generateBMElement("graph500");
                XMLProcessor p = new XMLProcessor(selectedFile.getAbsolutePath());
                p.genGraph500BM(Integer.parseInt(c.getNumprocs().getText()), 
                        Integer.parseInt(c.getScale().getText()), 
                        Integer.parseInt(c.getEdgeFactor().getText()),
                        Integer.parseInt(c.getEngine().getText()));
            }
        }, d, null);
    }

    @FXML
    private void NASBM() {
        final Stage d = new Stage();
        showDialog(false, "NAS", 400, 400, "benchmark/nas", new IHandler() {

            @Override
            public void handle(AbstractDialogController controller) {
                NASController c = (NASController) controller;
                generateBMElement("NAS");
                XMLProcessor p = new XMLProcessor(selectedFile.getAbsolutePath());
                p.genNASBM(c.getKernelCombobox().getValue(), 
                        c.getClassCombobox().getValue(), 
                        (c.getNumprocsCombobox().getValue()));
            }
        }, d, null);
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

    private void generatePlatformElement() {
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
