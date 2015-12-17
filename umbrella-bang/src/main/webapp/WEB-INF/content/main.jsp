<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>BanG</title>
    <%@include file="/WEB-INF/bang/tags.jsp" %>
    <script type="text/javascript" src="resources/bang/sys/controller/SystemController.js"></script>
    <script type="text/javascript">

        Ext.application({
            name: "Q.sys",
            appFolder: 'resources/bang/sys',
            controllers: ["SystemController"],
            autoCreateViewport: true
        });
    </script>
</head>
<body>
</body>
</html>
