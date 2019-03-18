package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Klasa obsługująca główne okno aplikacji
 */
public class Controller implements Initializable {
    private CsvReader csv;
    private File selectedFile;

    @FXML
    private ComboBox<String> chartTypeComboBox;
    ObservableList<String> chartTypes = FXCollections.observableArrayList("X(t)", "Y(t)", "Z(t)");
    @FXML
    private ComboBox<String> modeSelector;
    ObservableList<String> modes = FXCollections.observableArrayList("Marker coordinate", "COM");


    @FXML
    private Label fileDetail;
    @FXML
    private Label allertLabel;
    @FXML
    private Label markersLabel;
    @FXML
    private ListView<String> markersView;

    @FXML
    private ScatterChart<Number, Number> figure;

    @FXML
    private Button clear;
    @FXML
    private Button draw;
    @FXML
    private Button drawCOM;
    @FXML
    private Button animation;

    /**
     * Metoda wykonwana przy włączaniu danego okna
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clear.setVisible(false);
        draw.setVisible(false);
        animation.setVisible(false);
        figure.setVisible(false);
        chartTypeComboBox.setVisible(false);
        markersLabel.setVisible(false);
        markersView.setVisible(false);
        modeSelector.setVisible(false);
        drawCOM.setVisible(false);
        modeSelector.setItems(modes);
        figure.setLegendVisible(false);
    }

    /**
     * Metoda pozwalająca na wybranie pliku oraz wczytująca potrzebne elementy
     *
     * @param event
     */
    public void openFile(ActionEvent event) {
        csv = new CsvReader();
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        selectedFile = fc.showOpenDialog(null);
        {
            if (selectedFile == null) {
                allertLabel.setText("Your file is not valid");
            } else {
                csv.readCSV(selectedFile);
                markersView.setItems(csv.getMarkersNames());
                List<String> head = csv.headerLine(selectedFile);
                fileDetail.setText("File detail:\n" + head.get(0) + ": " + head.get(1) + "\n" + "File " + head.get(2).substring(5) + ": " + head.get(3) + "\n" + head.get(4) + ": " + head.get(5).substring(0, 5) + "\n"
                        + head.get(6) + ": " + head.get(7).substring(0, 5) + "\n" + "Capture Date: " + head.get(9).substring(0, 10) + "\n" + head.get(10) + ": " + head.get(11) + "\n" + head.get(12) + ": " + head.get(13) + "\n"
                        + head.get(14) + ": " + head.get(15) + "\n" + head.get(16) + ": " + head.get(17) + "\n" + head.get(18) + ": " + head.get(19));


            }
        }
        chartTypeComboBox.setItems(chartTypes);
        markersView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        markersLabel.setVisible(true);
        markersView.setVisible(true);
        modeSelector.setVisible(true);
    }

    /**
     * Metoda pozwalająca zapisać wybrane markery w pliku csv w wybranym miejscu na dysku
     *
     * @param event
     */
    public void saveFile(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        File exportFile = fc.showSaveDialog(null);
        if (exportFile != null) {
            save(csv.getExportData(markersView.getSelectionModel().getSelectedItems()), exportFile);
        }
    }

    /**
     * Metoda pozwalająca na zmianę trybu (wykreślanie współrzędnych oraz animacja lub wykreślanie COM)
     *
     * @param event
     */
    public void modeSelect(ActionEvent event) {
        if (modeSelector.getSelectionModel().getSelectedItem() == "COM") {
            drawCOM.setVisible(true);
            draw.setVisible(false);
            animation.setVisible(false);
            markersView.setVisible(false);
            markersLabel.setVisible(false);
        } else {
            draw.setVisible(true);
            animation.setVisible(true);
            drawCOM.setVisible(false);
            markersView.setVisible(true);
            markersLabel.setVisible(true);
        }
        clear.setVisible(true);
        figure.setVisible(true);
        figure.getData().clear();
        chartTypeComboBox.setVisible(true);
    }

