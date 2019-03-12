package networkController;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.commons.math3.util.Precision;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class provides methods to read currency data
 * @author dev.jakub.cierpial@gmail.com
 */
public class DataReader {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

    private DataReader() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * @param name name of currency
     * @param labels false to get data, true to get labels
     * @return list of currency prices, or time quote
     */
    public static List<String> readFromResources(String name,boolean labels)
    {
        FileReader file = null;
        List<String> values = new ArrayList<>();
        try {
            if(name.equals("time"))
            {
                file = new FileReader("networks/data/time/TimeSet.csv");
            }
            else if(labels) {
                file = new FileReader("networks/data/" + name.toLowerCase() + "/LabelSet.csv");
            }
            else
            {
                file = new FileReader("networks/data/" + name.toLowerCase() + "/DataSetToRead.csv");
            }
            BufferedReader dataBufferedReader = new BufferedReader(file);

            String curr = null;
            while ((curr = dataBufferedReader.readLine()) != null) {
                values.add(curr);
            }
            dataBufferedReader.close();
            file.close();

        } catch (IOException e) {
            Platform.runLater(() ->new Alert(Alert.AlertType.ERROR,e.toString()).showAndWait());        }
        return values;
    }

    /**
     * Reads actual prices of currency
     * @return map of <currrency name, currency value>
     */
    public static Map<String,String> readActual(){
        List<String> aud = DataReader.readFromResources("AUD",true);
        List<String> cad = DataReader.readFromResources("CAD",true);
        List<String> chf = DataReader.readFromResources("CHF",true);
        List<String> eur = DataReader.readFromResources("EUR",true);
        List<String> gbp = DataReader.readFromResources("GBP",true);
        List<String> jpy = DataReader.readFromResources("JPY",true);
        List<String> nok = DataReader.readFromResources("NOK",true);
        List<String> sek = DataReader.readFromResources("SEK",true);
        List<String> usd = DataReader.readFromResources("USD",true);

        TreeMap<String,String> lastValue = new TreeMap<>();

        lastValue.put("AUD", String.valueOf(Precision.round(Double.parseDouble(aud.get(aud.size()-1)),4)));
        lastValue.put("CAD",String.valueOf(Precision.round(Double.parseDouble(cad.get(cad.size()-1)),4)));
        lastValue.put("CHF",String.valueOf(Precision.round(Double.parseDouble(chf.get(chf.size()-1)),4)));
        lastValue.put("EUR",String.valueOf(Precision.round(Double.parseDouble(eur.get(eur.size()-1)),4)));
        lastValue.put("GBP",String.valueOf(Precision.round(Double.parseDouble(gbp.get(gbp.size()-1)),4)));
        lastValue.put("JPY",String.valueOf(Precision.round(Double.parseDouble(jpy.get(jpy.size()-1)),4)));
        lastValue.put("NOK",String.valueOf(Precision.round(Double.parseDouble(nok.get(nok.size()-1)),4)));
        lastValue.put("SEK",String.valueOf(Precision.round(Double.parseDouble(sek.get(sek.size()-1)),4)));
        lastValue.put("USD",String.valueOf(Precision.round(Double.parseDouble(usd.get(usd.size()-1)),4)));

        return lastValue;


    }

    /**
     * Using neural network predicts next currency price
     * @param name name of currency
     * @param val price of currrency
     * @return predicted price
     */
    public static double predict(String name,double val)
    {

        try {
            MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork("networks/"+name+".zip");
            DataNormalization normalizer = ModelSerializer.restoreNormalizerFromFile(new File("networks/"+name+".zip"));
            INDArray array = Nd4j.readNumpy("networks/data/" + name.toLowerCase() + "/LabelSet.csv");
            array = array.get(NDArrayIndex.interval(array.length()-365, array.length()), NDArrayIndex.all());
            DataNormalization dataNormalization = normalizer;
            dataNormalization.transform(array);
            INDArray predicted = net.rnnTimeStep(array);
            dataNormalization.revertLabels(predicted);

            return  predicted.getDouble(array.length()-1);
        } catch (IOException e) {
            Platform.runLater(() ->new Alert(Alert.AlertType.ERROR,e.toString()).showAndWait());        }

        return 0.0;
    }
}
