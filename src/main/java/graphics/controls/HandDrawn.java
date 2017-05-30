package graphics.controls;

import javafx.scene.shape.*;

import java.util.Random;
import java.util.function.Supplier;

import static java.lang.Math.*;

/**
 * Created by Mitch on 5/29/2017.
 */
public class HandDrawn {
    private static final double CONST = 0.05;
    private static final Random r = new Random(System.currentTimeMillis());

    public static Path line(double x1, double y1, double x2, double y2) {
        double distance = hypot(x2-x1, y2-y1);
        Supplier<Double> eps = () -> (r.nextDouble()-.5)*distance*CONST;

        MoveTo move_to = new MoveTo(x1, y1);
        CubicCurveTo curve_to = new CubicCurveTo(
                x1 + eps.get(),
                y1 + eps.get(),
                x2 + eps.get(),
                y2 + eps.get(),
                x2, y2);

        return new Path(move_to, curve_to);
    }

    public static Path rectangle(double x1, double y1, double x2, double y2) {
        Path[] lines = {
                HandDrawn.line(x1, y1, x2, y1),
                HandDrawn.line(x2, y1, x2, y2),
                HandDrawn.line(x2, y2, x1, y2),
                HandDrawn.line(x1, y2, x1, y1)
        };

        Path path = new Path();
        for(Path line : lines)
            path.getElements().addAll(line.getElements());
        return path;
    }
}
