package graphics;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Mitch on 5/21/2017.
 */
public class TitlePane implements Initializable {

    @FXML
    private StackPane title_pane;

    @FXML protected void play_clicked() {
        Main.set_screen("LevelSelectPane");
    }

    @FXML protected void sandbox_clicked() {
        Main.set_screen("CircuitPane");
    }

    @FXML protected void exit_clicked() {
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title_pane.prefWidthProperty().bind(Main.BOARD_WIDTH);
        title_pane.prefHeightProperty().bind(Main.BOARD_HEIGHT);
    }

}
