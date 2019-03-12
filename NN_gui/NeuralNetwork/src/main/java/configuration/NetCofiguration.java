package configuration;

import lombok.Data;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;

/**
 * Class storing configuration of neural network
 * @author dev.jakub.cierpial@gmail.com
 */
@Data
public class NetCofiguration {

    private NeuralNetConfiguration.ListBuilder listBuilder;


    /**
     * @param seed value for generating random data
     * @param iterations numbers of single batch data iterations
     * @param learningRate value according to which weights will be adjusted
     */
    public NetCofiguration(int seed, int iterations, double learningRate)
    {
        NeuralNetConfiguration.Builder config = new NeuralNetConfiguration.Builder();
        config.seed(seed);
        config.iterations(iterations);
        config.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
        config.learningRate(learningRate);
        config.regularization(true);
        config.updater(Updater.RMSPROP);
        config.gradientNormalization(GradientNormalization.ClipL2PerLayer);
        config.trainingWorkspaceMode(WorkspaceMode.SEPARATE);
        listBuilder = config.list();
    }

}
