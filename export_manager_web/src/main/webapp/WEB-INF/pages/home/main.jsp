<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" language="java" %>
<%@ include file="../base.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <!-- 页面meta -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>数据 - AdminLTE2定制版</title>
    <meta name="description" content="AdminLTE2定制版">
    <meta name="keywords" content="AdminLTE2定制版">
    <meta content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no" name="viewport">
</head>

<body class="hold-transition skin-purple sidebar-mini" >
<div class="wrapper">
    <!--引入头部-->
    <jsp:include page="header.jsp"></jsp:include>
    <!--引入左侧-->
    <jsp:include page="aside.jsp"></jsp:include>

    <!--内容区域-->
    <div class="content-wrapper">
        <iframe id="iframe" name="iframe" style="overflow:visible;"
                scrolling="auto" frameborder="no" height="100%" width="100%"
                src="/home/home.do">
        </iframe>
    </div>

    <!--尾部区域-->
    <jsp:include page="footer.jsp"></jsp:include>

</div>


<script src="../plugins/jQuery/jquery-2.2.3.min.js"></script>
<script src="../plugins/jQueryUI/jquery-ui.min.js"></script>
<script>
    $.widget.bridge('uibutton', $.ui.button);
</script>
<script src="../plugins/bootstrap/js/bootstrap.min.js"></script>
<script src="../plugins/raphael/raphael-min.js"></script>
<script src="../plugins/morris/morris.min.js"></script>
<script src="../plugins/sparkline/jquery.sparkline.min.js"></script>
<script src="../plugins/jvectormap/jquery-jvectormap-1.2.2.min.js"></script>
<script src="../plugins/jvectormap/jquery-jvectormap-world-mill-en.js"></script>
<script src="../plugins/knob/jquery.knob.js"></script>
<script src="../plugins/daterangepicker/moment.min.js"></script>
<script src="../plugins/daterangepicker/daterangepicker.js"></script>
<script src="../plugins/daterangepicker/daterangepicker.zh-CN.js"></script>
<script src="../plugins/datepicker/bootstrap-datepicker.js"></script>
<script src="../plugins/datepicker/locales/bootstrap-datepicker.zh-CN.js"></script>
<script src="../plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.all.min.js"></script>
<script src="../plugins/slimScroll/jquery.slimscroll.min.js"></script>
<script src="../plugins/fastclick/fastclick.js"></script>
<script src="../plugins/iCheck/icheck.min.js"></script>
<script src="../plugins/adminLTE/js/app.min.js"></script>
<script src="../plugins/treeTable/jquery.treetable.js"></script>
<script src="../plugins/select2/select2.full.min.js"></script>
<script src="../plugins/colorpicker/bootstrap-colorpicker.min.js"></script>
<script src="../plugins/bootstrap-wysihtml5/bootstrap-wysihtml5.zh-CN.js"></script>
<script src="../plugins/bootstrap-markdown/js/bootstrap-markdown.js"></script>
<script src="../plugins/bootstrap-markdown/locale/bootstrap-markdown.zh.js"></script>
<script src="../plugins/bootstrap-markdown/js/markdown.js"></script>
<script src="../plugins/bootstrap-markdown/js/to-markdown.js"></script>
<script src="../plugins/ckeditor/ckeditor.js"></script>
<script src="../plugins/input-mask/jquery.inputmask.js"></script>
<script src="../plugins/input-mask/jquery.inputmask.date.extensions.js"></script>
<script src="../plugins/input-mask/jquery.inputmask.extensions.js"></script>
<script src="../plugins/datatables/jquery.dataTables.min.js"></script>
<script src="../plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="../plugins/chartjs/Chart.min.js"></script>
<script src="../plugins/flot/jquery.flot.min.js"></script>
<script src="../plugins/flot/jquery.flot.resize.min.js"></script>
<script src="../plugins/flot/jquery.flot.pie.min.js"></script>
<script src="../plugins/flot/jquery.flot.categories.min.js"></script>
<script src="../plugins/ionslider/ion.rangeSlider.min.js"></script>
<script src="../plugins/bootstrap-slider/bootstrap-slider.js"></script>
<script src="../plugins/bootstrap-datetimepicker/bootstrap-datetimepicker.js"></script>
<script src="../plugins/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js"></script>
<script src="${pageContext.request.contextPath}/js/countDown.js"></script>
<script>
    function setIframeHeight(iframe) {
        if (iframe) {
            var iframeWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
            var height = iframeWin.document.getElementById('frameContent').scrollHeight +20 ;
            iframe.height =height;
        }
    };

    var iframe= document.getElementById("iframe");
    iframe.onload = function () {
        setIframeHeight(iframe);
    };
