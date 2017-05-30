package graphics.controls;

import core.circuit.Port;
import core.types.Direction;
import io.Level;
import io.Reader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;

import static java.lang.Math.*;

/**
 * Created by Mitch on 5/28/2017.
 */
public class LevelDiagram extends Pane {
    private final double OFFSET = 0.15;
    private final double ARROW_ANGLE  = PI*0.65;
    private final double ARROW_LENGTH = 0.13;

    private double size;

    public LevelDiagram(double size){
        this.size = size;
        this.setWidth(size);
        this.setHeight(size);
    }

    public void set_level(Level level){
        this.getChildren().clear();
        double offset = OFFSET * size;
        Rectangle sides  = new Rectangle(0, 0, size, size);
        sides.setFill(Color.ANTIQUEWHITE);
        Path square = HandDrawn.rectangle(offset, offset, size - offset, size - offset);
        this.getChildren().addAll(sides, square);

        for (int i = 0; i < 4; i++){
            Port port = Port.create(level.inputs()[i], level.outputs()[i]);
            if(port.port_type().id() < 2){
                Direction arrow_rot = Direction.apply(port.is_output() ? i : i+2);
                Path path = arrow(Direction.apply(i), arrow_rot);
                Label label = number(Direction.apply(i), port.capacity());

                this.getChildren().addAll(path, label);
            }
        }
    }

    private Path arrow(Direction side, Direction arrow_rotation){
        final double[][] ARROW_POSITIONS = {
                {0.5,           OFFSET},
                {1.0 - OFFSET,  0.5},
                {0.5,           1.0 - OFFSET},
                {OFFSET,        0.5}
        };
        double a = OFFSET * size;
        double p = OFFSET / (2*tan(ARROW_ANGLE/2)) * size;
        double c = cos(ARROW_ANGLE/2)*ARROW_LENGTH * size;
        double s = sin(ARROW_ANGLE/2)*ARROW_LENGTH * size;

        Path[] lines = {
            HandDrawn.line( a*.5, -a+p,  a*.5, a),
            HandDrawn.line(-a*.5, -a+p, -a*.5, a),
            HandDrawn.line(0, -a,  s, -a+c),
            HandDrawn.line(0, -a, -s, -a+c)
        };

        Path path = new Path();
        for (Path line : lines)
            path.getElements().addAll(line.getElements());

        path.setRotate(arrow_rotation.n() * 90);
        path.setLayoutX(ARROW_POSITIONS[side.n()][0] * size);
        path.setLayoutY(ARROW_POSITIONS[side.n()][1] * size);

        return path;
    }

    private Label number(Direction side, int capacity){
        final double[][] LABEL_POSITIONS = {
                {0.5-1.5*OFFSET,    0.0},
                {1.0-OFFSET,        0.5-1.5*OFFSET},
                {0.5-1.5*OFFSET,    1.0 - OFFSET},
                {0.0,               0.5-1.5*OFFSET}
        };
        final double FONT_OFFSET = 0.05; //Change this after changing font size
        double a = OFFSET * size;

        Label l = new Label(capacity+"");
        l.getStyleClass().add("capacity_marker");
        l.setPrefWidth(a);
        l.setPrefHeight(a);
        l.setAlignment(side.n() == 1 ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        l.setLayoutX(LABEL_POSITIONS[side.n()][0] * size);
        l.setLayoutY((LABEL_POSITIONS[side.n()][1] - FONT_OFFSET) * size);
        return l;
    }

}
