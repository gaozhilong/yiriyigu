package org.jianyi.yiriyigu.web;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.jianyi.yiriyigu.common.StockType;
import org.jianyi.yiriyigu.dl.DataUtil;
import org.jianyi.yiriyigu.dl.StockClassifier;
import org.jianyi.yiriyigu.ta.StockService;
import org.nd4j.linalg.dataset.DataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/admin")
public class AdministratorController {

    @Autowired
    private StockService stockService;

    @Autowired
    private DataUtil dataUtil;

    @Autowired
    private StockClassifier stockClassifier;

    private static final String start = "19901201";

    @ApiOperation("获取数据接口")
    @ApiResponses({
        @ApiResponse(message = "OK", response = String.class, code = 200),
        @ApiResponse(message = "获取数据出错", response = String.class, code = 500) })
    @RequestMapping(path = "/getData", method = RequestMethod.POST)
    public String ocrCard(@ApiParam("类型") @RequestParam StockType type,
            @ApiParam("代码") @RequestParam String code) {
        //String stockCode = type.value() + code;
        LocalDate date = LocalDate.now();
        DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = date.format(formater);

        Arrays.asList(code.split(",")).stream().forEach(it -> {
            String typeCode = "0";
            if (type.equals(StockType.SHEN)) {
                typeCode = "1";
            }
            this.stockService.getData(typeCode + code,
                AdministratorController.start, now);
        });

        return "success";
    }

    @ApiOperation("训练(无已训练脚本)接口")
    @ApiResponses({
        @ApiResponse(message = "OK", response = String.class, code = 200),
        @ApiResponse(message = "训练(无已训练脚本)出错", response = String.class,
                code = 500) })
    @RequestMapping(path = "/train", method = RequestMethod.POST)
    public String train(@ApiParam(value = "类型") @RequestParam StockType type,
            @ApiParam("代码") @RequestParam String code,
            @ApiParam(value = "均线周期",
                    defaultValue = "22") @RequestParam int period,
            @ApiParam(value = "MACD快速衰减周期",
                    defaultValue = "12") @RequestParam int fastperiod,
            @ApiParam(value = "MACD 慢速衰减周期",
                    defaultValue = "26") @RequestParam int slowperiod,
            @ApiParam(value = "MACD信号周期",
                    defaultValue = "9") @RequestParam int signalperiod) {
        try {

            LocalDate date = LocalDate.now();
            DateTimeFormatter formatergetdata = DateTimeFormatter
                .ofPattern("yyyyMMdd");
            DateTimeFormatter formater = DateTimeFormatter
                .ofPattern("yyyy-MM-dd");
            String now = date.format(formatergetdata);

            /*
             * String typeCode = "0";
             * if (type.equals(StockType.SHEN)) {
             * typeCode = "1";
             * }
             * this.stockService.getData(typeCode + code,
             * AdministratorController.start, now);
             */

            now = date.format(formater);

            date = date.minusMonths(2);
            String end = date.format(formater);

            DataSet dataSet = this.dataUtil.getDatasByCsv(code,
                AdministratorController.start, end, period, fastperiod,
                slowperiod, signalperiod);

            DataSet dataSetTest = this.dataUtil.getDatasByCsv(code, end, now,
                period, fastperiod, slowperiod, signalperiod);
            this.stockClassifier.train(code, dataSet, dataSetTest);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "success";
    }

    @ApiOperation("获取预测结果接口")
    @ApiResponses({
        @ApiResponse(message = "OK", response = String.class, code = 200),
        @ApiResponse(message = "获取预测结果出错", response = String.class,
                code = 500) })
    @RequestMapping(path = "/eval", method = RequestMethod.POST)
    public String eval(@ApiParam("代码") @RequestParam String code,
            @ApiParam(value = "预测的日期") @RequestParam(
                    required = false) String day,
            @ApiParam(value = "模型名称") @RequestParam(
                    required = false) String model,
            @ApiParam(value = "均线周期",
                    defaultValue = "22") @RequestParam int period,
            @ApiParam(value = "MACD快速衰减周期",
                    defaultValue = "12") @RequestParam int fastperiod,
            @ApiParam(value = "MACD 慢速衰减周期",
                    defaultValue = "26") @RequestParam int slowperiod,
            @ApiParam(value = "MACD信号周期",
                    defaultValue = "9") @RequestParam int signalperiod) {
        String result = null;
        try {
            LocalDate date = LocalDate.now();
            if (!StringUtils.isBlank(day)) {
                date = LocalDate.parse(day);
            }
            date = date.minusDays(1);
            DateTimeFormatter formater = DateTimeFormatter
                .ofPattern("yyyy-MM-dd");
            String now = date.format(formater);

            date = date.minusMonths(2);
            String startday = date.format(formater);

            DataSet dataSet = this.dataUtil.getLastDataByCsv(code, startday,
                now, period, fastperiod, slowperiod, signalperiod);
            result = this.stockClassifier.out(dataSet, code, model);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

}
