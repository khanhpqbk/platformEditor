package graphicaleditor.controller;

import graphicaleditor.controller.benchmark.HostFileController;
import graphicaleditor.controller.interfaces.AbstractDialogController;
import graphicaleditor.controller.interfaces.IHandler;
import graphicaleditor.controller.fileprocessors.XMLProcessor;
import graphicaleditor.model.ASView;
import graphicaleditor.model.Host;
import graphicaleditor.model.HostView;
import graphicaleditor.model.LinkView;
import graphicaleditor.model.RouteView;
import graphicaleditor.model.RouteViewRoom;
import graphicaleditor.model.RouterView;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author KHANH
 */
public class GraphicalModeController implements Initializable {

    public static final int VIEW_RANGE = 400;
    public static final int VIEW_SIZE = 40;

    boolean changeKernel = false;
    boolean changeClass = false;

    @FXML
    private Label statusHimeno;

    @FXML
    private Label statusNAS;

    @FXML
    private Label statusGraph500;

    @FXML
    private CheckBox useHimeno;

    @FXML
    private CheckBox useNAS;

    @FXML
    private CheckBox useGraph500;

    @FXML
    private ComboBox<Integer> himenoNumprocs;

    @FXML
    private ComboBox<String> himenoClass;

    @FXML
    private ComboBox<Integer> graph500Numprocs;

    @FXML
    private ComboBox<Integer> scale;

//    @FXML
//    private TextField edgeFactor;
//
//    @FXML
//    private TextField engine;
    @FXML
    private ComboBox<String> kernel;

    @FXML
    private ComboBox<String> klass;

    @FXML
    private ComboBox<Integer> NASNumprocs;

    private boolean openFirstTime = false;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private BorderPane hostFilePane;

    @FXML
    private BorderPane bmPane;

    @FXML
    private ListView listView;

    @FXML
    private List<ImageView> imageViews = new ArrayList<>();

    private Random r = new Random();

    private double xMouse, yMouse;

    private ASView asNow = null;

    private int id = 10;
    private int asId = 10;
    private boolean isOutside = true;
    private int addRoute = 0; // 
    private RouteView routeViewToAdd = null;
    private RouteViewRoom routeViewRoomToAdd = null;
    private boolean roomMode = false;
    private ArrayList<Polygon> plgs = new ArrayList<Polygon>();

    private int addRareaCount = -1; // not in add Restricted Area Mode
    private double xSrc, ySrc, xDes, yDes;

    private FXMLDocumentController parentController;
    private ContextMenu cmNow = null;

    public void setParentController(FXMLDocumentController c) {
        this.parentController = c;
    }

    public FXMLDocumentController getParentController() {
        return parentController;
    }

    public void renderOutsideView(XMLProcessor parser) {
        clearView();
        for (ASView as : parser.getAsList()) {
            this.addASView(as, r.nextInt(VIEW_RANGE) + 100, r.nextInt(VIEW_RANGE) + 100);
        }
    }

    public void renderInsideView(ASView as) {
        for (HostView h : as.getHostList()) {
            this.addHostViewOrRouterView(h, r.nextInt(VIEW_RANGE) + 100, r.nextInt(VIEW_RANGE) + 100);
        }

        for (RouterView rv : as.getRouterList()) {
            this.addHostViewOrRouterView(rv, r.nextInt(VIEW_RANGE) + 100, r.nextInt(VIEW_RANGE) + 100);
        }

        if (!roomMode) {
            clearLines();
            clearPolyLines();
            for (RouteView r : as.getRouteList()) {
                addRouteView(r, as);
            }
        } else {
            clearPolyLines();
            clearLines();
            for (RouteViewRoom r : as.getRouteRoomList()) {
                addRouteViewRoom(r, as, null);
            }
        }

    }

    private CheckBox cb = null;

    private void setListView() {

        if (asNow == null) {
            XMLProcessor p = new XMLProcessor(parentController.getSelectedFile().getAbsolutePath());
            p.parse();
            asNow = p.getAsList().get(0);
        }
//        ArrayLinkedList<String> ll = new ArrayLinkedList<>();
        listView.setItems(FXCollections.observableArrayList(asNow.getHostList()));
        listView.setCellFactory(new Callback<ListView<HostView>, ListCell<HostView>>() {
            @Override
            public ListCell<HostView> call(ListView<HostView> list) {
                return new ColorRectCell();
            }
        }
        );
    }

    class ColorRectCell extends ListCell<HostView> {

