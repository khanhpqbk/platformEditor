/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicaleditor.controller;

import graphicaleditor.connection.*;
//import graphicaleditor.connection.SessionStatus;
//import graphicaleditor.connection.StatusCode;
import graphicaleditor.controller.fileprocessors.XMLProcessor;
import graphicaleditor.controller.gentopo.TorusController;
import graphicaleditor.controller.gentopo.MeshController;
import graphicaleditor.controller.gentopo.RingController;
import graphicaleditor.controller.gentopo.StarController;
import graphicaleditor.controller.benchmark.BMController;
import graphicaleditor.controller.benchmark.HostFileController;
import graphicaleditor.controller.fileprocessors.BMResultProcessor;
import graphicaleditor.controller.fileprocessors.CoDecomFileProcessor;
import graphicaleditor.controller.fileprocessors.TextFileProcessor;
import graphicaleditor.controller.interfaces.AbstractDialogController;
import graphicaleditor.controller.interfaces.Controller;
import graphicaleditor.controller.interfaces.IHandler;
import graphicaleditor.controller.interfaces.IInit;
import graphicaleditor.model.ASView;
import graphicaleditor.model.Host;
import graphicaleditor.model.HostView;
import graphicaleditor.model.LinkView;
import graphicaleditor.model.benchmark.Benchmark;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import javafx.stage.DirectoryChooser;
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

    private HostFileController hostFileController;

    private File selectedFile;

    private SessionStatus sessionStatus;
    private JavaClient client = null;

    @FXML
    private MenuItem turnOnOff;

    public void setHostFileController(HostFileController c) {
        hostFileController = c;
    }

    public HostFileController getHostFileController() {
        return hostFileController;
    }

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
    public void showDialog(int type, String message, int width, int height, String fxmlName, IHandler okHandler, Stage d, Object initData) {
        if (type == 0) { // gen topo
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

            Controller controller
                    = loader.<Controller>getController();

            if (controller instanceof IInit) {
                IInit c = (IInit) controller;
//                c.setHostView(h);
                c.init(initData);
            }

            if (controller instanceof AbstractDialogController) {
                AbstractDialogController c = (AbstractDialogController) controller;
                c.setParentController(this);

                c.getOkBtn().setOnMouseClicked(
                        new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent event) {

                        if (type == 0) {
                            DirectoryChooser chooser = new DirectoryChooser();
                            selectedFile = new File(chooser.showDialog(null).getAbsolutePath(), "platform.xml");
                            generatePlatformElement();
                        } else if (type == 1) // gen BM 
                        {
                            DirectoryChooser chooser = new DirectoryChooser();
                            selectedFile = new File(chooser.showDialog(null).getAbsolutePath(), "settings.xml");
                            generateBMElement(selectedFile);
                        }

                        if (okHandler != null) {
                            okHandler.handle(c);
                        }
                        d.close();

                        if (type == 0) {
                            textModeController.loadTextModeAndHostFilePane(selectedFile.getAbsolutePath());
                        }

                    }

                }
                );

                c.getCancelBtn().setOnMouseClicked((MouseEvent event) -> {
                    d.close();
                });
            }

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
        showDialog(0, "ring", 400, 400, "gentopo/ring", new IHandler() {

            @Override
            public void handle(AbstractDialogController controller) {
                RingController c = (RingController) controller;

                XMLProcessor processor = new XMLProcessor(selectedFile.getAbsolutePath());
                processor.generateRingTopo(c.getAsId().getText(), Integer.parseInt(c.getNumOfHost().getText()));
                processor.parse();
                graphicalModeController.renderOutsideView(processor);
                textModeController.loadTextModeAndHostFilePane(selectedFile.getAbsolutePath());

            }
        }, d, null);
    }

    @FXML
    private void genStar() {
        final Stage d = new Stage();
        showDialog(0, "star", 400, 400, "gentopo/star", new IHandler() {

            @Override
            public void handle(AbstractDialogController controller) {
                StarController c = (StarController) controller;
                XMLProcessor processor = new XMLProcessor(selectedFile.getAbsolutePath());
                ASView as = new ASView(null, c.getAsId().getText());
                processor.generateStarTopo(false, as, Integer.parseInt(c.getNumOfHost().getText()),
                        "", "", 0, 1000000000, 1250000000);
                processor.parse();
                graphicalModeController.renderOutsideView(processor);
                textModeController.loadTextModeAndHostFilePane(selectedFile.getAbsolutePath());

            }
        }, d, null);
    }

    @FXML
    private void genTorus() {

        final Stage d = new Stage();
        showDialog(0, "torus", 400, 400, "gentopo/torus", new IHandler() {

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
                textModeController.loadTextModeAndHostFilePane(selectedFile.getAbsolutePath());
            }
        }, d, null);
    }

    @FXML
    private void genMesh() {
        final Stage d = new Stage();
        showDialog(0, "genMesh", 400, 400, "gentopo/mesh", new IHandler() {

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
                textModeController.loadTextModeAndHostFilePane(selectedFile.getAbsolutePath());

            }
        }, d, null);
    }

    private void generateBMElement(File f) {
        try {
            FileWriter fileWriter = null;

            fileWriter = new FileWriter(f);
            fileWriter.write(
                    "<simulation>" + "</simulation>");
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(XMLProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void genBM() {
        final Stage d = new Stage();
        showDialog(1, "bm", 500, 800, "benchmark/bm", new IHandler() {

            @Override
            public void handle(AbstractDialogController controller) {
                BMController c = (BMController) controller;
                XMLProcessor p = new XMLProcessor(selectedFile.getAbsolutePath());
                boolean useHimeno = c.getUseHimeno().isSelected();
                boolean useGraph500 = c.getUseGraph500().isSelected();
                boolean useNAS = c.getUseNAS().isSelected();
                String himenoClass = "";
                int HimenoNumprocs = 0;
                int graph500Numprocs = 0;
                int scale = 0;
                String kernel = "";
                String klass = "";
                int NASNumprocs = 0;

                if (useHimeno) {
                    himenoClass = c.getHimenoClass().getValue();
                    HimenoNumprocs = c.getHimenoNumprocs().getText().isEmpty() ? 0 : Integer.parseInt(c.getHimenoNumprocs().getText());
                }
                if (useGraph500) {
                    graph500Numprocs = c.getGraph500Numprocs().getText().isEmpty() ? 0 : Integer.parseInt(c.getGraph500Numprocs().getText());;
                    scale = c.getScale().getValue();
                }
//                int edgeFactor = c.getEdgeFactor().getText().isEmpty()? 0 : Integer.parseInt(c.getEdgeFactor().getText());;
//                int engine = c.getEngine().getText().isEmpty()? 0 : Integer.parseInt(c.getEngine().getText());;
                if (useNAS) {
                    kernel = c.getKernel().getValue();
                    klass = c.getKlass().getValue();
                    NASNumprocs = c.getNASNumprocs().getValue();
                }

                p.genBM(useHimeno, useGraph500, useNAS, HimenoNumprocs, himenoClass, graph500Numprocs, scale, kernel, klass, NASNumprocs);
            }
        }, d, null);
    }

    @FXML
    private void genHostfile() {
        final Stage d = new Stage();
        showDialog(2, "hostfile", 400, 400, "benchmark/hostfile", new IHandler() {

            @Override
            public void handle(AbstractDialogController controller) {

                File selectedF = getHostFileController().getSelectedFile();
                File savedFile = new File(selectedF.getParent(), "hostfile");
                XMLProcessor p = getHostFileController().getXMLProcessor();
                if (selectedF != null && p != null) {

                    List<String> list = new ArrayList<>();
                    for (HostView h : p.getAsList().get(0).getHostList()) {
                        if (h.isSelected()) {
                            list.add(h.getmId());
                        }
                    }
                    if (list.size() > 0) {
                        new TextFileProcessor().write(savedFile, list);
                        System.out.println("writing...");
                    } else {
                        System.out.println("no hostfile chosen");
                    }

                }
            }
        }, d, null);
    }

    private void saveHostFile() {
        List<String> list = new ArrayList<>();
        for (HostView h : getGraphicalModeController().getASNow().getHostList()) {
            if (h.isSelected()) {
                list.add(h.getmId());
            }
        }
        if (list.size() > 0) {
            File file = new File(selectedFile.getParent() + File.separator + "hostfile");
            
            new TextFileProcessor().write(file, list);
            System.out.println("writing...");
        } else {
            System.out.println("no hostfile chosen");
        }
    }

    private void saveBMFile() {
        File file = new File(selectedFile.getParent() + File.separator + "settings.xml");

        generateBMElement(file);
//        file = new File(selectedFile.getParent() + File.separator + "settings.xml");
        XMLProcessor p = new XMLProcessor(file.getAbsolutePath());
        GraphicalModeController c = getGraphicalModeController();
        boolean useHimeno = c.getUseHimeno().isSelected();
        boolean useGraph500 = c.getUseGraph500().isSelected();
        boolean useNAS = c.getUseNAS().isSelected();
        String himenoClass = "";
        int HimenoNumprocs = 0;
        int graph500Numprocs = 0;
        int scale = 0;
        String kernel = "";
        String klass = "";
        int NASNumprocs = 0;

        if (useHimeno) {
            himenoClass = c.getHimenoClass().getValue();
            HimenoNumprocs = c.getHimenoNumprocs().getValue();
        }
        if (useGraph500) {
            graph500Numprocs = c.getGraph500Numprocs().getValue();
            scale = c.getScale().getValue();
        }
//                int edgeFactor = c.getEdgeFactor().getText().isEmpty()? 0 : Integer.parseInt(c.getEdgeFactor().getText());;
//                int engine = c.getEngine().getText().isEmpty()? 0 : Integer.parseInt(c.getEngine().getText());;
        if (useNAS) {
            kernel = c.getKernel().getValue();
            klass = c.getKlass().getValue();
            NASNumprocs = c.getNASNumprocs().getValue();
        }

        p.genBM(useHimeno, useGraph500, useNAS, HimenoNumprocs, himenoClass, graph500Numprocs, scale, kernel, klass, NASNumprocs);
    }

    @FXML
    private void simulate() {
//        DirectoryChooser dc = new DirectoryChooser();
//        File dir = dc.showDialog(null);

        Alert alert = null;

        if (selectedFile != null && getGraphicalModeController().getASNow() != null) {
            saveHostFile();
            saveBMFile();
            client = new JavaClient(selectedFile.getParent());
            sessionStatus = client.simulate();

            //nen dung progressDialog
            System.out.println("simulating session id = " + sessionStatus.output);
        } else {
            alert = new Alert(AlertType.WARNING);
            alert.setTitle("Simulation Status");
            alert.setContentText("Please choose a platform and benchmark!");

            alert.showAndWait();
        }
        
        

    }

    @FXML
    private void getResult() {
        Alert alert = null;
        try {
            if (client == null) {
                // dialog simulation chua bat dau
                alert = new Alert(AlertType.WARNING);
                alert.setTitle("Simulation Status");
                alert.setContentText("Simulation not started!");

                alert.showAndWait();
//                System.out.println("simulation not started.");
                return;
            } else if (client != null && !StatusCode.FINISHED.equals(client.getStatusCode(sessionStatus.output))) {

                // dialog simulation chua ket thuc
                alert = new Alert(AlertType.WARNING);
                alert.setTitle("Simulation Status");
                alert.setContentText("Simulation not finished!");

                alert.showAndWait();
//                System.out.println("simulation not finished!");
                return;
            }
            // dialog get result
            Result res = client.getResult(sessionStatus.output);
            // end dialog get result

            ByteBuffer bb = res.resultfile;
            File f = new File("/tmp/zipfile.zip");

            FileChannel wChannel = new FileOutputStream(f, false).getChannel();

//            bb.flip();
            wChannel.write(bb);

            wChannel.close();

            // dialog analyzing result
            new CoDecomFileProcessor().unZipIt("/tmp/zipfile.zip", "/tmp/unzipfolder");
            List<Benchmark> bms = new BMResultProcessor().parseResult("/tmp/unzipfolder");
            // end dialog analyzing result

            final Stage d = new Stage();
            showDialog(3, "bm result", 500, 600, "benchmark/bm_result", null, d, bms);

        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
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
        DirectoryChooser fileChooser = new DirectoryChooser();
        File f = fileChooser.showDialog(null);
        if (f != null) {
            selectedFile = new File(f.getAbsolutePath() + File.separator + "platform.xml");
        
        
            graphicalModeController.clearView();
            textModeController.clearText();
            generatePlatformElement();
//            textModeController.loadTextModeAndHostFilePane(selectedFile.getAbsolutePath());
        }
        

    }

    private void generatePlatformElement() {
        try {
            FileWriter fileWriter = null;

            fileWriter = new FileWriter(selectedFile);
            fileWriter.write("<?xml version='1.0'?>\n"
                    + //"<!DOCTYPE platform SYSTEM \"http://simgrid.gforge.inria.fr/simgrid/simgrid.dtd\">\n" +
                    "<platform version=\"4\">" + "</platform>");
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(XMLProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void provideOpenFunctionality() throws InterruptedException, ExecutionException, SAXException, IOException, ParserConfigurationException {
        System.out.println("Open");

        graphicalModeController.setOpenFirstTime(true);

        FileChooser fileChooser = new FileChooser();
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            graphicalModeController.clearView();
            XMLProcessor parser = new XMLProcessor(selectedFile.getAbsolutePath());
            parser.parse();
            graphicalModeController.renderOutsideView(parser);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            textModeController.clearText();
                            textModeController.loadTextModeAndHostFilePane(selectedFile.getAbsolutePath());
                        }
                    });
                }

            }).start();

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

    public void setSeletedFile(File f) {
        selectedFile = f;
    }
}
