package sample;

import javafx.animation.PathTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Klasa obsługująca okienko animacji
 */
public class AnimationController implements Initializable {


    private Controller controller = new Controller();
    private CsvReader csv = new CsvReader();
    private String marker = "";
    @FXML
    private Button back;
    @FXML
    private Circle circle;
    @FXML
    private ComboBox<String> firstCoordinate = new ComboBox<>();


    ObservableList<String> coordinates = FXCollections.observableArrayList("X", "Y", "Z");


    private Path path = new Path();

    /**
     * Metoda pozwalająca na wykonanie animacji wybranego markera
     *
     * @param event
     * @throws Exception
     */
    public void play(ActionEvent event) throws Exception {
        circle.setVisible(true);
        List<List<Double>> data = csv.getMarkerCo_ordinate(marker);
        List<Double> time = csv.getTimeList();

        int yDirection = checkDirection(firstCoordinate.getSelectionModel().getSelectedItem());

        path.getElements().add(new MoveTo(0, 0));
        for (int i = 0; i < data.get(1).size(); i++) {
            path.getElements().add(new LineTo(time.get(i), data.get(yDirection).get(i) * 100));
        }

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(10));
        pathTransition.setCycleCount(1);
        pathTransition.setNode(circle);
        pathTransition.setPath(path);
        pathTransition.setAutoReverse(true);
        pathTransition.play();
        ((Button) event.getSource()).setVisible(false);
        firstCoordinate.setVisible(false);
        pathTransition.setOnFinished(event1 -> {
            circle.setVisible(false);
            back.setVisible(true);
        });
    }

    /**
     * Metoda wykorzystywana do ustawienia odpowiedniego kontrolera
     *
     * @param controller
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setCsv(CsvReader csv) {
        this.csv = csv;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        firstCoordinate.setItems(coordinates);
        back.setVisible(false);
        circle.setVisible(false);
    }

    /**
     * Metoda pozwalająca na powrót do głównego okna aplikacji
     *
     * @param event
     */
    public void back(ActionEvent event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }


    /**
     * Pomocnicza metoda wykorzystywana w ustalaniu współrzędnych w jakich ma być kreślona animacja
     *
     * @param direction
     * @return
     */
    private int checkDirection(String direction) {
        if (direction == "X") {
            return 0;
        } else if (direction == "Y") {
            return 1;
        } else return 2;
    }
}
