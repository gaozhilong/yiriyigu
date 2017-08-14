package org.jianyi.yiriyigu.dl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StockClassifierOut {

    @Autowired
    private DataUtil dataUtil;

    @Autowired
    private StockClassifier stockClassifier;

    @Test
    public void test() {
        try {

            String result = this.stockClassifier
                .out(
                    this.dataUtil.getLastDataByCsv("600516", "2017-05-01",
                        "2017-07-06", 22, 12, 26, 9),
                    "E:/models/600516model.zip");
            System.out.println(result);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
