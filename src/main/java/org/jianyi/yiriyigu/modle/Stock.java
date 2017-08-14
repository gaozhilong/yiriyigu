package org.jianyi.yiriyigu.modle;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_stock")
public class Stock implements Comparable<Stock> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //日期
    private String day;
    //股票代码
    private String code;
    //名称
    private String name;
    //收盘价
    private BigDecimal tclose;
    //最高价
    private BigDecimal high;
    //最低价
    private BigDecimal low;
    //开盘价
    private BigDecimal topen;
    //前收盘
    private BigDecimal lclose;
    //涨跌额
    private BigDecimal chg;
    //涨跌幅
    private BigDecimal pchg;
    //换手率
    private BigDecimal turnover;
    //成交量
    private BigDecimal voturnover;
    //成交金额
    private BigDecimal vaturnover;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDay() {
        return this.day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getTclose() {
        return this.tclose;
    }

    public void setTclose(BigDecimal tclose) {
        this.tclose = tclose;
    }

    public BigDecimal getHigh() {
        return this.high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return this.low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getTopen() {
        return this.topen;
    }

    public void setTopen(BigDecimal topen) {
        this.topen = topen;
    }

    public BigDecimal getLclose() {
        return this.lclose;
    }

    public void setLclose(BigDecimal lclose) {
        this.lclose = lclose;
    }

    public BigDecimal getChg() {
        return this.chg;
    }

    public void setChg(BigDecimal chg) {
        this.chg = chg;
    }

    public BigDecimal getPchg() {
        return this.pchg;
    }

    public void setPchg(BigDecimal pchg) {
        this.pchg = pchg;
    }

    public BigDecimal getTurnover() {
        return this.turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }

    public BigDecimal getVoturnover() {
        return this.voturnover;
    }

    public void setVoturnover(BigDecimal voturnover) {
        this.voturnover = voturnover;
    }

    public BigDecimal getVaturnover() {
        return this.vaturnover;
    }

    public void setVaturnover(BigDecimal vaturnover) {
        this.vaturnover = vaturnover;
    }

    @Override
    public int compareTo(Stock o) {
        return Integer.parseInt(this.day.replaceAll("-", ""))
            - Integer.parseInt(o.getDay().replaceAll("-", ""));
    }

}
