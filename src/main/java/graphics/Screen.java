package graphics;

/**
 * Created by Mitch on 5/19/2017.
 */
public abstract class Screen extends Resizeable {
    Screen(){
        size_change_bindings();
    }

    protected void size_change_bindings(){
        this.prefWidthProperty().bind(Main.BOARD_WIDTH);
        this.prefHeightProperty().bind(Main.BOARD_HEIGHT);
    }
}
