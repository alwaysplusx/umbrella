<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>BanG - Test Page</title>
	<%@include file="/WEB-INF/bang/tags.jsp" %>
    <script type="text/javascript">
        Ext.onReady(function () {

            var sourcePanel = Ext.create("Ext.form.Panel", {
                // title: "source",
                region: "west",
                width: "50%",
                border: false,
                split: true,
                layout: "fit",
                // collapsible: true,
                items: [{
                    layout: "column",
                    height: "100%",
                    margin: "27",
                    border: false,
                    defaults: {
                        hideLabel: true,
                        margin: "5, 5, 5, 0",
                        border: false
                    },
                    items: [{
                        hideLabel: false,
                        xtype: "textfield",
                        fieldLabel: "xtype",
                        name: "xtype",
                        value: "Ext.Panel"
                    }, {
                        xtype: "textarea",
                        fieldLabel: "source",
                        name: "source",
                        height: "90%",
                        width: "100%",
                        value: "{title: \"Hello\"}",
                        listeners: {
                            specialkey: function (t, e, eOpts) {
                                if (e.getKey() == e.TAB) {
                                    return false;
                                }
                            }
                        }
                    }]
                }],
                tbar: [{
                    text: "preview",
                    iconCls: "icon-app",
                    handler: function () {
                        var form = sourcePanel.getForm();
                        var source = form.findField("source").getValue();
                        var xtype = form.findField("xtype").getValue();
                        try {
                            var opt = Ext.decode(source);
                            var component = Ext.create(xtype, opt);
                            container.removeAll();
                            container.add(Ext.create("Ext.Panel", {
                            	border: false,
                            	items: component
                            }));
                        } catch (e) {
                            Q.tip(e.message);
                        }

                    }
                }]
            });

            var container = Ext.create("Ext.Panel", {
            	region: "center",
            	border: false,
            	margin: 20
            });
            
            var previewPanel = Ext.create("Ext.Panel", {
                // title: "preview",
                region: "center",
                border: false,
                layout: "border",
                tools: [{
                    type: 'refresh',
                    callback: function () {
                    	container.removeAll();
                    }
                }],
                header: {
                    title: "preview",
                    titlePosition: 2,
                    titleAlign: 'center'
                },
                items: [container]
            });

            var viewPort = Ext.create("Ext.Viewport", {
                layout: "border",
                items: [sourcePanel, previewPanel]
            })

        });
    </script>
</head>
<body>
</body>
</html>
