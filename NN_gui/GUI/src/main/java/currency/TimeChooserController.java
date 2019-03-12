package currency;

import frame.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author dev.jakub.cierpial@gmail.com
 */
public class TimeChooserController implements Initializable {




    @FXML
    private Label choose;
    @FXML
    private MenuButton menu;
    @FXML
    private MenuItem week;
    @FXML
    private MenuItem twoWeeks;
    @FXML
    private MenuItem threeWeeks;
    @FXML
    private MenuItem month;
    @FXML
    private MenuItem twoMonths;
    @FXML
    private MenuItem fourMonth;
    @FXML
    private MenuItem sixMonths;
    @FXML
    private MenuItem year;
    @FXML
    private MenuItem twoYears;
    @FXML
    private MenuItem threeYears;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        week.setOnAction(e -> Main.getCurrencyRepresentationController().updateGraphData(7));
        twoWeeks.setOnAction(e -> Main.getCurrencyRepresentationController().updateGraphData(14));
        threeWeeks.setOnAction(e -> Main.getCurrencyRepresentationController().updateGraphData(21));
        month.setOnAction(e -> Main.getCurrencyRepresentationController().updateGraphData(30));
        twoMonths.setOnAction(e -> Main.getCurrencyRepresentationController().updateGraphData(60));
        fourMonth.setOnAction(e -> Main.getCurrencyRepresentationController().updateGraphData(120));
        sixMonths.setOnAction(e -> Main.getCurrencyRepresentationController().updateGraphData(180));
        year.setOnAction(e -> Main.getCurrencyRepresentationController().updateGraphData(365));
        twoYears.setOnAction(e -> Main.getCurrencyRepresentationController().updateGraphData(730));
        threeYears.setOnAction(e -> Main.getCurrencyRepresentationController().updateGraphData(1095));

    }
}
