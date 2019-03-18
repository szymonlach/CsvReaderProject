package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Klasa zawierająca wszystkie metody związane z odczytywaniem pliku csv
 */
public class CsvReader {

    private ObservableList<List<String>> dataList = FXCollections.observableArrayList();

    private List<Double> timeList = new ArrayList<>();
    private ObservableList<String> headLine = FXCollections.observableArrayList();

    /**
     * Metoda zwraca nagłówek pliku csv
     *
     * @param file
     * @return
     */
    public ObservableList<String> headerLine(File file) {
        String line = "";
        String csvSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            line = br.readLine();
            String[] data = line.split(csvSplitBy);
            headLine.addAll(data);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headLine;
    }

    /**
     * Metoda czytająca plik csv zwracająca go w postaci listy tablicwej
     *
     * @param file
     * @return
     */
    public ObservableList<List<String>> readCSV(File file) {

        String line = "";
        String csvSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);
                if (data.length == 185) dataList.add(Arrays.asList(data));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 2; i < dataList.get(1).size(); i++) {
            String wczytanaKom = dataList.get(1).get(i);
            String slowo[] = wczytanaKom.split(":");
            dataList.get(1).set(i, slowo[1]);
        }

        return dataList;
    }

    /**
     * Metoda pozwalająca na wyodrębnienie nazw markerów z teblicy
     *
     * @return
     */
    public ObservableList<String> getMarkersNames() {
        ObservableList<String> markerNames = FXCollections.observableArrayList();
        for (int i = 2; i < dataList.get(1).size(); i += 3) {
            markerNames.add(dataList.get(1).get(i));
        }
        return markerNames;
    }

    /**
     * Metoda pozwalająca na wyodrębnienie współrzędnych danego markera podawanego jako argument metody
     *
     * @param markerName
     * @return
     */
    public List<List<Double>> getMarkerCo_ordinate(String markerName) {
        List<List<Double>> co_ordinateList = new ArrayList<>();
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        List<Double> z = new ArrayList<>();

        for (int i = 0; i < dataList.get(1).size(); i++) {
            if (dataList.get(1).get(i).equals(markerName)) {
                if (dataList.get(4).get(i).equals("X")) {
                    for (int j = 5; j < dataList.size(); j++) {
                        if (dataList.get(j).get(i).length() > 0)
                            x.add(Double.parseDouble(dataList.get(j).get(i)));
                    }
                } else if (dataList.get(4).get(i).equals("Y")) {
                    for (int j = 5; j < dataList.size(); j++) {
                        if (dataList.get(j).get(i).length() > 0)
                            y.add(Double.parseDouble(dataList.get(j).get(i)));
                    }
                } else for (int j = 5; j < dataList.size(); j++) {
                    if (dataList.get(j).get(i).length() > 0)
                        z.add(Double.parseDouble(dataList.get(j).get(i)));
                }
            }
        }
        co_ordinateList.add(x);
        co_ordinateList.add(y);
        co_ordinateList.add(z);
        return co_ordinateList;
    }

    /**
     * Metoda zwracająca wektor czasu (kolumnę zawierającą informacje o czasie eksperymentu)
     *
     * @return
     */
    public List<Double> getTimeList() {
        for (int i = 5; i < dataList.size(); i++) {
            timeList.add(Double.parseDouble(dataList.get(i).get(1)));
        }
        return timeList;
    }

    /**
     * Metoda konwertująca współrzędne wybranych markerów tak aby mogły być następnie zapisane
     *
     * @param markerList
     * @return
     */
    public String getExportData(List<String> markerList) {
        List<Integer> columnsNumbers = new ArrayList<>();
        columnsNumbers.add(1);
        for (int i = 0; i < dataList.get(1).size(); i++) {
            if (markerList.contains(dataList.get(1).get(i))) {
                columnsNumbers.add(i);
            }
        }

        List<String> finalLines = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {

            List<String> lines = new ArrayList<>();
            for (Integer e : columnsNumbers) {
                lines.add(dataList.get(i).get(e));
            }
            finalLines.add(String.join(",", lines));

        }
        return String.join("\n", finalLines);
    }

    /**
     * Metoda zwracająca współrzędne środka masy
     *
     * @return
     */
    public List<List<Double>> getCOM() {
        List<List<Double>> listCOM = new ArrayList<>();
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        List<Double> z = new ArrayList<>();

        List<List<Double>> valueLIAS = getMarkerCo_ordinate("LIAS");
        List<List<Double>> valueRIAS = getMarkerCo_ordinate("RIAS");
        List<List<Double>> valueLIPS = getMarkerCo_ordinate("LIPS");
        List<List<Double>> valueRIPS = getMarkerCo_ordinate("RIPS");

        for (int i = 0; i < valueLIAS.get(0).size(); i++) {


            double avX = (valueLIAS.get(0).get(i) + valueRIAS.get(0).get(i) + valueLIPS.get(0).get(i) + valueRIPS.get(0).get(i)) / 4.0;
            double avY = (valueLIAS.get(1).get(i) + valueRIAS.get(1).get(i) + valueLIPS.get(1).get(i) + valueRIPS.get(1).get(i)) / 4.0;
            double avZ = (valueLIAS.get(2).get(i) + valueRIAS.get(2).get(i) + valueLIPS.get(2).get(i) + valueRIPS.get(2).get(i)) / 4.0;
            x.add(avX);
            y.add(avY);
            z.add(avZ);

        }
        listCOM.add(x);
        listCOM.add(y);
        listCOM.add(z);
        return listCOM;
    }


}
