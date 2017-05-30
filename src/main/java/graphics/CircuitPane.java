package graphics;

import core.types.Coord;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.Styleable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Mitch on 5/19/2017.
 */
public class CircuitPane implements Initializable {
    public final static int MIN_TILES = 3;
    public final static int MAX_TILES = 10;

    public static Map<Coord, Square> squares = new HashMap<>();
    public static DoubleProperty  TILE_SIZE = new SimpleDoubleProperty(10);
    public static IntegerProperty NUM_TILES = new SimpleIntegerProperty(MIN_TILES);

    @FXML private GridPane root_pane;

    public void set_num_tiles(int n){
        NUM_TILES.set(n);

        root_pane.getChildren().removeAll(squares.values());
        squares.clear();

        for(int i = 0; i < n; i++)
            for(int j = 0; j < n; j++){
                Coord pos = Coord.apply(j, i);
                Square square = new Square(pos);
                squares.put(pos, square);
                root_pane.add(square, i, j);
            }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root_pane.prefWidthProperty().bind(Main.BOARD_WIDTH);
        root_pane.prefHeightProperty().bind(Main.BOARD_HEIGHT);
        TILE_SIZE.bind(Bindings.min(Main.BOARD_WIDTH, Main.BOARD_HEIGHT).divide(NUM_TILES));

        set_num_tiles(6);
    }
}
