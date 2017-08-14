package org.jianyi.yiriyigu.ta;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.jianyi.yiriyigu.modle.Stock;
import org.jianyi.yiriyigu.repository.StockRepository;
import org.jianyi.yiriyigu.ta.quantization.Boll;
import org.jianyi.yiriyigu.ta.quantization.Macd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Component
public class TaService {

    @Autowired
    private StockRepository stockRepository;

    private List<String> day;

    private List<Double> tclose;

    private List<Double> high;

    private List<Double> low;

    private List<Double> topen;

    private List<Double> lclose;

    private List<Double> chg;

    private List<Double> pchg;

    private List<Double> turnover;

    private List<Double> voturnover;

    private List<Double> vaturnover;

    private Core talib;

    public TaService() {
        super();
        this.talib = new Core();
        this.day = Lists.newArrayList();
        this.tclose = Lists.newArrayList();
        this.high = Lists.newArrayList();
        this.low = Lists.newArrayList();
        this.topen = Lists.newArrayList();
        this.lclose = Lists.newArrayList();
        this.chg = Lists.newArrayList();
        this.pchg = Lists.newArrayList();
        this.turnover = Lists.newArrayList();
        this.voturnover = Lists.newArrayList();
        this.vaturnover = Lists.newArrayList();
    }

    public void init(String code, String start, String end) {
        this.getData(this.stockRepository
            .findByCodeAndDayGreaterThanEqualAndDayLessThan(code, start, end));
    }

    public List<String> getTaData(int period, int fastperiod, int slowperiod,
            int signalperiod) {
        int length = this.tclose.size() - slowperiod - signalperiod + 1;
        double[] tclosed = this.getTclosed(length);
        double[] tclosesubsma = this.getSubtractSma(this.tclose, period,
            length);
        double[] smas = this.smad(period, length);
        double[][] macds = this.macdd(fastperiod, slowperiod, signalperiod);
        double[][] bolls = this.bolld(period, length);
        double[] tcloseclassic = this.getTclosedClassic(length);
        double[] voturnovers = this.getSubtractSma(this.voturnover, period,
            length);
        double[] vaturnovers = this.getSubtractSma(this.vaturnover, period,
            length);
        List<String> datas = Lists.newArrayList();
        for (int i = 0; i < length; i++) {
            StringBuffer line = new StringBuffer();
            line.append(tclosed[i]);
            line.append(",");
            line.append(tclosesubsma[i]);
            line.append(",");
            line.append(smas[i]);
            line.append(",");
            line.append(macds[0][i]);
            line.append(",");
            line.append(macds[1][i]);
            line.append(",");
            line.append(macds[2][i]);
            line.append(",");
            line.append(bolls[0][i]);
            line.append(",");
            line.append(bolls[1][i]);
            line.append(",");
            line.append(bolls[2][i]);
            line.append(",");
            line.append(voturnovers[i]);
            line.append(",");
            line.append(vaturnovers[i]);
            line.append(",");
            line.append(Double.valueOf(tcloseclassic[i]).intValue());
            datas.add(line.toString());
        }
        return datas;
    }

    public double[] getTcloseArray() {
        double[] tcloses = this.tclose.stream().mapToDouble(Double::doubleValue)
            .toArray();
        return tcloses;
    }

    public double[] getDoubleArrayByLength(double[] data, int length) {
        double[] result = new double[length];
        int startidx = data.length - length;
        for (int i = 0; i < length; i++) {
            result[i] = data[i + startidx];
        }
        return result;
    }

    public double[] getTclosed(int length) {
        double[] tcloses = this.getPData(this.tclose);
        return this.getDoubleArrayByLength(tcloses, length);
    }

    public double[] getTclosedClassic(int length) {
        double[] tcloses = this.getPData(this.tclose);
        double[] tclosesc = new double[length];
        int startidx = tcloses.length - length + 1;
        for (int i = 0; i < length - 1; i++) {
            if (tcloses[i + startidx] > 0) {
                tclosesc[i] = 2;
            } else if (tcloses[i + startidx] < 0) {
                tclosesc[i] = 0;
            } else {
                tclosesc[i] = 1;
            }
        }
        tclosesc[length - 1] = 0;
        return tclosesc;
    }

