<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>BanG</title>
    <%@include file="/WEB-INF/bang/tags.jsp" %>
    
    <script type="text/javascript">

        Ext.require('Q.bang.sys.LoginForm');

        Ext.onReady(function () {

            var loginForm = Ext.create("Q.bang.sys.LoginForm");

            var viewPort = Ext.create('Ext.Viewport', {
                layout: "form",
                items: loginForm
            });

        });
    </script>
</head>
<body></body>
</html>