package application;

import configuration.NetCofiguration;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import model.CreateModel;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Class provides methods to train, serialize and update neural networks
 *
 * @author dev.jakub.cierpial@gmail.com
 */
public class ModelController {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

    /**
     * Trains all available neural networks using current data
     */
    public static void trainAll()
    {
        ThreadPoolExecutor executor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        executor.submit(() -> trainAUDNet());
        executor.submit(() -> trainCADNet());
        executor.submit(() -> trainEURNet());
        executor.submit(() -> trainUSDNet());
        executor.submit(() -> trainSEKNet());
        executor.submit(() -> trainNOKNet());
        executor.submit(() -> trainJPYNet());
        executor.submit(() -> trainGBPNet());
        executor.submit(() -> trainCHFNet());
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Platform.runLater(() ->new Alert(Alert.AlertType.ERROR,e.toString()).showAndWait());
        }


    }

    /**
     * Serialize neural net to zip file in provided catalogue
     * @param net neural network
     */
    private static void modelSerialize(CreateModel net)
    {
        File dir = new File("networks");
        if(!dir.exists())
            dir.mkdir();

            File file = new File("networks/"+net.getCurrency().toUpperCase()+".zip");
            try {
                ModelSerializer.writeModel(net.getNet(),file,false);
                ModelSerializer.addNormalizerToModel(file,net.getDataNormalization());
            } catch (IOException e) {
                Platform.runLater(() ->new Alert(Alert.AlertType.ERROR,e.toString()).showAndWait());
            }

    }

    /**
     * Updates network to actual time stamp
     * @param values data intended for entry into the network
     * @param name name of currency
     */
    public static void updateNetwork(List<String[]> values,String name)
    {
        try {
            MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork("networks/"+name.toUpperCase()+".zip");
            DataNormalization normalizer = ModelSerializer.restoreNormalizerFromFile(new File("networks/"+name.toUpperCase()+".zip"));
            INDArray array = Nd4j.readNumpy("networks/data/" + name + "/LabelSet.csv");
            DataNormalization dataNormalization = normalizer;

            INDArray test = array.get(NDArrayIndex.interval(array.length() - 500, array.length()), NDArrayIndex.all());
            dataNormalization.transform(test);
            INDArray output = net.output(test);
            dataNormalization.revertLabels(test);
            dataNormalization.revertLabels(output);


            double accuracy = CreateModel.countCorrect(test, output, 2,0);
            System.out.println("after update "+accuracy);
            if(accuracy<70)
            {
                Platform.runLater(() ->new Alert(Alert.AlertType.INFORMATION,"Sieć przewidująca : "+name.toUpperCase()+" " +
                        "po aktualizacj ma sprawdzalność poniżej 70% ("+accuracy+")zalecane ponowne przetrenowanie sieci").showAndWait());
            }

        } catch (IOException e) {
            Platform.runLater(() ->new Alert(Alert.AlertType.ERROR,e.toString()).showAndWait());        }
    }
    /*
        TYPES :
                s - small network ; need small size of hidden layers ; currency has small volatility ; or easy to train
                m - medium network ; need medium size of hidden layers ; currency has medium volatility ; or not very hard to train
                h - large network ; need large size of hidden layers ; currency has large volatility ; or hard to train
     */

    /**
     * Trains neural network for AUD currency, using historical data
     * Tests neural network
     * @return accuracy rate
     */
    public static double trainAUDNet()
    {
        NetCofiguration netCofiguration = new NetCofiguration(12345, 1, 0.0002);
        CreateModel audNet = new CreateModel("aud", netCofiguration.getListBuilder(), 128, 128,1460);
        audNet.trainNetwork(10,122);
        modelSerialize(audNet);
        return audNet.testNetwork();
    }
    /**
     * Trains neural network for CAD currency, using historical data
     * Tests neural network
     * @return accuracy rate
     */
    public static double trainCADNet()
    {
        NetCofiguration netCofiguration = new NetCofiguration(12345, 1, 0.001);
        CreateModel cadNet = new CreateModel("cad", netCofiguration.getListBuilder(), 128, 128,1460);
        cadNet.trainNetwork(10,45);
        modelSerialize(cadNet);
        return cadNet.testNetwork();
    }
    /**
     * Trains neural network for CHF currency, using historical data
     * Tests neural network
     * @return accuracy rate
     */
    public static double trainCHFNet()
    {
        NetCofiguration netCofiguration = new NetCofiguration(12345, 1, 0.0002);
        CreateModel chfNet = new CreateModel("chf", netCofiguration.getListBuilder(), 128, 128,1460);
        chfNet.trainNetwork(10,122);
        modelSerialize(chfNet);
        return chfNet.testNetwork();
    }
    /**
     * Trains neural network for EUR currency, using historical data
     * Tests neural network
     * @return accuracy rate
     */
    public static double trainEURNet()
    {
        NetCofiguration netCofiguration = new NetCofiguration(12345, 1, 0.001);
        CreateModel eurNet = new CreateModel("eur", netCofiguration.getListBuilder(), 16, 16,2000);
        eurNet.trainNetwork(10,35);
        modelSerialize(eurNet);
        return eurNet.testNetwork();
    }
    /**
     * Trains neural network for GBP currency, using historical data
     * Tests neural network
     * @return accuracy rate
     */
    public static double trainGBPNet()
    {
        NetCofiguration netCofiguration = new NetCofiguration(12345, 1, 0.001);
        CreateModel gbpNet = new CreateModel("gbp", netCofiguration.getListBuilder(), 32, 32,1470);
        gbpNet.trainNetwork(10,35);
        modelSerialize(gbpNet);
        return gbpNet.testNetwork();
    }
    /**
     * Trains neural network for JPY currency, using historical data
     * Tests neural network
     * @return accuracy rate
     */
    public static double trainJPYNet()
    {
        NetCofiguration netCofiguration = new NetCofiguration(12345, 1, 0.0002);
        CreateModel jpyNet = new CreateModel("jpy", netCofiguration.getListBuilder(), 32, 32,1460);
        jpyNet.trainNetwork(10,122);//modify
        modelSerialize(jpyNet);
        return jpyNet.testNetwork();
    }
    /**
     * Trains neural network for NOK currency, using historical data
     * Tests neural network
     * @return accuracy rate
     */
    public static double trainNOKNet()
    {
        NetCofiguration netCofiguration = new NetCofiguration(12345, 1, 0.001);
        CreateModel nokNet = new CreateModel("nok", netCofiguration.getListBuilder(), 16, 16,1460);
        nokNet.trainNetwork(10,35);
        modelSerialize(nokNet);
        return nokNet.testNetwork();
    }
    /**
     * Trains neural network for SEK currency, using historical data
     * Tests neural network
     * @return accuracy rate
     */
    public static double trainSEKNet()
    {
        NetCofiguration netCofiguration = new NetCofiguration(12345, 1, 0.001);
        CreateModel sekNet = new CreateModel("sek", netCofiguration.getListBuilder(), 32, 32,1460);
        sekNet.trainNetwork(10,30);
        modelSerialize(sekNet);
        return sekNet.testNetwork();
    }

    /**
     * Trains neural network for USD currency, using historical data
     * Tests neural network
     * @return accuracy rate
     */
    public static double trainUSDNet()
    {
        NetCofiguration netCofiguration = new NetCofiguration(12345, 1, 0.0002);
        CreateModel usdNet = new CreateModel("usd", netCofiguration.getListBuilder(), 128, 128,1460);
        usdNet.trainNetwork(10,122);
        modelSerialize(usdNet);
        return usdNet.testNetwork();
    }
}
