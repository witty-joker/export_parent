<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../base.jsp" %>
<!DOCTYPE html>
<html>

    <head>
        <!-- 页面meta -->
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>数据 - AdminLTE2定制版</title>
        <meta name="description" content="AdminLTE2定制版">
        <meta name="keywords" content="AdminLTE2定制版">
        <!-- Tell the browser to be responsive to screen width -->
        <meta content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no" name="viewport">
        <!-- 页面meta /-->

    </head>
    <body>
        <div id="frameContent" class="content-wrapper" style="margin-left:0px;">
            <section class="content-header">
                <h1>
                    统计分析
                    <small>员工签订的购销合同数</small>
                </h1>
            </section>
            <section class="content">
                <div class="box box-primary">
                    <div id="main" style="width: 800px;height:600px;"></div>
                </div>
            </section>
        </div>
    </body>

    <script src="../plugins/jQuery/jquery-2.2.3.min.js"></script>
    <script src="../../plugins/echarts/echarts.min.js"></script>
    <script type="text/javascript">
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('main'));
        // 指定图表的配置项和数据
        $.get('/stat/contractCharts.do').done(function (data) {
            let titles = [];
            let values = [];
            for (let r of data) {
                titles.push(r.name);
                values.push(r.value);
            }

            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(
                {
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'shadow'
                        }
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    xAxis: {
                        type: 'value',
                        axisLine: {
                            show: false
                        },
                        axisTick: {
                            show: false
                        }
                    },
                    yAxis: {
                        type: 'category',
                        data: titles,
                        splitLine: {show: false},
                        axisLine: {
                            show: false
                        },
                        axisTick: {
                            show: false
                        },
                        offset: 10,
                        nameTextStyle: {
                            fontSize: 15
                        }
                    },
                    series: [
                        {
                            name: '数量',
                            type: 'bar',
                            data: values,
                            barWidth: 14,
                            barGap: 10,
                            smooth: true,
                            label: {
                                normal: {
                                    show: true,
                                    position: 'right',
                                    offset: [5, -2],
                                    textStyle: {
                                        color: '#F68300',
                                        fontSize: 13
                                    }
                                }
                            },
                            itemStyle: {
                                emphasis: {
                                    barBorderRadius: 7
                                },
                                normal: {
                                    barBorderRadius: 7,
                                    color: new echarts.graphic.LinearGradient(
                                        0, 0, 1, 0,
                                        [
                                            {offset: 0, color: '#3977E6'},
                                            {offset: 1, color: '#37BBF8'}

                                        ]
                                    )
                                }
                            }
                        }
                    ]
                })
        });
    </script>

</html>