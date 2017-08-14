package org.jianyi.yiriyigu.ta.quantization;

public class Macd {

    private double macd;

    private double signal;

    private double hist;

    public double getMacd() {
        return this.macd;
    }

    public void setMacd(double macd) {
        this.macd = macd;
    }

    public double getSignal() {
        return this.signal;
    }

    public void setSignal(double signal) {
        this.signal = signal;
    }

    public double getHist() {
        return this.hist;
    }

    public void setHist(double hist) {
        this.hist = hist;
    }

    @Override
    public String toString() {
        return "Macd [macd=" + this.macd + ", signal=" + this.signal + ", hist="
            + this.hist + "]";
    }

}