</script>

    <%-- 用户修改模态框 --%>
    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content"  style="width: 720px">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">账户信息及密码修改</h4>
                </div>
                <div class="modal-body">
                    <!--用户信息-->
                    <div class="panel panel-default">
                        <div class="panel-heading">用户信息</div>
                        <form id="editMessage" action="/home/update.do" method="post" enctype="multipart/form-data">
                            <input type="hidden" id="id" name="id" value="${loginUser.id}">
                            <input type="hidden" id="deptName" name="deptName" value="${loginUser.deptName}">
                            <div class="row data-type" style="margin: 0px">
                                <div class="col-md-2 title">用户名称</div>
                                <div class="col-md-4 data">
                                    <input type="text" class="form-control" placeholder="用户名称" name="userName" value="${loginUser.userName}">
                                </div>

                                <div class="col-md-2 title">性别</div>
                                <div class="col-md-4 data">
                                    <div class="form-group form-inline">
                                        <div class="radio"><label><input type="radio" ${loginUser.gender==0?'checked':''} name="gender" value="0">男</label></div>
                                        <div class="radio"><label><input type="radio" ${loginUser.gender==1?'checked':''} name="gender" value="1">女</label></div>
                                    </div>
                                </div>

                                <div class="col-md-2 title">邮箱</div>
                                <div class="col-md-4 data">
                                    <input type="text" class="form-control" placeholder="邮箱" name="email" value="${loginUser.email}">
                                </div>

                                <div class="col-md-2 title">密码</div>
                                <div class="col-md-4 data">
                                    <input type="password" class="form-control" placeholder="密码" name="password" value="${loginUser.password}">
                                </div>

                                <div class="col-md-2 title">电话</div>
                                <div class="col-md-4 data">
                                    <input type="text" class="form-control" placeholder="电话" name="telephone" id="telephone" value="${loginUser.telephone}">
                                </div>

                                <div class="col-md-2 title">头像</div>
                                <div class="col-md-4 data">
                                    <input type="file" class="form-control" placeholder="头像" name="portrait" value="${loginUser.pic}">
                                </div>

                                <div class="col-md-2 title">出生年月</div>
                                <div class="col-md-4 data">
                                    <div class="input-group date">
                                        <div class="input-group-addon">
                                            <i class="fa fa-calendar"></i>
                                        </div>
                                        <input type="text" placeholder="出生年月" class="form-control pull-right" name="birthday"
                                               value="${loginUser.birthday}" id="datepicker1">
                                    </div>
                                </div>

                                <div class="col-md-2 title">说明</div>
                                <div class="col-md-4 data">
                                    <input type="text" class="form-control" placeholder="说明" name="remark" value="${loginUser.remark}">
                                </div>

                                <div class="col-md-2 title">验证码</div>
                                <div class="col-md-4 data">
                                    <input type="text" class="form-control" id="login_check" name="smsCode" placeholder="请输入验证码">
                                </div>
                                <div class="col-md-6">
                                    <input id="login_sendSmsCode" href="#" class="btn btn-link" value="发送手机验证码"></input>
                                </div>

                            </div>
                        </form>
                    </div>
                    <!--用户信息/-->
                </div>

                <div class="modal-footer">
                    <span id="loginPhoneInfo" style="color:red"></span>
                <%--onclick='document.getElementById("editMessage").submit()' --%>
                    <button type="button" onclick='document.getElementById("editMessage").submit()' id="telLogin" class="btn bg-maroon">提交修改</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                </div>

            </div><!-- /.modal-content -->
        </div><!-- /.modal -->
    </div>
    <script>
        $('#datepicker1').datepicker({
            autoclose: true,
            format: 'yyyy-mm-dd'
        });
    </script>
    <!-- 模态框（Modal） -->

    <%--发送短信--%>
    <script>
        // 1.给按钮绑定点击事件
        $('#login_sendSmsCode').click(function () {
            // 2.获取手机号的值
            let telephone = $('#telephone').val();
            // 3.正则校验
            let reg = /^1[3456789]\d{9}$/;
            if (reg.test(telephone)) { // 校验通过
                // 4.通过ajax发送短信
                let url = '${pageContext.request.contextPath}/codeLogin.do';
                let data = 'action=ajaxSendSms&telephone=' + telephone;
                $.get(url, data, function (resp) {
                    $('#loginPhoneInfo').css('color', 'green').html('发送成功。');
                })

                // 调用60秒倒计时函数
                countDown(this)

            } else { // 校验失败
                $('#loginPhoneInfo').css('color', 'red').html('手机号输入错误。');
            }
        })
    </script>
    <%--发送短信--%>
    <input id="1" value="${loginMsg}"/>
    <%--手机登录--%>
    <script>
        // 1.给按钮绑定点击事件
        $(document).ready(function(){
            <%--alert(`<button type="button" class="btn btn-default" data-dismiss="modal">${sessionScope.loginMsg}</button>`)--%>
            let i = $('#1').val();
            if (i == "a") {
                alert("验证码错误")
            }
        })
    </script>
    <%--发送短信--%>
</body>

</html>
<!---->