    public double[] smad(int period, int length) {
        double[] sma = this.getPData(this.sma(this.tclose, period));
        return this.getDoubleArrayByLength(sma, length);
    }

    public double[] getSubtractSma(List<Double> datalst, int period,
            int length) {
        double[] data = datalst.stream().mapToDouble(Double::doubleValue)
            .toArray();
        double[] sma = this.sma(datalst, period).stream()
            .mapToDouble(Double::doubleValue).toArray();
        double[] result = new double[length];
        BigDecimal last = new BigDecimal(0);
        BigDecimal now = new BigDecimal(0);
        int starttclose = data.length - length;
        int startsma = sma.length - length;
        for (int i = 0; i < length; i++) {
            last = new BigDecimal(data[i + starttclose]);
            now = new BigDecimal(sma[i + startsma]);
            result[i] = last.subtract(now)
                .divide(last, 4, BigDecimal.ROUND_DOWN)
                .multiply(new BigDecimal(100))
                .setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
        }
        last = null;
        now = null;
        return result;
    }

    public double[][] macdd(int fastperiod, int slowperiod, int signalperiod) {
        List<Macd> macds = this.macd(fastperiod, slowperiod, signalperiod);
        List<Double> macd = Lists.newArrayList();
        List<Double> signal = Lists.newArrayList();
        List<Double> hist = Lists.newArrayList();
        double[][] result = new double[3][macds.size() - 1];
        macds.forEach(it -> {
            macd.add(it.getMacd());
            signal.add(it.getSignal());
            hist.add(it.getHist());
        });
        result[0] = this.getPData(macd);
        result[1] = this.getPData(signal);
        result[2] = this.getPData(hist);
        return result;
    }

    public double[][] bolld(int period, int length) {
        List<Boll> bolls = this.boll(period);
        List<Double> upperBand = Lists.newArrayList();
        List<Double> middleBand = Lists.newArrayList();
        List<Double> lowerBand = Lists.newArrayList();
        double[][] result = new double[3][bolls.size() - 1];
        bolls.forEach(it -> {
            lowerBand.add(it.getLowerBand());
            middleBand.add(it.getMiddleBand());
            upperBand.add(it.getUpperBand());
        });
        result[0] = this.getSubtract(lowerBand, length);
        result[1] = this.getSubtract(middleBand, length);
        result[2] = this.getSubtract(upperBand, length);
        return result;
    }

    private void getData(List<Stock> stock) {
        Collections.sort(stock);
        stock.forEach(it -> {

            double close = TaService.getDoubleValue(it.getTclose());
            if (close != 0) {
                this.day.add(it.getDay());
                this.tclose.add(close);
                this.high.add(TaService.getDoubleValue(it.getHigh()));
                this.low.add(TaService.getDoubleValue(it.getLow()));
                this.topen.add(TaService.getDoubleValue(it.getTopen()));
                this.lclose.add(TaService.getDoubleValue(it.getLclose()));
                this.chg.add(TaService.getDoubleValue(it.getChg()));
                this.pchg.add(TaService.getDoubleValue(it.getPchg()));
                this.turnover.add(TaService.getDoubleValue(it.getTurnover()));
                this.voturnover
                    .add(TaService.getDoubleValue(it.getVoturnover()));
                this.vaturnover
                    .add(TaService.getDoubleValue(it.getVaturnover()));
            }

        });

    }

    public double[] getSubtract(List<Double> data, int length) {
        double[] datas = data.stream().mapToDouble(Double::doubleValue)
            .toArray();
        double[] tcloses = this.tclose.stream().mapToDouble(Double::doubleValue)
            .toArray();
        double[] result = new double[datas.length];
        int starttclose = tcloses.length - length;
        int startdata = datas.length - length;
        BigDecimal last = new BigDecimal(0);
        BigDecimal now = new BigDecimal(0);
        for (int i = 0; i < length; i++) {
            last = new BigDecimal(tcloses[i + starttclose]);
            now = new BigDecimal(datas[i + startdata]);
            result[i] = last.subtract(now)
                .divide(last, 4, BigDecimal.ROUND_DOWN)
                .multiply(new BigDecimal(100))
                .setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
        }
        last = null;
        now = null;
        return result;
    }

