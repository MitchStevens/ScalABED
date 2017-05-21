package graphics;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Mitch on 5/21/2017.
 */
public class TitlePane implements Initializable {

    @FXML
    private AnchorPane title_pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title_pane.prefWidthProperty().bind(Main.BOARD_WIDTH);
        title_pane.prefHeightProperty().bind(Main.BOARD_HEIGHT);
    }
}
