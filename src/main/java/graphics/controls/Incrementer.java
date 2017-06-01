package graphics.controls;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Mitch on 5/31/2017.
 */
public class Incrementer implements Initializable {
    private final int DEFAULT_VALUE = 6;

    private int value;

    @FXML private Button    dec_button;
    @FXML private Text      num_label;
    @FXML private Button    inc_button;


    public int get_value() {
        return value;
    }

    public void set_value(int value){
        this.value = value;
        num_label.setText(value+"");
    }

    @FXML
    private void increment() {
        set_value(value + 1);
    }

    @FXML
    private void decrement() {
        set_value(value - 1);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        set_value(DEFAULT_VALUE);
    }
}
