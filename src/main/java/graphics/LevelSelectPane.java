package graphics;

import graphics.controls.LevelDiagram;
import io.Level;
import io.Reader;
import javafx.fxml.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Mitch on 5/21/2017.
 */
public class LevelSelectPane implements Initializable {

    @FXML private TreeView<Node> tree_view;
    @FXML private VBox level_details;
    @FXML private Label level_name;
    @FXML private Label level_desc;
    @FXML private StackPane level_diagram;
    @FXML private Label level_completion_info;

    private LevelDiagram diagram = new LevelDiagram(300);

    @FXML public void nav_title(){
        Main.set_screen("TitlePane");
    }

    public void set_level(Level level){
        diagram.set_level(level);
        level_details.setVisible(true);
        level_name.setText(level.name());
        level_desc.setText(level.instruction_text());
        level_completion_info.setText(
                "Completed with size 8\nMin size "+level.min_size()
        );

        System.out.println(level_desc.getWidth());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<Node> root = new TreeItem<>(new Label("Levels"));

        for (int i = 0; i < Reader.LEVELS_JAVA().size(); i++){
            TreeItem<Node> level_set_root = new TreeItem<>(new Label(i+1 +". "+ Reader.LEVEL_SET_NAMES_JAVA().get(i)));
            List<Level> level_set = Reader.LEVELS_JAVA().get(i);
            for(int j = 0; j < Reader.LEVELS_JAVA().get(i).size(); j++){
                Label l = new Label(String.format("%d.%d. %s", i+1, j+1, level_set.get(j).name()));
                final int I = i, J = j;
                l.setOnMouseClicked(e ->
                    this.set_level(Reader.LEVELS_JAVA().get(I).get(J))
                );
                level_set_root.getChildren().add(new TreeItem<>(l));
            }
            root.getChildren().add(level_set_root);
        }

        tree_view.setRoot(root);
        level_diagram.getChildren().add(diagram);
    }
}