    public double[] getPData(List<Double> data) {
        double[] datas = data.stream().mapToDouble(Double::doubleValue)
            .toArray();
        double[] result = new double[datas.length - 1];
        BigDecimal last = new BigDecimal(0);
        BigDecimal now = new BigDecimal(0);
        for (int i = 0; i < datas.length - 1; i++) {
            last = new BigDecimal(datas[i + 1]);
            now = new BigDecimal(datas[i]);
            result[i] = last.subtract(now)
                .divide(last, 4, BigDecimal.ROUND_DOWN)
                .multiply(new BigDecimal(100))
                .setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
        }
        last = null;
        now = null;
        return result;
    }

    private List<Double> sma(List<Double> datalst, int period) {
        List<Double> smas = Lists.newArrayList();
        int size = datalst.size();
        System.out.println("tclose size:" + size);
        double[] out = new double[size];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        double[] data = datalst.stream().mapToDouble(Double::doubleValue)
            .toArray();
        RetCode retCode = this.talib.sma(0, size - 1, data, period, begin,
            length, out);
        if (retCode == RetCode.Success) {
            for (int i = begin.value; i <= length.value + begin.value
                - 1; i++) {
                smas.add(out[i - begin.value]);
                //System.out.println(out[i - begin.value]);
            }
        }
        System.out.println("smas size====" + smas.size());
        return smas;
    }

    private List<Macd> macd(int fastperiod, int slowperiod, int signalperiod) {
        List<Macd> macds = Lists.newArrayList();
        int size = this.tclose.size();
        double[] out = new double[size];
        double[] outSignal = new double[size];
        double[] outHist = new double[size];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        double[] data = this.tclose.stream().mapToDouble(Double::doubleValue)
            .toArray();

        RetCode retCode = this.talib.macd(0, size - 1, data, fastperiod,
            slowperiod, signalperiod, begin, length, out, outSignal, outHist);
        if (retCode == RetCode.Success) {
            for (int i = begin.value; i <= length.value + begin.value
                - 1; i++) {
                Macd macd = new Macd();
                macd.setMacd(out[i - begin.value]);
                macd.setSignal(outSignal[i - begin.value]);
                macd.setHist(outHist[i - begin.value]);
                macds.add(macd);
            }
        }
        System.out.println("macds size====" + macds.size());
        return macds;
    }

    private List<Boll> boll(int period) {
        List<Boll> bolls = Lists.newArrayList();
        int size = this.tclose.size();
        double[] outRealUpperBand = new double[size];
        double[] outRealMiddleBand = new double[size];
        double[] outRealLowerBand = new double[size];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        double[] data = this.tclose.stream().mapToDouble(Double::doubleValue)
            .toArray();
        RetCode retCode = this.talib.bbands(0, size - 1, data, period, 2, 2,
            MAType.Sma, begin, length, outRealUpperBand, outRealMiddleBand,
            outRealLowerBand);

        if (retCode == RetCode.Success) {
            for (int i = begin.value; i <= length.value + begin.value
                - 1; i++) {
                Boll boll = new Boll();
                boll.setLowerBand(outRealLowerBand[i - begin.value]);
                boll.setMiddleBand(outRealMiddleBand[i - begin.value]);
                boll.setUpperBand(outRealUpperBand[i - begin.value]);
                bolls.add(boll);
            }
        }
        System.out.println("bolls size====" + bolls.size());
        return bolls;
    }

    private static double getDoubleValue(BigDecimal num) {
        if (num == null) {
            return 0;
        }
        return num.doubleValue();
    }

    public List<Double> getVoturnover() {
        return this.voturnover;
    }

    public void setVoturnover(List<Double> voturnover) {
        this.voturnover = voturnover;
    }

    public List<Double> getVaturnover() {
        return this.vaturnover;
    }

    public void setVaturnover(List<Double> vaturnover) {
        this.vaturnover = vaturnover;
    }

    public void setTclose(List<Double> tclose) {
        this.tclose = tclose;
    }

    public List<Double> getTclose() {
        return this.tclose;
    }

}
