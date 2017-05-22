package graphics;

/**
 * Created by Mitch on 5/19/2017.
 */

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    public final static double 	SIDE_BAR_WIDTH  = 300.0;
    public final static double 	MIN_WIDTH       = 800.0;
    public final static double 	MIN_HEIGHT      = 600.0;
    public final static double  DEFAULT_HEIGHT  = 768.0;
    public final static double  DEFAULT_WIDTH   = 1024.0;
    public final static DoubleProperty BOARD_HEIGHT = new SimpleDoubleProperty(768);
    public final static DoubleProperty BOARD_WIDTH	= new SimpleDoubleProperty(1024);

    public final static String	VERSION = "0.3";

    public Group root;
    private Scene scene;

    @Override public void start(Stage stage) {
        stage.setTitle("ABED: Version "+ VERSION);
        root = new Group();
        scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        //initalise_board();
        stage.setScene(scene);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMinWidth(MIN_WIDTH);
        stage.show();
        title_pane_test();
    }

    private void title_pane_test() {
        try {
            URL url = getClass().getResource("fxml/TitlePane.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            root.getChildren().add(loader.load());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void circuit_pane_test() {
        try {
            URL url = getClass().getResource("fxml/CircuitPane.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            CircuitPane circuit_pane = new CircuitPane();
            loader.setController(circuit_pane);
            root.getChildren().add(loader.load());
            circuit_pane.set_num_tiles(10);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
