package org.jianyi.yiriyigu.dl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nd4j.linalg.dataset.DataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataUtilTest {

    @Autowired
    private DataUtil dataUtil;

    @Test
    public void test() {
        try {
            DataSet dataSet = this.dataUtil.getDatasByCsv("000858", "20140101",
                "20170401", 22, 12, 26, 9);
            //System.out.println(dataSet.toString());
            /*
             * System.out.println(dataSet.getFeatureMatrix().toString());
             * System.out.println(dataSet.getLabels().toString());
             */
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
