package org.jianyi.yiriyigu;

import org.jianyi.yiriyigu.ta.StockService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetDataTest {

    @Autowired
    private StockService stockService;

    @Test
    public void test() {
        this.stockService.getData("0600516", "20100101", "20170707");
    }

}
