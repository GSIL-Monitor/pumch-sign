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
    <link rel="stylesheet" href="assets/lay/css/layui.css"/>
    <link rel="stylesheet" href="assets/css/main.css"/>
</head>
<body ryt13519="1">

<nav class="navbar navbj">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <h5 class="navbar-brand login-nav" ><a href="/"><img class="menu-icon" src="assets/image/logo.png" width="24">课程签到后台管理系统</a></h5>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav navbar-right">
                <li class="sys-a"><span><img src="assets/image/tx2.jpg">${userInfo.nickName}</span></li>
                <li><a  class="list" href="logout">退出</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container-fluid">
    <div class="row">
        <div class="sidebar">
            <ul class="nav nav-sidebar">
                <li class="active"><a href="javascript:void(0);"><img class="menu-icon" src="assets/image/Pen.png" width="16">签到查询</a></li>
                <li><a href="t/courseList4T"><img class="menu-icon" src="assets/image/Users.png" width="16">课程列表</a></li>
            </ul>
        </div>
        <div class="col-md-12 main ">
            <ol class="breadcrumb">
                <li><a href="#">首页</a></li>
                <li class="active"><a href="javascript:void(0);">教师用户查询</a></li>
            </ol>
            <div class="row main2 col-md-12">
                <h1 class="sub-header condition pull-left">查询条件</h1>
                <div class="pull-right">
                    <a class="btn btn-primary btn-chaxun" href="javascript:void(0);" role="button">
                        <img class="menu-icon" src="assets/image/search.png" width="13">查询
                    </a>
                    <a class="btn btn-default" href="#" role="button">
                        <img class="menu-icon" src="assets/image/Cancel.png" width="13">取消
                    </a>
                </div>
                <hr class="clearfix">
                <div class="col-md-4 ">
                    <div class="form-group">
                        <label  class="col-sm-3 control-label text-right text-list">姓名：</label>
                        <div class="col-sm-7">
                            <input type="text" id="nickName" class="form-control input2" placeholder="">
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label class="col-sm-2 control-label text-right text-list">状态：</label>
                        <div class="col-sm-8">
                            <select class="form-control input2" id="state">
                                <option>请选择状态</option>
                                <option>在籍</option>
                                <option>毕业</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class=" main mc">

            <h1 class="pull-left">查询信息</h1>
            <div class="row pull-right btn-zong">
                <a href="" class="btn btn-default "><img src="assets/image/Magnifier.png" width="13">查看设备</a>
                <a href="" class="btn btn-default "><img src="assets/image/Magnifier.png" width="13">查看积分</a>
            </div>

            <hr class="clearfix">
            <div class="table-responsive" class="clearfix">
                <table id="page-content" class="table table-striped table-hover">
                    <thead>
                    <tr>
                        <th>序号</th>
                        <th>课程名称</th>
                        <th>签到人</th>
                        <th>签到时间</th>
                    </tr>
                    </thead>
                    <tbody>
                        <template v-if="data&&data.length>0">
                            <tr role="row" id="1" tabindex="-1" class="ui-widget-content jqgrow ui-row-ltr" v-for="(record, index) in data">
                                <td role="gridcell" aria-describedby="grid-table_id">{{index+1}}</td>
                                <td role="gridcell" aria-describedby="grid-table_stock">{{record.courseName}}</td>
                                <td role="gridcell" aria-describedby="grid-table_note">{{record.nickName}}</td>
                                <td role="gridcell" aria-describedby="grid-table_ship">{{record.signInTime}}</td>
                            </tr>
                        </template>
                    </tbody>
                </table>
            </div>
            <hr>
            <div class="pull-right" id="pageCtrl"></div>
        </div>
    </div>
</div>
</body>
</html>
<script src="assets/js/jquery-1.11.1.min.js"></script>
<script src="assets/js/common.js"></script>
<script src="assets/lay/layui.js"></script>
<script src="assets/lay/lay/modules/laypage.js"></script>
<script src="assets/js/vue.min.js?v=2.5.9"></script>
<script>
    $(document).ready(function(){
        layui.config({
            dir : "assets/lay/" //layui.js 所在路径
        });

        var queryParam={};
        var vue = {};
        vue.initialize = function() {
            queryParam.sName="";
            queryParam.courseName="";
            queryParam.startTime="";
            queryParam.endTime="";
            // 翻页必须参数
            queryParam.pageSize=10;
            queryParam.currentPageIndex=1;
            queryParam.totalRecord=0;
            queryParam.data=[];
            new Vue({
                el:"#page-content",
                data:queryParam,
                methods:vue
            });
            vue.request();
            Vue.nextTick(function() {
                $("#page-content").show();
            });
        };

        vue.query=function(){
            vue.resetQuery();
            vue.request();
        };
        vue.request = function() {
            $.ajax({
                type: 'post', // 提交方式 get/post
                url: '${pageContext.request.contextPath}/t/signInList', // 需要提交的 url
                contentType: 'application/json;charset=UTF-8',
                data:JSON.stringify(queryParam),
                dataType: "json",
                success: function(data) { // data 保存提交后返回的数据，一般为 json 数据
                    if(data.error) {
                        alert("接口访问异常！");
                    } else {
                        $.extend(queryParam, data);
                        vue.page();
                    }
                },
                error: function(data){
                    console.log(data.error);
                    alert("系统内部错误，请重试。");
                }
            });
        };

        vue.resetQuery=function(){
            queryParam.currentPageIndex=1;
        };

        vue.page=function () {
            layui.use('laypage', function(){
                var laypage = layui.laypage;
                //执行一个laypage实例
                laypage.render({
                    elem:'pageCtrl', //注意，这里的是 ID，不用加 # 号
                    count: queryParam.totalRecord, //数据总数，从服务端得到 groups = 5;
                    groups:5,
                    first: "首页",
                    last: "尾页",
                    limit:queryParam.pageSize,
                    curr:queryParam.currentPageIndex,
                    limits: [10, 20, 30],
                    layout: ['prev', 'page', 'next', 'limit', 'count'],
                    jump : function(obj, first) {
                        queryParam.currentPageIndex=obj.curr;
                        if (!first) {
                            queryParam.pageSize=obj.limit;
                            vue.request();
                        }
                    }
                });
            });

            layui.use('laydate', function(){
                var laydate = layui.laydate;

                //执行一个laydate实例
                laydate.render({
                    elem: '#startTime' //指定元素
                    ,type: 'datetime'
                });
                laydate.render({
                    elem: '#endTime' //指定元素
                    ,type: 'datetime'
                });
            });
        };

        $("#firstpane .menu_body:eq(0)").show();
        $("#firstpane h3.menu_head").click(function(){
            $(this).addClass("current").next("div.menu_body").slideToggle(300).siblings("div.menu_body").slideUp("slow");
            $(this).siblings().removeClass("current");
        });
        $("#secondpane .menu_body:eq(0)").show();
        $("#secondpane h3.menu_head").mouseover(function(){
            $(this).addClass("current").next("div.menu_body").slideDown(500).siblings("div.menu_body").slideUp("slow");
            $(this).siblings().removeClass("current");
        });

        vue.initialize();

        $(".btn-chaxun").on("click", function doQuery() {
            queryParam.nickName=$("#nickName").val();
            queryParam.state=$("#state").val();
            queryParam.startTime="";
            queryParam.endTime="";
            vue.request();
        });

    });
</script>