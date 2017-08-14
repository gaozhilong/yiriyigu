package org.jianyi.yiriyigu.dl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.deeplearning4j.datasets.iterator.SamplingDataSetIterator;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingModelSaver;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.saver.LocalFileModelSaver;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.earlystopping.termination.BestScoreEpochTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StockClassifier {

    private final static Logger logger = LoggerFactory
        .getLogger(StockClassifier.class);

    @Value("${model.dir}")
    private String dir;

    int seed = 123;
    double learningRate = 0.002;
    int batchSize = 30;
    int nEpochs = 13;

    int numInputs = 11;
    int numOutputs = 3;
    int numHiddenNodes = 81;

    private MultiLayerNetwork getNetwork() {
        MultiLayerNetwork model = null;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .seed(this.seed).iterations(100).activation(Activation.TANH)
            .weightInit(WeightInit.XAVIER).learningRate(0.1)
            .regularization(true).l2(1e-4).list()
            .layer(0,
                new DenseLayer.Builder().nIn(this.numInputs).nOut(81).build())
            .layer(1, new DenseLayer.Builder().nIn(81).nOut(81).build())
            .layer(2,
                new OutputLayer.Builder(
                    LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX).nIn(81)
                        .nOut(this.numOutputs).build())
            .backprop(true).pretrain(false).build();

        model = new MultiLayerNetwork(conf);
        return model;
    }

    public void train(String code, DataSet dataSet, DataSet dataTest)
            throws IOException {
        MultiLayerNetwork model = this.getNetwork();

        DataSetIterator mnistTrain = new SamplingDataSetIterator(dataSet, 30,
            dataSet.numExamples());
        DataSetIterator mnistTest = new SamplingDataSetIterator(dataTest, 10,
            dataTest.numExamples());

        String exampleDirectory = this.dir + "/DL4JEarlyStoppingExample/";
        EarlyStoppingModelSaver<MultiLayerNetwork> saver = new LocalFileModelSaver(
            exampleDirectory);
        EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>()
            .epochTerminationConditions(
                new BestScoreEpochTerminationCondition(0.002))
            .evaluateEveryNEpochs(1)
            .iterationTerminationConditions(
                new MaxTimeIterationTerminationCondition(20, TimeUnit.MINUTES)) //Max of 20 minutes
            .scoreCalculator(new DataSetLossCalculator(mnistTest, true))     //Calculate test set score
            .modelSaver(saver).build();

        EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, model,
            mnistTrain);

        //Conduct early stopping training:
        EarlyStoppingResult<MultiLayerNetwork> result = trainer.fit();
        StockClassifier.logger
            .info("Termination reason: " + result.getTerminationReason());
        StockClassifier.logger
            .info("Termination details: " + result.getTerminationDetails());
        StockClassifier.logger.info("Total epochs: " + result.getTotalEpochs());
        StockClassifier.logger
            .info("Best epoch number: " + result.getBestModelEpoch());
        StockClassifier.logger
            .info("Score at best epoch: " + result.getBestModelScore());

        //Print score vs. epoch
        Map<Integer, Double> scoreVsEpoch = result.getScoreVsEpoch();
        List<Integer> list = new ArrayList<>(scoreVsEpoch.keySet());
        Collections.sort(list);
        StockClassifier.logger.info("Score vs. Epoch:");
        for (Integer i : list) {
            StockClassifier.logger.info(i + "\t" + scoreVsEpoch.get(i));
        }

        /*
         * model.init();
         * model.setListeners(new ScoreIterationListener(10)); //Print score
         * every 10 parameter updates
         * for (int n = 0; n < this.nEpochs; n++) {
         * model.fit(dataSet);
         * }
         */
        File locationToSave = new File(this.dir + "/" + code + "model.zip");      //Where to save the network. Note: the file is in .zip format - can be opened externally
        if (locationToSave.exists()) {
            locationToSave.delete();
        }
        boolean saveUpdater = true;                                             //Updater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this if you want to train your network more in the future
        ModelSerializer.writeModel(result.getBestModel(), locationToSave,
            saveUpdater);
    }

    public void trainAppend(DataSet dataSet, String modelPath)
            throws IOException {
        MultiLayerNetwork model = ModelSerializer
            .restoreMultiLayerNetwork(modelPath);
        model.init();
        model.setListeners(new ScoreIterationListener(10));  //Print score every 10 parameter updates
        for (int n = 0; n < this.nEpochs; n++) {
            model.fit(dataSet);
        }
        File locationToSave = new File(modelPath);      //Where to save the network. Note: the file is in .zip format - can be opened externally
        boolean saveUpdater = true;                                             //Updater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this if you want to train your network more in the future
        ModelSerializer.writeModel(model, locationToSave, saveUpdater);
    }

    public String eval(DataSet dataSet, String code) throws IOException {
        System.out.println("测试的结果集：" + dataSet.toString());
        MultiLayerNetwork model = ModelSerializer
            .restoreMultiLayerNetwork(this.dir + "/" + code + "model.zip");
        Evaluation eval = new Evaluation(3);
        INDArray output = model.output(dataSet.getFeatureMatrix());

        eval.eval(dataSet.getLabels(), output);
        StockClassifier.logger.info(eval.stats());
        return eval.stats();
    }

    public String out(DataSet dataSet, String code, String modelName)
            throws IOException {
        String modelPath = this.dir + "/" + code + "model.zip";
        if (!StringUtils.isBlank(modelName)) {
            modelPath = this.dir + "/" + modelName;
        }
        MultiLayerNetwork model = ModelSerializer
            .restoreMultiLayerNetwork(modelPath);
        Evaluation eval = new Evaluation(3);
        INDArray output = model.output(dataSet.getFeatureMatrix());

        eval.eval(dataSet.getLabels(), output);
        INDArray guessIndex = Nd4j.argMax(output, 1);

        System.out.println(guessIndex);

        return output.toString();
    }

}
