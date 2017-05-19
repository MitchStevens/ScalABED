package graphics;

import javafx.scene.layout.Region;

/**
 * Created by Mitch on 5/19/2017.
 */
public class CircuitPane extends Region implements Screen {

    private CircuitPane(){
        this.getStylesheets().add("res/css/GamePane.css");
        instance.prefHeightProperty().bind(Main.instance().);
    }

    @Override
    public void size_change_listeners() {

    }







    private static final CircuitPane instance = new CircuitPane();

    public static CircuitPane instance(){
        return instance;
    }

}
