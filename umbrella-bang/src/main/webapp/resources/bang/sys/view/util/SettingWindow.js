Ext.define("Q.sys.view.util.SettingWindow", {
    extend: "Ext.Window",
    xtype: "settingwindow",
    alias: "widget.settingwindow",
    title: $("label.system.setting"),
    layout: "border",
    height: 600,
    width: 400,
    border: false,
    maximizable: true,
    animCollapse: true,
    closeAction: "hide",
    initComponent: function () {
        var form = this.getSettingForm();

        Ext.apply(this, {
            defaults: {
                anchor: "100%",
                labelWidth: 120
            },
            items: [form]
        });

        this.callParent(arguments);
    },
    tools: [{
        type: "minus"
    }],
    buttonAlign: "center",
    buttons: [{
        text: $("button.apply"),
        action: "apply"
    }, {
        text: $("button.back"),
        action: "back"
    }],
    getFormPanel: function () {
        return this.getSettingForm();
    },
    getSettingForm: function () {
        var formPanel = Ext.create("Ext.form.Panel", {
            region: "center",
            border: false,
            autoScroll: true,
            viewModel: { // 定义
                data: {
                    themeLink: "{themeLink}"
                }
            },
            items: [{
                layout: "column",
                border: false,
                margin: 10,
                items: [{
                    xtype: "fieldset",
                    layout: "anchor",
                    border: false,
                    columnWidth: 1,
                    defaults: {
                        xtype: "textfield",
                        margin: 5,
                        border: false,
                        anchor: "100%"
                    },
                    items: [{
                        xtype: "displayfield",
                        fieldLabel: "登录用户",
                        value: "wuxii"
                    }, {
                        xtype: "displayfield",
                        fieldLabel: "登录时间",
                        value: new Date().format("Y-m-d H:i:s")
                    }, {
                        fieldLabel: $("label.system.theme.setting"),
                        xtype: "combo",
                        name: "themeName",
                        displayField: "name",
                        valueField: "code",
                        value: "classic",
                        store: Ext.create("Q.sys.store.ThemeStore")
                    }, {
                        xtype: "hidden",
                        name: "themeLink",
                        bind: "{themeLink}"
                    }, {
                        xtype: "radiogroup",
                        fieldLabel: $("label.system.doubleClick.action"),
                        name: "doubleClickAction",
                        layout: "column",
                        defaults: {
                            columnWidth: .33
                        },
                        items: [{
                            boxLabel: $("label.disable"),
                            name: "doubleClickAction",
                            inputValue: "disable"
                        }, {
                            boxLabel: $("label.edit"),
                            name: "doubleClickAction",
                            inputValue: "edit"
                        }, {
                            boxLabel: $("label.view"),
                            name: "doubleClickAction",
                            checked: true,
                            inputValue: "view"
                        }]
                    }, {
                        xtype: "radiogroup",
                        fieldLabel: $("label.system.list.load"),
                        name: "autoLoad",
                        layout: "column",
                        defaults: {
                            columnWidth: .45
                        },
                        items: [{
                            boxLabel: $("label.autoLoad"),
                            name: "autoLoad",
                            inputValue: "autoLoad",
                            checked: true
                        }, {
                            boxLabel: $("label.unload"),
                            name: "autoLoad",
                            inputValue: "unload"
                        }]
                    }, {
                        xtype: "button",
                        text: $("button.logout"),
                        action: "logout"
                    }]
                }]
            }]
        });
        this.getSettingForm = function () {
            return formPanel;
        }
        return formPanel;
    }
});
