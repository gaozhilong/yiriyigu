package org.jianyi.yiriyigu.dl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.jianyi.yiriyigu.ta.TaService;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.CharSink;
import com.google.common.io.Files;

@Component
public class DataUtil {

    @Value("${csv.dir}")
    private String dir;

    @Autowired
    private TaService taService;

    /*
     * public DataSet getDatas(String code, String start, String end, int
     * period,
     * int fastperiod, int slowperiod, int signalperiod) {
     * this.taService.init(code, start, end);
     * double[] tclose = this.taService.getTclosed(period);
     * double[] tclosesubsma = this.taService.tcloseSubtractSma(period);
     * double[] smas = this.taService.smad(period);
     * double[][] macds = this.taService.macdd(fastperiod, slowperiod,
     * signalperiod);
     * double[][] bolls = this.taService.bolld(period);
     * double[] tcloseclassic = this.taService.getTclosedClassic(period);
     * int size = macds[0].length;
     * double[][] input = new double[size][9];
     * double[][] lable = new double[size][1];
     * for (int i = 0; i < size; i++) {
     * double[] value = new double[9];
     * value[0] = tclose[i + slowperiod + signalperiod - 2];
     * value[1] = tclosesubsma[i + slowperiod - 1];
     * value[2] = smas[i + slowperiod - 2];
     * value[3] = macds[0][i];
     * value[4] = macds[1][i];
     * value[5] = macds[2][i];
     * value[6] = bolls[0][i + slowperiod - 1];
     * value[7] = bolls[1][i + slowperiod - 1];
     * value[8] = bolls[2][i + slowperiod - 1];
     * input[i] = value;
     * double[] lableval = new double[1];
     * lableval[0] = tcloseclassic[i + slowperiod - 2];
     * lable[i] = lableval;
     * }
     * CpuNDArrayFactory cpuNDArrayFactory = new CpuNDArrayFactory();
     * INDArray inputarray = cpuNDArrayFactory.create(input);
     * INDArray lablearray = cpuNDArrayFactory.create(lable);
     * DataSet trainingData = new DataSet(inputarray, lablearray);
     * return trainingData;
     * }
     */

    public DataSet getLastDataByCsv(String code, String start, String end,
            int period, int fastperiod, int slowperiod, int signalperiod)
            throws IOException, InterruptedException {

        this.taService.init(code, start, end);

        List<String> datas = this.taService.getTaData(period, fastperiod,
            slowperiod, signalperiod);
        File file = new File(this.dir + "temp.csv");
        if (file.exists()) {
            file.delete();
        }
        List<String> data = Lists.newArrayList();
        data.add(datas.get(datas.size() - 1));
        CharSink sink = Files.asCharSink(file, Charsets.UTF_8);
        try {
            sink.writeLines(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataSet trainingData = this.readCSVDataset(30, 11, 3);

        return trainingData;
    }

    public DataSet getDatasByCsv(String code, String start, String end,
            int period, int fastperiod, int slowperiod, int signalperiod)
            throws IOException, InterruptedException {

        this.taService.init(code, start, end);

        List<String> datas = this.taService.getTaData(period, fastperiod,
            slowperiod, signalperiod);
        datas.remove(datas.size() - 1);
        File file = new File(this.dir + "temp.csv");
        if (file.exists()) {
            file.delete();
        }
        CharSink sink = Files.asCharSink(file, Charsets.UTF_8);
        try {
            sink.writeLines(datas);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataSet trainingData = this.readCSVDataset(30, 11, 3);

        return trainingData;
    }

    private DataSet readCSVDataset(int batchSize, int labelIndex,
            int numClasses) throws IOException, InterruptedException {
        DataSet dataSet = null;
        try (RecordReader rr = new CSVRecordReader();) {
            rr.initialize(new FileSplit(new File(this.dir + "temp.csv")));
            DataSetIterator iterator = new RecordReaderDataSetIterator(rr,
                batchSize, labelIndex, numClasses);
            dataSet = iterator.next();
        }
        return dataSet;
    }

}