        @Override
        public void updateItem(HostView item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                cb = new CheckBox();

                cb.setSelected(item.isSelected());
                Label lbl = new Label(item.getmId());
                HBox rect = new HBox(cb, lbl);
                setGraphic(rect);

                cb.selectedProperty().addListener(new ChangeListener<Boolean>() {

                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        item.setSelected(newValue);
//                        System.out.println("host " + newValue);
                    }
                });
            }
        }
    }

    public void renderHostFilePane() {
        setListView();
    }

    public void renderBMPane() {
        System.out.println("render bm pane");
    }

    public void clearLines() {
        int size = anchorPane.getChildren().size();
        Iterator<Node> i = anchorPane.getChildren().iterator();
        while (i.hasNext()) {
            if (i.next() instanceof Line) {
                i.remove();
            }
        }
    }

    public void clearPolyLines() {
        int size = anchorPane.getChildren().size();
        Iterator<Node> i = anchorPane.getChildren().iterator();
        while (i.hasNext()) {
            Node node = i.next();
            if (node instanceof Polyline) {
                Polyline pll = (Polyline) node;
                pll.getPoints().clear();
                i.remove();
            }
        }
    }

    public void clearView() {
        anchorPane.getChildren().clear();
//        int size = anchorPane.getChildren().size();
//        Iterator<Node> i = anchorPane.getChildren().iterator();
//        while (i.hasNext()) {
//            Node node = i.next();
//            if (node instanceof ImageView) {
//                i.remove();
//            }
//            if(node instanceof Line) {
//                i.remove();
//            }
//        }
        plgs.clear();
    }

    public void addRouteView(RouteView rw, ASView as) {
        String src = rw.getSrc();
        String des = rw.getDes();

        double xSrc = 0, ySrc = 0, xDes = 0, yDes = 0;

        List<HostView> hosts = as.getHostList();
        for (HostView h : hosts) {
            if (h.getmId().equalsIgnoreCase(src)) {
                xSrc = h.getLayoutX() + VIEW_SIZE / 2;
                ySrc = h.getLayoutY() + VIEW_SIZE / 2;
            }

            if (h.getmId().equalsIgnoreCase(des)) {
                xDes = h.getLayoutX() + VIEW_SIZE / 2;
                yDes = h.getLayoutY() + VIEW_SIZE / 2;
            }

//            System.out.println("xsrc = " + xSrc + "ysrc = " + ySrc);
        }

        for (RouterView h : as.getRouterList()) {
            if (h.getmId().equalsIgnoreCase(src)) {
                xSrc = h.getLayoutX() + VIEW_SIZE / 2;
                ySrc = h.getLayoutY() + VIEW_SIZE / 2;
            }

            if (h.getmId().equalsIgnoreCase(des)) {
                xDes = h.getLayoutX() + VIEW_SIZE / 2;
                yDes = h.getLayoutY() + VIEW_SIZE / 2;
            }

//            System.out.println("xsrc = " + xSrc + "ysrc = " + ySrc);
        }

        if (xSrc != 0) {
            rw.setStartX(xSrc);
            rw.setStartY(ySrc);
            rw.setEndX(xDes);
            rw.setEndY(yDes);

            rw.setStroke(Color.BLACK);
            rw.setStrokeWidth(3);

            anchorPane.getChildren().add(rw);
        }

        for (int i = anchorPane.getChildren().size() - 1; i >= 0; i--) {
            Node node = anchorPane.getChildren().get(i);
            if (node instanceof RouteView) {
                RouteView rv = (RouteView) node;
                rv.setOnMouseClicked(new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getButton().equals(MouseButton.PRIMARY)) {
                            if (event.getClickCount() == 2) {
                                showRouteDetails(rv);
                            }

                        } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                            ContextMenu contextMenu = new ContextMenu();
//                            MenuItem gointo = new MenuItem("Go into");
                            MenuItem delete = new MenuItem("Delete");
                            contextMenu.getItems().addAll(delete);

                            delete.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    System.out.println("delete route...");
                                    anchorPane.getChildren().remove(rv);
                                    asNow.getRouteList().remove(rv);
                                    parentController.getTextModeController().removeRouteXml(rv);
                                    parentController.getTextModeController().loadTextModeAndHostFilePane(parentController.getSelectedFile().getAbsolutePath());
                                }
                            });

                            contextMenu.show(anchorPane, event.getScreenX(), event.getScreenY());
                        }
                    }
                });
            }
        }

    }

    public void addRouteViewRoom(RouteViewRoom rw, ASView as, ArrayList<Polygon> restrictedAreas) {
        String src = rw.getSrc();
        String des = rw.getDes();

        double xSrc = 0, ySrc = 0, xDes = 0, yDes = 0;

        List<HostView> hosts = as.getHostList();
        for (HostView h : hosts) {
            if (h.getmId().equalsIgnoreCase(src)) {
                xSrc = h.getLayoutX() + VIEW_SIZE / 2;
                ySrc = h.getLayoutY() + VIEW_SIZE / 2;
            }

            if (h.getmId().equalsIgnoreCase(des)) {
                xDes = h.getLayoutX() + VIEW_SIZE / 2;
                yDes = h.getLayoutY() + VIEW_SIZE / 2;
            }

//            System.out.println("xsrc = " + xSrc + "ysrc = " + ySrc);
        }

        for (RouterView h : as.getRouterList()) {
            if (h.getmId().equalsIgnoreCase(src)) {
                xSrc = h.getLayoutX() + VIEW_SIZE / 2;
                ySrc = h.getLayoutY() + VIEW_SIZE / 2;
            }

            if (h.getmId().equalsIgnoreCase(des)) {
                xDes = h.getLayoutX() + VIEW_SIZE / 2;
                yDes = h.getLayoutY() + VIEW_SIZE / 2;
            }

//            System.out.println("xsrc = " + xSrc + "ysrc = " + ySrc);
        }

        if (xSrc != 0) {
            double x1 = xSrc, y1 = ySrc, x2 = xSrc, y2 = yDes, x3 = xDes, y3 = yDes;
            Double[] doubles = new Double[]{
                x1, y1, x2, y2, x3, y3
            };
            if (restrictedAreas == null || restrictedAreas.isEmpty()) {

                rw.getPoints().addAll(doubles);

                rw.setStroke(Color.BLACK);
                rw.setStrokeWidth(3);

                anchorPane.getChildren().add(rw);
            } else {
                rw.getPoints().addAll(doubles);
//                RouteViewRoom rvr = new RouteViewRoom(null, null, null);
//                rvr.getPoints().addAll(rw.getPoints());
                addRVRWithRestrictedAreas(rw, as, doubles, restrictedAreas);
            }

        }

        for (int i = anchorPane.getChildren().size() - 1; i >= 0; i--) {
            Node node = anchorPane.getChildren().get(i);
            if (node instanceof RouteViewRoom) {
                RouteViewRoom rv = (RouteViewRoom) node;
                rv.setOnMouseClicked(new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getButton().equals(MouseButton.PRIMARY)) {
                            if (event.getClickCount() == 2) {
//                                showRouteDetails(rv);
                            }

                        } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                            ContextMenu contextMenu = new ContextMenu();
//                            MenuItem gointo = new MenuItem("Go into");
                            MenuItem delete = new MenuItem("Delete");
                            contextMenu.getItems().addAll(delete);

                            delete.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    System.out.println("delete route...");
                                    anchorPane.getChildren().remove(rv);
                                    asNow.getRouteRoomList().remove(rv);
                                    Iterator<RouteView> it = asNow.getRouteList().iterator();
                                    while (it.hasNext()) {
                                        RouteView r = it.next();
                                        if (r.getSrc().equals(rv.getSrc()) && r.getDes().equals(rv.getDes())) {
                                            it.remove();
                                        }
                                    }

                                    parentController.getTextModeController().removeRouteRoomXml(rv);
                                    parentController.getTextModeController().loadTextModeAndHostFilePane(parentController.getSelectedFile().getAbsolutePath());
                                }
                            });

                            contextMenu.show(anchorPane, event.getScreenX(), event.getScreenY());
                        }
                    }
                });
            }
        }

    }

    private void addRVRWithRestrictedAreas(RouteViewRoom rw, ASView as, Double[] doubles, ArrayList<Polygon> restrictedAreas) {
        RouteViewRoom rvr = new RouteViewRoom(null, null, null);
        rvr.getPoints().addAll(rw.getPoints());
        for (Polygon plg : restrictedAreas) {
            Double x1 = plg.getPoints().get(0), y1 = plg.getPoints().get(1), x2 = plg.getPoints().get(2),
                    y2 = plg.getPoints().get(3), x3 = plg.getPoints().get(4), y3 = plg.getPoints().get(5),
                    x4 = plg.getPoints().get(6), y4 = plg.getPoints().get(7);

            Double x1RVR = rvr.getPoints().get(0), y1RVR = rvr.getPoints().get(1), x2RVR = rvr.getPoints().get(2),
                    y2RVR = rvr.getPoints().get(3), x3RVR = rvr.getPoints().get(4), y3RVR = rvr.getPoints().get(5);
            if (((x1 - x1RVR) * (x2 - x1RVR) < 0 && (y1RVR - y1) * (y2RVR - y1) < 0)
                    || ((x1 - x1RVR) * (x2 - x1RVR) < 0 && (y1RVR - y3) * (y2RVR - y3) < 0)) {
                System.out.println("change route!!!");
                Double x1new = x1RVR, y1new = y1RVR, x2new = x1RVR, y2new = y1, x3new = x2, y3new = y1,
                        x4new = x2, y4new = y3RVR, x5new = x3RVR, y5new = y3RVR;
                rw.getPoints().clear();
                rw.getPoints().addAll(x1new, y1new, x2new, y2new, x3new, y3new, x4new, y4new, x5new, y5new);
                anchorPane.getChildren().add(rw);
            }

            if ((((y1 - y2RVR) * (y4 - y2RVR) < 0 && (x2RVR - x1) * (x3RVR - x1) < 0)
                    || ((y1 - y2RVR) * (y4 - y2RVR) < 0 && (x2RVR - x2) * (x3RVR - x2) < 0))) {
                System.out.println("change route!!!");
            }
        }

//        rw.getPoints().clear();
    }

    public void addASView(ASView a, double x, double y) {

//        enablePalleteFunction();
        a.setLayoutX(x);
        a.setLayoutY(y);

//        imgTest.setImage(new Image(new File("src\\graphicaleditor\\host.jpg").toURI().toString()));
        anchorPane.getChildren().add(a);
        for (int i = anchorPane.getChildren().size() - 1; i >= 0; i--) {
            final ImageView view = (ImageView) anchorPane.getChildren().get(i);
            view.setOnDragDetected((MouseEvent event) -> {
                Dragboard db = view.startDragAndDrop(TransferMode.ANY);

                /* Put a string on a dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString("frommovingas");
                db.setContent(content);

                event.consume();
            });

//            imgView.setOnMouseReleased(view);
            view.setOnDragDone((DragEvent event) -> {
                if (event.getTransferMode() == TransferMode.MOVE) {
//            source.setText("");
                    System.out.println("drag done. as view");
                }
                event.consume();
            });

            view.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        if (view instanceof ASView) {
                            ASView asView = (ASView) view;
                            showASDetails(asView);
                        } else if (view instanceof HostView) {
                            HostView hostView = (HostView) view;
                            showHostDetails(hostView);
                        } else if (view instanceof RouterView) {
                            RouterView routerView = (RouterView) view;
                            showRouterDetails(routerView);
                        }

                    }
                } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                    System.out.println("chuot phai");
                    if (view instanceof ASView) {
                        ASView as = (ASView) view;
                        final ContextMenu contextMenu = new ContextMenu();
                        MenuItem gointo = new MenuItem("Go into");
                        MenuItem delete = new MenuItem("Delete");
                        contextMenu.getItems().addAll(gointo, delete);
                        gointo.setOnAction((ActionEvent event) -> {
                            System.out.println("go Into...");
                            clearView();
                            asNow = as;
                            isOutside = false;
                            renderInsideView(as);
                        });

                        delete.setOnAction((ActionEvent event) -> {
                            System.out.println("delete AS...");
                            anchorPane.getChildren().remove(view);
                            parentController.getTextModeController().removeASXml((ASView) view);
                            parentController.getTextModeController().loadTextModeAndHostFilePane(parentController.getSelectedFile().getAbsolutePath());
                        });

                        contextMenu.show(anchorPane, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    }
                }
                mouseEvent.consume();
            });
        }

        anchorPane.setOnDragOver((DragEvent event) -> {
            event.acceptTransferModes(TransferMode.ANY);
//                }

            event.consume();

            System.out.println("drag over. as view");
        });

        anchorPane.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.getString().equalsIgnoreCase("frommovingas")) {
                ImageView view = (ImageView) event.getGestureSource();
                System.out.println("drag dropped. as view ");
                view.setLayoutX(event.getX());
                view.setLayoutY(event.getY());

                success = true;
            } else if (db.getString().equalsIgnoreCase("frompallete")) {
                workPallete(event, isOutside);
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);

            event.consume();
        });

    }

    public void addHostViewOrRouterView(ImageView h, double x, double y) {
//        ImageView view = new ImageView(h.getImage());

//        enablePalleteFunction();
        h.setLayoutX(x);
        h.setLayoutY(y);

//        imgTest.setImage(new Image(new File("src\\graphicaleditor\\host.jpg").toURI().toString()));
        anchorPane.getChildren().add(h);

        for (int i = anchorPane.getChildren().size() - 1; i >= 0; i--) {
            Node node = anchorPane.getChildren().get(i);
            if (node instanceof ImageView) {
                final ImageView view = (ImageView) anchorPane.getChildren().get(i);
                view.setOnDragDetected((MouseEvent event) -> {
                    Dragboard db = view.startDragAndDrop(TransferMode.ANY);

                    /* Put a string on a dragboard */
                    ClipboardContent content = new ClipboardContent();
                    content.putString("frommovinghostorrouter");
                    db.setContent(content);

                    event.consume();
                });

//            imgView.setOnMouseReleased(view);
                view.setOnDragDone((DragEvent event) -> {
                    if (event.getTransferMode() == TransferMode.MOVE) {
//            source.setText("");
                        System.out.println("drag done. host view");
                    }
                    event.consume();
                });

                view.setOnMouseClicked((MouseEvent mouseEvent) -> {
                    System.out.println("mouse clicked on view");
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        if (mouseEvent.getClickCount() == 2) {
                            if (view instanceof HostView) {
                                HostView hostView = (HostView) view;
                                showHostDetails(hostView);
                            } else if (view instanceof RouterView) {
                                RouterView routerView = (RouterView) view;
                                showRouterDetails(routerView);
                            }
                        } else if (mouseEvent.getClickCount() == 1) {
                            prepareAddRoute(view);

                        }
                    } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        System.out.println("chuot phai on host");
                        showContextMenu(view, mouseEvent);

                        mouseEvent.consume();
                    }
                });
            }

            anchorPane.setOnDragOver((DragEvent event) -> {
                event.acceptTransferModes(TransferMode.ANY);
//                }

                event.consume();

                System.out.println("drag over. host view");
            });

            anchorPane.setOnDragDropped((DragEvent event) -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.getString().equalsIgnoreCase("frommovinghostorrouter")) {
                    ImageView view = (ImageView) event.getGestureSource();
                    System.out.println("drag dropped. host view ");
                    view.setLayoutX(event.getX());
                    view.setLayoutY(event.getY());
                    if (asNow != null) {
                        if (!roomMode) {
                            clearLines();
                            clearPolyLines();
                            for (RouteView r1 : asNow.getRouteList()) {
                                addRouteView(r1, asNow);
                            }
                        } else if (roomMode) {
                            clearPolyLines();
                            clearLines();
                            for (RouteViewRoom r2 : asNow.getRouteRoomList()) {
                                addRouteViewRoom(r2, asNow, null);
                            }
                        }
                    }
                } else if (db.getString().equalsIgnoreCase("frompallete")) {
                    workPallete(event, isOutside);

                }
                success = true;
                event.setDropCompleted(success);
                event.consume();
            });

            if (h instanceof HostView) {
                HostView host = (HostView) h;
                host.booleanProperty().addListener(new ChangeListener<Boolean>() {

                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        HostFileController c = parentController.getHostFileController();
                        if (listView != null) {
//                            c.getListView();
                            setListView();
                            forceListRefreshOn(listView);
                        }

                    }
                });
            }

        }

    }

    private <T> void forceListRefreshOn(ListView<T> lsv) {
        ObservableList<T> items = lsv.<T>getItems();
        lsv.<T>setItems(null);
        lsv.<T>setItems(items);
    }

    private void prepareAddRoute(ImageView view) {
        System.out.println("addroute = " + addRoute);
        if (addRoute == 1) {
            // start add route
            routeViewToAdd = new RouteView(null, null, null);
            routeViewRoomToAdd = new RouteViewRoom(null, null, null);
            if (view instanceof HostView) {
                routeViewToAdd.setSrc(((HostView) view).getmId());
                routeViewRoomToAdd.setSrc(((HostView) view).getmId());
            } else if (view instanceof RouterView) {
                routeViewToAdd.setSrc(((RouterView) view).getmId());
                routeViewRoomToAdd.setSrc(((RouterView) view).getmId());
            }
            addRoute = 2; // end add route
        } else if (addRoute == 2) {
            // end add route

            if (view instanceof HostView) {
                routeViewToAdd.setDes(((HostView) view).getmId());
                routeViewRoomToAdd.setDes(((HostView) view).getmId());
            } else if (view instanceof RouterView) {
                routeViewToAdd.setDes(((RouterView) view).getmId());
                routeViewRoomToAdd.setDes(((RouterView) view).getmId());
            }

            List<LinkView> list = new ArrayList<>();
//            list.add(null)
            routeViewToAdd.setLinks(list);
            routeViewRoomToAdd.setLinks(list);
            if (!roomMode) {
                addRouteView(routeViewToAdd, asNow);
            } else {
                addRouteViewRoom(routeViewRoomToAdd, asNow, null);
            }
            asNow.getRouteList().add(routeViewToAdd);
            asNow.getRouteRoomList().add(routeViewRoomToAdd);
            asNow.getLinkList().addAll(routeViewToAdd.getLinks());
            parentController.getTextModeController().addRouteXml(asNow, routeViewToAdd);
            parentController.getTextModeController().loadTextModeAndHostFilePane(parentController.getSelectedFile().getAbsolutePath());

            addRoute = 0; // finish add route
            routeViewToAdd = null;
            routeViewRoomToAdd = null;
        }

    }

    private void showContextMenu(ImageView view, MouseEvent mouseEvent) {
        final ContextMenu contextMenu = new ContextMenu();
        cmNow = contextMenu;
//                            MenuItem gointo = new MenuItem("Go into");
        MenuItem delete = new MenuItem("Delete");
        MenuItem markForBM = null;
        contextMenu.getItems().add(delete);
//        contextMenu.

        if (view instanceof RouterView) {
            RouterView router = (RouterView) view;

            delete.setOnAction((ActionEvent event) -> {
                System.out.println("delete router...");
                anchorPane.getChildren().remove(view);
                asNow.getRouterList().remove(view);
                rmvRoute(view);
                parentController.getTextModeController().removeRouterXml(router);
                parentController.getTextModeController().loadTextModeAndHostFilePane(parentController.getSelectedFile().getAbsolutePath());
            });

        } else if (view instanceof HostView) {
            HostView h = (HostView) view;
            String text = h.isSelected() ? "Unmark" : "Mark for benchmarking";
            markForBM = new MenuItem(text);
            contextMenu.getItems().add(markForBM);
            markForBM.setOnAction((ActionEvent event) -> {
                h.setSelected(!h.isSelected());
            });
            delete.setOnAction((ActionEvent event) -> {
                System.out.println("delete Host...");
                anchorPane.getChildren().remove(view);
                asNow.getHostList().remove(view);
                rmvRoute(view);
                parentController.getTextModeController().removeHostXml(asNow, h);
                parentController.getTextModeController().loadTextModeAndHostFilePane(parentController.getSelectedFile().getAbsolutePath());
            });

        }

        contextMenu.show(anchorPane, mouseEvent.getScreenX(), mouseEvent.getScreenY());
    }

    private void rmvRoute(ImageView hr) {
        if (hr instanceof HostView) {
            HostView host = (HostView) hr;
            Iterator<RouteView> it = asNow.getRouteList().iterator();
            while (it.hasNext()) {
                RouteView rv = it.next();
                if (rv.getSrc().equals(host.getmId()) || rv.getDes().equals(host.getmId())) {
                    anchorPane.getChildren().remove(rv);
                    it.remove();
                    parentController.getTextModeController().removeRouteXml(rv);
                }
            }
        } else if (hr instanceof RouterView) {
            RouterView router = (RouterView) hr;
            Iterator<RouteView> it = asNow.getRouteList().iterator();
            while (it.hasNext()) {
                RouteView rv = it.next();
                if (rv.getSrc().equals(router.getmId()) || rv.getDes().equals(router.getmId())) {
                    anchorPane.getChildren().remove(rv);
                    it.remove();
                    parentController.getTextModeController().removeRouteXml(rv);
                }
            }
        }
    }

    private void workPallete(DragEvent event, boolean isOutside) {
        switch (GridPane.getRowIndex((Node) event.getGestureSource())) {
            case 0: //as
                System.out.println("case drop asview");
                if (isOutside) {
                    asId++;
                    Image img = new Image(new File("src/graphicaleditor/res/as.jpg").toURI().toString(), GraphicalModeController.VIEW_SIZE, GraphicalModeController.VIEW_SIZE, true, true);
                    ASView newAS = new ASView(img, "AS" + String.valueOf(asId));
                    addASView(newAS, event.getX(), event.getY());

                    FXMLDocumentController c = (FXMLDocumentController) parentController;
                    ((TextModeController) c.getTextModeController()).addASXml(newAS);
                    parentController.getTextModeController().loadTextModeAndHostFilePane(parentController.getSelectedFile().getAbsolutePath());
                } else {
                    System.out.println("cannot add AS here");
                }
                break;
            case 1: // host
                System.out.println("case drop hostview");
                if (!isOutside) {
                    id++;
                    Image img = new Image(new File("src/graphicaleditor/res/host.jpg").toURI().toString(), GraphicalModeController.VIEW_SIZE, GraphicalModeController.VIEW_SIZE, true, true);
                    HostView h = new HostView(img, "host" + id, 1000000000, "ON");
                    addHostViewOrRouterView(h, event.getX(), event.getY());
                    asNow.getHostList().add(h);
                    FXMLDocumentController c = (FXMLDocumentController) parentController;
                    ((TextModeController) c.getTextModeController()).addHostXml(asNow, h);
                    parentController.getTextModeController().loadTextModeAndHostFilePane(parentController.getSelectedFile().getAbsolutePath());

                } else {
                    System.out.println("cannot add Host here");
                }
                break;
            case 2:
                // router
                System.out.println("case drop routerview");
                if (!isOutside) {
                    id++;
                    Image img = new Image(new File("src/graphicaleditor/res/router.jpg").toURI().toString(), GraphicalModeController.VIEW_SIZE, GraphicalModeController.VIEW_SIZE, true, true);
                    RouterView r = new RouterView(img, "router" + id);
                    addHostViewOrRouterView(r, event.getX(), event.getY());
                    asNow.getRouterList().add(r);
                    parentController.getTextModeController().addRouterXml(asNow, r);
                    parentController.getTextModeController().loadTextModeAndHostFilePane(parentController.getSelectedFile().getAbsolutePath());

                } else {
                    System.out.println("cannot add router here");
                }
                break;
            default:
                System.out.println("case default");
                break;

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isOutside = true;
        enablePalleteFunction();
        renderBMPane();
        initBMPane();
        initAnchorPane();
    }

    private void initAnchorPane() {
        anchorPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (cmNow != null) {
                    cmNow.hide();
                }
            }
        });
    }

    private void enablePalleteFunction() {
        for (int i = 0; i < gridPane.getChildren().size(); i++) {
//            Image
            Node node = gridPane.getChildren().get(i);
            node.setOnDragDetected((MouseEvent event) -> {
                Dragboard db = node.startDragAndDrop(TransferMode.ANY);

                /* Put a string on a dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString("frompallete");
                db.setContent(content);

                event.consume();

                System.out.println("start drag on node ");
            });

            node.setOnDragDone((DragEvent event) -> {
                if (event.getTransferMode() == TransferMode.MOVE) {
//            source.setText("");
                    System.out.println("drag done. from pallete");
                }
                event.consume();
            });

            if (GridPane.getRowIndex(node) == 3) {
                node.setOnMouseClicked((MouseEvent event) -> {
                    System.out.println("click on add route");
                    addRoute = 1;
                });
            }
        }

        anchorPane.setOnDragOver((DragEvent event) -> {
            event.acceptTransferModes(TransferMode.ANY);
//                }

            event.consume();

            System.out.println("drag over. from pallete");
        });

        anchorPane.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.getString().equals("frompallete")) {
                System.out.println("drag dropped frompallete");
                workPallete(event, isOutside);

                success = true;
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);

            event.consume();
        });

        // Ađd Route
        Node node = gridPane.getChildren().get(3);

    }

    public void initBMPane() {
        himenoNumprocs.setDisable(true);
        himenoClass.setDisable(true);
        graph500Numprocs.setDisable(true);
//        edgeFactor.setDisable(true);
        scale.setDisable(true);
//        engine.setDisable(true);
        NASNumprocs.setDisable(true);
        kernel.setDisable(true);
        klass.setDisable(true);

        useHimeno.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                himenoNumprocs.setDisable(!newValue);
                himenoClass.setDisable(!newValue);
            }
        });

        useGraph500.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                graph500Numprocs.setDisable(!newValue);
//                edgeFactor.setDisable(!newValue);
                scale.setDisable(!newValue);
//                engine.setDisable(!newValue);
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

        NASNumprocs.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (asNow != null) {
                    numOfHost = asNow.getHostList().size();
                    if (newValue != null) {
                        if (newValue > numOfHost) {
                            setWarningNumOfHost("NAS");
                        } else {
                            setWarningNumOfHost("");
                        }
                    }
                }
            }
        });

        graph500Numprocs.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (asNow != null) {
                    numOfHost = asNow.getHostList().size();
                    if (newValue != null) {
                        if (newValue > numOfHost) {
                            setWarningNumOfHost("NAS");
                        } else {
                            setWarningNumOfHost("");
                        }
                    }
                }
            }
        });

        himenoNumprocs.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (asNow != null) {
                    numOfHost = asNow.getHostList().size();
                    if (newValue != null) {
                        if (newValue > numOfHost) {
                            setWarningNumOfHost("NAS");
                        } else {
                            setWarningNumOfHost("");
                        }
                    }
                }
            }
        });

    }

    private void setWarningNumOfHost(String str) {
        switch (str) {
            case "NAS":
                statusNAS.setText("Numprocs cannot greater than hosts");
                break;
            case "g":
                statusGraph500.setText("Numprocs cannot greater than hosts");
                break;
            case "h":
                statusHimeno.setText("Numprocs cannot greater than hosts");
                break;
            default:
                statusNAS.setText("");
                statusGraph500.setText("");
                statusHimeno.setText("");
                break;
        }

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
        } else if (klassStr.equals("S")) {
            setMaxNumprocs(16, "others");
        } else {
            setMaxNumprocs(128, "others");
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

    private void showRouteDetails(RouteView rv) {
        System.out.println("double click on Route View");
        System.out.println("src = " + rv.getSrc());
        System.out.println("des = " + rv.getDes());
    }

    private void showASDetails(ASView a) {
        System.out.println("show as details");
        System.out.println("id = " + a.getmId());
    }

    private void showRouterDetails(RouterView r) {
        System.out.println("show router details");
        System.out.println("id = " + r.getmId());
    }

    private void showHostDetails(HostView h) {
        final Stage dialog = new Stage();
        IHandler handler = (AbstractDialogController controller) -> {
            HostDetailController c = (HostDetailController) controller;

            HostView newHost = c.createHostFromFields();

            parentController.getTextModeController().modifyHostXml(newHost, h.getmId());
            saveRouteFromFields(asNow, newHost, h);
            saveHostFromFields(h, newHost);
            parentController.getTextModeController().loadTextModeAndHostFilePane(parentController.getSelectedFile().getAbsolutePath());
        };

        parentController.showDialog(3, "host details", 500, 400, "hostdetail", handler, dialog, h);
    }

    private void saveRouteFromFields(ASView asNow, HostView newHost, HostView oldHost) {
        for (RouteView r : asNow.getRouteList()) {
            if (r.getSrc().equals(oldHost.getmId())) {
                r.setSrc(newHost.getmId());
            }
            if (r.getDes().equals(oldHost.getmId())) {
                r.setDes(newHost.getmId());
            }
        }

    }

    private void saveHostFromFields(HostView old, HostView newHost) {
//        HostView h = new HostView(null);
        old.setmId(newHost.getmId());
        old.setPower((newHost.getPower()));
        old.setState(newHost.getState());
        old.setAvailability(newHost.getAvailability());
        old.setPstate(newHost.getPstate());
        old.setCore(newHost.getCore());
    }

    public void onAddRestrictedAreaMode() {
        if (roomMode) {
//            plgs = new ArrayList<>();
            addRareaCount = 0;
            anchorPane.setOnMouseClicked((MouseEvent event) -> {
                if (addRareaCount == 0) {
                    addRareaCount = 1;
                    xSrc = event.getX();
                    ySrc = event.getY();
                } else if (addRareaCount == 1) {
                    addRareaCount = 0;
                    xDes = event.getX();
                    yDes = event.getY();
                    double x1 = xSrc, y1 = ySrc, x2 = xDes, y2 = ySrc,
                            x3 = xDes, y3 = yDes, x4 = xSrc, y4 = yDes;
                    Polygon plg = new Polygon();
                    plg.getPoints().addAll(new Double[]{
                        x1, y1, x2, y2, x3, y3, x4, y4
                    });
                    plg.setFill(Color.RED);
                    anchorPane.getChildren().add(plg);
                    plgs.add(plg);

                    xSrc = ySrc = xDes = yDes = 0;

                    resetPolyLines(plgs);

                    for (int i = 0; i < anchorPane.getChildren().size(); i++) {
                        Node node = anchorPane.getChildren().get(i);
                        if (node instanceof Polygon) {
                            node.setOnMouseClicked((MouseEvent event1) -> {
                                if (event1.getButton().equals(MouseButton.SECONDARY)) {
                                    final ContextMenu contextMenu = new ContextMenu();
                                    MenuItem delete = new MenuItem("Delete");
                                    contextMenu.getItems().addAll(delete);
                                    delete.setOnAction((ActionEvent event2) -> {
                                        anchorPane.getChildren().remove(node);
                                        plgs.remove(node);
                                        addRareaCount = -1;
                                    });
                                    contextMenu.show(anchorPane, event1.getScreenX(), event1.getScreenY());
                                }
                            });
                        }
                    }

                }
            });
        }
    }

    private void resetPolyLines(ArrayList<Polygon> plgs) {
        if (roomMode) {
            clearPolyLines();
            for (RouteViewRoom r : asNow.getRouteRoomList()) {
                addRouteViewRoom(r, asNow, plgs);
            }
        }
    }

    ///////////////////////////////////////////////
    // getters, setters
    public boolean isIsOutside() {
        return isOutside;
    }

    public void setIsOutside(boolean isOutside) {
        this.isOutside = isOutside;
    }

    public ASView getASNow() {
        return asNow;
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    public void setAnchorPane(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
    }

    public boolean isRoomMode() {
        return roomMode;
    }

    public void setRoomMode(boolean roomMode) {
        this.roomMode = roomMode;
    }

    public int getAddRareaCount() {
        return addRareaCount;
    }

    public void setAddRareaCount(int addRareaCount) {
        this.addRareaCount = addRareaCount;
    }

    public ArrayList<Polygon> getPlgs() {
        return plgs;
    }

    public void setPlgs(ArrayList<Polygon> plgs) {
        this.plgs = plgs;
    }

    public boolean isOpenFirstTime() {
        return openFirstTime;
    }

    public void setOpenFirstTime(boolean openFirstTime) {
        this.openFirstTime = openFirstTime;
    }

    public CheckBox getUseHimeno() {
        return useHimeno;
    }

    public void setUseHimeno(CheckBox useHimeno) {
        this.useHimeno = useHimeno;
    }

    public CheckBox getUseNAS() {
        return useNAS;
    }

    public void setUseNAS(CheckBox useNAS) {
        this.useNAS = useNAS;
    }

    public CheckBox getUseGraph500() {
        return useGraph500;
    }

    public void setUseGraph500(CheckBox useGraph500) {
        this.useGraph500 = useGraph500;
    }

    public ComboBox<Integer> getHimenoNumprocs() {
        return himenoNumprocs;
    }

    public void setHimenoNumprocs(ComboBox<Integer> himenoNumprocs) {
        this.himenoNumprocs = himenoNumprocs;
    }

    public ComboBox<String> getHimenoClass() {
        return himenoClass;
    }

    public void setHimenoClass(ComboBox<String> himenoClass) {
        this.himenoClass = himenoClass;
    }

    public ComboBox<Integer> getGraph500Numprocs() {
        return graph500Numprocs;
    }

    public void setGraph500Numprocs(ComboBox<Integer> graph500Numprocs) {
        this.graph500Numprocs = graph500Numprocs;
    }

    public ComboBox<Integer> getScale() {
        return scale;
    }

    public void setScale(ComboBox<Integer> scale) {
        this.scale = scale;
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

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    private int numOfHost = 0;

}