    /**
     * Metoda kreśląca wykres wybranej współrzędnej mrakera w czasie
     *
     * @param event
     * @throws Exception
     */
    public void draw(ActionEvent event) throws Exception {
        List<List<Double>> markersCO = csv.getMarkerCo_ordinate(markersView.getSelectionModel().getSelectedItem());
        if (markersView.getSelectionModel().getSelectedItem() == null) {
            allertLabel.setText("Choose a marker");
            throw new Exception("Choose a marker");
        }

        if (markersView.getSelectionModel().getSelectedItems().size() > 1) {
            allertLabel.setText("Choose only one marker");
            throw new Exception("Choose only one marker");
        }

        String chartType = chartTypeComboBox.getValue();
        if (chartType == "X(t)" || chartType == "Y(t)" || chartType == "Z(t)") {
            List<Double> time = csv.getTimeList();

            int xyz;
            if (chartType == "X(t)") {
                xyz = 0;
            } else if (chartType == "Y(t)") {
                xyz = 1;
            } else xyz = 2;

            XYChart.Series data = new XYChart.Series();
            for (int i = 0; i < markersCO.get(xyz).size(); i++) {

                if (i % 10 == 0) {
                    data.getData().add(new XYChart.Data<Number, Number>(time.get(i), markersCO.get(xyz).get(i)));
                }
            }
            figure.getData().add(data);
            allertLabel.setText("");
        } else {
            allertLabel.setText("Choose chart type");
            throw new Exception("Choose chart type");
        }


    }

    /**
     * Metoda kreśląca wykres wybranej współrzędnej środka masy w czasie
     *
     * @param event
     * @throws Exception
     */
    public void drawCOM(ActionEvent event) throws Exception {
        List<List<Double>> markersCOM = csv.getCOM();
        String chartType = chartTypeComboBox.getValue();
        if (chartType == "X(t)" || chartType == "Y(t)" || chartType == "Z(t)") {
            List<Double> time = csv.getTimeList();

            int xyz;
            if (chartType == "X(t)") {
                xyz = 0;
            } else if (chartType == "Y(t)") {
                xyz = 1;
            } else xyz = 2;

            XYChart.Series data = new XYChart.Series();
            for (int i = 0; i < markersCOM.get(xyz).size(); i++) {

                if (i % 10 == 0) {
                    data.getData().add(new XYChart.Data<Number, Number>(time.get(i), markersCOM.get(xyz).get(i)));
                }
            }
            figure.getData().add(data);
            allertLabel.setText("");
        } else {
            allertLabel.setText("Choose chart type");
            throw new Exception("Choose chart type");
        }
    }

    /**
     * Metoda otwierająca okienko w którym zostanie zrealizowana animacja
     *
     * @param event
     * @throws Exception
     */
    public void anime(ActionEvent event) throws Exception {
        if (markersView.getSelectionModel().getSelectedItem() == null) {
            allertLabel.setText("Choose a marker");
            throw new Exception("Choose a marker");
        } else if (markersView.getSelectionModel().getSelectedItems().size() > 1) {
            allertLabel.setText("Choose only one marker");
            throw new Exception("Choose only one marker");
        } else {
            Stage animationStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AnimationProject.fxml"));
            Parent root = loader.load();
            loader.<AnimationController>getController().setController(this);
            loader.<AnimationController>getController().setCsv(csv);
            loader.<AnimationController>getController().setMarker(markersView.getSelectionModel().getSelectedItem());
            animationStage.setScene(new Scene(root));
            animationStage.setTitle("Animation");
            animationStage.setResizable(false);
            animationStage.initModality(Modality.APPLICATION_MODAL);
            allertLabel.setText("");
            animationStage.showAndWait();
        }
    }

    /**
     * Metoda pozwalająca wyczyścić obszar wykresu z danych
     *
     * @param event
     */
    public void clear(ActionEvent event) {
        figure.getData().clear();
    }

    /**
     * Pomocnicza metoda wykorzystywana przy zapisie pliku
     *
     * @param content
     * @param file
     */
    private void save(String content, File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(String.join(",", csv.headerLine(selectedFile)));
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            System.err.println("File is not valid");
        }
    }
}
