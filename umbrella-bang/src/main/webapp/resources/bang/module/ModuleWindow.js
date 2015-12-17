Ext.require([
    "Q.bang.module.ModuleForm"
]);

Ext.define("Q.bang.module.ModuleWindow", {
    extend: "Ext.Window",
    xtype: "modulewindow",
    // baseCls: "",
    title: "Module Window",
    header: {
        titlePosition: 0,
        titleAlign: 'center'
    },
    titleCollapse: false,
    // titleRotation: 0,
    closable: false,
    border: false,
    closeAction: "hide",
    maximized: true,
    // maximizable: true,
    layout: "border",
    initComponent: function () {
        var form = this.getModuleForm();

        Ext.apply(this, {
            defaults: {
                anchor: '100%',
                labelWidth: 120
            },
            items: [form]
        });

        this.callParent();
    },
    tbar: [{
        text: $("button.back"),
        iconCls: "icon-back",
        handler: function () {
            this.findParentByType("modulewindow").hide();
        }
    }],
    getModuleForm: function () {
        var form = Ext.create("Q.bang.module.ModuleForm");
        this.getModuleForm = function () {
            return form;
        };
        return form;
    },
    getFormPanel: function () {
        return this.getModuleForm();
    }
});