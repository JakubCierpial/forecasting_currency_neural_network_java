package frame;

import application.ModelController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;


/**
 * Main window controller
 * @author dev.jakub.cierpial@gmail.com
 */
public class FrameController {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());
    @FXML
    private Button home;

    @FXML
    private Menu trainingMenu;
    @FXML
    private MenuItem all;
    @FXML
    private MenuItem aud;
    @FXML
    private MenuItem cad;
    @FXML
    private MenuItem chf;
    @FXML
    private MenuItem eur;
    @FXML
    private MenuItem gbp;
    @FXML
    private MenuItem jpy;
    @FXML
    private MenuItem nok;
    @FXML
    private MenuItem sek;
    @FXML
    private MenuItem usd;

    @FXML
    private Menu help;
    @FXML
    private MenuItem about;
    @FXML
    private MenuItem howToUse;


    @FXML
    public void homeAction() throws IOException {
        Main.showMainItems();
    }

    private Stage s = null;
    private  final static String WARN = "Sieć zostanie wyczyszczona i wytrenowana na nowo.\nUcznie może zająć długi czas.\nCzy na pewno chcesz to zrobić? ";

    void makeDisable() {
        home.setVisible(false);
    }

    void makeEnable() {
        home.setVisible(true);
    }

    /**
     * Trains all networks
     */
    @FXML
    private void trainall() {
        int i = showWarnMessage("Wszsystkie sieci zostaną wyczyszczone i wytrenowane na nowo.\nUcznie może zająć długi czas.\nCzy na pewno chcesz to zrobić? ");
        if (i == 0) {
            s = showWaitingvView();
            Stage finalS = s;
            new Thread(() -> {
                ModelController.trainAll();
                Platform.runLater(() -> {
                    showEndMessage(-1);
                    finalS.close();
                });
            }).start();
        }

    }

    /**
     * Trains correct network based on menu item name
     * @param e action cause by button
     */
    @FXML
    private void train(ActionEvent e) {
        MenuItem source = (MenuItem) e.getSource();

        switch (source.getId()) {
            case "aud":
                if (showWarnMessage(WARN) == 0) {
                    s = showWaitingvView();
                    Stage finalS = s;
                    new Thread(() -> {
                        double ac = ModelController.trainAUDNet();
                        Platform.runLater(() -> {
                            showEndMessage(ac);
                            finalS.close();
                        });
                    }).start();
                    showVisualizationMessage();
                }
                break;
            case "cad":
                if (showWarnMessage(WARN) == 0) {
                    s = showWaitingvView();
                    Stage finalS = s;
                    new Thread(() -> {
                        double ac = ModelController.trainCADNet();
                        Platform.runLater(() -> {
                            showEndMessage(ac);
                            finalS.close();
                        });
                    }).start();
                    showVisualizationMessage();
                }
                break;
            case "chf":
                if (showWarnMessage(WARN) == 0) {
                    s = showWaitingvView();
                    Stage finalS = s;
                    new Thread(() -> {
                        double ac = ModelController.trainCHFNet();
                        Platform.runLater(() -> {
                            showEndMessage(ac);
                            finalS.close();
                        });
                    }).start();
                    showVisualizationMessage();
                }
                break;
            case "eur":
                if (showWarnMessage(WARN) == 0) {
                    s = showWaitingvView();
                    Stage finalS = s;
                    new Thread(() -> {
                        double ac = ModelController.trainEURNet();
                        Platform.runLater(() -> {
                            showEndMessage(ac);
                            finalS.close();
                        });
                    }).start();
                    showVisualizationMessage();
                }
                break;
            case "gbp":
                if (showWarnMessage(WARN) == 0) {
                    s = showWaitingvView();
                    Stage finalS = s;
                    new Thread(() -> {
                        double ac = ModelController.trainGBPNet();
                        Platform.runLater(() -> {
                            showEndMessage(ac);
                            finalS.close();
                        });
                    }).start();
                    showVisualizationMessage();

                }
                break;
            case "jpy":
                if (showWarnMessage(WARN) == 0) {
                    s = showWaitingvView();
                    Stage finalS = s;
                    new Thread(() -> {
                        double ac = ModelController.trainJPYNet();
                        Platform.runLater(() -> {
                            showEndMessage(ac);
                            finalS.close();
                        });
                    }).start();
                    showVisualizationMessage();
                }
                break;
            case "nok":
                if (showWarnMessage(WARN) == 0) {
                    s = showWaitingvView();
                    Stage finalS = s;
                    new Thread(() -> {
                        double ac = ModelController.trainNOKNet();
                        Platform.runLater(() -> {
                            showEndMessage(ac);
                            finalS.close();
                        });
                    }).start();
                    showVisualizationMessage();
                }
                break;
            case "sek":
                if (showWarnMessage(WARN) == 0) {
                    s = showWaitingvView();
                    Stage finalS = s;
                    new Thread(() -> {
                        double ac = ModelController.trainSEKNet();
                        Platform.runLater(() -> {
                            showEndMessage(ac);
                            finalS.close();
                        });
                    }).start();
                    showVisualizationMessage();
                }
                break;
            case "usd":
                if (showWarnMessage(WARN) == 0) {
                    s = showWaitingvView();
                    Stage finalS = s;
                    new Thread(() -> {
                        double ac = ModelController.trainUSDNet();
                        Platform.runLater(() -> {
                            showEndMessage(ac);
                            finalS.close();
                        });
                    }).start();
                    showVisualizationMessage();
                }
                break;


        }

    }

    /**
     * Shows stage representing neural network learning
     * @return Stage with waiting content
     */
    private Stage showWaitingvView() {
        Stage trainningView = new Stage();
        trainningView.setResizable(false);
        trainningView.setTitle("Trwa uczenie sieci");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Main.class.getResource("trainingView.fxml"));
        AnchorPane anchorPane = null;
        try {
            anchorPane = fxmlLoader.load();
        } catch (IOException e) {
            Platform.runLater(() ->new Alert(Alert.AlertType.ERROR,e.toString()).showAndWait());        }
        Scene scene = new Scene(anchorPane);
        trainningView.setScene(scene);
        trainningView.show();
        return trainningView;

    }

    /**
     * Shows warning message box
     * @param warn Text of warning message
     * @return if ok return 0 if not 1
     */
    private int showWarnMessage(String warn) {
        Optional<ButtonType> result = createAlert(Alert.AlertType.CONFIRMATION, "Ostrzeżenie", "Jesteś pewny?", warn,400);

        if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Shows information about network accuracy
     * @param accuracy accuracy of neural network
     */
    private void showEndMessage(double accuracy) {



        if(accuracy==-1)
        {
            createAlert(Alert.AlertType.INFORMATION,"Przeuczono sieci","Skończono nauczanie","Wsystkie sieci zostały przeuczone ponownie",400);
        }
        else {
            createAlert(Alert.AlertType.INFORMATION,"Przeuczono sieci","Skończono nauczanie","Sieć została nauczona ponownie\n" +
                    "poziom dokładności = " + accuracy,400);

        }

    }

    /**
     * Shows dailog box about learning graphic representation
     * If positive redirect to browser
     */
    private void showVisualizationMessage() {
        Optional<ButtonType> buttonType = createAlert(Alert.AlertType.CONFIRMATION, "Trenowanie", "Czy chesz zobaczyć wizualizacje uczenia?", "Kliknij \"OK\" aby otworzyć",400);

        if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(URI.create("http://localhost:9000"));
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }


        }
    }


    /**
     * Displaying information about program and author
     */
    @FXML
    private void showAbout() {
        String content = "Program prognozuje ceny walut z jednodniowym wyprzedzeniem.\nPrognozowanie wykonywane jest za pomocą sztucznych rekurencyjnych sieci neuronowych.";
        createAlert(Alert.AlertType.INFORMATION,"Informacje o programie","Autor - Jakub Cierpiał",content,400);
    }

    /**
     * Displaying information about using program
     */
    @FXML
    private void showHelp() {
        String content ="Główna strona -\n\n" +
                "       część centralna - \n" +
                "               pierwsza kolumna zawiera skrótowe nazwy walut, w celu uzyskania większej ilości informacji można kliknąć na wybraną walutę.\n" +
                "               druga kolumna przedstawia aktualne ceny poszczególnych walut.\n" +
                "               trzecia kolumna przedstawia prognozowane ceny poszczególnych walut.\n\n" +
                "       lewa strona -\n" +
                "               jest wizualizacją zmian poszczególnych walut.\n" +
                "                   * zielona strzałka w górę symbolizuje prognozowany wzrost ceny danej waluty\n" +
                "                   * czerwona strzałka w dół symbolizuje prognozowany spadek ceny danej waluty\n" +
                "                   * niebieska pozioma kreska symbolizuje prognozowany brak zmian ceny danej waluty\n\n" +
                "Po kliknięciu na walutę -\n" +
                "               część centralna -\n" +
                "                   karta \"Graph\" przedstawia wykres wartośći waluty\n" +
                "                   karta \"Data\" przedstawia tabelę wartości waluty wraz z datami ich zanotowania\n\n" +
                "               prawa strona -\n" +
                "                   rozwijane menu pozwala zdefniować zakres wyświetlanych danych do teraz\n\n" +
                "               przycisk \"Powrót\" wraca do listy walut\n\n" +
                "Dane wykorzystywane w programie pochodzą ze strony bossa.pl i są notowaniami NBP";
        createAlert(Alert.AlertType.INFORMATION,"Jak używać?","Jak używać?",content,1000);


    }

    private Optional<ButtonType> createAlert(Alert.AlertType alertType, String title, String header, String content,int prefSize) {
        Alert alert = new Alert(alertType);
        alert.setResizable(true);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(FrameController.class.getResource("dialog.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog");
        dialogPane.setPrefWidth(prefSize);
        return alert.showAndWait();
    }


}
