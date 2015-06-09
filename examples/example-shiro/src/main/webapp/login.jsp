<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login Page</title>
<link rel="stylesheet" type="text/css" href="http://www.jeasyui.com/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="http://www.jeasyui.com/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="http://www.jeasyui.com/easyui/themes/color.css">
<link rel="stylesheet" type="text/css" href="http://www.jeasyui.com/easyui/demo/demo.css">
<script type="text/javascript" src="http://code.jquery.com/jquery-1.6.min.js"></script>
<script type="text/javascript" src="http://www.jeasyui.com/easyui/jquery.easyui.min.js"></script>
<style type="text/css">
</style>
</head>
<body class="easyui-layout">
  <div data-options="region:'center'">
    <div class="easyui-dialog" data-options="closable:false" title="Login Form" style="width: 350px;">
      <div style="padding: 10px 0 10px 60px">
        <form name="loginForm" action="login" method="post">
          <table>
            <tr>
              <td>Username:</td>
              <td><input class="easyui-validatebox" type="text" name="username" data-options="required:true" /></td>
            </tr>
            <tr>
              <td>Password:</td>
              <td><input class="easyui-validatebox" type="text" name="password" data-options="required:true" /></td>
            </tr>
          </table>
        </form>
      </div>
      <div class="errmsg" style="text-align: center; padding: 5px;"></div>
      <div style="text-align: center; padding: 5px">
        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()">Submit</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="resetForm()">Reset</a>
      </div>
    </div>
  </div>
  <script type="text/javascript">
	function submitForm() {
		$("form[name=loginForm]").form("submit", {
			onSubmit : function() {
				return $(this).form("validate");
			},
			success : function(data) {
				var result = $.parseJSON(data);
				if (result.success) {
					window.location.href = result.url;
				} else {
					$.messager.show({
						title : ' 提 示 ',
						msg : result.errmsg,
						showType : 'show'
					});
				}
			}
		});
	}
	function resetForm() {
		$("form[name=loginForm]").form("clear");
	}
  </script>
</body>
</html>