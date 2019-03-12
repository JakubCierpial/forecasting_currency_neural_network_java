package currency;

import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import networkController.DataReader;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author dev.jakub.cierpial@gmail.com
 */
public class CurrencyRepresentationController {

    private String shortName;
    private List<Double> valueLabels;
    private List<String> timeLabels;

    @FXML
    private Label currency;
    @FXML
    private LineChart<Number, String> graphRepresentation;
    @FXML
    private TableView<CurrencyDataRecord> historycaldata;
    @FXML
    private TableColumn<CurrencyDataRecord, String> valueColumn;
    @FXML
    private TableColumn<CurrencyDataRecord, String> dateColumn;
    @FXML
    private CategoryAxis categoryAxis;
    @FXML
    private NumberAxis numberAxis;

    public void setName(String name) {
        this.currency.setText(name);
    }

    public String getLongName()
    {
        return this.currency.getText();
    }
    public String getShortNameName()
    {
        return this.shortName;
    }

    public void getData(String name) {

        this.shortName=name;
        initGraphParameters();
        valueLabels = DataReader.readFromResources(name,true).stream().mapToDouble(Double::parseDouble).boxed().collect(Collectors.toList());
        timeLabels = DataReader.readFromResources("time",true);


        List<Double> values = valueLabels.subList((valueLabels.size() - 50), valueLabels.size());
        List<String> timeValues = timeLabels.subList((timeLabels.size() - 50), timeLabels.size());


        Optional<Double> maxRange = valueLabels.stream().max(Double::compareTo);
        Optional<Double> minRange = valueLabels.stream().min(Double::compareTo);
        if(minRange.isPresent()&& maxRange.isPresent()) {
            numberAxis.setUpperBound(maxRange.get());
            numberAxis.setLowerBound(minRange.get());
        }


        XYChart.Series series = new XYChart.Series();
            series.setName("Dane historzyczne dla "+ name);


        addToSeries(series, values, timeValues);


    }

    /**
     * Updates graph to display data from different period
     * @param period period of time from which the data should be displayed
     */
    void updateGraphData(int period)
    {
        historycaldata.getItems().clear();
        graphRepresentation.getData().clear();
        XYChart.Series series = new XYChart.Series();
        series.setName("Dane historzyczne dla "+ shortName);
        List<Double> values = valueLabels.subList((valueLabels.size() - period), valueLabels.size());
        List<String> timeValues = timeLabels.subList((timeLabels.size() - period), timeLabels.size());
        addToSeries(series, values, timeValues);


    }

    /**
     * Adds data to series
     * @param series series of data
     * @param values prices of currency
     * @param timeValues times quotes
     */
    private void addToSeries(XYChart.Series series, List<Double> values, List<String> timeValues) {
        for (int i = 0; i < values.size(); i++) {
            series.getData().add(new XYChart.Data(timeValues.get(i), values.get(i)));
            historycaldata.getItems().add(new CurrencyDataRecord(String.valueOf(valueLabels.get(i)), timeLabels.get(i)));
        }
        graphRepresentation.getData().addAll(series);

        initNodeActiviti();
    }

    /**
     * Initialize series parameters
     * Initialize names of axis
     */
    private void initGraphParameters()
    {
        dateColumn.setCellValueFactory(new PropertyValueFactory<CurrencyDataRecord, String>("date"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<CurrencyDataRecord, String>("value"));
        numberAxis.setAutoRanging(false);
        categoryAxis.setAnimated(false);

    }

    /**
     * Initialize tooltip actions on hover
     * Initialize color of nodes on the graph
     */
    private void initNodeActiviti()
    {
        for (XYChart.Series<Number, String> s : graphRepresentation.getData()) {
            for (XYChart.Data<Number, String> d : s.getData()) {
                Tooltip t = new Tooltip("Wartość : " + String.valueOf(d.getYValue()) + "\n"
                        + "Data : " + d.getXValue());
                t.setStyle("-fx-border-color: BLUE;-fx-font-color: GREY; -fx-font-size: 10; -fx-font-weight: bold;");
                d.getNode().setStyle("-fx-background-color: LIGHTBLUE;");
                Tooltip.install(d.getNode(), t);
            }

        }
    }


}
