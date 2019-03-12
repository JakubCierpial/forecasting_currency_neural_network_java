package mainPage;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import frame.Main;
import javafx.scene.control.Label;

import networkController.DataReader;
import org.apache.commons.math3.util.Precision;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    private Button aud;
    @FXML
    private Button cad;
    @FXML
    private Button chf;
    @FXML
    private Button eur;
    @FXML
    private Button gbp;
    @FXML
    private Button jpy;
    @FXML
    private Button nok;
    @FXML
    private Button sek;
    @FXML
    private Button usd;

    @FXML
    private Label audActual;
    @FXML
    private Label cadActual;
    @FXML
    private Label chfActual;
    @FXML
    private Label eurActual;
    @FXML
    private Label gbpActual;
    @FXML
    private Label jpyActual;
    @FXML
    private Label nokActual;
    @FXML
    private Label sekActual;
    @FXML
    private Label usdActual;

    @FXML
    private Label audFuture;
    @FXML
    private Label cadFuture;
    @FXML
    private Label chfFuture;
    @FXML
    private Label eurFuture;
    @FXML
    private Label gbpFuture;
    @FXML
    private Label jpyFuture;
    @FXML
    private Label nokFuture;
    @FXML
    private Label sekFuture;
    @FXML
    private Label usdFuture;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Map<String, String> actualValues = DataReader.readActual();
        audActual.setText(actualValues.get("AUD")+" zł");
        cadActual.setText(actualValues.get("CAD")+" zł");
        chfActual.setText(actualValues.get("CHF")+" zł");
        eurActual.setText(actualValues.get("EUR")+" zł");
        gbpActual.setText(actualValues.get("GBP")+" zł");
        jpyActual.setText(actualValues.get("JPY")+" zł");
        nokActual.setText(actualValues.get("NOK")+" zł");
        sekActual.setText(actualValues.get("SEK")+" zł");
        usdActual.setText(actualValues.get("USD")+" zł");

        audFuture.setText(String.valueOf(Precision.round(DataReader.predict("AUD", Double.parseDouble(actualValues.get("AUD"))),4))+" zł");
        cadFuture.setText(String.valueOf(Precision.round(DataReader.predict("CAD", Double.parseDouble(actualValues.get("CAD"))),4))+" zł");
        chfFuture.setText(String.valueOf(Precision.round(DataReader.predict("CHF", Double.parseDouble(actualValues.get("CHF"))),4))+" zł");
        eurFuture.setText(String.valueOf(Precision.round(DataReader.predict("EUR", Double.parseDouble(actualValues.get("EUR"))),4))+" zł");
        gbpFuture.setText(String.valueOf(Precision.round(DataReader.predict("GBP", Double.parseDouble(actualValues.get("GBP"))),4))+" zł");
        jpyFuture.setText(String.valueOf(Precision.round(DataReader.predict("JPY", Double.parseDouble(actualValues.get("JPY"))),4))+" zł");
        nokFuture.setText(String.valueOf(Precision.round(DataReader.predict("NOK", Double.parseDouble(actualValues.get("NOK"))),4))+" zł");
        sekFuture.setText(String.valueOf(Precision.round(DataReader.predict("SEK", Double.parseDouble(actualValues.get("SEK"))),4))+" zł");
        usdFuture.setText(String.valueOf(Precision.round(DataReader.predict("USD", Double.parseDouble(actualValues.get("USD"))),4))+" zł");
    }


    @FXML
    private void audAction() throws IOException {
        Main.showCurrencyStats("AUD","Dolar Australijski");
    }
    @FXML
    private void cadAction() throws IOException {
        Main.showCurrencyStats("CAD", "Dolar Kanadyjski");
    }
    @FXML
    private void chfAction() throws IOException {
        Main.showCurrencyStats("CHF", "Frank Szwajcarski");
    }
    @FXML
    private void eurAction() throws IOException {
        Main.showCurrencyStats("EUR", "Euro");
    }
    @FXML
    private void gbpAction() throws IOException {
        Main.showCurrencyStats("GBP", "Funt Brytyjski");
    }
    @FXML
    private void jpyAction() throws IOException {
        Main.showCurrencyStats("JPY", "Jen Japoński");
    }
    @FXML
    private void nokAction() throws IOException {
        Main.showCurrencyStats("NOK", "Korona Norweska");
    }
    @FXML
    private void sekAction() throws IOException {
        Main.showCurrencyStats("SEK", "Korona Szwedzka");
    }
    @FXML
    private void usdAction() throws IOException {
        Main.showCurrencyStats("USD", "Dolar Amerykański");
    }

    public Label getAudActual() {
        return audActual;
    }

    public Label getCadActual() {
        return cadActual;
    }

    public Label getChfActual() {
        return chfActual;
    }

    public Label getEurActual() {
        return eurActual;
    }

    public Label getGbpActual() {
        return gbpActual;
    }

    public Label getJpyActual() {
        return jpyActual;
    }

    public Label getNokActual() {
        return nokActual;
    }

    public Label getSekActual() {
        return sekActual;
    }

    public Label getUsdActual() {
        return usdActual;
    }

    public Label getAudFuture() {
        return audFuture;
    }

    public Label getCadFuture() {
        return cadFuture;
    }

    public Label getChfFuture() {
        return chfFuture;
    }

    public Label getEurFuture() {
        return eurFuture;
    }

    public Label getGbpFuture() {
        return gbpFuture;
    }

    public Label getJpyFuture() {
        return jpyFuture;
    }

    public Label getNokFuture() {
        return nokFuture;
    }

    public Label getSekFuture() {
        return sekFuture;
    }

    public Label getUsdFuture() {
        return usdFuture;
    }
}
