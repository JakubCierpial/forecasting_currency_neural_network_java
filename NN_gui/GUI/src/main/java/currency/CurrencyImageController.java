package currency;

import frame.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mainPage.MainWindowController;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class used to graphical representation of  currency fluctuations
 * @author dev.jakub.cierpial@gmail.com
 */
public class CurrencyImageController implements Initializable {

    @FXML
    private ImageView aud;
    @FXML
    private ImageView cad;
    @FXML
    private ImageView chf;
    @FXML
    private ImageView eur;
    @FXML
    private ImageView gbp;
    @FXML
    private ImageView jpy;
    @FXML
    private ImageView nok;
    @FXML
    private ImageView sek;
    @FXML
    private ImageView usd;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image image = new Image(new File("networks/images/steady.png").toURI().toString());
        aud.setImage(image);
        cad.setImage(image);
        chf.setImage(image);
        eur.setImage(image);
        gbp.setImage(image);
        jpy.setImage(image);
        nok.setImage(image);
        sek.setImage(image);
        usd.setImage(image);
    }

    /**
     * Examines decreases and increases of currency and adjust adequate image representation
     */
    public void setImages()
    {
        MainWindowController mainWindowController = Main.getMainWindowController();
        setImage(aud,checkValue(mainWindowController.getAudActual().getText(),mainWindowController.getAudFuture().getText()));
        setImage(cad,checkValue(mainWindowController.getCadActual().getText(),mainWindowController.getCadFuture().getText()));
        setImage(chf,checkValue(mainWindowController.getChfActual().getText(),mainWindowController.getChfFuture().getText()));
        setImage(eur,checkValue(mainWindowController.getEurActual().getText(),mainWindowController.getEurFuture().getText()));
        setImage(gbp,checkValue(mainWindowController.getGbpActual().getText(),mainWindowController.getGbpFuture().getText()));
        setImage(jpy,checkValue(mainWindowController.getJpyActual().getText(),mainWindowController.getJpyFuture().getText()));
        setImage(nok,checkValue(mainWindowController.getNokActual().getText(),mainWindowController.getNokFuture().getText()));
        setImage(sek,checkValue(mainWindowController.getSekActual().getText(),mainWindowController.getSekFuture().getText()));
        setImage(usd,checkValue(mainWindowController.getUsdActual().getText(),mainWindowController.getUsdFuture().getText()));

    }

    /**
     * Compares which value is bigger
     * @param actual actual currency price
     * @param future future currency price
     * @return result of comparing
     */
    private int checkValue(String actual,String future)
    {
        Double act = Double.parseDouble(actual.substring(0,actual.length()-3));
        Double fut = Double.parseDouble(future.substring(0,future.length()-3));
        return act.compareTo(fut);
    }
    /**
     * Sets for correct image representation for currency
     */
    private void setImage(ImageView imageView,int n)
    {
        if(n==0)
        {
            Image image = new Image(new File("networks/images/steady.png").toURI().toString());
            imageView.setImage(image);
        }
        else if(n==1)
        {
            Image image = new  Image(new File("networks/images/red.png").toURI().toString());
            imageView.setImage(image);
        }
        else
        {
            Image image = new Image(new File("networks/images/green.png").toURI().toString());
            imageView.setImage(image);
        }
    }
}
