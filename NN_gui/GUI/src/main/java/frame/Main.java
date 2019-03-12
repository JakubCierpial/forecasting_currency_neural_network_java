package frame;

import currency.CurrencyRepresentationController;
import currency.CurrencyImageController;
import ftp.FtpController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mainPage.MainWindowController;
import updating.DataUpdater;
import welcomePage.WelcomeViewController;

import java.io.IOException;

public class Main extends Application {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());
    private static Stage stage;
    private static BorderPane layout;
    private static CurrencyRepresentationController currencyRepresentationController;
    private static FrameController frameController;
    private static MainWindowController mainWindowController;
    private static CurrencyImageController currencyImageController;
    private static WelcomeViewController welcomeViewController;


    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                close();
            }
        });
        stage = primaryStage;
        stage.setTitle("Przewidywanie cen walut z wykorzystaniem sztucznych sieci neuronowych");
        stage.setResizable(false);
        showMainPage();

    }
    public static void close()
    {
        Platform.exit();
        System.exit(0);
    }

    /**
     * Shows welcome page
     * Assigns scene controller to value
     * @throws IOException
     */
    private static void showEnterPage() throws IOException {
        Stage welcomestage = new Stage();
        welcomestage.setTitle("Strona Powitalna");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(WelcomeViewController.class.getResource("welcomeView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        welcomeViewController = fxmlLoader.getController();
        welcomeViewController.setCurrentOperation("Otwieranie głównego okna aplikacji");
        welcomestage.setScene(scene);
        welcomestage.show();
    }

    /**
     * Shows main page where all scenes will be added
     * Shows Welcome page
     * @throws IOException
     */
    private void showMainPage() throws IOException, InterruptedException {
        showMainStage();
        showEnterPage();


        Platform.runLater(() -> {
            Thread t = new Thread(()-> {
                try {
                    downloadData();
                    showMainItems();
                } catch (IOException e) {
                    Platform.runLater(() ->new Alert(Alert.AlertType.ERROR,e.toString()).showAndWait());
                }
            });
            t.start();
        });




    }

    /**
     * Shows main page where all scenes will be added
     * Assigns frame controller to value
     * @throws IOException
     */
    private static void showMainStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Main.class.getResource("mainApp.fxml"));
        layout = fxmlLoader.load();
        frameController = fxmlLoader.getController();
        frameController.makeDisable();
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();

    }
    private static void downloadData() throws IOException {
        welcomeViewController.setProgress(0.25);
        welcomeViewController.setCurrentOperation("Pobieranie zaległych danych");
        welcomeViewController.setProgress(0.25);
        FtpController ftpController = new FtpController();
        ftpController.downloadNew();
        ftpController.disconnect();
        welcomeViewController.setProgress(welcomeViewController.getProgress()+0.25);
        welcomeViewController.setCurrentOperation("Uaktualnianie danych oraz testowanie sieci");
        DataUpdater dataUpdater = new DataUpdater();
        dataUpdater.readAndSave();
    }

    /**
     * Shows main content for currency
     * Assigns main content scene controller to value
     * @throws IOException
     */
    static void showMainItems() throws IOException {
        frameController.makeDisable();
        welcomeViewController.setProgress(welcomeViewController.getProgress()+0.25);
        welcomeViewController.setCurrentOperation("Ładowanie zawartości okien");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainWindowController.class.getResource("items.fxml"));
        AnchorPane anchorPane = fxmlLoader.load();
        MainWindowController controller = fxmlLoader.getController();
        mainWindowController = controller;
        setCurrencyImage();
        Platform.runLater(() ->layout.setCenter(anchorPane));
        Platform.runLater(() ->layout.setRight(null));
        welcomeViewController.setCurrentOperation("Ustawianie grafik reprezentujących fluktuacje");
        currencyImageController.setImages();
        welcomeViewController.setProgress(1.00);


    }

    /**
     * Shows tab pane with detailed data of selected currency
     * @param curr name of currency
     * @param longCurr long name of currency
     * @throws IOException
     */
    public static void showCurrencyStats(String curr, String longCurr) throws IOException {
        frameController.makeEnable();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(CurrencyRepresentationController.class.getResource("currencyScene.fxml"));
        TabPane tabPane = fxmlLoader.load();
        currencyRepresentationController = fxmlLoader.getController();
        currencyRepresentationController.setName(longCurr);
        currencyRepresentationController.getData(curr);
        layout.setCenter(tabPane);
        showCurrencyChooser();

    }

    /**
     * Shows date picker to specified period of displaying data
     * @throws IOException
     */
    private static void showCurrencyChooser() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(CurrencyRepresentationController.class.getResource("timeChooser.fxml"));
        AnchorPane load = fxmlLoader.load();
        layout.setRight(load);

    }

    /**
     * Shows currency fluctuations with graphic representation
     */
    private static void setCurrencyImage() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(CurrencyImageController.class.getResource("currencyImageView.fxml"));
        GridPane gridPane = null;
        try {
            gridPane = fxmlLoader.load();
        } catch (IOException e) {
            Platform.runLater(() ->new Alert(Alert.AlertType.ERROR,e.toString()).showAndWait());
        }
        currencyImageController = fxmlLoader.getController();
        GridPane finalGridPane = gridPane;
        Platform.runLater(()->layout.setLeft(finalGridPane));

    }
    public static CurrencyRepresentationController getCurrencyRepresentationController()
    {
        return currencyRepresentationController;
    }

    public static MainWindowController getMainWindowController()
    {
        return mainWindowController;
    }

    public static void main(String[] args) {
        launch(args);

    }
}
