package org.jianyi.yiriyigu.ta;

import java.util.List;

import org.jianyi.yiriyigu.httpclient.NetEaseClient;
import org.jianyi.yiriyigu.modle.Stock;
import org.jianyi.yiriyigu.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private NetEaseClient netEaseClient;

    public void getData(String code, String start, String end) {
        byte[] data = this.netEaseClient.getData(code, start, end);
        this.save(CsvUtil.getValues(data));
    }

    public void save(List<String[]> datas) {
        datas.remove(0);
        datas.stream().forEach(it -> {
            String code = it[1].replaceAll("'", "");
            List<Stock> lst = this.stockRepository.findByDayAndCode(it[0],
                code);
            if (lst == null || lst.isEmpty()) {
                Stock stock = new Stock();
                stock.setDay(it[0]);
                stock.setCode(code);
                stock.setName(it[2]);
                stock.setTclose(CsvUtil.getNumber(it[3]));
                stock.setHigh(CsvUtil.getNumber(it[4]));
                stock.setLow(CsvUtil.getNumber(it[5]));
                stock.setTopen(CsvUtil.getNumber(it[6]));
                stock.setLclose(CsvUtil.getNumber(it[7]));
                stock.setChg(CsvUtil.getNumber(it[8]));
                stock.setPchg(CsvUtil.getNumber(it[9]));
                stock.setTurnover(CsvUtil.getNumber(it[10]));
                stock.setVoturnover(CsvUtil.getNumber(it[11]));
                stock.setVaturnover(CsvUtil.getNumber(it[12]));
                this.stockRepository.save(stock);
            }
        });
    }

}
