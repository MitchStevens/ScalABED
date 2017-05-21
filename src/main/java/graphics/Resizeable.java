package graphics;

import javafx.beans.Observable;
import javafx.scene.layout.Region;

/**
 * Created by Mitch on 5/20/2017.
 */
public abstract class Resizeable extends Region {

    protected Resizeable() {
        this.heightProperty().addListener(__ -> on_resize());
        this.widthProperty().addListener(__ -> on_resize());
    }

    protected abstract void size_change_bindings();
    protected void on_resize() {}

}
