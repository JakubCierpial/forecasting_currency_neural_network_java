package welcomePage;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Welcome page class
 * @author dev.jakub.cierpial@gmail.com
 */
public class WelcomeViewController {


    @FXML
    private ProgressBar progressBar;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label currentOperation;


    private void toMainPage()  {

        Stage window =  (Stage) progressBar.getScene().getWindow();

        window.setResizable(false);
        window.close();
    }
    public void setProgress(Double progress) throws IOException {
        Platform.runLater(() ->progressIndicator.setProgress(progress));
        progressBar.setProgress(progress);
        if(progressBar.getProgress()==1.0)
            Platform.runLater(()->toMainPage());
    }
    public double getProgress()
    {
        return progressBar.getProgress();
    }
    public void setCurrentOperation(String s)
    {
        Platform.runLater(()->currentOperation.setText(s));
    }
}
