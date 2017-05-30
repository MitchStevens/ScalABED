package graphics;

/**
 * Created by Mitch on 5/19/2017.
 */

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {
    public final static String	VERSION = "0.3";

    public final static double 	MIN_WIDTH       = 800.0;
    public final static double 	MIN_HEIGHT      = 600.0;
    public final static double  DEFAULT_HEIGHT  = 768.0;
    public final static double  DEFAULT_WIDTH   = 1024.0;
    public final static DoubleProperty BOARD_HEIGHT = new SimpleDoubleProperty(768);
    public final static DoubleProperty BOARD_WIDTH	= new SimpleDoubleProperty(1024);

    public final static List<String> SCREEN_NAMES  = Arrays.asList(
            "TitlePane",
            "LevelSelectPane",
            "CircuitPane"
    );

    public  static StackPane root;
    private static Scene     scene;

    private static Map<String, Pane> screens = new HashMap<>();

    @Override public void start(Stage stage) {
        stage.setTitle("ABED: Version "+ VERSION);
        root = new StackPane();
        scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        stage.setScene(scene);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMinWidth(MIN_WIDTH);
        stage.show();
        initialise_screens();
        set_screen("CircuitPane");
        set_screen("TitlePane");
    }

    private void initialise_screens() {
        try {
            for(String name: SCREEN_NAMES){
                URL url = getClass().getResource("fxml/"+ name + ".fxml");
                FXMLLoader loader = new FXMLLoader(url);
                Pane pane = loader.load();
                screens.put(name, pane);
                root.getChildren().add(pane);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void set_screen(String screen_name){
        root.getChildren().clear();
        if (screens.containsKey(screen_name))
            root.getChildren().add(screens.get(screen_name));
    }

    public static void main(String[] args) {
        launch(args);
    }

}
