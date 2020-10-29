// 60秒倒计时
let countNum = 10;

function countDown(obj) {
    countNum--;

    if (countNum == 0) {// 倒计时结束
        $(obj).prop('disabled', false); // 按钮可用
        $(obj).val("重新发送短信");
        countNum = 10;
    } else {// 倒计时继续
        $(obj).prop('disabled', true); // 按钮不可用
        $(obj).val(countNum + "秒后可发送短信");
        setTimeout(function () { // 递归调用
            countDown(obj)
        }, 1000)
    }
}