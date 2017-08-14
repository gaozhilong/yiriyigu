package org.jianyi.yiriyigu.httpclient;

import org.jianyi.yiriyigu.ta.CsvUtil;
import org.junit.Test;

public class NetEaseClientTest {

    @Test
    public void test() {
        NetEaseClient netEaseClient = new NetEaseClient();
        byte[] data = netEaseClient.getData("0000858", "20170701", "20170704");
        CsvUtil.getValues(data);
    }

}
