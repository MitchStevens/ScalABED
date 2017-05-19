package graphics;

/**
 * Created by Mitch on 5/19/2017.
 */

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public final static double 	SIDE_BAR_WIDTH  = 300.0;
    public final static double 	MIN_WIDTH       = 800.0;
    public final static double 	MIN_HEIGHT      = 600.0;
    public final static double  DEFAULT_HEIGHT  = 768.0;
    public final static double  DEFAULT_WIDTH   = 1024.0;
    public final static DoubleProperty BOARD_HEIGHT = new SimpleDoubleProperty(768);
    public final static DoubleProperty BOARD_WIDTH	= new SimpleDoubleProperty(1024);

    public final static String	VERSION = "1.0";

    private static final Main instance = new Main();

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
    }

    public static Main instance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Main(){}

}
