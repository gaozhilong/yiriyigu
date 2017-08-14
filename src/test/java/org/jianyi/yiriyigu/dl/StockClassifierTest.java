package org.jianyi.yiriyigu.dl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StockClassifierTest {

    @Autowired
    private DataUtil dataUtil;

    @Autowired
    private StockClassifier stockClassifier;

    @Test
    public void test() {
        try {
            this.stockClassifier
                .eval(
                    this.dataUtil.getDatasByCsv("600516", "2017-07-01",
                        "2017-07-10", 22, 12, 26, 9),
                    "D:/stockclassic600516.zip");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
