package org.jianyi.yiriyigu.ta;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

public class CsvUtil {

    private final static Logger logger = LoggerFactory.getLogger(CsvUtil.class);

    public static List<String[]> getValues(byte[] data) {
        List<String[]> datas = Lists.newArrayList();
        try {
            InputStream in = new ByteArrayInputStream(data);
            List<String> values = CharStreams
                .readLines(new BufferedReader(new InputStreamReader(in)));
            values.stream().forEach(it -> {
                String[] d = it.split(",");
                datas.add(d);
            });
        } catch (IOException e) {
            CsvUtil.logger.error("生成csv文件成出错", e);
        }
        return datas;
    }

    public static BigDecimal getNumber(String num) {
        if (StringUtils.isEmpty(num)) {
            return null;
        }
        BigDecimal result = null;
        try {
            result = new BigDecimal(num);
        } catch (Exception e) {
            CsvUtil.logger.error("获取股票信息数值转换出错:" + num, e);
        }
        return result;
    }

}
