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
    <body style="height: 100%; margin: 0">
        <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/echarts/dist/echarts.min.js"></script>
        <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/echarts-gl/dist/echarts-gl.min.js"></script>
        <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/echarts-stat/dist/ecStat.min.js"></script>
        <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/echarts/dist/extension/dataTool.min.js"></script>
        <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/echarts/map/js/china.js"></script>
        <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/echarts/map/js/world.js"></script>
        <script type="text/javascript" src="https://api.map.baidu.com/api?v=2.0&ak=pSs00m5v6E6UXOm8ELLg2vf99hmnsGl3&__ec_v__=20190126"></script>
        <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/echarts/dist/extension/bmap.min.js"></script>

        <%-- 自己的Demo--%>
        <div id="frameContent" class="content-wrapper" style="margin-left:0px;">
            <section class="content-header">
                <h1>
                    统计分析
                    <small>公司经营的航路图</small>
                </h1>
            </section>
            <section class="content">
                <div class="box box-primary">
                    <div id="main" style="width: 1000px;height:600px;"></div>
                </div>
            </section>
        </div>
        <%-- 自己的Demo--%>


        <script type="text/javascript">
            // 基于准备好的dom，初始化echarts实例
            var myChart = echarts.init(document.getElementById('main'));

            // 指定图表的配置项和数据
            $.get('/stat/contractCharts.do').done(function (data) {

            var app = {};
            option = null;
            var site = [
                {name: '上海', value: 50},
                {name: '厦门', value: 50},
                {name: '深圳', value: 60},
                {name: '三亚', value: 54},
                {name: '长崎', value: 60},
                {name: '新加坡', value: 60},
                {name: '马尼拉', value: 60},
                {name: '雅加达', value: 60},
            ];
            var geoCoordMap = {
                '上海':[121.48,31.22],
                '厦门':[118.1,24.46],
                '深圳':[114.07,22.62],
                '珠海':[113.52,22.3],
                '三亚':[109.511909,18.252847],
                '长崎':[130,30],
                '新加坡':[103.51,1.18],
                '马尼拉':[120.984219,14.599512],
                '雅加达':[106.845172,-6.211544],
            };

            var convertData = function (site) {
                var res = [];
                for (var i = 0; i < site.length; i++) {
                    var geoCoord = geoCoordMap[site[i].name];
                    if (geoCoord) {
                        res.push({
                            name: site[i].name,
                            value: geoCoord.concat(site[i].value)
                        });
                    }
                }
                return res;
            };

            option = {
                title: {
                    text: '航运路线图',
                    left: 'center'
                },
                tooltip : {
                    trigger: 'item'
                },
                bmap: {
                    center: [104.114129, 37.550339],
                    zoom: 5,
                    roam: true,
                    mapStyle: {
                        styleJson: [{
                            'featureType': 'water',
                            'elementType': 'all',
                            'stylers': {
                                'color': '#d1d1d1'
                            }
                        }, {
                            'featureType': 'land',
                            'elementType': 'all',
                            'stylers': {
                                'color': '#f3f3f3'
                            }
                        }, {
                            'featureType': 'railway',
                            'elementType': 'all',
                            'stylers': {
                                'visibility': 'off'
                            }
                        }, {
                            'featureType': 'highway',
                            'elementType': 'all',
                            'stylers': {
                                'color': '#fdfdfd'
                            }
                        }, {
                            'featureType': 'highway',
                            'elementType': 'labels',
                            'stylers': {
                                'visibility': 'off'
                            }
                        }, {
                            'featureType': 'arterial',
                            'elementType': 'geometry',
                            'stylers': {
                                'color': '#fefefe'
                            }
                        }, {
                            'featureType': 'arterial',
                            'elementType': 'geometry.fill',
                            'stylers': {
                                'color': '#fefefe'
                            }
                        }, {
                            'featureType': 'poi',
                            'elementType': 'all',
                            'stylers': {
                                'visibility': 'off'
                            }
                        }, {
                            'featureType': 'green',
                            'elementType': 'all',
                            'stylers': {
                                'visibility': 'off'
                            }
                        }, {
                            'featureType': 'subway',
                            'elementType': 'all',
                            'stylers': {
                                'visibility': 'off'
                            }
                        }, {
                            'featureType': 'manmade',
                            'elementType': 'all',
                            'stylers': {
                                'color': '#d1d1d1'
                            }
                        }, {
                            'featureType': 'local',
                            'elementType': 'all',
                            'stylers': {
                                'color': '#d1d1d1'
                            }
                        }, {
                            'featureType': 'arterial',
                            'elementType': 'labels',
                            'stylers': {
                                'visibility': 'off'
                            }
                        }, {
                            'featureType': 'boundary',
                            'elementType': 'all',
                            'stylers': {
                                'color': '#fefefe'
                            }
                        }, {
                            'featureType': 'building',
                            'elementType': 'all',
                            'stylers': {
                                'color': '#d1d1d1'
                            }
                        }, {
                            'featureType': 'label',
                            'elementType': 'labels.text.fill',
                            'stylers': {
                                'color': '#999999'
                            }
                        }]
                    }
                },
                series : [
                    {
                        name: '航运起点',
                        type: 'graph',
                        coordinateSystem: 'bmap',
                        layout: 'none',
                        data: convertData(site),
                        /* 这里修改 data 地址 */
                        links: [{
                            source: '深圳',
                            target: '长崎'
                        }, {
                            source: '深圳',
                            target: '新加坡'
                        },{
                            source: '深圳',
                            target: '马尼拉'
                        }, {
                            source: '深圳',
                            target: '雅加达'
                        },{
                            source: '三亚',
                            target: '雅加达'
                        },
                            {
                                source: '上海',
                                target: '长崎'
                            },
                            {
                                source: '上海',
                                target: '马尼拉'
                            },{
                                source: '上海',
                                target: '新加坡'
                            },{
                                source: '上海',
                                target: '深圳'
                            },{
                                source: '上海',
                                target: '三亚'
                            },{
                                source: '厦门',
                                target: '马尼拉',
                            },{
                                source: '马尼拉',
                                target: '厦门'
                            }
                        ],
                        roam: true,
                        focusNodeAdjacency: true,
                        itemStyle: {
                            normal: {
                                borderColor: '#fff',
                                borderWidth: 1,
                                shadowBlur: 10,
                                shadowColor: 'rgba(0, 0, 0, 0.3)'
                            }
                        },
                        hoverAnimation: true,
                        label: {
                            normal: {
                                formatter: '{b}',
                                position: 'right',
                                show: true
                            }
                        },
                        lineStyle: {
                            color: 'source',
                            curveness: 0.3
                        },
                        emphasis: {
                            lineStyle: {
                                width: 2
                            }
                        }
                    }
                ]
            };;

            if (option && typeof option === "object") {
                myChart.setOption(option, true)
            }
            });
        </script>
    </body>
</html>