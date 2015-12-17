Ext.define("Q.bang.module.ModuleForm", {
    extend: 'Ext.form.Panel',
    xtype: "moduleform",
    region: "center",
    border: false,
    height: 200,
    width: 200,
    initComponent: function () {

        Ext.apply(this, {
            defaults: {
                anchor: '100%',
                labelWidth: 120
            }
        });

        this.callParent();
    },
    items: [{
        border: false,
        layout: "form",
        defaults: {
            xtype: "textfield",
            border: false
        },
        items: [{
            fieldLabel: $("label.module.name"),
            name: "moduleName"
        }, {
            fieldLabel: $("label.module.code"),
            name: "moduleCode"
        }]
    }]
});