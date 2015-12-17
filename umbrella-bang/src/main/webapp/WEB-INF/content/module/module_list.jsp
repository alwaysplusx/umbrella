<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>BanG - Module Management</title>
    <%@include file="/WEB-INF/bang/tags.jsp" %>
    
    <script type="text/javascript">
        Ext.onReady(function () {
        	
            var gridPanel = Ext.create("Ext.bang.grid.PagePanel", {
                region: "center",
                border: false,
                tbar: [{
                    text: "新建",
                    iconCls: "icon-add",
                    handler: function () {
                        var win = Ext.create("Q.bang.module.ModuleWindow");
                        this.handler = function () {
                            win.show();
                        };
                        win.show();
                    }
                }, {
                    text: "编辑",
                    iconCls: "icon-edit",
                    handler: function () {
                    }
                }, {
                    text: "删除",
                    iconCls: "icon-delete",
                    handler: function () {
                    }
                }],
                columns: [
                    {text: "Module name", width: 120, dataIndex: 'username', sortable: true},
                    {text: "Module code", width: 120, dataIndex: 'nickname', sortable: true},
                    {text: "Link", flex: 1, dataIndex: 'password', sortable: true},
                    {text: "Description", width: 125, dataIndex: 'email', sortable: true}
                ]
            });

            var listPanel = Ext.create("Ext.bang.panel.ListPanel", {
                region: "center",
                layout: "border",
                border: false,
                gridRegion: "center",
                items: [gridPanel],
                columns: [
                    {text: "Module name", width: 120, dataIndex: 'username', sortable: true},
                    {text: "Module code", width: 120, dataIndex: 'nickname', sortable: true},
                    {text: "Link", flex: 1, dataIndex: 'password', sortable: true},
                    {text: "Description", width: 125, dataIndex: 'email', sortable: true}
                ]
            });

            var viewPort = Ext.create("Ext.Viewport", {
                layout: "border",
                items: [listPanel]
            });

        });
    </script>

</head>
<body>

</body>
</html>