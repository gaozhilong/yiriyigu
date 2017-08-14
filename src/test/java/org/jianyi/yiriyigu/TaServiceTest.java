package org.jianyi.yiriyigu;

import java.util.Arrays;

import org.jianyi.yiriyigu.ta.TaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaServiceTest {

    @Autowired
    private TaService taService;

    @Test
    public void test() {
        try {
            this.taService.init("600685", "20140101", "20170401");
            double[] tclose = this.taService.getTclosed(22);
            System.out.println(tclose.length);
            System.out.println(Arrays.toString(tclose));

            double[] tclosesubsma = this.taService
                .getSubtractSma(this.taService.getTclose(), 22, 10);
            System.out.println(tclosesubsma.length);
            System.out.println(Arrays.toString(tclosesubsma));

            double[] smas = this.taService.smad(22, 10);
            System.out.println(smas.length);
            System.out.println(Arrays.toString(smas));

            double[][] macds = this.taService.macdd(12, 26, 9);
            System.out.println(macds[0].length);
            System.out.println(Arrays.toString(macds[0]));
            System.out.println(macds[1].length);
            System.out.println(Arrays.toString(macds[1]));
            System.out.println(macds[2].length);
            System.out.println(Arrays.toString(macds[2]));

            double[][] bolls = this.taService.bolld(22, 10);
            System.out.println(bolls[0].length);
            System.out.println(Arrays.toString(bolls[0]));
            System.out.println(bolls[1].length);
            System.out.println(Arrays.toString(bolls[1]));
            System.out.println(bolls[2].length);
            System.out.println(Arrays.toString(bolls[2]));

            double[] tcloseclassic = this.taService.getTclosedClassic(22);
            System.out.println(tcloseclassic.length);
            System.out.println(Arrays.toString(tcloseclassic));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
