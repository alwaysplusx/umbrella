<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>BanG - User Management</title>
    <%@include file="/WEB-INF/bang/tags.jsp" %>

    <script type="text/javascript" src="resources/plugins/extjs/container/Viewport.js"></script>
    <script type="text/javascript" src="resources/plugins/extjs/grid/PagePanel.js"></script>

    <script type="text/javascript" src="resources/bang/user/controller/UserController.js"></script>
    <script type="text/javascript" src="resources/bang/user/model/UserModel.js"></script>
    <script type="text/javascript" src="resources/bang/user/store/UserStore.js"></script>
    <script type="text/javascript" src="resources/bang/user/view/UserPage.js"></script>
    <script type="text/javascript" src="resources/bang/user/view/Viewport.js"></script>

    <script type="text/javascript" src="resources/plugins/extjs/app/Controller.js"></script>

    <script type="text/javascript">
        
        Ext.application({
            name: "Q.user",
            appFolder: 'resources/bang/user',
            controllers: ["UserController"],
            autoCreateViewport: true
        });

    </script>

</head>
<body>

</body>
</html>