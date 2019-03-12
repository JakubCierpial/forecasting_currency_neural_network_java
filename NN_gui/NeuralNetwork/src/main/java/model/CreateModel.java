package model;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.Data;
import org.apache.commons.math3.util.Precision;
import org.deeplearning4j.api.storage.StatsStorageRouter;
import org.deeplearning4j.api.storage.impl.RemoteUIStatsStorageRouter;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.eval.RegressionEvaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.memory.MemoryWorkspace;
import org.nd4j.linalg.api.memory.conf.WorkspaceConfiguration;
import org.nd4j.linalg.api.memory.enums.AllocationPolicy;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class represents neural network
 * Stores network, currency, quantity Of Data to training
 * Performs operations to train network
 * @author dev.jakub.cierpial@gmail.com
 */
@Data
public class CreateModel implements Serializable {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());
    private String currency;
    private MultiLayerNetwork net;
    private int quantityOfData;
    private SplitTestAndTrain splitTestAndTrain;
    private DataNormalization dataNormalization;

    /**
     * @param currency name of currency
     * @param configListBuilder configuration for network
     * @param sizeOfFirstHidden size of first hidden layer
     * @param sizeOfSeondHidden size of second hidden layer
     * @param quantityOfData quantity of data used for training
     */
    public CreateModel(String currency, NeuralNetConfiguration.ListBuilder configListBuilder, int sizeOfFirstHidden, int sizeOfSeondHidden, int quantityOfData)
    {
        this.currency = currency;
        this.quantityOfData = quantityOfData;
        UIServer uiServer = UIServer.getInstance();
        uiServer.enableRemoteListener();
        StatsStorageRouter remoteUIRouter = new RemoteUIStatsStorageRouter("http://localhost:9000");
        //uiServer.attach(statsStorage);

        configListBuilder.layer(0, new GravesLSTM.Builder().nIn(1).nOut(sizeOfFirstHidden)
                .activation(Activation.TANH).l2(0.0001).weightInit(WeightInit.XAVIER).dropOut(0.2)
                .build());
        configListBuilder.layer(1, new GravesLSTM.Builder().nIn(sizeOfFirstHidden).nOut(sizeOfSeondHidden)
            .activation(Activation.TANH).l2(0.0001).weightInit(WeightInit.XAVIER).dropOut(0.2)
            .build());
        configListBuilder.layer(2, new GravesLSTM.Builder().nIn(sizeOfSeondHidden).nOut(sizeOfSeondHidden)
                .activation(Activation.TANH).l2(0.0001).weightInit(WeightInit.XAVIER).dropOut(0.2)
                .build());
        configListBuilder.layer(3, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .activation(Activation.IDENTITY).l2(0.0001).weightInit(WeightInit.XAVIER)
                .nIn(sizeOfSeondHidden).nOut(1).build());
        configListBuilder.pretrain(false).backprop(true);

        MultiLayerConfiguration conf = configListBuilder.build();
        net = new MultiLayerNetwork(conf);
        net.setListeners(new StatsListener(remoteUIRouter,100));
    }

    /**
     * @param batch size of single package of data delivered to network
     * @param epoch define how many times repeat the entire teaching process
     */
    public void trainNetwork(int batch, int epoch)
    {
        makeDataSet();
        DataSet train = splitTestAndTrain.getTrain();
        List<DataSet> dataSets = train.asList();
        DataSet test = splitTestAndTrain.getTest();
        ListDataSetIterator listDataSetIterator = new ListDataSetIterator(dataSets,batch);

        for (int i = 0; i < epoch; i++) {
            // train the model

            while (listDataSetIterator.hasNext()) {
                DataSet next = listDataSetIterator.next();

                net.fit(next);
            }// fit model using mini-batch data
            LOG.info("Epoch : {}",i);
            listDataSetIterator.reset();
            net.rnnClearPreviousState();
            RegressionEvaluation evaluation = new RegressionEvaluation();
            INDArray features = test.getFeatures();


            INDArray predicted = net.output(features, false);
            INDArray labeles = test.getLabels().reshape(predicted.shape());
            evaluation.evalTimeSeries(labeles, predicted);

        }
        Nd4j.getWorkspaceManager().destroyAllWorkspacesForCurrentThread();


    }

    /**
     * Tests verifiability of network
     * @return accuracy of network
     */
    public double testNetwork()
    {
        DataSet test = splitTestAndTrain.getTest();
        INDArray netOutput = net.output(test.getFeatures());
        INDArray labels = test.getLabels();

        dataNormalization.revertLabels(labels);
        dataNormalization.revertLabels(netOutput);

        System.out.println(currency);
        System.out.println("For 3 : "+CreateModel.countCorrect(labels, netOutput, 3,1));

        return countCorrect(labels,netOutput,2,1);


    }
    public static double countCorrect(INDArray labels, INDArray netOutput, int scale,int offset)
    {

        List<Double> predictedEuro = new ArrayList<>();
        List<Double> actualEuro = new ArrayList<>();


        for (int i = 0; i < labels.getColumn(0).length()-2; i++) {

            predictedEuro.add(Precision.round(netOutput.getColumn(0).getDouble(i), scale));
            actualEuro.add(Precision.round(labels.getColumn(0).getDouble(i-offset), scale));


        }
        double[] predicted = predictedEuro.stream().mapToDouble(Double::doubleValue)
                .toArray();
        double[] actual = actualEuro.stream().mapToDouble(Double::doubleValue)
                .toArray();

        double correct = 0.0;
        for (int i = 0; i < actual.length; i++) {
            if (predicted[i] == actual[i])
                correct++;
        }
        LOG.info("Correct predictions : " + correct + "/" + (actual.length) +" ; Accuracy  : "+(correct/actual.length)*100.0);
        //PlotUtil.plot(predicted, actual, "net");

        return (correct/actual.length)*100;
    }

    /**
     * Creates data set to train network (data extracted from resources)
     */
    private void makeDataSet()
    {
        INDArray learningData =  null;
        INDArray labels = null;
//        WorkspaceConfiguration mmap = WorkspaceConfiguration.builder()
//                .initialSize(1000000000)
//                .policyAllocation(AllocationPolicy.STRICT)
//                .build();
//
//        try (MemoryWorkspace ws = Nd4j.getWorkspaceManager().getAndActivateWorkspace(mmap, "M2")) {
//            INDArray x = Nd4j.create(10000);
//        }
        try {
            learningData = Nd4j.readNumpy("networks/data/"+currency + "/DataSetToRead.csv");
            labels = Nd4j.readNumpy("networks/data/"+currency + "/LabelSet.csv");
            INDArray trainSet = learningData.get(NDArrayIndex.interval(learningData.length()-quantityOfData, learningData.length()), NDArrayIndex.all());
            INDArray labesSet = labels.get(NDArrayIndex.interval(learningData.length()-quantityOfData, learningData.length()), NDArrayIndex.all());
            DataSet dataSet = new DataSet(trainSet, labesSet);
            normalize(dataSet);
            splitTestAndTrain = dataSet.splitTestAndTrain(0.70);
        } catch (IOException e) {
            Platform.runLater(() ->new Alert(Alert.AlertType.ERROR,e.toString()).showAndWait());
        }



    }

    /**
     * Normalizes data to the appropriate for the network
     * @param data data set provided to train network
     */
    private void normalize(DataSet data)
    {

        dataNormalization = new NormalizerMinMaxScaler(-1, 1);
        dataNormalization.fitLabel(true);
        dataNormalization.fit(data);
        dataNormalization.transform(data);
    }

}
