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
        <%-- 自己的Demo--%>
        <div id="frameContent" class="content-wrapper" style="margin-left:0px;">
            <section class="content-header">
                <h1>
                    统计分析
                    <small>报运天气查询分析</small>
                </h1>
                <br>
                <div id="main" style="width: 100%;height:600px;">
                    <iframe src="https://tianqi.qq.com/" style="width: 100%;height:100%;display:inline-block;*display:inline;*zoom:1;"></iframe>
                </div>
            </section>
        </div>
    </body>
</html>