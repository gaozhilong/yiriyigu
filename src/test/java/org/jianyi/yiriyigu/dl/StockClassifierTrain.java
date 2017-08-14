package org.jianyi.yiriyigu.dl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StockClassifierTrain {

    @Autowired
    private DataUtil dataUtil;

    @Autowired
    private StockClassifier stockClassifier;

    @Test
    public void test() {
        try {
            this.stockClassifier.train("600516",
                this.dataUtil.getDatasByCsv("600516", "2010-01-01",
                    "2017-05-01", 22, 12, 26, 9),
                this.dataUtil.getDatasByCsv("600516", "2017-05-01",
                    "2017-07-01", 22, 12, 26, 9));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
