<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <base href="<%=request.getContextPath()%>/">
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>课程签到后台管理系统</title>
    <link rel="stylesheet" href="assets/bootstrap/css/bootstrap-theme.min.css"/>
    <link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="assets/css/main.css"/>
</head>
<body ryt13519="1">

<nav class="navbar  navbj">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <h5 class="navbar-brand login-nav" ><a href="index0.html"><img class="menu-icon" src="assets/image/logo.png" width="24">课程签到后台管理系统</a></h5>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav navbar-right">
                <li class="sys-a"><span><img src="assets/image/tx2.jpg">${userInfo.nickName}</span></li>
                <li><a class="list" href="logout">退出</a></li>
            </ul>
        </div>
    </div>
</nav>
<div class="container-fluid">
    <div class="row">
        <div class="sidebar">
            <ul class="nav nav-sidebar">
                <li><a href="mt/sList"><img class="menu-icon" src="assets/image/Users.png" width="16">学生查询</a></li>
                <li><a href="mt/tList"><img class="menu-icon" src="assets/image/Users.png" width="16">教师查询</a></li>
                <li><a href="mt/courseList"><img class="menu-icon" src="assets/image/Users.png" width="16">课程查询</a></li>
                <li class="active"><a href="javascript:void(0);"><img class="menu-icon" src="assets/image/Users.png" width="16">新建用户</a></li>
                <li><a href="mt/newCourse"><img class="menu-icon" src="assets/image/Users.png" width="16">添加课程</a></li>
                <li><a href="updatePassword"><img class="menu-icon" src="assets/image/Users.png" width="16">修改密码</a></li>
            </ul>
        </div>
        <div class="main">
            <ol class="breadcrumb">
                <li><a href="javascript:void(0);">首页</a></li>
                <li><a href="javascript:void(0);">新建用户</a></li>
            </ol>
            <h1 class="pull-left">创建用户</h1>

            <hr class="clearfix">
            <form class="form-horizontal cont" id="mainForm">
                <div class="form-group">
                    <label class="col-sm-5 control-label text-right text-list">用户类型：</label>
                    <div class="col-sm-7">
                        <select class="form-control input2" id="userType">
                            <option>请选择</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label  class="col-sm-5 control-label text-right text-list">用户姓名：</label>
                    <div class="col-sm-7">
                        <input type="text" class="form-control input2" placeholder="" id="nickName">
                    </div>
                </div>
                <div class="form-group">
                    <label  class="col-sm-5 control-label text-right text-list">登录名：</label>
                    <div class="col-sm-7">
                        <input type="text" class="form-control input2" placeholder="" id="loginName">
                    </div>
                </div>

                <div class="form-group">
                    <label  class="col-sm-5 control-label">身份证号：</label>
                    <div class="col-sm-7">
                        <input type="text" class="form-control input2" placeholder="" id="idNo">
                    </div>
                </div>
                <hr>
                <div class="form-group cont">
                    <label  class="col-sm-5 control-label"></label>
                    <div class="col-sm-7">
                        <button type="button" class="btn btn-default save">保存</button>
                        <button type="reset" class="btn btn-default reset">重置</button>
                    </div>
                </div>
            </form>

        </div>
    </div>
</div>
<script src="assets/js/jquery-1.11.1.min.js"></script>
<script>
    $(document).ready(function(){
        var roleList = ${roleList};
        for(var i=0;i<roleList.length;i++) {
            var role = roleList[i];
            var html = "<option value=" + role.id + ">"+role.description+"</option>";
            $("#userType").append(html);
        }

        $(".save").on("click", function() {
            var roleId = $("#userType").val();
            if(roleId=='请选择') {
                alert("请选择用户类型！");
                return false;
            }
            var nickName = $("#nickName").val();
            if(!nickName) {
                alert("请填写用户姓名");
                return false;
            }
            var loginName = $("#loginName").val();
            if(!loginName) {
                alert("请填写用户登录名");
                return false;
            }
            var idNo = $("#idNo").val();
            if(!idNo) {
                alert("请填写用户身份证号");
                return false;
            }
            var queryParam = {"roleId":roleId, "nickName":nickName, "loginName":loginName, "idNo":idNo};

            $.ajax({
                type: 'post',
                url: '${pageContext.request.contextPath}/mt/crusr',
                contentType: 'application/json;charset=UTF-8',
                data:JSON.stringify(queryParam),
                dataType: "json",
                success: function(data) { // data 保存提交后返回的数据，一般为 json 数据
                    if(data.result=='failed') {
                        alert("数据读取访问异常！");
                    } else {
                        alert("用户创建成功。");
                    }
                },
                error: function(data){
                    console.log(data.error);
                    alert("系统内部错误，请重试。");
                }
            });
        });
        
        $(".reset").on("click", function () {
            $("#mainForm").reset();
        });
    });
</script>

</body>
</html>
