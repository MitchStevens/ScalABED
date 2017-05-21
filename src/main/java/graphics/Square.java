package graphics;

import core.circuit.Circuit;
import core.types.Coord;
import core.types.Direction;
import javafx.css.PseudoClass;
import javafx.fxml.Initializable;
import javafx.scene.layout.Region;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Scale;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Mitch on 5/20/2017.
 */
public class Square extends Region implements Initializable {
    private static final PseudoClass EDGE_PSEUDO_CLASS   = PseudoClass.getPseudoClass("edge");
    private static final PseudoClass CORNER_PSEUDO_CLASS = PseudoClass.getPseudoClass("corner");
    private static final double m = 0.05;

    private Polygon chevron = new Polygon(
            0.5, 0+m,
            1-m, 0.5,
            1-m, 1-m,
            0.5, 0.5,
            0+m, 1-m,
            0+m, 0.5);

    public final Coord pos;

    public Square(Coord pos) {
        this.getChildren().add(chevron);
        this.getStyleClass().add("square");
        this.pos = pos;
        this.set_fill();

        chevron.getStyleClass().add("chevron");

        initialize(null, null);
    }

    /*
    *
    * */
    public void set_fill() {
        int num_edges = pos.on_side(CircuitPane.NUM_TILES.get());
        this.pseudoClassStateChanged(EDGE_PSEUDO_CLASS,   num_edges == 1);
        this.pseudoClassStateChanged(CORNER_PSEUDO_CLASS, num_edges == 2);
    }

    public void chevron_direction(Direction dir) {
        chevron.setRotate(90 * dir.n());
    }

    public void show_chevron(boolean bool) {
        chevron.setVisible(bool);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Scale chevron_scaler = new Scale();
        chevron_scaler.setPivotX(0.0);
        chevron_scaler.setPivotY(0.0);
        chevron_scaler.xProperty().bind(this.widthProperty());
        chevron_scaler.yProperty().bind(this.heightProperty());
        chevron.getTransforms().add(chevron_scaler);

        this.prefHeightProperty().bind(CircuitPane.TILE_SIZE);
        this.prefWidthProperty().bind(CircuitPane.TILE_SIZE);
    }
}
